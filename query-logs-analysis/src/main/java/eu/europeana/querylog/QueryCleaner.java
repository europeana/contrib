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

import java.util.ArrayList;
import java.util.List;

import eu.europeana.querylog.clean.AsciiCleaner;
import eu.europeana.querylog.clean.BooleanFilter;
import eu.europeana.querylog.clean.Cleaner;
import eu.europeana.querylog.clean.FieldCleaner;
import eu.europeana.querylog.clean.HTMLCleaner;
import eu.europeana.querylog.clean.LowerCaseCleaner;
import eu.europeana.querylog.clean.MultipleSpacesCleaner;
import eu.europeana.querylog.clean.NumberFilter;
import eu.europeana.querylog.clean.RegexCleaner;
import eu.europeana.querylog.clean.StarQueryFilter;
import eu.europeana.querylog.clean.TabCleaner;
import eu.europeana.querylog.clean.TrimCleaner;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 4, 2014
 */
public class QueryCleaner {

	List<Cleaner> cleaners = new ArrayList<Cleaner>();

	public QueryCleaner() {

	}

	public static QueryCleaner getStandardQueryCleaner() {
		QueryCleaner qc = new QueryCleaner();
		qc.addCleaner(new StarQueryFilter());
		qc.addCleaner(new HTMLCleaner());
		qc.addCleaner(new TabCleaner());

		qc.addCleaner(new AsciiCleaner());
		qc.addCleaner(new FieldCleaner());
		qc.addCleaner(new LowerCaseCleaner());
		qc.addCleaner(new RegexCleaner("[\\\\/\"*]"));
		qc.addCleaner(new TrimCleaner(" ()'\"[.],-"));
		qc.addCleaner(new TrimCleaner(" ()"));
		qc.addCleaner(new BooleanFilter());
		qc.addCleaner(new NumberFilter());
		qc.addCleaner(new MultipleSpacesCleaner());
		return qc;
	}

	public void addCleaner(Cleaner cleaner) {
		cleaners.add(cleaner);
	}

	public String clean(String query) {
		for (Cleaner c : cleaners) {
			query = c.clean(query);
			if (query == null)
				break;
		}
		// normalize spaces
		if (query != null) {
			query = query.trim();
			if (query.isEmpty())
				return null;
		}
		return query;
	}

}
