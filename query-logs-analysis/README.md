## Europeana Query Logs analysis framework 

This package mainly provides two functionalities: 

  * tools to manage the europeana solr logs in order mine knowledge by what users are looking for; 
  * it allows to automatically produce a goldentruth of good results for a set of queries, and learn the best values of the bm25f ranking function from this assessments
  
The package contains several command line commands (CLI) for managing the logs and performing the learning. 
We also provided some bash scripts for running the commands without caring of the correct package paths. 
In order to use the scripts you will have to create a jar with all the dependencies, running the maven command
 
     mvn assembly:assembly 

The scripts are in the folder `scripts`, in the following we will describe the available commands.   
  
### Manipolate the Europeana Query Log 

The Europeana query logs have two different formats:
  
  * **the clickstream format**: the toString() method of a Clickstream object
  * **a json format**: introduced recently, currently we don't have logs in this format. 
 
The package contains several cli to manipulate the logs. But first the clickstrem logs must be converted
in a json-format. The script 

    ./scripts/parse-logfile-to-json.sh log-clickstream.txt[.gz] log.json[.gz]

will convert a clickstream file to the json format. 

We provided code to study how users query and clicks on the documents. The code filter out from the json logs 
the bots and generate tab separated value files (.tsv) containing the query and clicks performed by the users. 
The file can be generated running: 

    ./scripts/convert-json-logs-to-tsv.sh log.json[.gz] log.tsv

The format of the tsv file is: 

	<timestamp>	<user-id> <query> <cleaned query> <page> <uri clicked> <page> <start> <user-agent>

Each line can represent a query or a click. We apply several heuristics for cleaning the query, and we 
put the cleaned query in the fields. Page contains the current page visited by a user looking for a result,
while start contains the rank of the document, in case of a click. User-agent contains a unique id for a user:
if europeana provides one, otherwise contains an id generated from the user-agent. 

In order to convert a folder containing clickstream logs and produce one unique tsv file (need for the learning to rank), 
you can run: 

    ./scripts/prepare-logs.sh clickstream-log-directory json-log-directory logs.tsv[.gz]

That will generate the json log files in the json-log-directory, and one tsv file containing all the queries and clicks. 


### Learning the BM25F parameters

In order to learn the parameters, it is required a goldentruth, i.e., a set of queries, and for each query in the set its relevant
documents. Usually relevant documents for each query are created by professional editors, but they can  be approximately created
from user logs. If given a query users clicks on a document, that document can be considered relevant. Of course this is biased
by the fact that users usually examine only the first result page. In order to mitigate that document are sorted by their
click-trough-rate (ctr), i.e., how many times the document was clicks versus how many time it was displayed to the users. 

In order to generate the assessments for all the querylog, you can run: 

    ./scripts/convert-tsv-logs-to-json-assessments.sh logs.tsv[.gz] assessment.json[.gz]

The command will create a json file containing one assessment for query. Each line contains one assessment encoded in json. 
The assessment contains the query and its (clicked) relevant documents. It also contains the set of the users that clicked for 
the documents, the total number of clicks etc etc. 

In order to train the logs we need to select a significant subset from the assessment, this subset can be 
produced using the command: 

    ./scripts/print-json-assessments.sh assessments.json[.gz] min-number-of-clicks min-number-of-documents min-number-of-users goldentruth.txt

The command will filter only the assessments with at least `min-number-of-clicks` (over all the documents clicked for the query), clicked 
by `min-number-of-users` distinct users and with at least `min-number-of-documents` distinct relevant documents. (A good setting is 10, 10 ,10). 
It will produce a plain file containing the filtered queries with the relevant documents, that is used to learn the bm25f parameters. 

We also provide an interface for manually edit the scores associated using the query logs. Human editing is not mandatory but it would improve
quality of learning. If the assessments file is `goldentruth.txt`, run as:

	europeana-eval/europeana-eval.py goldentruth.txt

This will open a browser window showing the first document in `foo.txt` that  requires an assessment.
By submitting the rating, the browser is redirected to the next document that needs an assessment. It is possible to move around assessments with the "Previous" and "Next" links, and it is possible to change the ratings.
More documentation is available in the folder europeana-eval.


#### How to perform the learning

In order to perform the learning you will need two cores: one for the bm25f and one for europeana. The rationale for this is that
bm25f and the standard tf-idf function use two different Similarity functions, and you can set only one similarity function per core. 
You'll only need two cores that share the same data folder. In the bm25f core you will need to add a bm25f query parser plugin (see the 
`bm25f-ranking` documentation for the details). 

Once you have set up the cores, you will have to edit the `project.properties` file to add your server URLs and the fields to consider in the 
bm25f ranking (that should correspond to the ones set in the solr parser plugin). Fields must be inserted in the field `bm25f.fields`, separated
by a comma. 

Then, you'll be able to run learning running: 

	scripts/learn-bm25f-params.sh assessment-dir measure-to-optimize[bNDCG@24,NDCG@24,P@24,R@24] log-file

where `assessment-dir` is a directory containing the one or several goldentruth files, `measure-to-optmize` is the measure to 
optimize for learning the parameters, we usually used bNDCG@24 (since Europeana displays usally 24 documents). `log-file` is 
a file where the best values for the parameters will be stored, ready to be copypasted in solr config (the last record in the file will be 
always the best). 





 




 
