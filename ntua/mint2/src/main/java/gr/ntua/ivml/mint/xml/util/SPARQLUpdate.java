package gr.ntua.ivml.mint.xml.util;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class SPARQLUpdate {
//	
//	static String assemblerFile = "/usr/local/fuseki/config-thesauri.ttl";
//	static Dataset ds = DatasetFactory.assemble(assemblerFile) ;
////	static Dataset ds = DatasetFactory.create("http://localhost:3030/thesauri");
//	static GraphStore graphStore = GraphStoreFactory.create(ds) ;
//	
//	public static void execUpdateQuery(String sparqlEndpoing, String update){
//		GraphStore graphStore = GraphStoreFactory.create(ds) ;
//		UpdateAction.parseExecute("DROP ALL", graphStore) ;
//	}
//	
//	public static void execOperations(String sparqlEnpoint, String operations){
//		UpdateRequest request = UpdateFactory.create() ;
//		request.add("DROP ALL")
//		       .add("CREATE GRAPH <http://example/g2>")
//		       .add("LOAD <file:etc/update-data.ttl> INTO <http://example/g2>") ;
//
//		// And perform the operations.
//		UpdateAction.execute(request, graphStore) ;
//	}
	
	public static String file2Triples(String filename){
		Model model = ModelFactory.createDefaultModel() ; 
//		file://
		model.read("file:///"+filename, "RDF/XML");
		StringWriter out = new StringWriter();
		model.write(out, "N-TRIPLES");
//		model.write(out, "TURTLE");
		return out.toString();
		
	}
	
	public static void update(String repository,String query){
		UpdateRequest queryObj = UpdateFactory.create(query); 
		UpdateProcessor qexec = UpdateExecutionFactory.createRemote(queryObj, repository); 
		qexec.execute(); 
	}
	
	public static void main(String args[]){
		String endpoint2 = "http://localhost:3030/thesauri/update";
		String file = "Users/nsimou/Desktop/Linked Heritage SKOS/Euphoto.rdf";
//		System.out.println(file2Triples(file));
		
//		String updateQuery1 = "CLEAR ALL";
//		update(endpoint2, updateQuery1);
//		
//		String updateQuery3 = "DELETE DATA { "+file2Triples(file)+" }";
//		update(endpoint2, updateQuery3);
		
		String input = "Nikos ";
		int hashCode = input.hashCode();
		System.out.println("input hash code = " + hashCode);
	}

}
