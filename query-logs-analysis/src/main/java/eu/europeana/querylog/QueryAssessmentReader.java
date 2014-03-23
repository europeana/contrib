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
package eu.europeana.querylog;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.io.reader.TsvRecordParser;
import it.cnr.isti.hpc.io.reader.TsvTuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 1, 2014
 */
public class QueryAssessmentReader implements Iterator<QueryAssessment> {

	private static final Logger logger = LoggerFactory
			.getLogger(QueryAssessmentReader.class);

	BufferedReader br = null;
	TsvTuple currentTuple = null;
	String currentString = null;
	TsvRecordParser parser = new TsvRecordParser("docid", "query", "uri",
			"prel", "rel");
	TsvRecordParser parser2 = new TsvRecordParser("docid", "query", "uri",
			"prel");

	public QueryAssessmentReader(String queryAssessmentFile) {
		br = IOUtils.getPlainOrCompressedUTF8Reader(queryAssessmentFile);

		nextTuple();

	}

	private void nextTuple() {

		do {
			try {
				currentString = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (currentString != null
				&& (currentString.trim().isEmpty() || currentString.trim()
						.charAt(0) == '#'));
		if (currentString != null) {
			try {
				currentTuple = parser.decode(currentString);
			} catch (Exception e) {
				// not using human relevance
				try {
					currentTuple = parser2.decode(currentString);
					currentTuple.put("rel", currentTuple.get("prel"));
				} catch (Exception e1) {
					logger.error("parsing line {} ", currentString);
					nextTuple();
				}
			}
		} else {
			currentTuple = null;
		}
	}

	@Override
	public boolean hasNext() {
		return currentTuple != null;
	}

	@Override
	public QueryAssessment next() {
		QueryAssessment qa = new QueryAssessment(currentTuple.get("query"));
		qa.addRelevantDocument(currentTuple.get("uri"),
				currentTuple.getInt("rel"));
		nextTuple();
		while (currentTuple != null
				&& qa.getQuery().equals(currentTuple.get("query"))) {
			qa.addRelevantDocument(currentTuple.get("uri"),
					currentTuple.getInt("rel"));
			nextTuple();
		}
		qa.sort();
		return qa;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		QueryAssessmentReader reader = new QueryAssessmentReader(
				"/tmp/test.txt");
		while (reader.hasNext()) {
			System.out.println(reader.next().asTrec());
		}
	}

}
