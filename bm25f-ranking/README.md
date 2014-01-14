## BM25F Ranking SOLR 4.0 plugin 

<script type="text/x-mathjax-config">
  MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$'], ['\\(','\\)']]}});
</script>
<script type="text/javascript"
  src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
</script>

### Introduction
Ranking functions are one of the most important components of a document retrieval system. 
A ranking function answers to the question "*what is the relevance of a document d for the user query q?*". Therefore, 
the goodness of the ranking function adopted determines the quality of the results returned.
The probably most widely used ranking function is [BM25][bm25], and it is still considered the 
most relevant baseline. Grounded in the probabilistic language modelling theory, BM25 was designed 
as a non-linear combination of three important document attributes: term frequency, document frequency, 
and document length. Even if originally, Web documents where considered as composed of few fields, such
 as body, title, URL, BM25 uses a flat representation of a document, where its fields are simply concatenated
  into a single textual description. But we know that Europeana documents have a very rich structure and they 
  are described by means of many fields, each possibly playing a different role in the document retrieval task.
  
[BM25F][bm25f] is an extension of BM25 that exploits a document description having multiple fields, 
and it is still a non-linear function, thus capable of modelling non-trivial factors that determine 
relevance of a document for a given query. Given a document d, having fields F, and a query q, BM25F 
produces a score of the document computed as follows:



$$ BM25F(q,d) = \sum_{t \in q}{TF(t,d) \cdot} $$



The [BM25F ranking function][bm25f] comes as a [Solr Query Parser plugig][solr]. In order to install
it, the following steps have to be performed. 


[bm25]: http://dl.acm.org/citation.cfm?id=188561 "Some simple effective approximations to the 2-Poisson model for probabilistic weighted retrieval, SE Robertson, S Walker - SIGIR 1994"
[bm25f]: http://dl.acm.org/citation.cfm?id=1031181 "Simple BM25 extension to multiple weighted fields, S Robertson, H Zaragoza, M Taylor, CIKM 2004"
[bm25f2]: http://dl.acm.org/citation.cfm?id=1704810 "The Probabilistic Relevance Framework: BM25 and Beyond"
[solr]: http://wiki.apache.org/solr/SolrPlugins 


