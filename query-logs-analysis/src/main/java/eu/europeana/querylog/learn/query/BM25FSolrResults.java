/**
 *  Copyright 2014 Diego Ceccarelli
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
package eu.europeana.querylog.learn.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * Implements a Solr Server that answers a query using the BM25F ranking
 * function.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 11, 2014
 */
public class BM25FSolrResults implements SolrResultsRetriever {

	private static final String PREFIX = "http://europeana.eu/portal/record";
	private static HttpSolrServer server;

	public BM25FSolrResults() {
		if (server == null)
			server = new HttpSolrServer(
					"http://node1.novello.isti.cnr.it:8080/bm25f/");
	}

	public List<String> results(String q, int n) {
		SolrQuery query = new SolrQuery(q);
		query.setRows(n);
		query.setFields("europeana_id");
		return retrieveTopDocuments(query);
	}

	@Override
	public List<String> results(SolrQuery query, int n) {
		query.setRows(n);
		query.setFields("europeana_id");
		return retrieveTopDocuments(query);
	}

	public List<String> retrieveTopDocuments(SolrQuery solrQuery) {
		List<String> results = new ArrayList<String>();
		QueryResponse qr = null;
		try {
			qr = server.query(solrQuery);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SolrDocumentList res = qr.getResults();
		for (SolrDocument solrDoc : res) {
			String doc = (String) solrDoc.getFieldValue("europeana_id");
			results.add(String.format("%s%s.html", PREFIX, doc));
		}

		return results;
	}

	public static void main(String[] args) {
		BM25FSolrResults producer = new BM25FSolrResults();

		for (String q : producer.results("picasso", 10)) {
			System.out.println(q);
		}
	}

	@Override
	public String getName() {
		return "bm25f";
	}
}
