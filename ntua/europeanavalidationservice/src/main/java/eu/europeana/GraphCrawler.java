package eu.europeana;

import java.util.Map;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.impl.util.StringLogger;
import org.neo4j.kernel.logging.BufferingLogger;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.helpers.collection.IteratorUtil;
import scala.collection.Iterator;
import  org.neo4j.graphdb.Path;

public class GraphCrawler {

	GraphDatabaseService graphDb;
	ExecutionEngine engine;
	private static Index<Node> nodeIndex;
	
	public GraphCrawler(){
		graphDb = new RestGraphDatabase("http://localhost:7474/db/data");
		
		StringLogger logger = new BufferingLogger();
		engine = new ExecutionEngine( graphDb, logger  );
		nodeIndex = graphDb.index().forNodes("nodes");
	}
	
	   /**
     * @author Georgios Markakis (gwarkx@hotmail.com)
     *
     * Nov 29, 2013
     */
    private class  RelType implements RelationshipType{
    	private String name;
    	
    	/**
    	 * @param name
    	 */
    	public RelType(String name){
    		this.name = name;
    	}
    	
		@Override
		public String name() {
			return name;
		}

    }
	
	
	public IndexHits<Node> totalItems(String itemTypeLabel){
		
		IndexHits<Node> nd = nodeIndex.get("type", itemTypeLabel);

		/*
		while(nd.hasNext()){
			Node node = nd.next();
			 System.out.println( node + ": " + node.getProperty( "rdf:about" )  + node.getProperty( "type" )) ;
		}
		*/
		
		//ExecutionResult result = engine.execute( "start n=node(*) where n.name = 'my node' return n, n.name" );
		//ExecutionResult result = engine.execute( "start n=node(*) where n.type = '"+
        //itemTypeLabel+"' return n, n.type" );

		
		///Iterator<Node> n_column = result.columnAs( "n" );

		//while(n_column.hasNext()){
		//	Node node = n_column.next();
		//	System.out.println(node + ": " + node.getProperty( "name" )) ;
		//}
		/*
		//for ( Node node : IteratorUtil.asIterable( (scala.collection.Iterator) n_column ) )
		for ( Node node : IteratorUtil.asIterable( (scala.collection.Iterator) n_column ) )
		{
		    System.out.println(node + ": " + node.getProperty( "name" )) ;
		}
		*/
		return nd;
	}
	
	
	/**
	 * @param node
	 * @param depth
	 * @return
	 */
	public String nodesTraverser( Node node,int depth )
    {
        String output = "";

        for ( Path position : Traversal.description()
                .depthFirst()
                .evaluator( Evaluators.toDepth( depth ) )
                .traverse( node ) )
        {
        	
            //output += position + "\n";
        	output += "++++++++++++++++++++++=";
        	output += "\n";
        	Node startnode = position.startNode();
        	
        	output +=(String) startnode.getProperty("type") + (String) startnode.getProperty("rdf:about");
        	Node endnode = position.endNode();
        	output += (String) endnode.getProperty("type") + (String) endnode.getProperty("rdf:about");
        }
        return output;
    }
	
}
