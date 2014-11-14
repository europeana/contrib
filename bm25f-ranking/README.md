## BM25F Ranking SOLR 4.0 plugin 


### Introduction
Ranking functions are one of the most important components of a document retrieval system.  A ranking function answers to the question "*what is the relevance of a document d for the user query q?*". Therefore,  the goodness of the ranking function adopted determines the quality of the results returned.The probably most widely used ranking function is [BM25][bm25], and it is still considered the most relevant baseline. Grounded in the probabilistic language modelling theory, BM25 was designed  as a non-linear combination of three important document attributes: term frequency, document frequency, and document length. Even if originally, Web documents where considered as composed of few fields, such as body, title, URL, BM25 uses a flat representation of a document, where its fields are simply concatenated  into a single textual description. But we know that Europeana documents have a very rich structure and they are described by means of many fields, each possibly playing a different role in the document retrieval task.
  
[BM25F][bm25f] is an extension of BM25 that exploits a document description having multiple fields, and it is still a non-linear function, thus capable of modelling non-trivial factors that determine relevance of a document for a given query. You can find a detailed explanation of bm25f in this [paper](http://trec.nist.gov/pubs/trec13/papers/microsoft-cambridge.web.hard.pdf).

### BM25F Solr Plugin

The [BM25F ranking function][bm25f] comes as a [Solr Query Parser plugin][solr]. In order to install it, the following steps have to be performed: 

1. Create a jar of the module ./bm25f-solr-ranking containing all the dependences.  Run the command mvn assembly:assembly within the ./bm25f-solr-ranking folder will produce the jar in the target folder, (i.e. ''bm25f-solr-ranking-xx-jar-with-dependencies.jar'')

2. Move the jar in the lib folder of the core containing the your index folder, or in the main lib of solr ( see also [the CoreAdmin documentation][solr1]), please note that by default Solr does not have the lib folder, you’ll have to create it.
3. Override the default Solr Similarity function with the BM25F Similarity function, i.e., open the ''schema.xml'' file in your core and add: 

		<schema name="europeana" version="1.4">
			<!-- BM25FSimilarity overriding the default similarity -->
			<similarity class="eu.europeana.ranking.bm25f.similarity.BM25FSimilarityFactory" />
			...
		</schema>
    
4. Add the new query type and the new query handler, i.e., open the ''solrconfig.xml'' file in your core and add:
		
		<queryParser name="bm25f"
			class="eu.europeana.ranking.bm25f.BM25FParserPlugin">
				<str name="mainField">text</str>
					<float name="k1">2.052941</float>
					<lst name="fieldsBoost">
					<float name="text"> 0.6684119</float>
					<float name="title"> 1.9444914</float>
					<float name="what"> 0.015632015</float>
					<float name="when"> 1.8931717</float>
					<float name="who"> 0.7799127</float>
					</lst>
				<lst name="fieldsB">
					<float name="text"> 0.9766173</float>
					<float name="title"> 0.5606159</float>
					<float name="what"> 0.43865865</float>
					<float name="when"> 0.8196297</float>
					<float name="who"> 0.8619799</float>
				</lst>
		</queryParser>

The configuration file allows the admin to change the parameters. The customizable parameters are:
  * **K1**, the saturation factor;
  * **fieldsBoost**  containing the boosts to apply on the various fields;
  * **fieldsB**, containing the boosts to apply to the length of a field;
  * **mainField** the default field on which the query is performed.



Once the plugin has been plugged in, the BM25F ranking function can be called by simply adding the parameter defType=bm25f to the GET HTTP request, e.g. :

	http://mysolrmachine:8983/solr/select/?defType=bm25f&q=leonardo
  





[bm25]: http://dl.acm.org/citation.cfm?id=188561 "Some simple effective approximations to the 2-Poisson model for probabilistic weighted retrieval, SE Robertson, S Walker - SIGIR 1994"
[bm25f]: http://dl.acm.org/citation.cfm?id=1031181 "Simple BM25 extension to multiple weighted fields, S Robertson, H Zaragoza, M Taylor, CIKM 2004"
[bm25f2]: http://dl.acm.org/citation.cfm?id=1704810 "The Probabilistic Relevance Framework: BM25 and Beyond"
[solr]: http://wiki.apache.org/solr/SolrPlugins 
[solr1]: http://wiki.apache.org/solr/CoreAdmin


