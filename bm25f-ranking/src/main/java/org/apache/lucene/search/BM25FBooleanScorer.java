package org.apache.lucene.search;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.BM25FBooleanQuery.BM25FBooleanWeight;


final class BM25FBooleanScorer extends Scorer {

	private int docID = NO_MORE_DOCS;
	private boolean initialized = false;
	private Scorer[] requiredScorers;
	private Scorer[] optionalScorers;
	private Scorer[] prohibitedScorers;

	int minNrShouldMatch;

	BM25FBooleanScorer(BM25FBooleanWeight weight, boolean disableCoord,
			int minNrShouldMatch, List<Scorer> requiredScorers,
			List<Scorer> optionalScorers, List<Scorer> prohibitedScorers,
			int maxCoord) throws IOException {
		super(weight);
		
		this.requiredScorers = requiredScorers
				.toArray(new Scorer[requiredScorers.size()]);
		this.optionalScorers = optionalScorers
				.toArray(new Scorer[optionalScorers.size()]);
		this.prohibitedScorers = prohibitedScorers
				.toArray(new Scorer[prohibitedScorers.size()]);
		this.minNrShouldMatch = minNrShouldMatch;
		if (this.minNrShouldMatch  == 0) {
			this.minNrShouldMatch = this.requiredScorers.length;
		}
		if (this.minNrShouldMatch  == 0) {
			this.minNrShouldMatch = this.optionalScorers.length;
		}

	}

	private void init() throws IOException {

		for (Scorer s : requiredScorers) {
			s.nextDoc();
		}
		for (Scorer s : optionalScorers) {
			s.nextDoc();
		}
		for (Scorer s : prohibitedScorers) {
			s.nextDoc();
		}

	}

	@Override
	public float score() throws IOException {
		float result = 0f;
		for (Scorer s : requiredScorers) {

			if (s.docID() == docID)
				result += s.score();
		}
		for (Scorer s : optionalScorers) {
			if (s.docID() == docID)
				result += s.score();
		}

		return result;
	}

	@Override
	public float freq() throws IOException {
		float freq = 0;
		for (Scorer s : requiredScorers) {

			if (s.docID() == docID) {
				freq += s.freq();
			}
		}
		for (Scorer s : optionalScorers) {
			if (s.docID() == docID) {
				freq += s.freq();
			}
		}
		return freq;

	}


	@Override
	public int docID() {
		return docID;
	}


	@Override
	public int nextDoc() throws IOException {

		if (!this.initialized) {
			this.initialized = true;
			this.init();

		}



		int min = NO_MORE_DOCS;
		int count = 0;
		boolean prohibited = true;
		while (count < minNrShouldMatch || (prohibited)){
				min = NO_MORE_DOCS;
				prohibited = false;
				int i,j,k = 0;
				count = 0;
				for (i = 0; i < requiredScorers.length ; i++){
					if (requiredScorers[i].docID() == NO_MORE_DOCS) {
						continue;
					}
					if (requiredScorers[i].docID()  == docID) {
						requiredScorers[i].nextDoc();
					}
					if (requiredScorers[i].docID()  == min) {
						count++;
					}
					if (requiredScorers[i].docID()  < min) {
						min = requiredScorers[i].docID();
						count = 1;
					}
				}
				for (j = 0; j < optionalScorers.length ; j++){
					if (optionalScorers[j].docID() == NO_MORE_DOCS) {
						continue;
					}
					if (optionalScorers[j].docID()  == docID) {
						optionalScorers[j].nextDoc();
					}
					if (optionalScorers[j].docID()  == min) {
						count++;
					}
					if (optionalScorers[j].docID()  < min) {
						min = optionalScorers[j].docID();
						count = 1;
					}
				}
				for (k = 0; k < prohibitedScorers.length ; k++){
					if (prohibitedScorers[k].docID() == NO_MORE_DOCS) {
						continue;
					}
					if (prohibitedScorers[k].docID()  == docID) {
						prohibitedScorers[k].nextDoc();
					}
					if (prohibitedScorers[k].docID()  == min) {
						prohibited = true;
						break;
					}
	
				}
				
				
				
				
				
				docID = min; // --> doc = min doc
				if (docID == NO_MORE_DOCS) break;
			
		}
		return docID;
	}


	@Override
	public int advance(int target) throws IOException {
		if (target == NO_MORE_DOCS)
			return NO_MORE_DOCS;
		while ((docID = nextDoc()) < target) {
		}
		return docID;
	}

}
