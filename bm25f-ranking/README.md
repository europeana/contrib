## BM25F Ranking SOLR 4.0 plugin 


### Introduction
Ranking functions are one of the most important components of a document retrieval system.  A ranking function answers to the question "*what is the relevance of a document d for the user query q?*". Therefore,  the goodness of the ranking function adopted determines the quality of the results returned.The probably most widely used ranking function is [BM25][bm25], and it is still considered the most relevant baseline. Grounded in the probabilistic language modelling theory, BM25 was designed  as a non-linear combination of three important document attributes: term frequency, document frequency, and document length. Even if originally, Web documents where considered as composed of few fields, such as body, title, URL, BM25 uses a flat representation of a document, where its fields are simply concatenated  into a single textual description. But we know that Europeana documents have a very rich structure and they are described by means of many fields, each possibly playing a different role in the document retrieval task.
  
[BM25F][bm25f] is an extension of BM25 that exploits a document description having multiple fields, and it is still a non-linear function, thus capable of modelling non-trivial factors that determine relevance of a document for a given query. Given a document d, having fields F, and a query q, BM25F produces a score of the document computed as follows:


![bm25f](http://www.texify.com/img/%5CLARGE%5C%21%20BM25F%28q%2Cd%29%20%3D%20%5Csum_%7Bt%20%5Cin%20q%7D%7BTF%28t%2Cd%29%20%5Ccdot%7D.gif)

where TF(t,d) measures the importance of term t for document d, and IDF(t) is the usual inverse document  frequency measuring the importance of term t in the whole collection of document. Let df(t) be the document  frequency of term t, i.e. the number of documents in the collection containing the term t, the IDF function is defined as follows:

![bm25f-2](http://www.texify.com/img/%5CLARGE%5C%21IDF%28t%29%3Dlog%20%5Cfrac%7BN-df%28t%29%2B0.5%7D%7Bdf%28t%29%2B0.5%7D.gif)

More precisely, BM25 and BM25F adopt a term frequency saturation function which accounts for the fact that finding twice the term t in d, is not twice as surprising (i.e. relevant) as finding the same term once. We can update the BM25F formula as follows:

![bm25f-3](http://www.texify.com/img/%5CLARGE%5C%21BM25F%28q%2Cd%29%20%3D%20%5Csum_%7Bt%20%5Cin%20q%7D%20%5Cfrac%7BTF%28t%2Cd%29%7D%7Bk%2BTF%28t%2Cd%29%7D%5Ccdot%20IDF%28t%29.gif)

The parameter k realizes the saturation: the larger k, the more important is the variation of term frequency. As we mentioned above, BM25F takes into account the multiple fields of the document, and this is done when computing the term frequency component TF(t,d). Indeed, the term frequency is computed independently for each field, and a linear combination is computed as follows:

![bm25f-4](http://www.texify.com/img/%5CLARGE%5C%21TF%28t%2Cd%29%20%3D%20%5Csum_%7Bf%20%5Cin%20F%7D%20w_f%20%5Ccdot%20TF%28t%2Cd%2Cf%29.gif)

where w_f is a weight (or boosting factor) for the field f, and TF(t,d,f) is the frequency-based contribution of term t in the field f of document d. Finally, the frequency TF(t,d,f) is normalized on the basis of the length of the document field f:

![bm25f-5](http://www.texify.com/img/%5CLARGE%5C%21TF%28t%2Cd%2Cf%29%20%3D%20%5Cfrac%7Boccurs%28t%2Cd%2Cf%29%7D%7B%281-b_f%29%20%2B%20b_f%20%28%5Cfrac%7Bl_%7Bd%2Cf%7D%7D%7Bl_f%7D%29%7D.gif)

where occurs(t,d,f) is the actual number of occurrences of term t in the field f of document d, ld,f is the length of the field f of document d, lf is the average length of field f across the whole collection, and bf is a model parameter tuning the impact of document length  normalization.  BM25F can be considered the state of the art of ranking functions in multi-field document retrieval. However, its accuracy depends on the ability to fine-tune its parameters k, wf, bf. Note that for |F| fields there are 2|F|+1 parameters to be tuned. 

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
		
		<queryParser name="bm25f" class="eu.europeana.ranking.bm25f.BM25FParserPlugin">
			<str name="mainField">text</str>

			<float name="k1">18.0</float>

			<lst name="fieldsBoost">
				<float name="text">3</float>
				<float name="title">39.0</float>
				<float name="author">8.0</float>
				<float name="description">10.0</float>
			</lst>
		
			<lst name="fieldsB">
				<float name="text">0.15</float>
				<float name="title">0.05</float>
				<float name="author">0</float>
				<float name="description">0.75</float>
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


