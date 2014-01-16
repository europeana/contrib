
import gr.ntua.ivml.mint.concurrent.*
import gr.ntua.ivml.mint.persistent.*
import org.apache.log4j.Logger;
import net.sf.ehcache.*

// delete old solr index and reindex everything
 
solr = Solarizer.getSolrServer();
solr.deleteByQuery("*:*");
solr.commit();

for( Dataset ds: DB.datasetDAO.findAll() ) {
	si = new Solarizer(ds );
	Queues.queue( si, "single" );
}

def fin = {
	log = Logger.getLogger( Queues.class );
	log.warn( "Finished indexing")
	// clean the cache
//	CacheManager.getInstance().getEhcache("SimplePageCachingFilter").removeAll()
}
// Queues.queue( fin, "single")
  
  