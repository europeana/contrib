package eu.europeana;

import java.io.FileNotFoundException;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */

public class AppTest 
//    extends TestCase
{
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public AppTest( String testName )
//    {
//        super( testName );
//    }

    
    
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite()
//    {
//        return new TestSuite( AppTest.class );
//    }


    /**
     * Rigourous Test :-)
     * @throws FileNotFoundException 
     * 
     */
    @Test 
    public void parsefile() 
    {
    	GraphConstructor constr = new GraphConstructor();
    	
    	try {
			constr.parseFile("src/test/resources/EDM_test_record-update.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        assertTrue( true );
    }
    
    
    /**
     * 
     */
    public void findResults(){
    	GraphCrawler crawler = new GraphCrawler(); 
    	crawler.totalItems("edm:WebResource");
    	crawler.totalItems("edm:Agent");
    	crawler.totalItems("edm:Place");
    	crawler.totalItems("edm:TimeSpan");
    	crawler.totalItems("skos:Concept"); 
    	crawler.totalItems("ore:Aggregation");
    	crawler.totalItems("ore:Proxy");
    }
    
    public void traverse(){
    	GraphCrawler crawler = new GraphCrawler();
    	
    	IndexHits<Node> items = crawler.totalItems("ore:Aggregation");
    	

		while(items.hasNext()){
			Node node = items.next();
			String path = crawler.nodesTraverser(node,2);
			
			System.out.println(path);
			System.out.println("++++++++++++++++++++++++++++++");
		}

    	
    }
    

}
