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
import it.cnr.isti.hpc.io.reader.JsonRecordParser;
import it.cnr.isti.hpc.io.reader.RecordReader;
import eu.europeana.querylog.QueryAssessment;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Feb 1, 2014
 */
public class PrintFilteredAssessmentsCLI extends AbstractCommandLineInterface {

	public static String[] params = new String[] { INPUT, OUTPUT, "clicks",
			"docs", "users" };
	public static String usage = "java $jar "
			+ PrintFilteredAssessmentsCLI.class
			+ " -input assessment.json -clicks min-number-of-clicks -docs min-number-of-clicked-documents -users min-number-of-users";

	public PrintFilteredAssessmentsCLI(String[] args) {
		super(args, params, usage);
	}

	public static void main(String[] args) {
		PrintFilteredAssessmentsCLI cli = new PrintFilteredAssessmentsCLI(args);
		int clicks = cli.getIntParam("clicks");
		int docs = cli.getIntParam("docs");
		int users = cli.getIntParam("users");
		boolean html = cli.getOutput().endsWith(".html");
		boolean trec = cli.getOutput().endsWith(".trec");
		RecordReader<QueryAssessment> reader = new RecordReader<QueryAssessment>(
				cli.getInput(), new JsonRecordParser<QueryAssessment>(
						QueryAssessment.class));
		cli.openOutput();
		reader.filter(new QueryAssessment.MinClicksFilter(),
				new QueryAssessment.DocumentNumberFilter(docs),

				new QueryAssessment.NumberOfClicksFilter(clicks),
				new QueryAssessment.QueryLengthFilter(),
				new QueryAssessment.UsersFilter(users));

		int i = 0;
		for (QueryAssessment er : reader) {
			i++;
			if (html) {
				cli.writeInOutput(er.asHtml());
			} else if (trec) {
				cli.writeInOutput(er.asTrec());
			} else {
				cli.writeInOutput(er.asString());
			}
		}
		System.out.println(i + " queries found");
		cli.closeOutput();

	}
}
