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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Jan 31, 2014
 */
public class EuropeanaRecordTest {

	@Test
	public void testSimpleRecord() {
		String record = "INFO: [action=INDEXPAGE, view=default/index/index, userId=, lang=en, req=http://127.0.0.1:8080/portal/?1354201901777, date=2013-01-22T22:31:32.281+01:00, ip=127.0.0.1, user-agent=Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0), referer=http://portal2.europeana.eu/portal2/record/15502/DF491FE4AE4F7C667FB1C2B5F525BC7CDC01239A.html?start=1&query=paris&startPage=1&rows=24, utma=, utmb=, utmc=, v=2.0]";
		EuropeanaRecord.Parser parser = new EuropeanaRecord.Parser();
		EuropeanaRecord er = parser.decode(record);
		Assert.assertEquals("INDEXPAGE", er.getAction());
		Assert.assertEquals("default/index/index", er.getView());
		Assert.assertEquals(null, er.getUserId());
		Assert.assertEquals("http://127.0.0.1:8080/portal/?1354201901777",
				er.getReq());
		Assert.assertEquals(
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
				er.getUserAgent());
		Assert.assertEquals(
				"http://portal2.europeana.eu/portal2/record/15502/DF491FE4AE4F7C667FB1C2B5F525BC7CDC01239A.html?start=1&query=paris&startPage=1&rows=24",
				er.getReferrer());

		Assert.assertEquals("2.0", er.getVersion());

		System.out.println(parser.encode(er));

	}

	@Test
	public void testSimpleRecordWithQuery() {
		String record = "INFO: [action=BRIEF_RESULT, view=default/search/search, query=india, queryType=null, queryConstraints=\"\", page=1, numFound=61,734, langFacet=nl (21736),en (21056),mul (7756),sv (4984),es (2121),de (1783), countryFacet=netherlands (21825),united kingdom (20209),europe (6941),sweden (5147),spain (2618),germany (1289), userId=, lang=en, req=http://127.0.0.1:8080/portal/search.html?query=india, date=2013-01-25T13:47:19.139+01:00, ip=127.0.0.1, user-agent=Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:18.0) Gecko/20100101 Firefox/18.0, referer=null, utma=53518249.1676717375.1357810032.1359022818.1359032451.12, utmb=, utmc=118166301, v=2.0]";
		EuropeanaRecord.Parser parser = new EuropeanaRecord.Parser();
		EuropeanaRecord er = parser.decode(record);
		Assert.assertEquals("BRIEF_RESULT", er.getAction());
		Assert.assertEquals("india", er.getQuery());
		Assert.assertEquals(null, er.getQueryType());
		Assert.assertEquals(null, er.getQueryContraints());
		Assert.assertEquals(1, er.getPage());
		// Assert.assertEquals(61734, er.getNumFound());
		Assert.assertEquals(
				"53518249.1676717375.1357810032.1359022818.1359032451.12",
				er.getUtma());

	}

	@Test
	public void testEuropeanaUri() {
		String record = "INFO: [action=FULL_RESULT_HMTL, europeana_uri=/2020704/A4CFB1DCDF630502AE30290A086F0108CB62C95F, query=null, start=1, numFound=0, userId=, lang=en, req=http://127.0.0.1:8080/portal/record/2020704/A4CFB1DCDF630502AE30290A086F0108CB62C95F.html?record=2020704/A4CFB1DCDF630502AE30290A086F0108CB62C95F, date=2013-01-22T22:18:19.558+01:00, ip=127.0.0.1, user-agent=Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0), referer=null, utma=, utmb=, utmc=, v=2.0]";
		EuropeanaRecord.Parser parser = new EuropeanaRecord.Parser();
		EuropeanaRecord er = parser.decode(record);
		Assert.assertEquals("FULL_RESULT_HMTL", er.getAction());
		Assert.assertEquals(
				"/2020704/A4CFB1DCDF630502AE30290A086F0108CB62C95F",
				er.getEuropeanaUri());
		Assert.assertEquals(null, er.getQuery());
		Assert.assertEquals(
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
				er.getUserAgent());
		Assert.assertEquals("127.0.0.1", er.getIp());
	}

	@Test
	public void testStart() {
		String record = "INFO: [action=FULL_RESULT_HMTL, europeana_uri=/09429/CCDBE63C5CF07DE49032517A11A9DAA8BBE6F75C, query=null, start=1, numFound=0, userId=, lang=en, req=http://127.0.0.1:8080/portal/record/09429/CCDBE63C5CF07DE49032517A11A9DAA8BBE6F75C.html?record=09429/CCDBE63C5CF07DE49032517A11A9DAA8BBE6F75C, date=2013-01-22T21:32:34.295+01:00, ip=127.0.0.1, user-agent=Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0), referer=null, utma=, utmb=, utmc=, v=2.0]";
		EuropeanaRecord.Parser parser = new EuropeanaRecord.Parser();
		EuropeanaRecord er = parser.decode(record);
		Assert.assertEquals(1, er.getStart());
	}
}
