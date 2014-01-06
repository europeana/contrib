// assign thesaurus
for(Element element: mappings.find("//dc:type/@rdf:about")) {
	element.setThesaurus(MappingPrimitives.thesaurus("http://thesaurus.europeanafashion.eu/thesaurus/Type"));
}

// assign thesaurus
for(Element element: mappings.find("//dcterms:medium/@rdf:resource")) {
	element.setThesaurus(MappingPrimitives.thesaurus("http://thesaurus.europeanafashion.eu/thesaurus/Materials"));
}

// assign thesaurus
for(Element element: mappings.find("//gr:color/@rdf:about")) {
	element.setThesaurus(MappingPrimitives.thesaurus("http://thesaurus.europeanafashion.eu/thesaurus/Colours"));
}

// assign thesaurus
for(Element element: mappings.find("//edmfp:technique/@rdf:resource")) {
	element.setThesaurus(MappingPrimitives.thesaurus("http://thesaurus.europeanafashion.eu/thesaurus/Techniques"));
}

// assign thesaurus
for(Element element: mappings.find("//dc:subject/@rdf:resource")) {
	element.setThesaurus(MappingPrimitives.thesaurus("http://thesaurus.europeanafashion.eu/thesaurus/Subject"));
}

// set labels for mrel names
handlers = template.getHandlersForName("aut");
for(Element handler: handlers) { handler.setLabel("aut (author)"); }

handlers = template.getHandlersForName("clb");
for(Element handler: handlers) { handler.setLabel("clb (collaborator)"); }

handlers = template.getHandlersForName("cur");
for(Element handler: handlers) { handler.setLabel("cur (curator)"); }

handlers = template.getHandlersForName("drt");
for(Element handler: handlers) { handler.setLabel("drt (director)"); }

handlers = template.getHandlersForName("dsr");
for(Element handler: handlers) { handler.setLabel("dsr (designer)"); }

handlers = template.getHandlersForName("edt");
for(Element handler: handlers) { handler.setLabel("edt (editor)"); }

handlers = template.getHandlersForName("ill");
for(Element handler: handlers) { handler.setLabel("ill (illustrator)"); }

handlers = template.getHandlersForName("ive");
for(Element handler: handlers) { handler.setLabel("ive (interviewee)"); }

handlers = template.getHandlersForName("ivr");
for(Element handler: handlers) { handler.setLabel("ivr (interviewer)"); }

handlers = template.getHandlersForName("pht");
for(Element handler: handlers) { handler.setLabel("pht (photographer)"); }

handlers = template.getHandlersForName("pro");
for(Element handler: handlers) { handler.setLabel("pro (producer)"); }

handlers = template.getHandlersForName("sds");
for(Element handler: handlers) { handler.setLabel("sds (sound designer)"); }

handlers = template.getHandlersForName("spn");
for(Element handler: handlers) { handler.setLabel("spn (sponsor)"); }

handlers = template.getHandlersForName("std");
for(Element handler: handlers) { handler.setLabel("std (set designer)"); }

edmProvider = template.getHandlersForPrefixAndName("edm", "provider").get(0).getChild("edm:Agent");
edmProviderRes = edmProvider.getAttribute("@rdf:about");
edmProviderRes.addConstantMapping("http://www.europeanafashion.eu/").setFixed(true);
edmProviderLabel = edmProvider.getChild("skos:prefLabel");
edmProviderLabel.addConstantMapping("Europeana Fashion");
edmProviderLabel.setFixed(true);