//Comments
// ****
// **** Vocabulary method does not exist
// - next to structural
// Search input and target schema 
// Solr???
// Mandatory
// 



// Set project title to lidoRecID
schemaId = template.getChild("lido:lidoRecID");
schemaId.addConstantMapping("/" + Config.get("mint.title") + ":000000");
schemaId.setFixed(true);
schemaIdType = schemaId.getAttribute("@lido:type");
schemaIdType.addConstantMapping(Config.get("mint.title"));
schemaIdType.setFixed(true);

// Make the lang title mandatory
template.getChild("lido:descriptiveMetadata").getAttribute("@xml:lang").setMandatory(true);
template.getChild("lido:administrativeMetadata").getAttribute("@xml:lang").setMandatory(true);

// Duplicate classificatioType and add the Europeana enumerations
europeanaClassification = cache.duplicate(template.findFirst("lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification").getId());
europeanaClassification.setLabel("classification (europeana)");
europeanaType = europeanaClassification.getAttribute("@lido:type").addConstantMapping("europeana:type");
europeanaTerm = europeanaClassification.getChild("lido:term")
    .addEnumeration("IMAGE")
	.addEnumeration("SOUND")
	.addEnumeration("TEXT")
	.addEnumeration("VIDEO")
	.addEnumeration("3D")
	.setMandatory(true)
   
// Make the recordInfoLink mandatory
recordInfoLink =  template.findFirst("//lido:recordInfoSet/lido:recordInfoLink").setMandatory(true); //****It is not made MANDATORY

// Add the record type "Photography"
recordType = template.findFirst("//lido:recordType/lido:term").addConstantMapping("Photography");//**** ????

// Make the europeana data provider mandatory
originalRecordSource = template.findFirst("//lido:recordSource");
recordSource = cache.duplicate(template.findFirst("//lido:administrativeMetadata/lido:recordWrap/lido:recordSource").getId());
recordSource.setLabel("recordSource (europeana)");
recordSourceType = recordSource.getAttribute("@lido:type").addConstantMapping("europeana:dataProvider").setFixed(true)
recordSourceAppellation = recordSource.getChild("lido:legalBodyName").setMandatory(true);
originalRecordSource.setString(JSONMappingHandler.ELEMENT_MINOCCURS, "0");

// Set europeana resource set
resource = cache.duplicate(template.findFirst("//lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet").getId());
resource.setLabel("resourceSet (europeana)");

// 	Set image thumb and image master
master = cache.duplicate(template.findFirst("//lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation").getId());
master.setLabel("resourceRepresentation (master)");
master.setRemovable(true);
thumb = cache.duplicate(template.findFirst("//lido:administrativeMetadata/lido:resourceWrap/lido:resourceSet/lido:resourceRepresentation").getId());
thumb.setLabel("resourceRepresentation (thumb)");
thumb.setRemovable(true);

linkResource = master.getChild("lido:linkResource");
linkResource.setLabel("linkResource (master)");
linkType = master.getAttribute("@lido:type");
linkType.addConstantMapping("image_master");
linkType.setFixed(true);
linkResourceMaster = linkResource;

linkResource = thumb.getChild("lido:linkResource");
linkResource.setLabel("linkResource (thumb)");
linkType = thumb.getAttribute("@lido:type");
linkType.addConstantMapping("image_thumb");
linkType.setFixed(true);
linkResourceThumb = linkResource;

// 	Set rights
rights = resource.getChild("lido:rightsResource");
rights.setLabel("rightsResource (europeana)");
rights.setMandatory(true);

rightsType = rights.getChild("lido:rightsType");
rightsType.setLabel("rightsType (europeana)");
rightsType = rightsType.getChild("lido:term");
rightsType.setLabel("term (europeana)");
rightsType.getAttribute("@lido:pref").addConstantMapping("preferred");
rightsType.setMandatory(true);
rightsType.addEnumeration("http://www.europeana.eu/rights/rr-f/");
rightsType.addEnumeration("http://www.europeana.eu/rights/rr-p/");
rightsType.addEnumeration("http://www.europeana.eu/rights/rr-r/");
rightsType.addEnumeration("http://www.europeana.eu/rights/unknown/");
rightsType.addEnumeration("http://creativecommons.org/publicdomain/mark/1.0/");
rightsType.addEnumeration("http://creativecommons.org/publicdomain/zero/1.0/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by/3.0/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-sa/3.0/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nc/3.0/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nc-sa/3.0/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nd/3.0/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nc-nd/3.0/");
rightsTypeEuropeana = rightsType;

// Set Rights work set
recordRights = cache.duplicate(template.findFirst("//lido:administrativeMetadata/lido:recordWrap/lido:recordRights").getId());
recordRights.setLabel("recordRights (europeana)");
rightsType = recordRights.getChild("lido:rightsType").getChild("lido:term");
rightsType.addEnumeration("CC0");
rightsType.addEnumeration("CC0 (no descriptions)");
rightsType.addEnumeration("CC0 (mandatory only)");

// Add the event types
eventTypes = template.find("//lido:eventType"); //***** de douleuei
for(Element eventType : eventTypes) {
    conceptID = eventType.getChild("lido:conceptID")
        .addEnumeration("http://terminology.lido-schema.org/lido00001","http://terminology.lido-schema.org/lido00001 - Acquisition")
        .addEnumeration("http://terminology.lido-schema.org/lido00010","http://terminology.lido-schema.org/lido00010 - Collecting")
        .addEnumeration("http://terminology.lido-schema.org/lido00226","http://terminology.lido-schema.org/lido00226 - Commissioning")
        .addEnumeration("http://terminology.lido-schema.org/lido00012","http://terminology.lido-schema.org/lido00012 - Creation")
        .addEnumeration("http://terminology.lido-schema.org/lido00224","http://terminology.lido-schema.org/lido00224 - Designing")
        .addEnumeration("http://terminology.lido-schema.org/lido00026","http://terminology.lido-schema.org/lido00026 - Destruction")
        .addEnumeration("http://terminology.lido-schema.org/lido00033","http://terminology.lido-schema.org/lido00033 - Excavation")
        .addEnumeration("http://terminology.lido-schema.org/lido00225","http://terminology.lido-schema.org/lido00225 - Exhibition")
        .addEnumeration("http://terminology.lido-schema.org/lido00002","http://terminology.lido-schema.org/lido00002 - Finding")
        .addEnumeration("http://terminology.lido-schema.org/lido00009","http://terminology.lido-schema.org/lido00009 - Loss")
        .addEnumeration("http://terminology.lido-schema.org/lido00006","http://terminology.lido-schema.org/lido00006 - Modification")
        .addEnumeration("http://terminology.lido-schema.org/lido00223","http://terminology.lido-schema.org/lido00223 - Move")
        .addEnumeration("http://terminology.lido-schema.org/lido00008","http://terminology.lido-schema.org/lido00008 - Part addition")
        .addEnumeration("http://terminology.lido-schema.org/lido00021","http://terminology.lido-schema.org/lido00021 - Part removal")
        .addEnumeration("http://terminology.lido-schema.org/lido00030","http://terminology.lido-schema.org/lido00030 - Performance")
        .addEnumeration("http://terminology.lido-schema.org/lido00032","http://terminology.lido-schema.org/lido00032 - Planning")
        .addEnumeration("http://terminology.lido-schema.org/lido00007","http://terminology.lido-schema.org/lido00007 - Production")
        .addEnumeration("http://terminology.lido-schema.org/lido00227","http://terminology.lido-schema.org/lido00227 - Provenance")
        .addEnumeration("http://terminology.lido-schema.org/lido00228","http://terminology.lido-schema.org/lido00228 - Publication")
        .addEnumeration("http://terminology.lido-schema.org/lido00034","http://terminology.lido-schema.org/lido00034 - Restoration")
        .addEnumeration("http://terminology.lido-schema.org/lido00029","http://terminology.lido-schema.org/lido00029 - Transformation")
        .addEnumeration("http://terminology.lido-schema.org/lido00023","http://terminology.lido-schema.org/lido00023 - Type assignment")
        .addEnumeration("http://terminology.lido-schema.org/lido00013","http://terminology.lido-schema.org/lido00013 - Type creation")
        .addEnumeration("http://terminology.lido-schema.org/lido00011","http://terminology.lido-schema.org/lido00011 - Use")
        .addEnumeration("http://terminology.lido-schema.org/lido00003","http://terminology.lido-schema.org/lido00003 - (Non-specified)")
}

// Set event Creation
eventSetCreation = cache.duplicate(template.findFirst("//lido:descriptiveMetadata/lido:eventWrap/lido:eventSet").getId());
eventSetCreation.setLabel("eventSet (Creation)");
eventSetConceptID = eventSetCreation.getChild("lido:event").getChild("lido:eventType").getChild("lido:conceptID");
eventSetConceptID.addConstantMapping("http://terminology.lido-schema.org/lido00012");
eventSetConceptID.getAttribute("@lido:type").addConstantMapping("URI");
eventSetTerm = eventSetCreation.getChild("lido:event").getChild("lido:eventType").getChild("lido:term");
eventSetTerm.addConstantMapping("Creation");
eventSetTerm.getAttribute("@xml:lang").addConstantMapping("en");
eventSetCreation.setRemovable(true);

// Init elements for bookmarks
eventDate = eventSetCreation.findFirst("lido:event/lido:eventDate/lido:date/lido:earliestDate");
eventAuthor = eventSetCreation.findFirst("lido:event/lido:eventActor/lido:actorInRole/lido:actor/lido:nameActorSet/lido:appellationValue");
eventTechnique = eventSetCreation.findFirst("lido:event/lido:eventMethod");
eventPlace = eventSetCreation.findFirst("lido:event/lido:eventPlace/lido:place");
eventPractice = eventSetCreation.findFirst("lido:event/lido:eventDescriptionSet/lido:descriptiveNoteID");
eventMaterial = eventSetCreation.findFirst("lido:event/lido:eventMaterialsTech/lido:materialsTech/lido:termMaterialsTech");
eventMaterial.getAttribute("@lido:type").addConstantMapping("material");

// Object Work Type
//objectWorkType = template.findFirst("//lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType");
//objectWorkType.getChild("lido:term").addConstantMapping("Ancient Photography"); 

// Photography
workType = cache.duplicate(template.findFirst("//lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType").getId());
workType.setLabel("objectWorkType (Photography)");
workType.getChild("lido:term").setFixed(true).addConstantMapping("Photography");


// Bookmarks
mappings.addBookmarkForXpath("Identifier", "/lido/administrativeMetadata/recordWrap/recordID");
mappings.addBookmarkForXpath("Descriptive metadata language", "/lido/descriptiveMetadata/@lang");
mappings.addBookmarkForXpath("Administrative metadata language", "/lido/administrativeMetadata/@lang");
mappings.addBookmark("Link to Metadata", recordInfoLink);
mappings.addBookmark("Link to DCHO", linkResourceMaster);
mappings.addBookmark("Link to DCHO (thumbnail)", linkResourceThumb);

mappings.addBookmark("Provider", recordSourceAppellation);
mappings.addBookmark("Europeana Type", europeanaTerm);
mappings.addBookmark("Europeana Rights", rightsTypeEuropeana);
mappings.addBookmarkForXpath("Title", "/lido/descriptiveMetadata/objectIdentificationWrap/titleWrap/titleSet/appellationValue");
mappings.addBookmark("Date", eventDate);
mappings.addBookmark("Author", eventAuthor);
mappings.addBookmark("Technique", eventTechnique);
mappings.addBookmark("Place", eventPlace);
mappings.addBookmark("Photographic practice", eventPractice);
mappings.addBookmark("Material", eventMaterial);
mappings.addBookmarkForXpath("Description", "/lido/descriptiveMetadata/objectIdentificationWrap/objectDescriptionWrap/objectDescriptionSet/descriptiveNoteValue");
mappings.addBookmarkForXpath("Copyright", "/lido/administrativeMetadata/rightsWorkWrap/rightsWorkSet/rightsHolder/legalBodyName/appellationValue");
mappings.addBookmarkForXpath("Subject concept", "/lido/descriptiveMetadata/objectRelationWrap/subjectWrap/subjectSet/subject/subjectConcept");
mappings.addBookmarkForXpath("Subject actor", "/lido/descriptiveMetadata/objectRelationWrap/subjectWrap/subjectSet/subject/subjectActor");
mappings.addBookmarkForXpath("Subject place", "/lido/descriptiveMetadata/objectRelationWrap/subjectWrap/subjectSet/subject/subjectPlace");
mappings.addBookmarkForXpath("Dimensions", "/lido/descriptiveMetadata/objectIdentificationWrap/objectMeasurementsWrap/objectMeasurementsSet");
mappings.addBookmarkForXpath("Related Works", "/lido/descriptiveMetadata/objectRelationWrap/relatedWorksWrap/relatedWorkSet/relatedWork/object/objectNote");


// Vocabs
handlers = template.find("//lido:eventMethod/lido:conceptID")
for(Element conceptID: handlers) {
    conceptID.setThesaurus(MappingPrimitives.thesaurus("http://bib.arts.kuleuven.be/photoVocabulary/Technique"));
}

handlers = template.find("//lido:subjectConcept/lido:conceptID")
for(Element conceptID: handlers) {
    conceptID.setThesaurus(MappingPrimitives.thesaurus("http://bib.arts.kuleuven.be/photoVocabulary/Photographic_practice"));
}

//**** Vocabulary method does not exist
//Enumerated xml langs
//handlers = template.find("//@xml:lang");
//for(Element handler: handlers) {
  //  handler.setThesaurus(MappingPrimitives.vocabulary("http://mint.image.ece.ntua.gr/Vocabularies/Languages/LangThesaurus"));
//}
