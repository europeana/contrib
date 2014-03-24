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

import java.util.Scanner;

import eu.europeana.querylog.learn.measure.filter.TopKFilter;

/**
 * MeasureFactory creates a benchmark measure given its code. Available codes
 * are:
 * <ul>
 * <li> <code> NDCG </code>: Normalized Discount Comulative Gain;</li>
 * <li> <code> P </code>: precision;</li>
 * <li> <code> R </code>: recall.</li>
 * </ul>
 * 
 * You can also ask to compute the benchmark only over the first K retrieved
 * results, appending <code>@K</code> at the end of the code, e.g.,
 * <code>P@1</code> will compute the precision only considering the first result
 * returned by a ranking function.
 * 
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Mar 14, 2014
 */
public class MeasureFactory {

	public static Measure getMeasure(String code) {
		int k = -1;
		String measure = code;
		if (code.contains("@")) {
			Scanner scanner = new Scanner(code).useDelimiter("@");
			measure = scanner.next();
			if (scanner.hasNextInt()) {
				k = scanner.nextInt();
			}

		}
		Measure m = get(measure);
		if (k > 0) {
			m.addFilter(new TopKFilter(k));
		}
		return m;
	}

	private static Measure get(String code) {
		if (code.equals(Ndcg.NAME))
			return new Ndcg();
		if (code.equals(BNdcg.NAME))
			return new BNdcg();
		if (code.equals(Precision.NAME))
			return new Precision();
		if (code.equals(Recall.NAME))
			return new Recall();
		throw new UnsupportedOperationException("cannot find measure " + code);
	}

}
