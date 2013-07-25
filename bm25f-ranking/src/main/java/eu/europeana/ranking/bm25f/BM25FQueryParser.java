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



import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BM25FBooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.DisMaxQParser;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europeana.ranking.bm25f.enums.SolrFields;




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
	private static final Logger logger = LoggerFactory.getLogger(BM25FQueryParser.class);

	public static final String DEFAULT_FIELD = SolrFields.getInstance().getDefaultField();
	String qstr;
	String queryField;

	/**
	 * @param qstr
	 * @param localParams
	 * @param params
	 * @param req
	 */
	public BM25FQueryParser(String qstr, SolrParams localParams, SolrParams params,
			SolrQueryRequest req) {
		super(qstr, localParams, params,req);
		this.qstr = qstr;
		
		String query = req.getParams().get("q");
		
		
		queryField = StringUtils.split(query,":").length>1?StringUtils.split(query,":")[0]:DEFAULT_FIELD;
	}

	
	@Override
	public Query parse() throws ParseException {
			SolrQueryParser parser = new SolrQueryParser(this, queryField);
			Query q = parser.parse(qstr);
		//		if (q instanceof DisjunctionMaxQuery ){
//			return new BM25FQuery((DisjunctionMaxQuery)q);
//		}
		if (q instanceof BooleanQuery){
			BooleanQuery bq = (BooleanQuery)q;
			
			BM25FBooleanQuery bm25fQuery = new BM25FBooleanQuery();
			for (BooleanClause clause : bq){
				bm25fQuery.add(clause);
			}
			return bm25fQuery;
			
		}
		if (q instanceof TermQuery){
			TermQuery tq = (TermQuery)q;
			BM25FBooleanQuery bm25fQuery = new BM25FBooleanQuery();
			bm25fQuery.add(new BooleanClause(tq, Occur.MUST));
			return bm25fQuery;
		}
		
		return q;
	}
		
		
		
		
//		SolrParams p = req.getParams();
//		NamedList<Object> params = p.toNamedList();
//		if (params.get("k1") != null){
//			BM25FQuery.k1 = Double.parseDouble((String)params.get("k1"));
//		} else { BM25FQuery.k1 = 1; }
//		if (params.get("b") != null){
//			BM25FQuery.b = Double.parseDouble((String)params.get("b"));
//		} else { BM25FQuery.b = 0; }
//		if (params.get("avglen") != null){
//			BM25FQuery.avgLength = Integer.parseInt((String)params.get("avglen"));
//		}
//		
//		
//		String userQuery = getString();
//		SolrQueryParser sqp = new SolrQueryParser(this,DEFAULT_FIELD);
//		Query q = sqp.parse(userQuery);
//		Set<Term> termsSet = new HashSet<Term>();
//		q.extractTerms(termsSet);
//		TermQuery[] terms = new TermQuery[termsSet.size()];
//		int i = 0;
//		for (Term t : termsSet){
//			terms[i++] = new TermQuery(new Term(DEFAULT_FIELD, t.text()));
//		}
//		
//		logger.debug("query terms = {}", termsSet);
//		Query twitterQuery = new  BM25FQuery(terms);
		
		
//		
//		if (q instanceof BooleanQuery){
//			// Dismax returns a boolean query, so this condiction should be always true
//			logger.debug("Boolean Twitter Query! ");
//			Query twitterQuery = new  TwitterBooleanQuery((BooleanQuery)q);
//			return twitterQuery;
//		} else{
//			logger.warn("the parsed query is not a boolean query ");
//			Set<Term> termsSet = new HashSet<Term>();
//			q.extractTerms(termsSet);
//			BooleanQuery bq = new BooleanQuery();
//			for (Term t : termsSet){
//				
//			}
//			return q;
//		}
		
	

}
