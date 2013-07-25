/*
 *  Copyright 2010 Diego Ceccarelli
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


import java.util.Map;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

import eu.europeana.ranking.bm25f.enums.SolrFields;
import eu.europeana.ranking.bm25f.params.BM25FParameters;



public class BM25FParserPlugin extends QParserPlugin {

	public static String NAME = "bm25f";


	@Override
	public QParser createParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		
		req.getSchema();
		SolrFields solrFields =SolrFields.getInstance();
		if(solrFields.getDefaultField()==null){
			solrFields.setDefaultField(req.getSchema().getDefaultSearchFieldName());
		}
		if(solrFields.getBoosts()==null){
			solrFields.createSimilarityFields(params.toNamedList());
		}
		
		return new BM25FQueryParser(qstr, localParams,params,req);
	}

	@SuppressWarnings("unchecked")
	public void init(NamedList args) {
//		BM25FParameters bmParams = new BM25FParameters();
//		if (args != null) {
//			float k1 = (Float) args.get("k1");
//			String mainField = (String) args.get("mainField");
//
//			Map<String, String> averageLengthFields = SolrParams
//					.toMap((NamedList) args.get("averageLengthFields"));
//			Map<String, String> fieldsBoost = SolrParams.toMap((NamedList) args
//					.get("fieldsBoost"));
//			Map<String, String> fieldsB = SolrParams.toMap((NamedList) args
//					.get("fieldsB"));
//			Object[] tmp = averageLengthFields.keySet().toArray();
//			String[] fields = new String[tmp.length];
//			for (int i = 0; i < tmp.length; i++)
//				fields[i] = (String) tmp[i];
//
//			Float[] boosts = new Float[fields.length];
//			Float[] bParams = new Float[fields.length];
//			for (int i = 0; i < fields.length; i++) {
//				boosts[i] = Float.parseFloat(fieldsBoost.get(fields[i]));
//				bParams[i] = Float.parseFloat(fieldsB.get(fields[i]));
//				float avg = Float
//						.parseFloat(averageLengthFields.get(fields[i]));
//			//	bmParams.setAverageLength(fields[i], avg);
//			}
//			bmParams.setK1(k1);
//			bmParams.setBoosts(boosts);
//			bmParams.setFields(fields);
//			bmParams.setbParams(bParams);
	
	//	 }
	}

}
