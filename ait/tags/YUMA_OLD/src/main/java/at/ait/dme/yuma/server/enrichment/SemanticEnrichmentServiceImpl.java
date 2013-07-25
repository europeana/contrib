/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.server.enrichment;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.annotation.SemanticTagGroup;
import at.ait.dme.yuma.client.server.SemanticEnrichmentService;
import at.ait.dme.yuma.client.server.exception.SemanticEnrichmentServiceException;
import at.ait.dme.yuma.server.util.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the {@link SemanticEnrichmentService}
 * 
 * @author Manuel Gay
 * @author Rainer Simon
 */
public class SemanticEnrichmentServiceImpl extends RemoteServiceServlet 
		implements SemanticEnrichmentService {

    private static final long serialVersionUID = 828296400911475297L;
    
    private static Logger logger = Logger.getLogger(SemanticEnrichmentServiceImpl.class);

    /**
     * UniVie link discovery service base URL property name
     */
    private static final String ENRICHMENT_SERVICE_URL_PROPERTY = "enrichmentServiceUrl";
    
    /**
     * UniVie link discovery service base URL
     */
    private static String univieLinkDiscoveryServiceBaseURL;
    
    /**
     * Tags used in UniVie link discovery service response
     */
    private static final String LINK_ABSTRACT = "linkAbstract";
    private static final String LINK_LABEL = "linkLabel";
    private static final String LINK = "link";
    private static final String REFERENCE = "reference";
    private static final String ENTITY_TYPE = "entityType";
    private static final String ENTITY_NAME = "entityName";

    /**
     * OpenCalais endpoint base URL
     */
	private static final String OPENCALAIS_API_URL = "http://api.opencalais.com/enlighten/rest";
	
	/**
	 * OpenCalais API key property name
	 */
	private static final String OPENCALAIS_API_KEY_PROPERTY = "openCalaisLicenceID";
	
	/**
	 * OpenCalais licence ID 
	 */
	private static String openCalaisLicenceID;
	
	/**
	 * DBpediaLookup endpoint base URL
	 */
	private static final String DBPEDIA_LOOKUP_URL = "http://lookup.dbpedia.org/";
	
	/**
	 * Tags used in DBpedia lookup response
	 */
	private static final String DBPEDIA_RESULT = "Result";
	private static final String DBPEDIA_LABEL = "Label";
	private static final String DBPEDIA_DESCRIPTION = "Description";
	private static final String DBPEDIA_URI = "URI";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        Config config = new Config(servletConfig, 
        		getClass().getResourceAsStream("enrichment-service.properties"));
        univieLinkDiscoveryServiceBaseURL = 
        	config.getStringProperty(ENRICHMENT_SERVICE_URL_PROPERTY);
        openCalaisLicenceID = config.getStringProperty(OPENCALAIS_API_KEY_PROPERTY);
    }
    
	@Override
	public Collection<SemanticTagGroup> getTagSuggestions(String text, String service) 
			throws SemanticEnrichmentServiceException {
		
		if (service.equals(UNIVIE_LINK_DISCOVERY_SERVICE)) {
			return getTagSuggestionsUniVie(text);
		} else if (service.equals(OPENCALAIS_DBPEDIA_LOOKUP)) {
			return getTagSuggestionsOpenCalaisDBpedia(text);
		}
		throw new SemanticEnrichmentServiceException("unsupported semantic enrichment service: " + service);
	}
	
	private Collection<SemanticTagGroup> getTagSuggestionsUniVie(String text) 
			throws SemanticEnrichmentServiceException {
		
		Collection<SemanticTagGroup> resources = null;
        try {
            // call the enrichment service
            ClientResponse<String> response = getUniVieLinkDiscoveryEndpoint().findEntities(text);

            // check the response
            if (response.getStatus() != HttpResponseCodes.SC_OK)
                throw new SemanticEnrichmentServiceException(response.getStatus());

            // parse the result
            resources = parseUniVieResponse(response.getEntity());

        } catch (SemanticEnrichmentServiceException sese) {
            logger.error(sese.getMessage(), sese);
            throw sese;
        } catch (Exception e) {
            logger.error(e);
            throw new SemanticEnrichmentServiceException(e.getMessage());
        }

        return resources;
	}
	
	private Collection<SemanticTagGroup> getTagSuggestionsOpenCalaisDBpedia(String text) 
			throws SemanticEnrichmentServiceException {
		
		// First, resolve named entities using OpenCalais
		ClientResponse<String> response = getOpenCalaisEndpoint().findEntities(openCalaisLicenceID, text);

        if (response.getStatus() != HttpResponseCodes.SC_OK)
        	throw new SemanticEnrichmentServiceException(response.getStatus());
		
		// Then, try obtaining links for each entity via DBpediaLookup
		DBpediaLookupEndpoint dbpedia = getDBpediaLookupEndpoint();
		ArrayList<SemanticTagGroup> tagSuggestions = new ArrayList<SemanticTagGroup>();
		try {
			for (String namedEntity : parseOpenCalaisResponse(response.getEntity())) {
				response = dbpedia.keyWordSearch(namedEntity, "any", "4");
				
				if (response.getStatus() != HttpResponseCodes.SC_OK)
					throw new SemanticEnrichmentServiceException(response.getStatus());
				
				SemanticTagGroup ambiguousTags = new SemanticTagGroup(); 
				ambiguousTags.setTitle(namedEntity);
				ambiguousTags.setTags(parseDBpediaLookupResponse(namedEntity, response.getEntity()));
				tagSuggestions.add(ambiguousTags);
			}
		} catch (Exception e) {
			throw new SemanticEnrichmentServiceException(e.getMessage());
		}
		return tagSuggestions;
	}

    private Collection<SemanticTagGroup> parseUniVieResponse(String text) throws Exception {

        Collection<SemanticTagGroup> resources = new ArrayList<SemanticTagGroup>();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(text.getBytes("UTF-8")));

        NodeList childNodes = doc.getChildNodes();
        for(int a = 0; a < childNodes.getLength(); a++) {
            if(childNodes.item(a).getNodeName().equals("entities")) {
                NodeList entities = childNodes.item(a).getChildNodes();
                for (int i = 0; i < entities.getLength(); i++) {
                    Node entity = entities.item(i);
                    NodeList entityContent = entity.getChildNodes();
                    SemanticTagGroup e = new SemanticTagGroup();
                    resources.add(e);

                    for (int j = 0; j < entityContent.getLength(); j++) {
                        Node n = entityContent.item(j);
                        String nName = n.getNodeName();
                        if (nName.equals(ENTITY_NAME)) {
                            e.setTitle(n.getTextContent());
                        } else if (nName.equals(ENTITY_TYPE)) {
                            e.setType(n.getTextContent());
                        } else if (nName.equals(REFERENCE)) {
                            NodeList referenceContent = n.getChildNodes();
                            String url = "", label = "", description = "";
                            for (int k = 0; k < referenceContent.getLength(); k++) {
                                Node r = referenceContent.item(k);

                                String nodeName = r.getNodeName();
                                if (nodeName.equals(LINK)) {
                                    url = r.getTextContent();
                                } else if (nodeName.equals(LINK_LABEL)) {
                                    label = r.getTextContent();
                                } else if (nodeName.equals(LINK_ABSTRACT)) {
                                    description = r.getTextContent();
                                }
                            }
                            e.addTag(new SemanticTag(e.getTitle(), e.getType(), label, description, url));
                        }
                    }
                }
            }
        }
        return resources;
    }
    
	private List<String> parseOpenCalaisResponse(String xml) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        
        ArrayList<String> namedEntities = new ArrayList<String>();
        NodeList nodes = doc.getElementsByTagName("c:name");
        String name; 
        for (int i=0; i<nodes.getLength(); i++) {
        	name = nodes.item(i).getTextContent();
        	if (!name.contains(",") && !namedEntities.contains(name)) namedEntities.add(name);
        }
		return namedEntities;
	}

	private Collection<SemanticTag> parseDBpediaLookupResponse(String term, String xml) 
			throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        ArrayList<SemanticTag> tags = new ArrayList<SemanticTag>();
        NodeList results = doc.getElementsByTagName(DBPEDIA_RESULT);
        for (int i=0; i<results.getLength(); i++) {
        	NodeList children = results.item(i).getChildNodes();
        	String title = null;
    		String description = null;
    		String uri = null;
        	for (int j=0; j<children.getLength(); j++) {
        		if (children.item(j).getNodeName().equals(DBPEDIA_LABEL)) {
        			title = children.item(j).getTextContent();
        		} else if (children.item(j).getNodeName().equals(DBPEDIA_DESCRIPTION)) {
        			description = children.item(j).getTextContent();
        		} else if (children.item(j).getNodeName().equals(DBPEDIA_URI)) {
        			uri =  children.item(j).getTextContent();
        		}
        	}
        	
    		if (description != null && uri != null && title != null && title.equalsIgnoreCase(term))
    			tags.add(new SemanticTag(title, "", "en", description, uri));
        }
        
        return tags;
	}
	
    private UniVieLinkDiscoveryEndpoint getUniVieLinkDiscoveryEndpoint() {
        HttpClient client = new HttpClient();
        return ProxyFactory.create(UniVieLinkDiscoveryEndpoint.class, univieLinkDiscoveryServiceBaseURL,
        		new ApacheHttpClientExecutor(client));

    }
    
    private OpenCalaisEndpoint getOpenCalaisEndpoint() {
    	HttpClient client = new HttpClient();
        return ProxyFactory.create(OpenCalaisEndpoint.class, OPENCALAIS_API_URL, 
        		new ApacheHttpClientExecutor(client)); 
    }
    
    private DBpediaLookupEndpoint getDBpediaLookupEndpoint() {
        HttpClient client = new HttpClient();
        return ProxyFactory.create(DBpediaLookupEndpoint.class, DBPEDIA_LOOKUP_URL,
        		new ApacheHttpClientExecutor(client));
    }
}
