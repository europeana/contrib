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
package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.search.BM25FBooleanTermQuery.BM25FTermWeight;
import org.apache.lucene.search.similarities.Similarity.ExactSimScorer;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Nov 25, 2012
 */
public class BM25FTermScorer extends Scorer {
	ExactSimScorer[] scorers;
	DocsEnum[] docsEnums;
	int docId = 0;
	float k1;
	float idf;

	private boolean initializated = false;

	/**
	 * @param bm25fTermWeight
	 * @param docs
	 * @param scorers
	 * @param docFreq
	 */
	public BM25FTermScorer(BM25FTermWeight bm25fTermWeight,
			ExactSimScorer[] scorers, DocsEnum[] docs) {
		super(bm25fTermWeight);
		this.scorers = scorers;
		this.docsEnums = docs;
		idf = bm25fTermWeight.idf;
		k1 = bm25fTermWeight.k1;

	}

	public int getFieldFreq(int field) throws IOException {
		if (docsEnums[field] == null)
			return 0;
		return docsEnums[field].freq();
	}

	@Override
	public float score() throws IOException {
		float acum = 0;

		for (int i = 0; i < scorers.length; i++) {
			if (docsEnums[i] == null || scorers[i] == null)
				continue;
			if (docsEnums[i].docID() == docId) {
				acum += scorers[i].score(docId, docsEnums[i].freq());
			}
		}

		float den = acum + k1;
		if (den == 0) {
			return 0;
		}
		float score = idf * acum / den;

		return score;
	}

	@Override
	public float freq() throws IOException {
		float freq = 0;
		for (int i = 0; i < scorers.length; i++) {
			if (docsEnums[i] == null || scorers[i] == null)
				continue;
			if (docsEnums[i].docID() == docId) {
				freq += docsEnums[i].freq();
			}
		}
		return freq;
	}

	@Override
	public int docID() {
		return docId;
	}

	@Override
	public int nextDoc() throws IOException {

		if (!initializated) {
			this.initializated = true;
			if (this.init()) {

				return this.docId;
			} else {

				return NO_MORE_DOCS;
			}

		}

		int min = NO_MORE_DOCS;
		for (int i = 0; i < this.docsEnums.length; i++) {
			if (docsEnums[i] == null || scorers[i] == null)
				continue;

			if (this.docsEnums[i].docID() == docId) {
				this.docsEnums[i].nextDoc();

			}
			min = Math.min(min, this.docsEnums[i].docID());

		}
		docId = min;

		return docId;

	}

	@Override
	public int advance(int target) throws IOException {
		docId = 0;
		while ((docId = nextDoc()) < target) {
		}
		return docId;
	}

	private boolean init() throws IOException {
		boolean result = false;
		int min = NO_MORE_DOCS;

		for (int i = 0; i < docsEnums.length; i++) {
			if (docsEnums[i] == null || scorers[i] == null)
				continue;
			if (this.docsEnums[i].nextDoc() < NO_MORE_DOCS) {
				result = true;
				min = Math.min(min, this.docsEnums[i].docID());
			}
		}
		docId = min;
		return result;
	}

}
