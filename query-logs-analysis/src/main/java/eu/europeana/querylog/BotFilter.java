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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 3, 2014
 */
public class BotFilter implements Filter<EuropeanaRecord> {

	private final List<String> botSignature = new ArrayList<String>();

	private static final Logger logger = LoggerFactory
			.getLogger(BotFilter.class);

	public BotFilter() {
		logger.info("loading bot signatore");
		try {
			loadSignatures();
		} catch (IOException e) {
			logger.error("loading bot signatures");
			System.exit(-1);
		}
	}

	private void loadSignatures() throws IOException {
		InputStream is = this.getClass().getResourceAsStream(
				"/bot-user-agents.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			// logger.info("line {} ", line);
			botSignature.add(line.trim());
		}
		return;
	}

	@Override
	public boolean isFilter(EuropeanaRecord item) {
		String ua = item.getUserAgent();
		if (ua == null || ua.isEmpty())
			return false;
		ua = ua.toLowerCase();
		for (String bot : botSignature) {
			if (ua.contains(bot))
				return true;
		}
		return false;
	}
}
