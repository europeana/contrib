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

//Create europeana classification
recordSource = cache.duplicate(template.findFirst("//lido:administrativeMetadata/lido:recordWrap/lido:recordSource").getId());

// create europeana classification
europeanaClassification = cache.duplicate(template.findFirst("lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification").getId());
europeanaGroupClassification = cache.duplicate(template.findFirst("lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification").getId());

// Duplicate classificatioType and add the Europeana enumerations
europeanaClassification.setLabel("classification (europeana)");
europeanaType = europeanaClassification.getAttribute("@lido:type").addConstantMapping("europeana:type");
europeanaTerm = europeanaClassification.getChild("lido:term")
    .addEnumeration("IMAGE")
	.addEnumeration("SOUND")
	.addEnumeration("TEXT")
	.addEnumeration("VIDEO")
	.addEnumeration("3D")
	.setMandatory(true)
	
europeanaGroupClassification.setLabel("classification (europeana:project)");
europeanaType = europeanaGroupClassification.getAttribute("@lido:type").addConstantMapping("europeana:project");
europeanaGroupClassificationTerm = europeanaGroupClassification.getChild("lido:term");
europeanaGroupClassificationTerm.addConstantMapping("Athena Plus");
europeanaGroupClassificationTerm.setFixed(true);

// Make the recordInfoLink mandatory
recordInfoLink =  template.findFirst("//lido:recordInfoSet/lido:recordInfoLink").setMandatory(true); //****It is not made MANDATORY

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
recRightsType = recordRights.getChild("lido:rightsType").getChild("lido:term");
recRightsType.addEnumeration("CC0");
recRightsType.addEnumeration("CC0 (no descriptions)");
recRightsType.addEnumeration("CC0 (mandatory only)");

// partage repository
repositorySet = cache.duplicate(template.findFirst("lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:repositoryWrap/lido:repositorySet").getId());
repositorySet.setLabel("repositorySet (current)");
repositoryType = repositorySet.getAttribute("@lido:type");
repositoryType.addConstantMapping("current");
repositoryType.setFixed(true);
repositoryName = repositorySet.getChild("lido:repositoryName").getChild("lido:legalBodyName").getChild("lido:appellationValue");
repositoryWorkID = repositorySet.getChild("lido:workID");
repositoryLocation = repositorySet.getChild("lido:repositoryLocation").getChild("lido:namePlaceSet").getChild("lido:appellationValue");

// Add the event types
eventTypes = template.find("//lido:eventType/lido:conceptID");
for(eventType in eventTypes) {
    eventType.setThesaurus(MappingPrimitives.thesaurus("http://terminology.lido-schema-types.org"));
}
//Original Event
event = template.findFirst("//lido:descriptiveMetadata/lido:eventWrap/lido:eventSet");

//Production event
eventSetProduction = cache.duplicate(template.findFirst("//lido:descriptiveMetadata/lido:eventWrap/lido:eventSet").getId());
eventSetProduction.setLabel("eventSet (Production)");

eventSetConceptID = eventSetProduction.findFirst("lido:event/lido:eventType/lido:conceptID");
eventSetConceptID.addConstantMapping("http://terminology.lido-schema.org/lido00007");
eventSetConceptID.getAttribute("@lido:type").addConstantMapping("URI");
eventSetProduction.setRemovable(true);
eventPrAuthor = eventSetProduction.findFirst("lido:event/lido:eventActor/lido:actorInRole/lido:actor");
eventPrAuthorForSkos = eventSetProduction.findFirst("lido:event/lido:eventActor/lido:actorInRole/lido:actor/lido:actorID");
eventPrDate = eventSetProduction.findFirst("lido:event/lido:eventDate/lido:date");
eventPrPlace = eventSetProduction.findFirst("lido:event/lido:eventPlace/lido:place");
eventPrPlaceName = eventSetProduction.findFirst("lido:event/lido:eventPlace/lido:place/lido:namePlaceSet/lido:appellationValue");




//Object Work Type
objectWorkType = cache.duplicate(template.findFirst("lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType").getId());
objectWorkType.setLabel("objectWorkType - Controlled by PP vocabulary")
objectWorkTypeForSkos = objectWorkType.findFirst("lido:conceptID");
objectWorkTypeForSkos.setThesaurus(MappingPrimitives.thesaurus("http://partage.vocnet.org/Objects"));

//Subject
subject = cache.duplicate(template.findFirst("lido:descriptiveMetadata/lido:objectRelationWrap/lido:subjectWrap/lido:subjectSet/lido:subject/lido:subjectConcept").getId());
subject.setLabel("subjectConcept - Controlled by EuPhoto vocabulary")
subjectForSkos = subject.findFirst("lido:conceptID");
subjectForSkos.setThesaurus(MappingPrimitives.thesaurus("http://bib.arts.kuleuven.be/photoVocabulary/Subject"));

//PPMaterial
eventPrMaterial = cache.duplicate(eventSetProduction.findFirst("lido:event/lido:eventMaterialsTech/lido:materialsTech/lido:termMaterialsTech").getId());
eventPrMaterial.getAttribute("@lido:type").addConstantMapping("material");
eventPrMaterialForSkos = eventPrMaterial.findFirst("lido:conceptID");
eventPrMaterialForSkos.setThesaurus(MappingPrimitives.thesaurus("http://partage.vocnet.org/Materials"));

//PPTechnique
eventPrTechnique = cache.duplicate(eventSetProduction.findFirst("lido:event/lido:eventMaterialsTech/lido:materialsTech/lido:termMaterialsTech").getId());
eventPrTechnique.getAttribute("@lido:type").addConstantMapping("technique");
eventPrTechniqueForSkos = eventPrTechnique.findFirst("lido:conceptID");
eventPrTechniqueForSkos.setThesaurus(MappingPrimitives.thesaurus("http://partage.vocnet.org/Activities"));


//Actor Role
eventPrAuthorRole = eventSetProduction.findFirst("lido:event/lido:eventActor/lido:actorInRole/lido:roleActor");
eventPrAuthorRoleForSkos = eventSetProduction.findFirst("lido:event/lido:eventActor/lido:actorInRole/lido:roleActor/lido:conceptID");
eventPrAuthorRoleForSkos.setThesaurus(MappingPrimitives.thesaurus("http://terminology.lido-schema.org"));

////////////////////
// Bookmarks
////////////////////
mappings.addBookmarkForXpath("Identifier", "/lido/administrativeMetadata/recordWrap/recordID");
mappings.addBookmarkForXpath("Descriptive metadata language", "/lido/descriptiveMetadata/@lang");
mappings.addBookmarkForXpath("Administrative metadata language", "/lido/administrativeMetadata/@lang");


mappings.addBookmark("Europeana type", europeanaClassification);

mappings.addBookmark("Object/Work Type", template.find("lido:descriptiveMetadata/lido:objectClassificationWrap/lido:objectWorkTypeWrap/lido:objectWorkType").get(1));
mappings.addBookmark("Object/Work Type (PP vocabulary)", objectWorkType);
mappings.addBookmarkForXpath("Object Title/Name", "/lido/descriptiveMetadata/objectIdentificationWrap/titleWrap/titleSet/appellationValue");
mappings.addBookmarkForXpath("Object Description","/lido/descriptiveMetadata/objectIdentificationWrap/objectDescriptionWrap/objectDescriptionSet/descriptiveNoteValue");


mappings.addBookmarkForXpath("Dimensions", "/lido/descriptiveMetadata/objectIdentificationWrap/objectMeasurementsWrap/objectMeasurementsSet");
 

mappings.addBookmark("Production Event",eventSetProduction); 
mappings.addBookmark("- Producer", eventPrAuthor);
mappings.addBookmark("- Producer's role", eventPrAuthorRole);
mappings.addBookmark("- Production Date",  eventPrDate);
mappings.addBookmark("- Production Place Name",  eventPrPlaceName);
mappings.addBookmark("- Material (Production Event - PP Vocab)",  eventPrMaterial); 
mappings.addBookmark("- Technique (Production Event - PP Vocab)", eventPrTechnique);

mappings.addBookmark("Other Event (type to specify)", event); 


mappings.addBookmark("Subject / Theme (Concept)", template.find("lido:descriptiveMetadata/lido:objectRelationWrap/lido:subjectWrap/lido:subjectSet/lido:subject/lido:subjectConcept").get(1)); 
mappings.addBookmark("Subject / Theme (Concept) (EuPhoto vocabulary)", subject);  

mappings.addBookmark("Repository",  repositorySet); 


mappings.addBookmarkForXpath("Rights Information for Work", "/lido/administrativeMetadata/rightsWorkWrap/rightsWorkSet");  
mappings.addBookmarkForXpath("Record Type", "/lido/administrativeMetadata/recordWrap/recordType"); 
mappings.addBookmarkForXpath("Record Source (Name)", "/lido/administrativeMetadata/recordWrap/recordSource/legalBodyName/appellationValue"); 
mappings.addBookmark("Record Link (Link to metadata)", recordInfoLink);

 
mappings.addBookmark("Link to DCHO (Master)",  linkResourceMaster); 
mappings.addBookmark("Link to DCHO (Thumbnail)", linkResourceThumb); 
 
mappings.addBookmark("Resource Rights Type", rightsType); 


handlers = template.find("//@xml:lang");
for(Element handler: handlers) {
  handler.setThesaurus(MappingPrimitives.vocabulary("http://mint.image.ece.ntua.gr/Vocabularies/Languages/LangThesaurus"));
}


