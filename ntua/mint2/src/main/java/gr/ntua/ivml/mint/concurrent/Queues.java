package gr.ntua.ivml.mint.concurrent;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * Use queue to put your runnables into the appropriate queue. Add one if you think
 * you need your own.
 * @author Arne Stabenau 
 *
 */
public class Queues {
	
	static class LightSerialQueue {
		String name;
		boolean active;
		Queue<Runnable> jobs = new LinkedList<Runnable>();
		class WrappedRunnable implements Runnable {
			String[] serialQueues;
			int currentQueue;
			String finalQueue;
			Runnable job;
			public void run() {
				try {
					job.run();
				} finally {
					// tell everybody I'm done
					for( String name: serialQueues ) {
						Queues.serialQueues.get( name ).done();
					}
				}
			}
			public WrappedRunnable(  Runnable job, String finalQueue, String[] serialQueues ) {
				this.serialQueues = serialQueues;
				this.finalQueue = finalQueue;
				this.job = job;
				currentQueue = -1;
			}
			
		}
		public void serialQueue( Runnable job, String finalQueue, String... serialQueues ) {
			if( active ) {
				Runnable wrappedRunnable = job;
				jobs.add( wrappedRunnable );
			} else {
				active=true;
				
			}
		}
		
		// job announces that it is finished
		public void done( ) {
			if( !jobs.isEmpty() ) {
				Runnable newJob = jobs.poll();
				
			} else {
				// should remove the queue
				serialQueues.remove( name );
			}
		}
	}
	static public interface ConditionedRunnable extends Runnable {
		// when this returns true, the Runnable is put to the queue it was 
		// submitted to
		public boolean isRunnable();
		
		// returns true, when isRunnable() cannot return true any more.
		public boolean isUnRunnable();
	}
	
	static Map<String, ExecutorService> queues = new HashMap<String,ExecutorService>();
	static Map<Runnable, Future<?>> futures = new HashMap<Runnable, Future<?>>();
	static List<Tuple<Runnable, String>> conditionedRunnables = new ArrayList<Tuple<Runnable, String>>();
	
	static HashMap<String, LightSerialQueue> serialQueues = new HashMap<String, Queues.LightSerialQueue>();

	static {
		// db queue allows 2 parallel jobs on db heavy stuff
		// transformation goes here and upload part two ( parse and index )
		ExecutorService e = Executors.newFixedThreadPool(2);
		queues.put( "db", e);
		// net queue allows for parallel network heavy operations
		// upload part one goes here (with blob to db part)
		e = Executors.newFixedThreadPool(4);
		queues.put( "net", e);
		
		// for stuff that is not thread safe
		e = Executors.newFixedThreadPool(1);
		queues.put( "single", e);
		
		// Jobs that need a thread very often and immediately use this one
		e =  Executors.newCachedThreadPool();
		queues.put( "now", e);
		
		// something to check the conditioned tasks regularly
		Ticker.getTimer().schedule( new TimerTask() {
			@Override
			public void run() {
				try {
					testConditionedRunnables();
				} catch( Exception e ) {
					
				} finally {
					DB.closeSession();
					DB.closeStatelessSession();
				}
			}
		}, 60*1000l, 60*1000l);
	}

	static Logger log = Logger.getLogger(Queues.class);
	
	/**
	 * Lookup of the queue and put the Job there.
	 * 
	 * "db" - queue for database heavy tasks (parsing / indexing)
	 * "net" - queue for network access (currently oai / ftp / http download )
	 * "now" - queue for simple thread pool access and immediate execution
	 * @param r
	 * @param queueName
	 */
	public synchronized static void queue( Runnable r, String queueName ) {
		
		if( r instanceof ConditionedRunnable ) {
			if( ! ((ConditionedRunnable)r).isRunnable() ) {
				conditionedRunnables.add( new Tuple<Runnable, String>(r,queueName));
				return;
			}
		}
		
		ExecutorService es = queues.get( queueName );
		log.debug( "Submitting "+ r.getClass().getCanonicalName() + " to " + queueName );
		Future<?> f = es.submit(r);
		
		// cleanup the futures
		Iterator<Entry<Runnable,Future<?>>> i = futures.entrySet().iterator();
		while( i.hasNext()) {
			Entry<Runnable,Future<?>> e = i.next();
			if( e.getValue().isDone())
				i.remove();
		}
		// put the next
		futures.put( r, f );
	} 
	
	
	/**
	 * Try to cancel an upload. If its in the Queue or running
	 * this will do something, but it might not be here yet...
	 * It might still be in its own thread.
	 * @param du
	 */
	public synchronized static boolean cancelUpload( DataUpload du ) {
		Long id = du.getDbID();
		boolean success = false;
		for( Entry<Runnable, Future<?>> e: futures.entrySet()) {
			try {
				Runnable r = e.getKey();
				if( r instanceof UploadIndexer ) {
					UploadIndexer ui = (UploadIndexer) r;
					if( ui.getDataUpload().getDbID().equals( du.getDbID())) {
						success = e.getValue().cancel(true);
					}
				}
			} catch( Exception e2 ) {
				log.error( "couldnt cancel Upload "+du.getDbID(), e2);
			}
		}
		return success;
	}
	
	// TODO: This might work simply with Hash access .. :-) too scared to try
	private static Future<?> getFuture( Runnable r ) {
		for( Entry<Runnable, Future<?>> e: futures.entrySet()) 
			if( e.getKey() == r ) return e.getValue();
		return null;
	}
	
	
	
	
	/**
	 * Submit all Runnables that fulfill their condition. Will be called regularly from the timer.
	 */
	public static void testConditionedRunnables() {
		Iterator<Tuple<Runnable, String >> i = conditionedRunnables.iterator();
		while( i.hasNext()) {
			Tuple<Runnable, String> t  = i.next();
			ConditionedRunnable cr = (ConditionedRunnable) t.first();
			if( cr.isRunnable()) {
				log.info("Scheduled a conditioned run.");
				queue( t.first(), t.second());
				i.remove();
			} else {
				if( cr.isUnRunnable()) i.remove();
			}
		}
	}
	
	
	/**
	 * Wait until the given Runnable has finished.
	 * @param r
	 */
	public static void join( Runnable r ) {
		Future<?> f = getFuture(r);
		if( f == null ) return;
		else {
			try {
				log.debug( "Waiting for "+ (r.getClass().toString()));
				f.get();
				log.debug( "Done");
			} catch( Exception e ) {
				log.error( "Task didnt complete well ", e );
			}
		}
	}
}


// two ideas that might be funny / useful
// the light serial queues. You submit a job there and it guarantees, that it will only execute 
// the next one if the previous has run

// multiple jobs on a dataset could be serialized in one dataset related queue.
// and in a queue chain, once the original job finished execution, next one is started down the chain

// conditionedJobs are nice and lightweight, but require timer and pulling. How about some message based system

// submit a job to a conditionedqueue, and have the queue receive messages that prompt the check of all
// queued jobs if they can run (or not run)
// eliminates the polling, allow for params in the message

