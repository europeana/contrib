/*
 *  Copyright 2011 Diego Ceccarelli
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
 *
 */
package eu.europeana.ranking.bm25f;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BM25FBooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.parser.QueryParser.Operator;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.apache.solr.search.SyntaxError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europeana.ranking.bm25f.params.BM25FParameters;

/**
 * Parser for a twitter query
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 16/dec/2010
 */
public class BM25FQueryParser extends QParser {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(BM25FQueryParser.class);

	String qstr;

	private final String mainField;
	BM25FParameters bm25fparams;

	// String queryField;

	/**
	 * @param qstr
	 * @param localParams
	 * @param params
	 * @param req
	 */
	public BM25FQueryParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req, BM25FParameters bm25fparams) {
		super(qstr, localParams, params, req);
		this.bm25fparams = bm25fparams;
		this.qstr = qstr;
		logger.info("q: {}", qstr);
		mainField = bm25fparams.getMainField();

		// WAT?
		// queryField =
		// StringUtils.split(query,":").length>1?StringUtils.split(query,":")[0]:DEFAULT_FIELD;
	}

	@Override
	public Query parse() throws SyntaxError{
		BM25FParameters param = manageRuntimeBm25fParams();
		if (param == null)
			param = bm25fparams;
		String[] fields = bm25fparams.getFields();
		SolrQueryParser parser = new SolrQueryParser(this, mainField);
		parser.setDefaultOperator(Operator.AND);

		Query q = parser.parse(qstr);
		// if (q instanceof DisjunctionMaxQuery ){
		// return new BM25FQuery((DisjunctionMaxQuery)q);
		// }

		if (q instanceof BooleanQuery) {
			BooleanQuery bq = (BooleanQuery) q;

			BM25FBooleanQuery bm25fQuery = new BM25FBooleanQuery(param);
			Set<Term> terms = new HashSet<Term>();
			bm25fQuery.extractTerms(terms);
			for (Term t : terms) {
				// if is using external fields
				if (indexOf(t.field(), fields) < 0) {
					return bq;
				}
			}
			for (BooleanClause clause : bq) {
				Occur occur = clause.getOccur();

				if (occur == Occur.SHOULD
						|| !(clause.getQuery() instanceof TermQuery)) {
					return bq;
				}

				bm25fQuery.add(clause);
			}
			return bm25fQuery;

		}
		if (q instanceof TermQuery) {

			TermQuery tq = (TermQuery) q;
			String field = tq.getTerm().field();
			if (indexOf(field, fields) < 0)
				return q;
			BM25FBooleanQuery bm25fQuery = new BM25FBooleanQuery(param);
			bm25fQuery.add(new BooleanClause(tq, Occur.MUST));
			return bm25fQuery;
		}
		return q;
	}

	private int indexOf(String element, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(element))
				return i;
		}
		return -1;
	}

	private BM25FParameters manageRuntimeBm25fParams() {
		SolrParams p = req.getParams();
		float k1 = -1;
		Float[] boosts = null;
		Float[] bParams = null;
		boolean set = false;
		int nFields = bm25fparams.getFields().length;

		Object o = p.get("k1");
		if (o != null) {
			k1 = Float.parseFloat((String) o);
			set = true;
		}
		o = p.get("b");
		if (o != null) {
			String bstr = (String) o;
			boosts = new Float[nFields];
			Scanner scanner = new Scanner(bstr);
			scanner = scanner.useDelimiter(":");
			for (int i = 0; i < nFields; i++) {
				boosts[i] = scanner.nextFloat();
			}
			scanner.close();
			set = true;
		}

		o = p.get("lb");
		if (o != null) {
			String bParamsStr = (String) o;
			bParams = new Float[nFields];
			Scanner scanner = new Scanner(bParamsStr);
			scanner = scanner.useDelimiter(":");
			for (int i = 0; i < nFields; i++) {
				bParams[i] = scanner.nextFloat();
			}
			scanner.close();
			set = true;
		}
		if (!set) {
			return null;
		}
		BM25FParameters params = bm25fparams.clone();
		if (k1 >= 0)
			params.setK1(k1);
		if (boosts != null)
			params.setBoosts(boosts);
		if (bParams != null)
			params.setbParams(bParams);

		return params;

	}
}
