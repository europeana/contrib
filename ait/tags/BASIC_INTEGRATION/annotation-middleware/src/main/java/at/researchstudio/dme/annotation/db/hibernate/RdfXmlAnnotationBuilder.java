package at.researchstudio.dme.annotation.db.hibernate;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.sail.memory.MemoryStore;

import at.researchstudio.dme.annotation.config.Config;


/**
 * converts annotations to rdf/xml and vice versa.
 * we use an Annotea (http://www.w3.org/2001/Annotea/) based model to represent annotations.
 * 
 * @author Christian Sadilek
 */
public class RdfXmlAnnotationBuilder {
	public static final String ANNOTATION_NS = "http://lemo.mminf.univie.ac.at/annotation-core#";
	public static final String ANNOTATION = ANNOTATION_NS+"Annotation";
	public static final String ANNOTATION_ANNOTATES = ANNOTATION_NS+"annotates";
	public static final String ANNOTATION_CREATED = ANNOTATION_NS+"created";
	public static final String ANNOTATION_MODIFIED = ANNOTATION_NS+"modified";			
	public static final String ANNOTATION_FRAGMENT = ANNOTATION_NS+"fragment";		
	public static final String ANNOTATION_LABEL = ANNOTATION_NS+"label";		
	public static final String ANNOTATION_AUTHOR = ANNOTATION_NS+"author";		
	
	public static final String ANNOTEA_ANNOTATION_NS = "http://www.w3.org/2000/10/annotation-ns#";
	public static final String ANNOTEA_ANNOTATION_BODY = ANNOTEA_ANNOTATION_NS+"body";

	public static final String ANNOTATION_REL_NS = "http://lemo.mminf.univie.ac.at/ann-relationship#";
	public static final String ANNOTATION_LINKED_TO = ANNOTATION_REL_NS+"isLinkedTo";		
	
	public static final String ANNOTATION_TYPE_NS = "http://www.w3.org/2000/10/annotationType#";	
	public static final String ANNOTATION_TYPE_COMMENT = ANNOTATION_TYPE_NS+"Comment";

	public static final String ANNOTATION_IMAGE_NS = "http://lemo.mminf.univie.ac.at/annotation-image#";
	public static final String ANNOTATION_IMAGE_FRAGMENT = ANNOTATION_IMAGE_NS+"svg-fragment";		

	public static final String ANNOTATION_VIDEO_NS = "http://lemo.mminf.univie.ac.at/annotation-video#";
	public static final String ANNOTATION_VIDEO_FRAGMENT = ANNOTATION_VIDEO_NS+"video-fragment";		

	public static final String ANNOTATION_SCOPE_NS = "http://lemo.mminf.univie.ac.at/ann-tel#";
	public static final String ANNOTATION_SCOPE = ANNOTATION_SCOPE_NS+"scope";		
	
	public static final String REPLY_TYPE = "http://www.w3.org/2001/12/replyType#";	
	public static final String REPLY_TYPE_AGREE = REPLY_TYPE+"Agree";		
	
	public static final String THREAD_NS = "http://www.w3.org/2001/03/thread#";
	public static final String THREAD_REPLY = THREAD_NS+"Reply";
	public static final String THREAD_IN_REPLY_TO = THREAD_NS+"inReplyTo";
	public static final String THREAD_ROOT = THREAD_NS+"root";

	public static final String HTTP_NS = "http://www.w3.org/1999/xx/http#";
	public static final String HTTP_BODY = HTTP_NS+"Body";
	public static final String HTTP_CONTENT_TYPE = HTTP_NS+"ContentType";
	public static final String HTTP_CONTENT_LENGTH = HTTP_NS+"ContentLength";
	
	public static final String DUBLIN_CORE_NS = "http://purl.org/dc/elements/1.1/";
	public static final String DUBLIN_CORE_DATE = DUBLIN_CORE_NS+"date";	
	public static final String DUBLIN_CORE_CREATOR = DUBLIN_CORE_NS+"creator";
	public static final String DUBLIN_CORE_TITLE = DUBLIN_CORE_NS+"title";
	public static final String DUBLIN_CORE_FORMAT = DUBLIN_CORE_NS+"format";
	
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";	
	public static final String RDFS_SEEALSO = RDFS_NS + "seeAlso";	

	public static final String XLINK = "xlink";
	public static final String XLINK_HREF = "href";	
	public static final Namespace XLINK_NS = Namespace.getNamespace(XLINK,"http://www.w3.org/1999/xlink");
	
	
	public static String toRdfXml(final Annotation annotation) throws RepositoryException, 
		RDFHandlerException, IOException {
		return toRdfXml(new ArrayList<Annotation>() {{ add(annotation); }});	
	}
	
	/**
	 * create an rdf/xml representation of the given annotations
	 * see example: http://www.w3.org/2001/Annotea/User/Protocol.html .
	 * 
	 * @param annotation
	 * @return string representation
	 */
	public static String toRdfXml(Collection<Annotation> annotations) throws RepositoryException, 
		RDFHandlerException, IOException {		
		
		Repository repository = new SailRepository(new MemoryStore());
		repository.initialize();		
		RepositoryConnection con = repository.getConnection();		
		ValueFactory valueFactory = repository.getValueFactory();
			
		for(Annotation annotation : annotations)
			toRdf(con,valueFactory,annotation);
	    
	    StringWriter sw = new StringWriter();
	    RDFXMLWriter rdfXmlWriter=new RDFXMLPrettyWriter(sw);
	    rdfXmlWriter.handleNamespace("a", ANNOTATION_NS);
	    rdfXmlWriter.handleNamespace("ann", ANNOTEA_ANNOTATION_NS);
	    rdfXmlWriter.handleNamespace("scope", ANNOTATION_SCOPE_NS);
	    rdfXmlWriter.handleNamespace("image", ANNOTATION_IMAGE_NS);
	    rdfXmlWriter.handleNamespace("rel", ANNOTATION_REL_NS);
	    
	    rdfXmlWriter.handleNamespace("dc", DUBLIN_CORE_NS);
	    rdfXmlWriter.handleNamespace("h", HTTP_NS);
	    rdfXmlWriter.handleNamespace("tr", THREAD_NS);
	    
	    con.export(rdfXmlWriter);
	    return sw.toString();
	}
	
	private static void toRdf(RepositoryConnection con, ValueFactory vFactory, Annotation 
			annotation) throws RepositoryException, RDFHandlerException, IOException {		
		Resource annotationNode = (annotation.getId()==null)?
				vFactory.createBNode():
				vFactory.createURI(createAnnotationUrl(annotation.getId()));
				
		URI annotationType = vFactory.createURI(ANNOTATION);
		URI annotationCommentType = vFactory.createURI(ANNOTATION_TYPE_COMMENT);		
		URI replyType = vFactory.createURI(THREAD_REPLY);
		URI agreeType = vFactory.createURI(REPLY_TYPE_AGREE);		
		URI annotatesPredicate = vFactory.createURI(ANNOTATION_ANNOTATES);
		URI linkedToPredicate = vFactory.createURI(ANNOTATION_LINKED_TO);
		URI annotationCreatedPredicate = vFactory.createURI(ANNOTATION_CREATED);
		URI annotationModifiedPredicate = vFactory.createURI(ANNOTATION_MODIFIED);		
		URI annotationBodyPredicate = vFactory.createURI(ANNOTEA_ANNOTATION_BODY);
		URI annotationLabelPredicate = vFactory.createURI(ANNOTATION_LABEL);		
		URI videoFragmentPredicate = vFactory.createURI(ANNOTATION_VIDEO_FRAGMENT);
		URI imageFragmentPredicate = vFactory.createURI(ANNOTATION_IMAGE_FRAGMENT);
		URI annotationAuthorPredicate = vFactory.createURI(ANNOTATION_AUTHOR);
		URI annotationScopePredicate = vFactory.createURI(ANNOTATION_SCOPE);
		
		URI rdfsSeeAlso = vFactory.createURI(RDFS_SEEALSO); 
		
		URI dcTitlePredicate = vFactory.createURI(DUBLIN_CORE_TITLE);
		URI dcCreatorPredicate = vFactory.createURI(DUBLIN_CORE_CREATOR);
		URI dcDateTypePredicate = vFactory.createURI(DUBLIN_CORE_DATE);
		URI dcFormatPredicate = vFactory.createURI(DUBLIN_CORE_FORMAT);		
		URI threadRootPredicate = vFactory.createURI(THREAD_ROOT);
		URI threadInReplyToPredicate = vFactory.createURI(THREAD_IN_REPLY_TO);
		
		Resource annotationBodyNode = vFactory.createBNode();
		URI htmlContentTypePredicate = vFactory.createURI(HTTP_CONTENT_TYPE);
		URI htmlContentLengthPredicate = vFactory.createURI(HTTP_CONTENT_LENGTH);
		URI htmlBodyPredicate = vFactory.createURI(HTTP_BODY);
	
		// annotation resource
		con.add(annotationNode,annotatesPredicate,vFactory.createURI(annotation.getObjectUrl()));
		
		// seeAlso
		if(annotation.getLinks()!=null) {
			for(String link : annotation.getLinks()) 
				con.add(annotationNode, rdfsSeeAlso, vFactory.createURI(link));					
		}
		
		// linked to
		if(annotation.getExternalObjectId()!=null) {
			con.add(annotationNode,linkedToPredicate,
					vFactory.createLiteral(annotation.getExternalObjectId()));
		}			
		// reply?
		if(annotation.getParentId()!=null) {
			con.add(annotationNode,RDF.TYPE,replyType);
			con.add(annotationNode,RDF.TYPE,agreeType);
			con.add(annotationNode,threadInReplyToPredicate,
					vFactory.createURI(createAnnotationUrl(annotation.getParentId())));			
		} else {
			con.add(annotationNode,RDF.TYPE,annotationType);
			con.add(annotationNode,RDF.TYPE,annotationCommentType);					
		}
		
		// root
		if(annotation.getRootId()!=null)
			con.add(annotationNode,threadRootPredicate,vFactory.createURI(
					createAnnotationUrl(annotation.getRootId())));		
		
		// title
		if(annotation.getTitle()!=null)			
			con.add(annotationNode,dcTitlePredicate, vFactory.createLiteral(annotation.getTitle()));
		
		// creator and author
		if(annotation.getCreatedBy()!=null) {
			con.add(annotationNode,annotationAuthorPredicate,
					vFactory.createLiteral(annotation.getCreatedBy()));
			con.add(annotationNode,dcCreatorPredicate,
					vFactory.createLiteral(annotation.getCreatedBy()));
		}
		
		// date
		String modified=W3CDateTimeParser.formatW3CDateTime(annotation.getModified());		
		con.add(annotationNode,annotationModifiedPredicate,vFactory.createLiteral(modified));		
		con.add(annotationNode,dcDateTypePredicate,vFactory.createLiteral(modified));
		String date=W3CDateTimeParser.formatW3CDateTime(annotation.getCreated());
		con.add(annotationNode,annotationCreatedPredicate,vFactory.createLiteral(date));	
		
		// format (mime type)
		if(annotation.getMimeType()!=null)
			con.add(annotationNode,dcFormatPredicate,
					vFactory.createLiteral(annotation.getMimeType()));			
		
		// annotated fragment
		if(annotation.getAnnotatedFragment()!=null) {
			URI fragmentPredicate = imageFragmentPredicate;
			if (annotation.getMimeType().contains("video"))
				fragmentPredicate = videoFragmentPredicate;
			
			con.add(annotationNode, fragmentPredicate, vFactory.createLiteral(annotation
					.getAnnotatedFragment(), RDF.XMLLITERAL));
		}

		// body and label
	    con.add(annotationNode, annotationLabelPredicate, 
	    		vFactory.createLiteral(annotation.getText()));	    
		String annotationBody = annotation.toHtml();
	    con.add(annotationNode, annotationBodyPredicate, annotationBodyNode);	    
	    con.add(annotationBodyNode,htmlContentTypePredicate,vFactory.createLiteral("text/html"));
	    con.add(annotationBodyNode,htmlContentLengthPredicate,
	    		vFactory.createLiteral(new Integer(annotationBody.length()).toString()));	    
	    con.add(annotationBodyNode,htmlBodyPredicate, 
	    		vFactory.createLiteral(annotationBody,RDF.XMLLITERAL));	
	    
	    // scope
	    con.add(annotationNode, annotationScopePredicate, 
	    		vFactory.createLiteral(annotation.getScopeAsString()));
	  
	    // replies
	    if(annotation.getReplies()!=null) {
	    	for(Object reply : annotation.getReplies()) toRdf(con,vFactory,(Annotation)reply);
	    }
	}
	
	
	/**
	 * convert a rdf/xml representation into an <code>Annotation</code>. In case the representation
	 * contains more than one annotation, we will return the first one.
	 * 
	 * @param rdfXml
	 * @return annotation
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	public static Annotation fromRdfXml(String rdfXml) throws RDFParseException, 
		RDFHandlerException, IOException {		 
		
		if(rdfXml==null || rdfXml.isEmpty()) return null;
		
		RdfXmlAnnotationHandler annotationRdfHandler=new RdfXmlAnnotationHandler(); 
		RDFParser parser = new RDFXMLParser();
		parser.setRDFHandler(annotationRdfHandler);
		parser.setVerifyData(true);
		parser.parse(new StringReader(rdfXml), "http://foo/bar");

		return annotationRdfHandler.getAnnotations().get(0);
	}
	
	/**
	 * construct URL for annotation
	 * 
	 * @param annId
	 * @return annotation url 
	 */
	private static String createAnnotationUrl(Long annId) {
		String id = annId.toString();
		if (!id.startsWith(Config.getInstance().getAnnotationBaseUrl())) {
			id = Config.getInstance().getAnnotationBaseUrl() + id;
		}

		return id;
	}	
}
