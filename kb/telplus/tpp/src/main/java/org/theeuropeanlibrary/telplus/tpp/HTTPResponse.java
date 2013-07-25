package org.theeuropeanlibrary.telplus.tpp;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class HTTPResponse {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static String ENCODING = "Content-Encoding";
	private static String TYPE = "Content-Type";

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private byte[] body;
	private int status;
	private Map<String, List<String>> headerFieldMap;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public HTTPResponse(byte[] body, int status, 
								Map<String, List<String>> responseHeader) {
		this.body = body;
		this.status = status;
		
		this.headerFieldMap = new HashMap<String, List<String>>();
		
		// we need to copy the responseHeader map, because it's immutable
		for (String field : responseHeader.keySet()) {
			// field name null may occur...
			if (field != null)
				headerFieldMap.put(field, responseHeader.get(field));
		}
		
		if (!headerFieldMap.containsKey(ENCODING)) {
			List<String> encoding = new ArrayList<String>();
			encoding.add("UTF-8");
			headerFieldMap.put(ENCODING, encoding);
		}
		if (!headerFieldMap.containsKey(TYPE)) {
			List<String> type = new ArrayList<String>();
			type.add("text/plain");
			headerFieldMap.put(TYPE, type);
		}
		
		
	}
	
	public HTTPResponse() {
		this("".getBytes(), HttpURLConnection.HTTP_OK, 
									new HashMap<String, List<String>>());
	}
	
	public HTTPResponse(byte[] body, String encoding, String contentType, 
															int status) {
		this(body, status, new HashMap<String, List<String>>());
	}
	
	public HTTPResponse(byte[] body, int status) {
		this(body, "UTF-8", "text/plain", status);
	}
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public byte[] getBody() {
		return body;
	}
	
	public String getEncoding() {
		return this.headerFieldMap.get(ENCODING).get(0);
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getContentType() {
		return this.headerFieldMap.get(TYPE).get(0);
	}
	
	public Map<String, List<String>> getHeaderFieldMap() {
		return this.headerFieldMap;
	}
	
	public void addHeaderField(String name, String value) {
		List<String> values = this.headerFieldMap.get(name);
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(value);
		this.headerFieldMap.put(name, values);
	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
