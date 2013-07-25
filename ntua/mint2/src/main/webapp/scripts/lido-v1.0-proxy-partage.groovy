mappingHandler = new JSONMappingHandler(mapping);
template = new JSONMappingHandler(mapping.getJSONObject("template"));

schemaId = template.getHandlersForPath("/lido/lidoRecID").get(0);
schemaId.addConstantMapping("/" + Config.get("mint.title") + ":000000");
schemaId.setFixed(true);
schemaIdType = schemaId.getAttribute("@lido:type");
schemaIdType.addConstantMapping(Config.get("mint.title"));
schemaIdType.setFixed(true);

template.getChild("lido:descriptiveMetadata").getAttribute("@xml:lang").setMandatory(true);
template.getChild("lido:administrativeMetadata").getAttribute("@xml:lang").setMandatory(true);

// create europeana classification
europeanaClassification = template.duplicatePath("/lido/descriptiveMetadata/objectClassificationWrap/classificationWrap/classification");
europeanaGroupClassification = template.duplicatePath("/lido/descriptiveMetadata/objectClassificationWrap/classificationWrap/classification");
generalClassification = template.duplicatePath("/lido/descriptiveMetadata/objectClassificationWrap/classificationWrap/classification");
styleClassification = template.duplicatePath("/lido/descriptiveMetadata/objectClassificationWrap/classificationWrap/classification");

europeanaClassification.setLabel("classification (europeana)");
europeanaType = europeanaClassification.getAttribute("@lido:type")
	.addConstantMapping("europeana:type");
europeanaTerm = europeanaClassification.getChild("lido:term")
	.addEnumeration("IMAGE")
	.addEnumeration("SOUND")
	.addEnumeration("TEXT")
	.addEnumeration("VIDEO")
	.addEnumeration("3D")
	.setMandatory(true)

europeanaGroupClassification.setLabel("classification (europeana group)");
europeanaType = europeanaGroupClassification.getAttribute("@lido:type")
	.addConstantMapping("Europeana Group")
europeanaTerm = europeanaClassification.getChild("lido:term")
	.addConstantMapping("Partage Plus")
	.setFixed(true);

generalClassification.setLabel("classification (general)");
generalType = generalClassification.getAttribute("@lido:type")
	.addConstantMapping("general");

styleClassification.setLabel("classification (style)");
styleType = styleClassification.getAttribute("@lido:type")
	.addConstantMapping("style");

// europeana record source
recordInfoLink =  template.getHandlersForName("recordInfoSet").get(0).getHandlersForName("recordInfoLink").get(0)
	.setMandatory(true);

originalRecordSource = template.getHandlersForName("recordSource").get(0);
recordSource = template.duplicatePath("/lido/administrativeMetadata/recordWrap/recordSource")
	.setLabel("recordSource (europeana)")
recordSourceType = recordSource.getAttribute("@lido:type")
	.addConstantMapping("europeana:dataProvider")
	.setFixed(true)
recordSourceAppellation = recordSource.getChild("lido:legalBodyName")
	.setMandatory(true);
originalRecordSource.setString(JSONMappingHandler.ELEMENT_MINOCCURS, "0");

// create master & thumb resource, resource rights
resource = template.duplicatePath("/lido/administrativeMetadata/resourceWrap/resourceSet");
resource.setLabel("resourceSet (europeana)");

master = template.duplicatePath("/lido/administrativeMetadata/resourceWrap/resourceSet/resourceRepresentation");
master.setLabel("resourceRepresentation (master)");
master.setRemovable(true);

thumb = template.duplicatePath("/lido/administrativeMetadata/resourceWrap/resourceSet/resourceRepresentation");
thumb.setLabel("resourceRepresentation (thumb)");
thumb.setRemovable(true);
rights = resource.getChild("lido:rightsResource");
rights.setLabel("rightsResource (europeana)");
rights.setMandatory(true);

linkResource = master.getChild("lido:linkResource");
linkResource.setLabel("linkResource (master)");
linkType = master.getAttribute("@lido:type");
linkType.addConstantMapping("image_master");
linkType.setFixed(true);

linkResource = thumb.getChild("lido:linkResource");
linkResource.setLabel("linkResource (thumb)");
linkType = thumb.getAttribute("@lido:type");
linkType.addConstantMapping("image_thumb");
linkType.setFixed(true);

rightsType = rights.getChild("lido:rightsType");
rightsType.setLabel("rightsType (europeana)");
rightsType = rightsType.getChild("lido:term");
rightsType.setLabel("term (europeana)");
rightsType.setMandatory(true);
rightsType.addEnumeration("http://www.europeana.eu/rights/rr-f/");
rightsType.addEnumeration("http://www.europeana.eu/rights/rr-p/");
rightsType.addEnumeration("http://www.europeana.eu/rights/rr-r/");
rightsType.addEnumeration("http://www.europeana.eu/rights/unknown/");
rightsType.addEnumeration("http://creativecommons.org/licenses/publicdomain/mark/");
rightsType.addEnumeration("http://creativecommons.org/licenses/publicdomain/zero/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-sa/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nc/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nc-sa/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nd/");
rightsType.addEnumeration("http://creativecommons.org/licenses/by-nc-nd/");

// rights work set
recordRights = template.duplicatePath("/lido/administrativeMetadata/recordWrap/recordRights");
recordRights.setLabel("recordRights (europeana)");
rightsType = recordRights.getChild("lido:rightsType").getChild("lido:term");
rightsType.addEnumeration("CC0");
rightsType.addEnumeration("CC0 (no descriptions)");
rightsType.addEnumeration("CC0 (mandatory only)");

// partage repository
repositorySet = template.duplicatePath("/lido/descriptiveMetadata/objectIdentificationWrap/repositoryWrap/repositorySet");
repositorySet.setLabel("repositorySet (current)");
repositoryType = repositorySet.getAttribute("@lido:type");
repositoryType.addConstantMapping("current");
repositoryType.setFixed(true);

eventTypes = template.getHandlersForName("eventType");
for(eventType in eventTypes) {
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

eventSet = template.duplicatePath("/lido/descriptiveMetadata/eventWrap/eventSet").setLabel("eventSet (Production)");
eventSet.getChild("lido:event").getChild("lido:eventType").getChild("lido:conceptID").addConstantMapping("http://terminology.lido-schema.org/lido00007");
eventSet.setRemovable(true);
