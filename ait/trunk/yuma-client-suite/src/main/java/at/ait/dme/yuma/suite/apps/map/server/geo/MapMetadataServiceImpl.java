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

package at.ait.dme.yuma.suite.apps.map.server.geo;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import at.ait.dme.yuma.suite.apps.map.shared.geo.MapMetadata;
import at.ait.dme.yuma.suite.apps.map.shared.server.MapMetadataService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class MapMetadataServiceImpl extends RemoteServiceServlet implements MapMetadataService {
	
	private static Logger logger = Logger.getLogger(MapMetadataServiceImpl.class);
	
	private static final String ELEMENT_TITLE = "title";
	private static final String ELEMENT_AUTHOR = "author"; 
	private static final String ELEMENT_DATE = "date";
	private static final String ELEMENT_SOURCE = "source"; 
	private static final String ELEMENT_DESCRIPTION = "description"; 
	private static final String ELEMENT_THUMBNAIL = "thumbnail";
	private static final String ELEMENT_LINK = "link"; 
	
	@Override
	public MapMetadata getMetadata(String url) {
		try {
			GetMethod getMetadata = new GetMethod(url);
			int statusCode = new HttpClient().executeMethod(getMetadata);
			if (statusCode  == HttpStatus.SC_OK) {
				return fromXML(getMetadata.getResponseBodyAsStream());
			}
		} catch (Exception e) {
			logger.info("No metadata found for " + url + " (" + e.getClass() + ")");
		}
		return null;
	}
	
	private MapMetadata fromXML(InputStream xml) {
		try {
			MapMetadata meta = new MapMetadata();
            
			Document document = new SAXBuilder().build(new InputStreamReader(xml));
            Element e = document.getRootElement();
            
        	meta.setTitle(e.getChildText(ELEMENT_TITLE));
        	meta.setAuthor(e.getChildText(ELEMENT_AUTHOR));
        	meta.setDate(e.getChildText(ELEMENT_DATE));
        	
        	meta.setSource(e.getChildText(ELEMENT_SOURCE));
        	meta.setDescription(e.getChildText(ELEMENT_DESCRIPTION));
        	meta.setThumbnail(e.getChildText(ELEMENT_THUMBNAIL));
        	meta.setLink(e.getChildText(ELEMENT_LINK));

            return meta;
		} catch (Exception e) {
			logger.info("Unable to parse map metadata: " + e.getMessage());
		}
		return null;
	}

}
