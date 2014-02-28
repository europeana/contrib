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
package eu.europeana.querylog.clean;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.europeana.querylog.QueryCleaner;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 28, 2014
 */
public class CleanersTest {

	@Test
	public void testHtmlClean() {
		HTMLCleaner cleaner = new HTMLCleaner();
		assertEquals("\"anne frank\"", cleaner.clean("%22anne%20frank%22"));
		assertEquals("<", cleaner.clean("&lt;"));
	}

	@Test
	public void testQueryCleaner() {
		QueryCleaner cleaner = QueryCleaner.getStandardQueryCleaner();
		assertEquals("anne frank", cleaner.clean("%22aNne%20fRank%22"));
		assertEquals("anne frank", cleaner.clean("%22anne%20frank%22"));
		assertEquals("anne frank", cleaner.clean("%22aNne\tfRank%22"));
		assertEquals("anne frank", cleaner.clean("%22aNne\t\tfRank%22"));
		assertEquals(
				"afbeeldinge van de stadt amsterdam in nieuw neederlande",
				cleaner.clean("'Afbeeldinge van de Stadt Amsterdam in Nieuw Neederlande'"));

		assertEquals("william shakespeare",
				cleaner.clean("' william shakespeare'"));

		assertEquals(
				"ogrody pamieci w sztuce ogrodowej i architekturze krajobrazu",
				cleaner.clean(",,ogrody pamieci w sztuce ogrodowej i architekturze krajobrazu"));
		assertEquals(null, cleaner.clean("-"));
		assertEquals("tripoli", cleaner.clean("tripoli"));

	}
}
