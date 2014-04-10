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
 
The package contains several cli to manipulate the logs. But first the clickstream logs must be converted
in a json-format. The script 

    ./scripts/parse-logfile-to-json.sh log-clickstream.txt[.gz] log.json[.gz]

will convert a clickstream file to the json format. 

We provided code to study how users query and click on the documents. The code filters out from the json logs 
the bots and generates tab separated value files (.tsv) containing the queries and clicks performed by the users. 
The file can be generated running: 

    ./scripts/convert-json-logs-to-tsv.sh log.json[.gz] log.tsv

The format of the tsv file is: 

	<timestamp>	<user-id> <query> <cleaned query> <page> <uri clicked> <page> <start> <user-agent>

Each line can represent a query or a click. We apply several heuristics for cleaning the queries, and we 
put the cleaned query in the particular field `cleaned query`. `page` contains the current page visited by a user looking for a result,
while `start` contains the rank of the document in case of a click. `user-id` contains a unique id for a user
if europeana provides one, otherwise contains an id generated from the user-agent. 

In order to convert a folder containing clickstream logs and produce one unique tsv file (needed for the learning to rank), 
you can run: 

    ./scripts/prepare-logs.sh clickstream-log-directory json-log-directory logs.tsv[.gz]

the command will generate the json log files in the json-log-directory, and one tsv file containing all the queries and clicks. 


### Learning the BM25F parameters

In order to learn the parameters, it is required a //goldentruth//, which consists in:
  
  * a set of queries;
  * for each query in the set its relevant documents. 
  
Usually relevant documents are created by professional editors, but they can  be approximately generated
from the query logs. If given a query a user clicks on a document, that document can be considered relevant. Of course this is biased
by the fact that users usually examine only the first result page. In order to mitigate this phenomena documents are sorted by their
click-trough-rate (ctr), i.e., how many times the document was clicked versus how many time it was displayed to the users. 

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

We also provide a command for manually editing the scores associated using the query logs. Human editing is not mandatory but it would improve
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

Once you set up the cores, you will have to edit the `project.properties` file to add your server URLs and the fields to consider in the 
bm25f ranking (that should correspond to the ones set in the solr parser plugin). Fields must be inserted in the field `bm25f.fields`, separated
by a comma. 

We provided two different algorithms to perform the learning: 

   * **Line Search**, that starting from an initial point in the parameter space, performs a search along each coordinate axis varying one parameter only and keeping fixed the others. For each sample point, a given performance measure is computed, and the location corresponding to the best value of the measure is recorded. Such location identifies a promising search direction. Therefore, a line search is performed along the direction from the starting point to the best score location. If the parameter space has dimension k, we need to perform k+1 line searches to complete an iteration, or epoch, and possibly move to an improved solution. The new solution is then used as the starting point of the next iteration. This iterative process continues until no improvement is found, or a maximum number of epochs is reached (for more details see [this paper](research.microsoft.com/apps/pubs/default.aspx?id=65237) ). You can start this learning strategy with the command: 
   
    	scripts/learn-bm25f-params-linesearch.sh assessment-dir measure-to-optimize[bNDCG@24,NDCG@24,P@24,R@24] log-file    
 
   * **CMA-ES** stands for Covariance Matrix Adaptation Evolution Strategy. Evolution strategies (ES) are stochastic, derivative-free methods for numerical optimization of non-linear or non-convex continuous optimization problems. They belong to the class of evolutionary algorithms and evolutionary computation (for more details see [this article](http://en.wikipedia.org/wiki/CMA-ES)).  You can start this learning strategy with the command: 
   
		scripts/learn-bm25f-params-cma.sh assessment-dir measure-to-optimize[bNDCG@24,NDCG@24,P@24,R@24] log-file    
   
 
where in both the commands `assessment-dir` is a directory containing the one or several goldentruth files, `measure-to-optmize` is the measure to 
optimize for learning the parameters, we usually used bNDCG@24 (since Europeana usually displays  24 documents). `log-file` is 
a file where the best values for the parameters will be stored, ready to be copypasted in solr config (the last record in the file will be 
always the best). 

#### Some results

Using an assessment composed by 640 queries: 

  * Europeana-ranking: P@24 = 0.075574;
  * BM25F (CMA): P@24 = 0.083130 (+10%);
  
  * score europeana > bm25f  75 queries;
  * score bm25f > europeana 103 queries;
  * same score on 462 queries;
  
  * in 284 queries bm25f has at least one relevant result in the first page;
  * in 276 queries europeana has at least one relevant result in the first page.
 
 
#### Learning progress:  
 
![learning](https://dl.dropboxusercontent.com/u/4663256/tmp/learning.png) 

The x-axis represents time in seconds while, y-axis represents the precision over the first 24 results (number of relevant results / retrieved results, averaged over all the queries in the assessment).
Both the methods converge in around 1 hour. 





 




 
