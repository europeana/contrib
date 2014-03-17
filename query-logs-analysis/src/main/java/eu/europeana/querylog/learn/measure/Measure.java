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
package eu.europeana.querylog.learn.measure;

import java.util.List;

import eu.europeana.querylog.QueryAssessment;
import eu.europeana.querylog.learn.measure.filter.Filter;

/**
 * A benchmarks measure reports how much a ranking function is good, given a
 * goldentruth assessment.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 11, 2014
 */
public interface Measure {
	/**
	 * returns a score reporting how much a ranking function is good, given a
	 * goldentruth assessment. The higher is the score the better the ranking
	 * function.
	 * 
	 * @param results
	 *            the ranked document-id returned by the ranking function that
	 *            is evaluated
	 * @param assessment
	 *            a query assessment;
	 * @return a score representing how much the ranking function is good for
	 *         the given query assessment, given a goldentruth assessment. The
	 *         higher is the score the better the ranking function.
	 * 
	 */
	public double getScore(List<String> results, QueryAssessment assessment);

	/**
	 * adds a filter, the filter will be applied every time on the given list of
	 * the results.
	 * 
	 * @param f
	 *            a filter to apply to the list of results before evaluating
	 *            getScore.
	 */
	public void addFilter(Filter f);

	/**
	 * returns the list of set filters
	 */
	public List<Filter> getFilters();

	/**
	 * return the name of this measure.
	 */
	public String getName();

}
