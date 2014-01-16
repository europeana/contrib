/**
 *  Copyright 2012 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.europeana.solr;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.BeforeClass;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Nov 27, 2012
 */
public class Collection2Test {
	private static SimpleCollectionSolrInstance instance;

	@BeforeClass
	public static void runSolrInstance() throws SolrServerException,
			IOException {
		instance = new SimpleCollectionSolrInstance();
		instance.setSolrdir(new File("./src/test/resources/solr/collection2"));

	}

	private static SolrDocumentList getResults(String query)
			throws SolrServerException {
		System.out.println("query performed: " + query);
		SolrQuery q = new SolrQuery(query);
		q.set("debugQuery", "on");
		q.set("defType", "bm25f");
		q.set("fl", "*,score");
		q.setRows(10);
		QueryResponse qr = instance.query(q);
		// Map<String, String> explainmap = qr.getExplainMap();
		return qr.getResults();
	}

	static String join(float[] s, String delimiter) {
		StringBuilder builder = new StringBuilder();

		for (float f : s) {
			builder.append(f);
			builder.append(delimiter);
		}
		builder.setLength(builder.length() - 1);
		return builder.toString();
	}

	private static SolrDocumentList getResults(String query, float k1,
			float[] boosts, float[] bParams) throws SolrServerException {
		System.out.println("query performed: " + query);
		SolrQuery q = new SolrQuery(query);
		q.set("debugQuery", "on");
		q.set("defType", "bm25f");
		q.set("fl", "*,score");
		// q.set("k1", String.valueOf(k1));
		// q.set("b", join(boosts, ":"));
		// q.set("lb", join(bParams, ":"));

		q.setRows(10);
		QueryResponse qr = instance.query(q);
		// Map<String, String> explainmap = qr.getExplainMap();
		return qr.getResults();
	}

	private static Map<String, Object> explain(String query)
			throws SolrServerException {
		System.out.println("query performed " + query);
		SolrQuery q = new SolrQuery(query);
		q.set("debugQuery", "on");
		// q.set("debug", "results");
		q.set("defType", "bm25f");
		q.setRows(10);
		QueryResponse qr = instance.query(q);
		Map<String, Object> explainmap = qr.getDebugMap();
		return explainmap;
	}

}