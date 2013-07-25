import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.*;

import gr.ntua.ivml.awareness.search.SolrHelper;
import gr.ntua.ivml.awareness.search.SolrModifier;
import gr.ntua.ivml.awareness.util.MongoDB;
import play.Application;
import play.GlobalSettings;
import play.Logger;

public class Global extends GlobalSettings {

  @Override
  public void onStart(Application app) {
	  MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);  
    Logger.info("Application has started...");
    //init the mongodb connection and morphia mappings
    MongoDB mgdb=new MongoDB();
    SolrHelper.setDocumentModifier( new SolrModifier() );
  }  
  
  @Override
  public void onStop(Application app) {
    Logger.info("Application shutdown...");
  }  
    
}