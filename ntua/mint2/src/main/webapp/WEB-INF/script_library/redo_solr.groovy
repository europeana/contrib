
import gr.ntua.ivml.mint.concurrent.*
import gr.ntua.ivml.mint.persistent.*
import org.apache.log4j.Logger;

// delete old solr index and reindex everything
 
solr = Solarizer.getSolrServer();
solr.deleteByQuery("*:*");
solr.commit();
count = 0

for( Dataset ds: DB.datasetDAO.findAll() ) {
	si = new Solarizer(ds );
	Queues.queue( si, "single" );
	count++
}
println "Queued $count datasets for indexing!"

 
  