package org.theeuropeanlibrary.telplus.tpp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * 
 * For now just a helper class that retrieves an HTTPResponse instance 
 * corresponding to an URL.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 7 apr 2008
 */
public class HTTPConnectionManager {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static final String XFF = "X-Forwarded-For";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String HEAD = "HEAD";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String TEXT_PLAIN = "text/plain";
	
	public static final int TIMEOUT = 180;

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private static int BUFFER_SIZE = 2048;
	
	private static Logger logger = Logger.getLogger(HTTPConnectionManager.class);
	
	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public HTTPConnectionManager() {
		
	}
	
	
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public HTTPResponse getResponse(String url, String method, 
									Map<String, List<String>> requestHeader,
									String postData) 
															throws IOException,
																SocketTimeoutException {
		HTTPResponse response;
		HttpURLConnection connection;
		byte[] body;
		Map<String, List<String>> headerFields = new HashMap<String, List<String>>();
		
		connection = (HttpURLConnection)new URL(url).openConnection();
		connection.setReadTimeout(TIMEOUT * 1000);
		connection.setRequestMethod(method);
		
		if (requestHeader != null) {
			if (requestHeader.containsKey(XFF)) {
				String xffHeader = "";
				for (String value : requestHeader.get(XFF)) {
					xffHeader += value;
				}
				connection.setRequestProperty(XFF, xffHeader);
			}
			
			// set request header fields
			for (String field : requestHeader.keySet()) {
				List<String> values = requestHeader.get(field);
				connection.setRequestProperty(field, values.get(0));
				if (values.size() > 1) {
					for (int i = 1; i < values.size(); i++) {
						connection.addRequestProperty(field, values.get(i));
					}
				}
			}
		}
		
		// send POST data
		if (method.equalsIgnoreCase(POST)) {
			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			os.write(postData.getBytes());
			os.flush();
			os.close();
		}
		
		int responseCode = connection.getResponseCode();
		
		// we need a modifiable copy of the reponse header
		Map<String, List<String>> tmp = connection.getHeaderFields();
		for (String key : tmp.keySet()) {
			headerFields.put(key, tmp.get(key));
		}
		
		if (responseCode == HttpURLConnection.HTTP_OK) {
			if (method.equals(HEAD)) {
				body = "".getBytes();
			} else {
				InputStream inputStream = connection.getInputStream();
				
				byte[] buffer = new byte[BUFFER_SIZE];
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(buffer.length);
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				body = outputStream.toByteArray();
			}
		} else {

			String msg = connection.getResponseMessage();
			if (msg != null) {
				body = msg.getBytes(); 
			} else {
				body = "".getBytes();
			}
			// make sure we have sane values here
			List<String> contentTypeValue = new ArrayList<String>();
			contentTypeValue.add(TEXT_PLAIN);
			headerFields.put(CONTENT_TYPE, contentTypeValue);
			List<String> contentLengthValue = new ArrayList<String>();
			contentLengthValue.add(Integer.toString(body.length));
			headerFields.put(CONTENT_LENGTH, contentLengthValue);

		}
		
		response = new HTTPResponse(body, responseCode, headerFields);

		return response;
	}
	
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
