package gr.ntua.ivml.mint.util;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.db.Meta;
import gr.ntua.ivml.mint.persistent.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/**
 * Accepts requests to publish stuff on Noterik server.
 * A  thread is running when there is stuff to send.
 * It'll block if there is nothing to do and it will wait
 * if it can't reach the server for a while and retry.
 * 
 * @author Arne Stabenau 
 *
 */

public class PublishQueue {
	
	private static long currentRetryDelay = 10*1000l;
	
	static final Logger log = Logger.getLogger( PublishQueue.class );
	
	private static BlockingQueue<QueueItem> queue = new LinkedBlockingQueue<QueueItem>();
	private static final Thread queueWorker = new Thread( new Runnable() {
		@Override
		public void run() {
			PublishQueue.workQueue();
		}
	}, "noterikPublisher");

	static {
		queueWorker.start();
	}
	
	static boolean wait = false;
	
	// we store those in a queue, so that when there is no contact
	// we wait some time and retry
	public static class QueueItem {
		String euscreenId;
		Long internalId;
		boolean removeRequest;
	}
	
	// wakes up the queue for retry
	public static void retry() {
		// the queue will wait on the queueWorker Thread
		synchronized( queueWorker ) {
			queueWorker.notify();
		}
	}
	
	// at most one more item will be taken from the queue after this returns
	// Should be zero, but that needs re-implement of the blocking queue
	public static void haltQueue() {
		synchronized(queueWorker) {
			wait = true;
		}
	}
	
	// this will as well resume the queue if it was stopped on error
	public static void resumeQueue() {
		synchronized(queueWorker) {
			wait = false;
			queueWorker.notify();
		}
	}
	
	public static boolean queueUpdate( String euscreenId ) {
		return queueEuscreen(euscreenId, false );
	}
	
	public static boolean queueRemove( String euscreenId ) {
		return queueEuscreen(euscreenId, true );
	}
	
	
	private static boolean queueEuscreen( String euscreenId, boolean remove ) {
		// get all items with this euscreenID
		List<Item> li=  DB.getItemDAO().simpleList("persistentId='"+euscreenId+"'");
		// find the appropriate item id and queue
		// just get the newest if there are more than 1 
		// TODO: check published state and validity!
		if( li.size() == 0 ) {
			log.info( "Id " + euscreenId + " not found" );
			return false;
		} else if( li.size()> 1 ) {
			Collections.sort( li, new Comparator<Item>() {
				public int compare(Item o1, Item o2) {
					return o2.getLastModified().compareTo(o1.getLastModified());
				}
			});
			if( li.size()>2 ) log.warn( "Id " + euscreenId + " is more than twice in the db.");
		}
		queueItem( li.get(0),remove );		
		return true;
	}
	
	public static void queueUpdateItem( Long itemId ) {
		Item it = DB.getItemDAO().getById(itemId, false);
		queueItem( it, false);
	}
	
	public static void queueRemoveItem( Long itemId ) {
		Item it = DB.getItemDAO().getById(itemId, false);
		queueItem( it, true);
	}
	
	
	
	public static void queueItem( Item item, boolean remove ) {
		// TODO: should check schema and validity!!
		QueueItem qi = new QueueItem();
		qi.euscreenId = item.getPersistentId();
		qi.internalId = item.getDbID();
		qi.removeRequest = remove;
		// queue
		queue.add( qi );
	}
	

	
	/**
	 * runs permanently, blocks if nothing is to do.
	 */
	public static void workQueue() {
		QueueItem currentItem;

		//TODO: don't hold connection forever
			// stateless or stateful, it will expire 
		while( true ) {
			try {
				// we may be stuck here for long, return the connection
				DB.closeSession();

				// stop the queue for a bit
				synchronized( queueWorker ) {
					while( wait ) {
						queueWorker.wait();
					}
				}
				currentItem = queue.take();

				boolean success = true;
				if( currentItem.removeRequest ) {
					if( ! sendItem( null, currentItem.euscreenId )) 
						success = false;

				} else {
					Item i = DB.getItemDAO().getById(currentItem.internalId, false );
					if( i == null ) {
						log.info( "Item " + currentItem.internalId + " not available any more!" );
					} else 
						if( ! sendItem( i, currentItem.euscreenId )) 
							success = false;
				}
				if( ! success ) {
					queue.add( currentItem );
					synchronized( queueWorker ) {
						log.info( "Posting resumes in " + (currentRetryDelay/1000) + "secs." );
						queueWorker.wait(currentRetryDelay);
						currentRetryDelay *= 1.5;
					}
				}
			} catch( InterruptedException ie ) {}
		}
	}

	
	
	/**
	 * POST item to noterick server, return false if things don't go well.
	 * @param i
	 * @return
	 */
	public static boolean sendItem( Item i, String euscreenId ) {
		HttpClient hc = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Config.get( "portal.indexurl" ));

		try {
			boolean onPortal = false;
			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			if( i != null ) {
				params.add(new BasicNameValuePair("doc", i.getXml()));
				onPortal = true; 
				euscreenId = i.getPersistentId();
			}
			params.add(new BasicNameValuePair("id", euscreenId));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			//Execute and get the response.
			HttpResponse response = hc.execute(httppost);
			log.debug( "Noterick answered " + euscreenId + " with code " + response.getStatusLine().getStatusCode());
			
			// housekeeping
			currentRetryDelay = 10*1000l; // 10 sec
			
			// make note in db
			DB.getStatelessSession().beginTransaction();
			Meta.put( "ItemOnPortal["+euscreenId+"]", (onPortal?"true":"false"));
			// maybe this helps with expiring connections
			DB.closeStatelessSession();
			return true;
		} catch( Exception e ) {
			// something went wrong
			log.error( "Posting to Noterick failed at id " + euscreenId, e );
			return false;
		}
	}
}
