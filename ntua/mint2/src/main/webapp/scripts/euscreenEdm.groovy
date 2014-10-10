//Script

//Set predefined values for rights
rightElements = template.find("//edm:rights/@rdf:resource");
for(Element rights: rightElements) {
    rights.addEnumeration("http://www.europeana.eu/rights/rr-f/");
    rights.addEnumeration("http://www.europeana.eu/rights/rr-f/");
	rights.addEnumeration("http://www.europeana.eu/rights/rr-p/");
	rights.addEnumeration("http://www.europeana.eu/rights/rr-r/");
	rights.addEnumeration("http://www.europeana.eu/rights/unknown/");
	rights.addEnumeration("http://creativecommons.org/publicdomain/mark/1.0/");
	rights.addEnumeration("http://creativecommons.org/publicdomain/zero/1.0/");
	rights.addEnumeration("http://creativecommons.org/licenses/by/3.0/");
	rights.addEnumeration("http://creativecommons.org/licenses/by-sa/3.0/");
	rights.addEnumeration("http://creativecommons.org/licenses/by-nc/3.0/");
	rights.addEnumeration("http://creativecommons.org/licenses/by-nc-sa/3.0/");
	rights.addEnumeration("http://creativecommons.org/licenses/by-nd/3.0/");
	rights.addEnumeration("http://creativecommons.org/licenses/by-nc-nd/3.0/");
}

provider = template.findFirst("//edm:provider");
provider.addConstantMapping("EUscreenXL");
provider.setFixed(true);

//Add enum to data provider
dataPr = template.findFirst("//edm:dataProvider");
dataPr.addEnumeration("UU");
dataPr.addEnumeration("NISV");
dataPr.addEnumeration("RHUL");
dataPr.addEnumeration("BUFVC");
dataPr.addEnumeration("LUCE");
dataPr.addEnumeration("ELTE");
dataPr.addEnumeration("KB");
dataPr.addEnumeration("Noterik");
dataPr.addEnumeration("AALTO");
dataPr.addEnumeration("EBU");
dataPr.addEnumeration("ATiT");
dataPr.addEnumeration("RTV SLO");
dataPr.addEnumeration("DW");
dataPr.addEnumeration("RTBF");
dataPr.addEnumeration("CT");
dataPr.addEnumeration("TVC");
dataPr.addEnumeration("MU");
dataPr.addEnumeration("TVR");
dataPr.addEnumeration("ORF");
dataPr.addEnumeration("LCVA");
dataPr.addEnumeration("RTP");
dataPr.addEnumeration("INA");
dataPr.addEnumeration("TVP");
dataPr.addEnumeration("NINA");
dataPr.addEnumeration("SASE");
dataPr.addEnumeration("RTE");
dataPr.addEnumeration("VRT");
dataPr.addEnumeration("DR");
dataPr.addEnumeration("NAVA");

//Add enum to publisher
publisher = template.findFirst("edm:ProvidedCHO/dc:publisher");


language = template.findFirst("edm:ProvidedCHO/dc:language");
language.setThesaurus(MappingPrimitives.vocabulary("http://mint.image.ece.ntua.gr/Vocabularies/Languages/LangThesaurus","http://www.w3.org/2004/02/skos/core#prefLabel"));

//Bookmarks
mappings.addBookmark("Title", template.findFirst("edm:ProvidedCHO/dc:title"));
mappings.addBookmark("Alternative Title", template.findFirst("edm:ProvidedCHO/dcterms:alternative"));

mappings.addBookmark("Creator", template.findFirst("edm:ProvidedCHO/dc:creator"));
mappings.addBookmark("Contributor", template.findFirst("edm:ProvidedCHO/dc:contributor"));
mappings.addBookmark("Date", template.findFirst("edm:ProvidedCHO/dc:date"));
mappings.addBookmark("Creation Date", template.findFirst("edm:ProvidedCHO/dcterms:created"));
mappings.addBookmark("Broadcast date", template.findFirst("edm:ProvidedCHO/dcterms:issued"));
mappings.addBookmark("Language", template.findFirst("edm:ProvidedCHO/dc:language"));
mappings.addBookmark("Temporal", template.findFirst("edm:ProvidedCHO/dcterms:temporal"));
mappings.addBookmark("Publisher/Broadcaster", template.findFirst("edm:ProvidedCHO/dc:publisher"));
mappings.addBookmark("Medium", template.findFirst("edm:ProvidedCHO/dcterms:medium"));
mappings.addBookmark("Duration", template.findFirst("edm:ProvidedCHO/dcterms:extent"));
mappings.addBookmark("Provider Rights", template.findFirst("edm:ProvidedCHO/dc:rights"));
mappings.addBookmark("Description", template.findFirst("edm:ProvidedCHO/dc:description"));
mappings.addBookmark("Subject", template.findFirst("edm:ProvidedCHO/dc:subject"));
mappings.addBookmark("Genre", template.findFirst("edm:ProvidedCHO/dc:type"));
mappings.addBookmark("Coverage", template.findFirst("edm:ProvidedCHO/dc:coverage"));
mappings.addBookmark("Spatial", template.findFirst("edm:ProvidedCHO/dcterms:spatial"));
mappings.addBookmark("Material Type", template.findFirst("//edm:type"));
mappings.addBookmark("Provider", dataPr);
//mappings.addBookmark("Publisher", publisher);
mappings.addBookmark("Landing page URL", template.findFirst("ore:Aggregation/edm:isShownAt/@rdf:resource"));
mappings.addBookmark("Link to image thumb", template.findFirst("ore:Aggregation/edm:object/@rdf:resource"));
mappings.addBookmark("Digital item URL", template.findFirst("ore:Aggregation/edm:isShownBy/@rdf:resource"));
mappings.addBookmark("Other views", template.findFirst("ore:Aggregation/edm:hasView/@rdf:resource"));
mappings.addBookmark("Rights", template.findFirst("ore:Aggregation/edm:rights/@rdf:resource"));
mappings.addBookmark("Language", language);


//**** Vocabulary method does not exist
//Enumerated xml langs
handlers = template.find("//@xml:lang");
for(Element handler: handlers) {
  handler.setThesaurus(MappingPrimitives.vocabulary("http://mint.image.ece.ntua.gr/Vocabularies/Languages/LangThesaurus"));
}

//handlers = template.find("//skos:Concept/@rdf:about")
//for(Element conceptID: handlers) {
  //  conceptID.setThesaurus(MappingPrimitives.thesaurus("http://bib.arts.kuleuven.be/photoVocabulary/Photographic_practice"));
//}