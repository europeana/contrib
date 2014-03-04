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

import it.cnr.isti.hpc.io.reader.Filter;

import java.util.ArrayList;
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
	private String query;
	private Set<String> users;
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

	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append("query: ").append(query).append('\n');
		sb.append("total-clicks: ").append(totalClicks).append('\n');
		sb.append("original queries: ").append('\n');
		for (String q : originalQueries)
			sb.append("\t- ").append(q).append('\n');
		sb.append("users: ").append('\n');
		for (String u : users)
			sb.append("\t- ").append(u).append('\n');
		sb.append(assessment.size()).append(" rel. documents:").append('\n');
		for (RelevantDocument doc : assessment) {
			sb.append(doc.asString());
		}
		return sb.toString();
	}

	public String asHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<h2>" + query + "</h2>");
		sb.append("<table>\n");
		sb.append("<tr>\n");
		sb.append("<td>query: </td>").append("<td>" + query + "</td>")
				.append('\n');
		sb.append("</tr>\n");
		sb.append("<tr>\n");
		sb.append("<td>total-clicks: </td>")
				.append("<td>" + totalClicks + "</td>").append('\n');
		sb.append("</tr>\n");
		sb.append("<tr>\n");
		sb.append("<td>original queries: </td><td><ul>").append('\n');
		for (String q : originalQueries)
			sb.append("<li>").append(q).append("</li>\n");
		sb.append("</ul></tr>\n");
		sb.append("<tr>\n");
		sb.append("<td>distinct users: </td><td> ").append('\n');
		sb.append(users.size());
		sb.append("</td></tr>\n");
		sb.append("</table>\n");
		sb.append(assessment.size()).append(" rel. documents:").append('\n');
		for (RelevantDocument doc : assessment) {
			sb.append(doc.asHtml());
		}
		sb.append("\n<hr>\n");
		return sb.toString();

	}

	private static int id = 0;

	public String asTrec() {

		StringBuilder sb = new StringBuilder();
		sb.append("# query-id: ").append(id++).append('\n');
		sb.append("# query: ").append(query).append('\n');
		sb.append("# total-clicks: ").append(totalClicks).append('\n');
		sb.append("# distinct users: ").append(users.size()).append('\n');
		sb.append("# " + assessment.size()).append(" rel. documents:")
				.append('\n');
		for (RelevantDocument doc : assessment) {
			sb.append(String.format(
					"%-20s\thttp://europeana.eu/portal/record%s.html\t%s\n",
					query, doc.uri, Math.min((int) ((doc.getCtr() * 5) + 1), 5)));
		}

		sb.append("\n\n");
		return sb.toString();
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

	public int getTotalClicks() {
		return totalClicks;
	}

	public void setTotalClicks(int totalClicks) {
		this.totalClicks = totalClicks;
	}

	public List<RelevantDocument> getAssessment() {
		return assessment;
	}

	public void setAssessment(List<RelevantDocument> assessment) {
		this.assessment = assessment;
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

		public String asString() {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%-10s%s\n", "uri", uri));
			sb.append(String.format("%-10s%s\n", "clicks", clicks));
			sb.append(String.format("%-10s%s\n", "seen", seen));
			sb.append(String.format("%-10s%s\n", "rank", rank));
			sb.append(String.format("%-10s%s\n", "page", page));
			sb.append(String.format("%-10s%s\n", "ctr", ctr));
			sb.append('\n');
			return sb.toString();
		}

		public String asHtml() {
			StringBuilder sb = new StringBuilder();
			sb.append("<table>");
			sb.append(String
					.format("<tr><td>%s</td><td><a href=\"http://europeana.eu/portal/record/%s.html\">%s</a></td></tr>",
							"uri", uri, uri));
			sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>",
					"clicks", clicks));
			sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>", "seen",
					seen));
			sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>", "rank",
					rank));
			sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>", "page",
					page));
			sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>", "ctr",
					ctr));
			sb.append("</table>");

			sb.append('\n');
			return sb.toString();
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

	public static class DocumentNumberFilter implements Filter<QueryAssessment> {

		private int minNumberOfDocuments = 10;

		public DocumentNumberFilter(int minNumberOfDocuments) {
			this.minNumberOfDocuments = minNumberOfDocuments;
		}

		@Override
		public boolean isFilter(QueryAssessment item) {
			return item.assessment.size() < minNumberOfDocuments;
		}

	}

	public static class NumberOfClicksFilter implements Filter<QueryAssessment> {

		private int minNumberOfClicks = 10;

		public NumberOfClicksFilter(int minNumberOfClicks) {
			this.minNumberOfClicks = minNumberOfClicks;
		}

		@Override
		public boolean isFilter(QueryAssessment item) {
			return item.totalClicks < minNumberOfClicks;
		}

	}

	public static class QueryLengthFilter implements Filter<QueryAssessment> {

		private int minTerms = 1;
		private int maxTerms = 10;

		public QueryLengthFilter(int minTerms, int maxTerms) {
			this.minTerms = minTerms;
			this.maxTerms = maxTerms;
		}

		public QueryLengthFilter() {
			this(1, 4);
		}

		@Override
		public boolean isFilter(QueryAssessment item) {
			String[] terms = item.getQuery().split(" ");
			int nTerms = terms.length;
			return (nTerms > maxTerms || nTerms < minTerms);
		}
	}

	public static class UsersFilter implements Filter<QueryAssessment> {

		private int minUsers = 5;

		public UsersFilter(int minUsers) {
			this.minUsers = minUsers;
		}

		@Override
		public boolean isFilter(QueryAssessment item) {
			return item.getUsers().size() < minUsers;
		}
	}

	public static class MinClicksFilter implements Filter<QueryAssessment> {

		private int minClicks = 2;

		public MinClicksFilter() {

		}

		public MinClicksFilter(int minClicks) {
			this.minClicks = minClicks;
		}

		@Override
		public boolean isFilter(QueryAssessment item) {
			List<RelevantDocument> docs = item.getAssessment();
			List<RelevantDocument> rel = new ArrayList<RelevantDocument>();
			for (RelevantDocument d : docs) {
				if (d.getClicks() >= minClicks) {
					rel.add(d);
				}
			}
			item.setAssessment(rel);
			return false;
		}
	}

}
