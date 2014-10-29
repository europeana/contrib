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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Nov 27, 2012
 */
public class SimpleCollectionTest {
	private static SimpleCollectionSolrInstance instance;

	@BeforeClass
	public static void runSolrInstance() throws SolrServerException,
			IOException {
		System.setProperty("solr.allow.unsafe.resourceloading", "true");
		instance = new SimpleCollectionSolrInstance();
		instance.setSolrdir(new File(
				"./src/test/resources/solr/simple-collection"));
		instance.deleteByQuery("*:*");

		index(instance);
	}

	// index a small collection of 3 documents, just to test if the scores
	// are computed correctly
	public static void index(SimpleCollectionSolrInstance tester)
			throws SolrServerException, IOException {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("europeana_id", "0");
		doc.addField("title", "leonardo da vinci");
		doc.addField("author", "leonardo da");
		doc.addField("description", "leonardo leonardo leonardo");
		tester.add(doc);
		doc = new SolrInputDocument();
		doc.addField("europeana_id", "1");
		doc.addField("title", "leonardo ");
		doc.addField("author", "leonardo da vinci ");
		doc.addField("description", "leonardo test");
		tester.add(doc);
		doc = new SolrInputDocument();
		doc.addField("europeana_id", "2");
		doc.addField("title", "leonardo leonardo ");
		doc.addField("author", "leonardo da ");
		doc.addField("description", "leonardo leonardo leonardo leonardo vinci");
		tester.add(doc);
		doc = new SolrInputDocument();
		doc.addField("europeana_id", "3");
		doc.addField("title", "picasso");
		doc.addField("author", "pablo picasso");
		doc.addField("description", "picasso is a good guy vinci");
		tester.add(doc);
		tester.commit();
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
		q.set("k1", String.valueOf(k1));
		q.set("b", join(boosts, ":"));
		q.set("lb", join(bParams, ":"));

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
		q.set("debug", "results");
		q.set("defType", "bm25f");
		q.setRows(10);
		QueryResponse qr = instance.query(q);
		Map<String, Object> explainmap = qr.getDebugMap();
		return explainmap;
	}

	@Test
	public void test1Result() {
		try {

			// // check if the status is not corrupted
			SolrDocumentList results = getResults("leonardo");
			assertEquals(3, results.size());
		} catch (SolrServerException e) {
			fail(e.toString());
		}
	}

	@Test
	public void testResults() {
		try {

			SolrDocumentList results = getResults("*:*");
			assertEquals(4, results.size());

			// // check if the status is not corrupted
			results = getResults("leonardo");
			assertEquals(3, results.size());
			results = getResults("vinci");
			assertEquals(4, results.size());
			results = getResults("test");
			assertEquals(1, results.size());
			results = getResults("thistermisnotintheindexmuahahah");
			assertEquals(0, results.size());
			results = getResults("picasso");
			assertEquals(1, results.size());
			results = getResults("vinci");
			assertEquals(4, results.size());
			results = getResults("picasso vinci");
			assertEquals(1, results.size());
			results = getResults("picasso vinci leonardo");
			assertEquals(0, results.size());
			results = getResults("description:leonardo vinci");
			assertEquals(3, results.size());
			results = getResults("vinci -picasso");
			assertEquals(3, results.size());
			results = getResults("leonardo -picasso");
			assertEquals(3, results.size());
			results = getResults("leonardo -leonardo");
			assertEquals(0, results.size());
			results = getResults("picasso -leonardo");
			assertEquals(1, results.size());
		} catch (SolrServerException e) {
			fail(e.toString());
		}

	}

	@Test
	public void testOR() throws SolrServerException {
		SolrDocumentList results = getResults("picasso OR test");
		assertEquals(2, results.size());

	}

	@Test
	public void phraseTest() throws SolrServerException {
		SolrDocumentList results = getResults("author:\"leonardo da\"");
		assertEquals(3, results.size());
		results = getResults("author:\"leonardo da\" AND description:test");
		assertEquals(1, results.size());
		results = getResults("author:\"leonardo da\" AND description:test");
		assertEquals(1, results.size());

	}

	public static SimpleCollectionSolrInstance getInstance() {
		return instance;
	}

	public static void setInstance(SimpleCollectionSolrInstance instance) {
		SimpleCollectionTest.instance = instance;
	}

	@Test
	public void testFieldQuery() throws SolrServerException {
		SolrDocumentList results = getResults("description:leonardo description:vinci");
		assertEquals(1, results.size());

	}

	@Test
	public void testFieldQuery2() throws SolrServerException {
		SolrDocumentList results = null; // getResults("vinci");
		results = getResults("description:vinci");
		assertEquals(2, results.size());

	}

	@Test
	public void testQueryParsing() {
		try {
			SolrDocumentList results = getResults("la joconde OR la gioconda OR mona lisa");

		} catch (SolrServerException e) {
			fail(e.toString());
		}

	}

	// <float name="k1">18.0</float>
	//
	// <lst name="fieldsBoost">
	// <float name="text">3</float>
	// <float name="title">39.0</float>
	// <float name="author">8.0</float>
	// <float name="description">10.0</float>
	// </lst>
	// <lst name="fieldsB">
	// <float name="text">0.15</float>
	// <float name="title">0.05</float>
	// <float name="author">0</float>
	// <float name="description">0.75</float>
	// </lst>

	public float picassoAndGoodScore(float k1, float[] boosts, float[] bParams) {

		double idf = idf(1, 4);
		double titleAvgLength = 1.75; // lengths lossy encoded in the index
		double authorAvgLength = 2.25;
		double descriptionAvgLength = 4.0;
		double textAvgLength = 8.0;
		double titleBoost = boosts[3];
		double authorBoost = boosts[0];
		double descriptionBoost = boosts[1];
		double textBoost = boosts[2];
		double titleLengthBoost = bParams[3];
		double authorLengthBoost = bParams[0];
		double descriptionLengthBoost = bParams[1];
		double textLengthBoost = bParams[2];
		double titleWeight = (titleBoost * 1)
				/ ((1 - titleLengthBoost) + titleLengthBoost * 1.0
						/ titleAvgLength);
		double authorWeight = (authorBoost * 1)
				/ ((1 - authorLengthBoost) + authorLengthBoost * 2.56
						/ authorAvgLength);
		double descriptionWeight = (descriptionBoost * 1)
				/ ((1 - descriptionLengthBoost) + descriptionLengthBoost
						* 7.111111 / descriptionAvgLength);
		double textWeight = (textBoost * 3)
				/ ((1 - textLengthBoost) + textLengthBoost * 10.24
						/ textAvgLength);

		System.out.println("authorWeight = " + authorWeight);
		System.out.println("descriptionWeight = " + descriptionWeight);

		System.out.println("textWeight = " + textWeight);

		float expectedPicassoScore = (float) (titleWeight + authorWeight
				+ descriptionWeight + textWeight);

		// good
		descriptionWeight = (descriptionBoost * 1)
				/ ((1 - descriptionLengthBoost) + descriptionLengthBoost
						* 7.111111 / descriptionAvgLength);
		textWeight = (textBoost * 1)
				/ ((1 - textLengthBoost) + textLengthBoost * 10.24
						/ textAvgLength);

		float expectedGoodScore = (float) (descriptionWeight + textWeight);

		double den = k1 + expectedGoodScore;
		if (den > 0)
			expectedGoodScore = (float) (expectedGoodScore / den * idf);

		den = k1 + expectedPicassoScore;
		if (den > 0)
			expectedPicassoScore = (float) (expectedPicassoScore / den * idf);
		return expectedPicassoScore + expectedGoodScore;
	}

	public float picassoScore(float k1, float[] boosts, float[] bParams) {

		double idf = idf(1, 4);
		double titleAvgLength = 1.75; // lengths lossy encoded in the index
		double authorAvgLength = 2.25;
		double descriptionAvgLength = 4.0;
		double textAvgLength = 8.0;
		double titleBoost = boosts[3];
		double authorBoost = boosts[0];
		double descriptionBoost = boosts[1];
		double textBoost = boosts[2];
		double titleLengthBoost = bParams[3];
		double authorLengthBoost = bParams[0];
		double descriptionLengthBoost = bParams[1];
		double textLengthBoost = bParams[2];
		double titleWeight = (titleBoost * 1)
				/ ((1 - titleLengthBoost) + titleLengthBoost * 1.0
						/ titleAvgLength);
		double authorWeight = (authorBoost * 1)
				/ ((1 - authorLengthBoost) + authorLengthBoost * 2.56
						/ authorAvgLength);
		double descriptionWeight = (descriptionBoost * 1)
				/ ((1 - descriptionLengthBoost) + descriptionLengthBoost
						* 7.111111 / descriptionAvgLength);
		double textWeight = (textBoost * 3)
				/ ((1 - textLengthBoost) + textLengthBoost * 10.24
						/ textAvgLength);

		System.out.println("authorWeight = " + authorWeight);
		System.out.println("descriptionWeight = " + descriptionWeight);

		System.out.println("textWeight = " + textWeight);

		float expectedScore = (float) (titleWeight + authorWeight
				+ descriptionWeight + textWeight);
		double den = k1 + expectedScore;
		if (den > 0)
			expectedScore = (float) (expectedScore / den * idf);
		return expectedScore;
	}

	@Test
	public void testScores() throws SolrServerException {
		System.out.println(explain("picasso"));
		SolrDocumentList results = getResults("picasso");

		double score = ((Float) results.get(0).get("score"));
		double idf = idf(1, 4);
		double titleAvgLength = 1.75; // lengths lossy encoded in the index
		double authorAvgLength = 2.25;
		double descriptionAvgLength = 4.0;
		double textAvgLength = 8.0;
		double titleBoost = 39.0;
		double authorBoost = 8.0;
		double descriptionBoost = 10.0;
		double textBoost = 3.0;
		double titleLengthBoost = 0.05;
		double authorLengthBoost = 0;
		double descriptionLengthBoost = 0.75;
		double textLengthBoost = 0.15;
		double titleWeight = (titleBoost * 1)
				/ ((1 - titleLengthBoost) + titleLengthBoost * 1.0
						/ titleAvgLength);
		double authorWeight = (authorBoost * 1)
				/ ((1 - authorLengthBoost) + authorLengthBoost * 2.56
						/ authorAvgLength);
		double descriptionWeight = (descriptionBoost * 1)
				/ ((1 - descriptionLengthBoost) + descriptionLengthBoost
						* 7.111111 / descriptionAvgLength);
		double textWeight = (textBoost * 3)
				/ ((1 - textLengthBoost) + textLengthBoost * 10.24
						/ textAvgLength);

		double k1 = 18;

		double expectedScore = (titleWeight + authorWeight + descriptionWeight + textWeight);
		double den = k1 + expectedScore;
		if (den > 0)
			expectedScore = expectedScore / den * idf;

		System.out.println("authorWeight = " + authorWeight);
		System.out.println("descriptionWeight = " + descriptionWeight);

		System.out.println("textWeight = " + textWeight);
		assertEquals(expectedScore, score, 0.00001);
		System.out.println("score = " + score);
		System.out.println("expected = " + expectedScore);

	}

	public void assertPicassoSameScores(float k1, float[] boosts,
			float[] bparams) throws SolrServerException {

		SolrDocumentList results = getResults("picasso", k1, boosts, bparams);
		double score = ((Float) results.get(0).get("score"));
		float expectedScore = picassoScore(k1, boosts, bparams);
		System.out.println("score = " + score);
		System.out.println("expected = " + expectedScore);

		assertEquals(expectedScore, score, 0.00001);

	}

	public void assertPicassoAndGoodSameScores(float k1, float[] boosts,
			float[] bparams) throws SolrServerException {

		SolrDocumentList results = getResults("picasso good", k1, boosts,
				bparams);
		double score = ((Float) results.get(0).get("score"));
		float expectedScore = picassoAndGoodScore(k1, boosts, bparams);
		System.out.println("score = " + score);
		System.out.println("expected = " + expectedScore);

		assertEquals(expectedScore, score, 0.00001);

	}

	@Test
	public void testParamsSwitch() throws SolrServerException {

		assertPicassoSameScores(18, new float[] { 8.0f, 10.0f, 3.0f, 39.0f },
				new float[] { 0, 0.75f, 0.15f, 0.05f });
		assertPicassoSameScores(18, new float[] { 8.0f, 11.0f, 3.0f, 39.0f },
				new float[] { 0, 0.75f, 0.15f, 0.05f });
		assertPicassoSameScores(18, new float[] { 0.0f, 50.0f, 3.0f, 39.0f },
				new float[] { 0, 0.75f, 0.15f, 4.05f });
		assertPicassoSameScores(1, new float[] { 10.0f, 10.0f, 3.0f, 39.0f },
				new float[] { 1, 0.75f, 0.15f, 0.05f });
		assertPicassoSameScores(18, new float[] { 30.0f, 10.0f, 3.0f, 39.0f },
				new float[] { 0, 0.75f, 0.12f, 0.95f });
		assertPicassoSameScores(0, new float[] { 0f, 0f, 0f, 0f }, new float[] {
				0f, 0f, 0f, 0f });
		assertPicassoSameScores(0, new float[] { 1f, 0f, 0f, 0f }, new float[] {
				0.1f, 0f, 0f, 0f });

	}

	@Test
	public void testParamsSwitch2Terms() throws SolrServerException {

		assertPicassoAndGoodSameScores(18, new float[] { 8.0f, 10.0f, 3.0f,
				39.0f }, new float[] { 0, 0.75f, 0.15f, 0.05f });
		assertPicassoAndGoodSameScores(18, new float[] { 8.0f, 11.0f, 3.0f,
				39.0f }, new float[] { 0, 0.75f, 0.15f, 0.05f });
		assertPicassoAndGoodSameScores(18, new float[] { 0.0f, 50.0f, 3.0f,
				39.0f }, new float[] { 0, 0.75f, 0.15f, 4.05f });
		assertPicassoAndGoodSameScores(1, new float[] { 10.0f, 10.0f, 3.0f,
				39.0f }, new float[] { 1, 0.75f, 0.15f, 0.05f });
		assertPicassoAndGoodSameScores(18, new float[] { 30.0f, 10.0f, 3.0f,
				39.0f }, new float[] { 0, 0.75f, 0.12f, 0.95f });
		assertPicassoAndGoodSameScores(0, new float[] { 0f, 0f, 0f, 0f },
				new float[] { 0f, 0f, 0f, 0f });
		assertPicassoAndGoodSameScores(0, new float[] { 1f, 0f, 0f, 0f },
				new float[] { 0.1f, 0f, 0f, 0f });

	}

	private float idf(long docFreq, long numDocs) {
		return (float) Math.log(1 + (numDocs - docFreq + 0.5D)
				/ (docFreq + 0.5D));
	}

}
