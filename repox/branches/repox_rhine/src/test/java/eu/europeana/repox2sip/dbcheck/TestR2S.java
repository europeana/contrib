package eu.europeana.repox2sip.dbcheck;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import eu.europeana.definitions.domain.Country;
import eu.europeana.repox2sip.Repox2SipException;
import eu.europeana.repox2sip.dao.Repox2SipImpl;
import eu.europeana.repox2sip.fixture.DatabaseFixture;
import eu.europeana.repox2sip.models.Aggregator;
import eu.europeana.repox2sip.models.Provider;
import eu.europeana.repox2sip.models.ProviderType;

/**
 * This class should call public methods in the DbActions class in order to test Repox2Sip functionalities.
 * After every call the db status is modified.
 */

public class TestR2S {

	 
	 
	
	public static void main(String[] args) {
		DbActions dba= new DbActions();
		//dba.insertAggregator();
		try {
			
			dba.getRequestMetadataRecords(10);
			
		} catch (Repox2SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        System.out.println("*********  main: done");

	}
	
	
}
