# DBpedix

DBpedix is a Java library that allows you to build your own [Lucene] (http://lucene.apache.org/)
index from [DBpedia] (http://dbpedia.org) dump file data.

## Getting started

DBpedix is a [Maven] (http://maven.apache.org/) project. If you are using Eclipse, use

     mvn eclipse:eclipse
     
to generate project files. 


## Building the Index

Usually, you will want to control yourself which parts of DBpedia should go into your index, and
which shouldn't. The ``at.ait.dme.dpedix.maintenance`` package contains various Java classes you
can use to download and index different types of DBpedia data, in different languages. At the
moment, you are limited to:

* Labels
* Short abstracts
* Geo-coordinates

But dowloaders and indexers are relatively simple classes, so adding your own shouldn't take
too long. 

To assemble your desired index, you will then need to create a small "workflow" out of the various
downloaders and indexers. E.g. in the form of a simple executable class, a Groovy script, etc.

The executable class ``at.ait.dme.dpedix.maintenance.InitializeIndex`` will give you an example
on how to get started.

A word of caution: DBpedia dumps are large. Be prepared that indexing may take some time.
__Example:__ indexing English and German labels, plus English short abstracts and the 
geo-coordinates from the English-language dump (that amounts to about 7.9 million DBpedia
records in total!) takes approx. 35 minutes on my development machine.

## Querying the Index

To query the index from your own Java code, get hold of the ``at.ait.dme.dpedix.DBpediaIndexReader``.
(It's a thread-safe singleton, so use the static ``getInstance()`` method to do this.) The reader's
most important method is ``.findResources(String query, String lang, int limit)``. It will return a
list of ``DBpediaResource``s, based on the query term and the provided language code. The ``limit``
parameter specifies the maximum number of resources that should be returned as result.

The executable class ``at.ait.dme.dpedix.maintenance.QueryConsole`` provides a simple text-based
command-line interface to query your index for testing purposes. Simply type your query in the
form 

     query:lang

E.g. ``vienna:en`` will search for 'vienna' in the English-language labels; ``wien:de`` will search
in the German-language labels. If you omit the language postfix (i.e. type ``vienna``), English will
be assumed by the QueryConsole by default.

Type ``quit`` to exit the QueryConsole