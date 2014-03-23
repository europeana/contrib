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
import it.cnr.isti.hpc.io.reader.RecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import it.cnr.isti.hpc.log.ProgressLogger;

import com.google.gson.Gson;

import eu.europeana.querylog.EuropeanaRecord;
import eu.europeana.querylog.QueryAssessment;

/**
 * Converts a tsv-encoded query log, sorted by query and produce a file with
 * automatically generated assessments, encoded in json: <br/>
 * <br/>
 * <code> java eu.europeana.querylog.cli.GenerateAssessmentsCLI -input log.tsv[.gz] -output assessments.json[.gz] </code>
 * <br/>
 * <br/>
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 1, 2014
 */
public class GenerateAssessmentsCLI extends AbstractCommandLineInterface {

	private static Gson gson = new Gson();

	public GenerateAssessmentsCLI(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		GenerateAssessmentsCLI cli = new GenerateAssessmentsCLI(args);
		RecordParser<EuropeanaRecord> parser = new EuropeanaRecord.TsvParser();
		RecordReader<EuropeanaRecord> reader = new RecordReader<EuropeanaRecord>(
				cli.getInput(), parser);
		ProgressLogger pl = new ProgressLogger("read {} queries", 100);
		cli.openOutput();
		QueryAssessment qa = null;
		for (EuropeanaRecord er : reader) {

			if (qa == null) {
				qa = new QueryAssessment(er);
				continue;
			}
			if (!qa.add(er)) {
				qa.generateAssessment();
				cli.writeLineInOutput(gson.toJson(qa));
				qa = new QueryAssessment(er);
				pl.up();
			}

		}
		qa.generateAssessment();
		cli.writeLineInOutput(gson.toJson(qa));
		cli.closeOutput();

	}

}
