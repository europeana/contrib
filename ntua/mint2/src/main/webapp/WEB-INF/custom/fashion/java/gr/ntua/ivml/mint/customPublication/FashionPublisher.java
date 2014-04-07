package gr.ntua.ivml.mint.customPublication;


import gr.ntua.ivml.mint.RecordMessageProducer;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.pi.messages.ExtendedParameter;
import gr.ntua.ivml.mint.pi.messages.ItemMessage;
import gr.ntua.ivml.mint.pi.messages.Namespace;
import gr.ntua.ivml.mint.xml.transform.XSLTransform;
import gr.ntua.ivml.mint.xsd.ReportErrorHandler;
import gr.ntua.ivml.mint.xsd.SchemaValidator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.opensymphony.xwork2.util.TextParseUtil;


/**
 * This class publishes metadata from the Fashion portal database to the OAI. 
 * 
 * See the comments at the main class and the parameters.
 * @author nsimou
 *
 */
public class FashionPublisher
{
	
	//Database properties
	String url = "jdbc:mysql://panic.image.ntua.gr:3306/";  //the portal database server
	String dbName = "portal";								//the DB name
	String driver = "com.mysql.jdbc.Driver";                //the DB driver
	String userName = "fashion";                            //the portal username
	String password = "portal";								//the portal password

	//Paths
	String xslPath = "/Users/nsimou/Documents/git/mint2/WEB-INF/custom/fashion/xsl/"; 					//the path where the xsls used for the transformation are saved
	String xmlPath ="/Users/nsimou/Documents/Education/NTUA/Projects/Running/Fashion/Publication/";		//the path uses to export DB dump and Invalid files
	
	
	//OAI publication parameters
	String routingKey = "fashion.oai";									//the queue routing key
	String schemaPrefix = "rdf";										//the prefix used for the OAI
	String schemaUri= "http://www.w3.org/1999/02/22-rdf-syntax-ns#";	//the uri of the schema
	String oaiServerHost = "panic.image.ece.ntua.gr";					//the oai server
	int oaiServerPort = 3009;											//the oai server port
	String queueHost = "guinness.image.ece.ntua.gr";					//the oai queue host
	XmlSchema xs = DB.getXmlSchemaDAO().getByName("EDM");               //the schema used for validation - it has to be included in the database defined in hibernate.properties
	RecordMessageProducer rmp;
	
	public void inintRMP(){
		try {
			rmp = new RecordMessageProducer(queueHost, "mint" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		FashionPublisher fashionExporter = new FashionPublisher();
//		fashionExporter.exportXML();      			//Exports the XMLs of the DB to the 
//		fashionExporter.exportXMLFromEachOrg(5);    //Exports the specified number of records from each organization
		fashionExporter.inintRMP();					//Initializes the Record message Producer used for publishing
//		fashionExporter.publish(10);                //Publishes to OAI the specified number of records from each organization
		
		fashionExporter.publish(-1);				//Publishes to OAI all the records from each organization - it uses getOrgs() modify depending on the
													//organizations you want to publish.
//		fashionExporter.detectOrgs(-1);             //Prints the dataProvider field for the records that are under NTUA (org_id=1). The correct ids have to be
													//found for each org and used for updating the database.
	}
	
	private Connection getConnection(){
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	private void exportXML(){
		Connection conn = null;
		try {
			conn = getConnection();
			Statement st = conn.createStatement();

			String countQuery = "select count(*) from record";
			String query = "select mint_org_id,xml from record";
			
			ResultSet res = st.executeQuery(countQuery);
			res.next();
			int orgTotal = res.getInt("count(*)");
			res = st.executeQuery(query);
			int countRecords = 0;
			while (res.next()) {
				countRecords++;
				String xml = res.getString("xml");
				System.out.println("Processing record "+countRecords+"/"+orgTotal+" "+(countRecords/orgTotal * 100) );
				PrintWriter out = new PrintWriter(xmlPath+"xml/Dump/Record_"+countRecords+".xml");
				out.println(xml);
				out.close();
			}
			
		}catch (Exception e) {
	      e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void exportXMLFromEachOrg(int recordsFromEachOrg){
		Connection conn = null;
		try {
			conn = getConnection();
			ArrayList<Integer> orgs = getOrgs();
			
			for (int i =0; i < orgs.size(); i++){
				String query;
				if (recordsFromEachOrg > 0 )
					query = "select xml from record where mint_org_id="+orgs.get(i)+" limit "+recordsFromEachOrg+"";
				else 
					query = "select xml from record where mint_org_id="+orgs.get(i);
				
				Statement st = conn.createStatement();
				ResultSet res = st.executeQuery(query);
				
				int countRecords = 0;
				while (res.next()) {
					countRecords++;
					String xml = res.getString("xml");
					PrintWriter out = new PrintWriter(xmlPath+"DataPerOrgs/Record_"+orgs.get(i)+"_"+countRecords+".xml");
					out.println(xml);
					out.close();
				}
			}
		}catch (Exception e) {
	      e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private ArrayList<Integer> getOrgs(){
		Connection conn = null;
		try {
			conn = getConnection();
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery("select distinct mint_org_id from record");
			ArrayList<Integer> orgs = new ArrayList<Integer>();

			while (res.next()) {
				int mintOrgID = res.getInt("mint_org_id");
//				if(mintOrgID != 1 && mintOrgID != 1002 && mintOrgID != 1008 
//						&& mintOrgID != 1014 && mintOrgID != 1016 && mintOrgID != 1018)
//					orgs.add(new Integer(mintOrgID));
				if(mintOrgID == 1002)
					orgs.add(new Integer(mintOrgID));
//				if(mintOrgID != 1)
//					orgs.add(new Integer(mintOrgID));
				
			}
			
			return orgs;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private void publish(int recordsFromEachOrg){
		Connection conn = null;
		try {
			conn = getConnection();
			ArrayList<Integer> orgs = getOrgs();
			String report = "\n\nOrg_id	Records	Published	Invalid\n";
			for (int i = 0; i < orgs.size(); i++){
				String query;
				if( recordsFromEachOrg > 0)
					query = "select * from record where mint_org_id="+orgs.get(i)+" limit "+recordsFromEachOrg+"";
				else
					query = "select * from record where mint_org_id="+orgs.get(i);

				String countQuery = "select count(*) from record where mint_org_id="+orgs.get(i);
				
				
				Statement st = conn.createStatement();
				ResultSet res = st.executeQuery(countQuery);
				res.next();
				int orgTotal = res.getInt("count(*)");
			    int orgCount = 0;
			    int published = 0;
			    int invalid = 0;
				res = st.executeQuery(query);
				
				while (res.next()) {
				    orgCount++;
				    if(orgCount > 65000){
				    	String xml = res.getString("xml");
				    	System.out.println("Publishing "+orgCount+"/"+orgTotal+" from org "+orgs.get(i)+" "+(i+1)+"/"+orgs.size());
				    	String transformed = transform(xml,res.getString("hash"),res.getInt("mint_org_id"));
				    	System.out.println("	Transformed "+orgCount+"/"+orgTotal+" from org "+orgs.get(i)+" "+(i+1)+"/"+orgs.size());
				    	if (validate (transformed) ){
				    		System.out.println("	Validated "+orgCount+"/"+orgTotal+" from org "+orgs.get(i)+" "+(i+1)+"/"+orgs.size());
				    		publishItem(res.getInt("mint_dataset_id"), res.getInt("report_id"), res.getInt("mint_org_id"), res.getInt("mint_record_id"), transformed);
				    		System.out.println("	Published "+orgCount+"/"+orgTotal+" from org "+orgs.get(i)+" "+(i+1)+"/"+orgs.size());
				    		published++;
				    	}else{
				    		invalid++;
				    		File file = new File(xmlPath+"Invalid/"+orgs.get(i));
				    		FileUtils.forceMkdir(file);
				    		PrintWriter out = new PrintWriter(xmlPath+"Invalid/"+orgs.get(i)+"/"+orgs.get(i)+"_"+invalid+".xml");
				    		out.println(xml);
				    		out.close();
				    	}
				    }
				}
				
				report = report+"Org_id:"+orgs.get(i)+"	Records:"+orgTotal+"	Published:"+published+"	Invalid:"+invalid+"\n";
			}
			
			System.out.println(report);
			System.exit(0);
		}catch (Exception e) {
	      e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean validate (String xml){
		try {
			ReportErrorHandler report = SchemaValidator.validate(xml, xs);
			if(report.isValid())
				return true;
			else
				return false;
		} catch (Exception e) {			
			e.printStackTrace();
			return false;
		}

	}
	
	private String transform(String input, String hash, int orgID){
		try {
		XSLTransform transformer = new XSLTransform();
		
		File xslPart1File = new File( xslPath + "EDMFP2EDMPart1.xsl");
		File xslPart2File = new File( xslPath + "EDMFP2EDMPart2.xsl");
		String printDescription = "true";
		if (orgID == 1003) //SPK
			printDescription = "false";
		
		String xslPart1 = FileUtils.readFileToString(xslPart1File, "UTF-8" );
		String xslPart2 = FileUtils.readFileToString(xslPart2File, "UTF-8" );
		String xsl = xslPart1 +
				"	<xsl:param name=\"var2\" select=\"'"+hash+"'\" /> \n  "+
				"	<xsl:param name=\"var3\" select=\"'"+printDescription+"'\" />  "+xslPart2;
		
		return transformer.transform(input, xsl);
		
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} 
	}
	
	private void detectOrgs(int recordsFromEachOrg){
		Connection conn = null;
		try {
			ArrayList<String> orgs = new ArrayList<String>();
			conn = getConnection();
			String query;
			if( recordsFromEachOrg > 0)
				query = "select * from record where mint_org_id=1 limit "+recordsFromEachOrg+"";
			else
				query = "select * from record where mint_org_id=1";
			String countQuery = "select count(*) from record where mint_org_id=1";


			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery(countQuery);
			res.next();
			int orgTotal = res.getInt("count(*)");
			int count=0;
			res = st.executeQuery(query);

			while (res.next()) {
				String xml = res.getString("xml");
				String dataProvider = getDataProvider(xml);
				System.out.println(dataProvider+" "+count++);
				if(!orgs.contains(dataProvider))
					orgs.add(dataProvider);
			}
			
			
			for(int i =0; i<orgs.size(); i++){
				System.out.println(orgs.get(i));
			}
		}catch (Exception e) {
	      e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getDataProvider(String xml){
		String dataProvider = "";
		Document doc = parseXML(xml);
		NodeList properties = doc.getElementsByTagName("edm:dataProvider");
		
		Node edmDataProvider = properties.item(0);
//		System.out.println(edmDataProvider.getNodeName());
		Node skosConcept = edmDataProvider.getFirstChild();
//		System.out.println(skosConcept.getNodeName());
		NodeList skosConceptProperties = skosConcept.getChildNodes();
		for(int i=0; i < skosConceptProperties.getLength(); i++){
			Node skosProperty = skosConceptProperties.item(i);
			if(skosProperty.getNodeName().equals("skos:prefLabel"))
				dataProvider = skosProperty.getTextContent();			
		}
		
		return dataProvider;
		
	}
	
	private Document parseXML(String xml) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new ByteArrayInputStream(xml.getBytes()));

			// normalize text representation
			doc.getDocumentElement ().normalize ();
			return doc;
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void publishItem(int mintDatasetID,  int reportID,  int mintOrgID,  int mintRecordID,  String transformation){
		try {
//			RecordMessageProducer rmp = new RecordMessageProducer(queueHost, "mint" );
			Namespace ns = new Namespace();
			int schemaId = 1003; //EDM
			String routingKeysConfig = routingKey;
			Set<String> routingKeys =  TextParseUtil.commaDelimitedStringToSet(routingKeysConfig);
			
			ns.setPrefix(schemaPrefix);
			ns.setUri(schemaUri);
			
			String projectName = "";
			for( String s: routingKeys ) {
				if( s.contains("oai")) projectName= s;
			}
						
			ExtendedParameter ep = new ExtendedParameter();
			ep.setParameterName("reportId" );
			ep.setParameterValue(""+reportID);
			final ArrayList<ExtendedParameter> params = new ArrayList<ExtendedParameter>();
			params.add( ep );
			

			ItemMessage im = new ItemMessage();
			im.setDataset_id(mintDatasetID);
			im.setDatestamp(System.currentTimeMillis());
			im.setItem_id(mintRecordID);
			im.setOrg_id(mintOrgID);
			im.setPrefix(ns);
			im.setProject("");
			im.setSchema_id(schemaId);
			im.setSourceDataset_id(mintDatasetID);
			im.setSourceItem_id(mintRecordID);
			im.setUser_id(1);
			im.setXml(transformation);
			im.setParams(params);


			for( String routingKey: routingKeys ) 
				rmp.send(im, routingKey );
			
						
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
}

