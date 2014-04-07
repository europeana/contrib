package gr.ntua.ivml.mint.db;


import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.util.ApplyI;

import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.StatelessSession;

public class ItemDAO extends DAO<Item, Long> {
	
	
	public List<Item> getItemsByDataset( Dataset ds, long start, int max ) {
		List<Item> li = getSession().createQuery( " from Item where dataset=:ds order by item_id ")
			.setEntity("ds", ds)
			.setMaxResults(max)
			.setFirstResult((int) start )
			.list();
		return li;
	}
	
	public List<Item> getValidItemsByDataset( Dataset ds, long start, int max ) {
		List<Item> li = getSession().createQuery( " from Item where dataset=:ds and valid = true order by item_id ")
			.setEntity("ds", ds)
			.setMaxResults(max)
			.setFirstResult((int) start )
			.list();
		return li;
	}
	
	public List<Item> getInvalidItemsByDataset( Dataset ds, long start, int max ) {
		List<Item> li = getSession().createQuery( " from Item where dataset=:ds and valid = false order by item_id ")
			.setEntity("ds", ds)
			.setMaxResults(max)
			.setFirstResult((int) start )
			.list();
		return li;
	}
	
	public List<Item> getDerived( Item item ) {
		List<Item> li = getSession().createQuery( " from Item where sourceItem = :item")
		.setEntity("item", item)
		.list();
		return li;		
	}
	
	public Item getDerived( Item item, Dataset ds ) {
		if( ds == null ) return null;
		Item result = (Item) getSession().createQuery( " from Item where sourceItem = :item and dataset = :ds")
		.setEntity("item", item)
		.setEntity( "ds", ds)
		.uniqueResult();
		return result;		
	}
	
	/**
	 * Under which schemas is this persistent id published? In which datasets
	 * @param id
	 * @return
	 */
	public List<Object[]> getIdPublished( String id) {
		List<Object[]> published = getSession().createQuery( "select xs.name, ds from Item i, PublicationRecord pr " +
				"join i.dataset as ds " +
				"join ds.schema as xs " +
				"where pr.publishedDataset = ds " +
				"and pr.status = 'OK' " +
				"and i.valid = true " +
				"and i.persistentId = :id" )
			.setString( "id", id )
			.list();
		return published;
	}
	
	/**
	 * Go over the item for the Dataset and apply operation.
	 * The items will not be in hibernate session, to not pollute it
	 * Stops the operation on Exception in apply.
	 * @param ds
	 * @param operation
	 */
	public void applyForDataset( Dataset ds, ApplyI<Item> operation, boolean withState ) throws Exception {
		String cond = "dataset = " + ds.getDbID() + " order by item_id";
		if( withState )
			onAll( operation, cond, true );
		else
			onAllStateless(operation, cond);
	}
	
	/**
	 * Apply operation for each item in the id list, store all the changed items back into the db.
	 * Keep caches clean and mem use low.
	 * @param idList
	 * @param operation
	 * @throws Exception
	 */
	public void applyForIdList( List<Long> idList, ApplyI<Item> operation ) throws Exception {
		// make id list string
		if(( idList == null ) || idList.isEmpty()) return;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( Long id: idList ) {
			if( !first ) {
				sb.append( ", " );
			} else {
				first = false;
			}
			sb.append( id.toString());
		}

		onAll( operation, "dbID in (" + sb.toString()+")", true );
	}
	
	
	/**
	 * Iterates and repeatedly throws the retrieved Item out of session.
	 * If you change the item that will be committed.
	 * Careful: Session has to be cleared regularly here.
	 * Do not commit in the middle !!!
	 * @param ds
	 * @param operation
	 * @throws Exception
	 */
	public void changeAllItems( Dataset ds, ApplyI<Item> operation ) throws Exception {
		ScrollableResults sr=null;

		sr =  getSession().createQuery("from Item where dataset = :ds order by item_id" )
				.setEntity("ds", ds)
				.setFetchSize(100)
				.scroll(ScrollMode.FORWARD_ONLY);
		CacheMode cm = getSession().getCacheMode();
		getSession().setCacheMode(CacheMode.IGNORE);
		try {
			while( sr.next()) {
				Item i = (Item) sr.get()[0];
				operation.apply(i);
				getSession().flush();
				getSession().evict( i );
			}	
			DB.commit();
		} catch( Throwable th ) {
			log.error( "Iterate over items problem!",  th);
			throw new Exception( th );
		} finally {
			getSession().setCacheMode(cm);
			if( sr != null ) sr.close();
		}

	}
}
