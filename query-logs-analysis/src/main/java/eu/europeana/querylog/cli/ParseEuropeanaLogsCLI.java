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
package eu.europeana.querylog.cli;

import it.cnr.isti.hpc.cli.AbstractCommandLineInterface;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;
import eu.europeana.querylog.EuropeanaRecord;

/**
 * Parse europeana clickstream logs and produce europeana logs encoded in json.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 1, 2014
 */
public class ParseEuropeanaLogsCLI extends AbstractCommandLineInterface {

	public ParseEuropeanaLogsCLI(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		ParseEuropeanaLogsCLI cli = new ParseEuropeanaLogsCLI(args);
		EuropeanaRecord.Parser parser = new EuropeanaRecord.Parser();
		RecordReader<EuropeanaRecord> reader = new RecordReader<EuropeanaRecord>(
				cli.getInput(), parser);
		ProgressLogger pl = new ProgressLogger("reader {} records", 100);
		cli.openOutput();
		for (EuropeanaRecord er : reader) {
			if (er == null)
				continue;
			pl.up();
			cli.writeLineInOutput(parser.encode(er));
		}
		cli.closeOutput();

	}

}
