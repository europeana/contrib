package gr.ntua.ivml.mint.db;


import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.ValueEdit;
import gr.ntua.ivml.mint.persistent.XpathHolder;
import gr.ntua.ivml.mint.util.Tuple;

import java.math.BigInteger;
import java.util.List;

public class ValueEditDAO extends DAO<ValueEdit, Long> {
	
	public List<ValueEdit> listByDataset( Dataset ds ) {
		List<ValueEdit> res = getSession().createQuery(
				"from ValueEdit where dataset = :ds order by xpathHolder, dbID" )
				.setEntity("ds", ds )
				.list();
		return res;
	}
	
	public List<ValueEdit> listByXpathHolder( XpathHolder xp ) {
		List<ValueEdit> res = getSession().createQuery(
		"from ValueEdit where xpathHolder = :xp order by dbID desc" )
		.setEntity("xp", xp )
		.list();
		return res;
	}
	
	public void removeLatestByXpathHolder( XpathHolder xp ) {
		getSession().createQuery("delete from ValueEdit where dbID = " +
				" ( select dbID from ValueEdit where path = :xp order by dbID desc limit 1)")
				.setEntity("xp", xp)
				.executeUpdate();
	}
	
	/**
	 * Cache for all changes is keyed on number of edits and the maximum edit for the holder.
	 * Edit removal will change the number, adding edits will change the max.
	 * @param xp
	 * @return
	 */
	public Tuple<Integer,Integer> getEditKey( XpathHolder xp ) {
		List<Object[]> res = getSession().createQuery( "max( ve.dbID ), count(*) from ValueEdit ve where path = :xp" )
			.setEntity( "xp", xp )
			.list();
		for( Object[] o2: res ) {
			Integer max, count;
			max = -1; count = 0;
			if( o2[0] instanceof BigInteger ) {
				max = ((BigInteger) o2[0]).intValue();
			}
			if( o2[1] instanceof BigInteger ) {
				count = ((BigInteger) o2[1]).intValue();
			}
			return new Tuple<Integer,Integer>( max, count);
		}
		return new Tuple<Integer, Integer>(-1, 0);
	}
}
