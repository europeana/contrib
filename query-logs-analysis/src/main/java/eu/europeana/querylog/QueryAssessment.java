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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 1, 2014
 */
public class QueryAssessment {

	private static final int RESULTS_PER_PAGE = 24;
	private final Set<String> originalQueries;
	private final String query;
	private final Set<String> users;
	private int totalClicks = 0;
	private transient final Map<Integer, Set<String>> usersPerPage;
	private transient final Map<String, Set<String>> usersPerClick;
	private transient Map<String, RelevantDocument> uriToDocument;

	private static final Logger logger = LoggerFactory
			.getLogger(QueryAssessment.class);

	private List<RelevantDocument> assessment;

	public QueryAssessment(EuropeanaRecord rec) {
		query = rec.getNormalizedQuery();
		users = new HashSet<String>();
		originalQueries = new HashSet<String>();
		usersPerPage = new HashMap<Integer, Set<String>>();
		usersPerClick = new HashMap<String, Set<String>>();
		uriToDocument = new HashMap<String, RelevantDocument>();
		add(rec);
	}

	public boolean add(EuropeanaRecord rec) {
		String rquery = rec.getNormalizedQuery();
		if (!query.equals(rquery)) {
			return false;
		}
		originalQueries.add(rec.getQuery());
		users.add(rec.getUserId());
		if (rec.isClick()) {

			incrementClick(rec.getEuropeanaUri(), rec.getUserAgent());

			RelevantDocument rd = new RelevantDocument();
			rd.setRank(rec.getStart());
			rd.setPage((rec.getStart() - 1) / RESULTS_PER_PAGE + 1);
			rd.setUri(rec.getEuropeanaUri());
			uriToDocument.put(rec.getEuropeanaUri(), rd);

		} else {
			incrementVisits(rec.getPage(), rec.getUserAgent());
		}
		return true;

	}

	public QueryAssessment generateAssessment() {
		assessment = new LinkedList<RelevantDocument>();
		for (RelevantDocument rd : uriToDocument.values()) {
			rd.setClicks(usersPerClick.get(rd.getUri()).size());
			totalClicks += rd.getClicks();
			if (!usersPerPage.containsKey(rd.getPage())) {
				logger.warn("cannot find page {} frequencies, skipping",
						rd.getPage());
				continue;
			}
			rd.setSeen(usersPerPage.get(rd.getPage()).size());
			rd.setCtr((double) rd.getClicks() / (double) rd.getSeen());
			assessment.add(rd);

		}
		Collections.sort(assessment);

		return this;
	}

	private void incrementVisits(int page, String user) {
		if (!usersPerPage.containsKey(page)) {
			usersPerPage.put(page, new HashSet<String>());
		}
		usersPerPage.get(page).add(user);

	}

	private void incrementClick(String uri, String user) {
		if (!usersPerClick.containsKey(uri)) {
			usersPerClick.put(uri, new HashSet<String>());
		}
		usersPerClick.get(uri).add(user);

	}

	public class RelevantDocument implements Comparable<RelevantDocument> {
		String uri;
		int clicks;
		double ctr;
		int seen;
		int rank;
		int page;

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public int getClicks() {
			return clicks;
		}

		public void setClicks(int clicks) {
			this.clicks = clicks;
		}

		public double getCtr() {
			return ctr;
		}

		public void setCtr(double ctr) {
			this.ctr = ctr;
		}

		public int getSeen() {
			return seen;
		}

		public void setSeen(int seen) {
			this.seen = seen;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		@Override
		public int compareTo(RelevantDocument rd) {
			if (rd.ctr > ctr) {
				return 1;
			}
			if (ctr > rd.ctr) {
				return -1;
			}
			// same ctr, based on number of view
			return rd.clicks - clicks;
		}

	}
}
