identifier = template.findFirst("//eus:identifier").addConstantMapping("EUS_00000000000000000000000000000000").setFixed(true);
recordType = template.findFirst("//eus:recordType").addConstantMapping("SERIES/COLLECTION").setFixed(true);

////////////////////
// Bookmarks
////////////////////
mappings.addBookmarkForXpath("Identifier", "/metadata/AdministrativeMetadata/identifier");
mappings.addBookmarkForXpath("Series/collection title", "/metadata/eus:ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/seriesOrCollectionTitle");
mappings.addBookmarkForXpath("Series/collection title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/seriesOrCollectionTitle");
mappings.addBookmarkForXpath("Summary", "/metadata/ContentDescriptiveMetadata/summary");
mappings.addBookmarkForXpath("Summary in English", "/metadata/ContentDescriptiveMetadata/summaryInEnglish");
mappings.addBookmarkForXpath("Provider", "/metadata/AdministrativeMetadata/provider");
mappings.addBookmarkForXpath("Publisher/Broadcaster", "/metadata/AdministrativeMetadata/publisherbroadcaster");
mappings.addBookmarkForXpath("Broadcast Date (mandatory if broadcast)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/broadcastDate");
mappings.addBookmarkForXpath("Production Year", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/productionYear");
mappings.addBookmarkForXpath("Material Type", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/materialType");

//Optional
mappings.addBookmarkForXpath("First Broadcast Channel (opt)", "/metadata/AdministrativeMetadata/firstBroadcastChannel");
mappings.addBookmarkForXpath("Extended Description (opt)", "/metadata/ContentDescriptiveMetadata/extendedDescription");
mappings.addBookmarkForXpath("Information (opt)", "/metadata/ObjectDescriptiveMetadata/information");
mappings.addBookmarkForXpath("Last Broadcast Date (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/lastBroadCastDate");
mappings.addBookmarkForXpath("Last Production Year (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/lastProductionYear");
mappings.addBookmarkForXpath("Original Language (opt)", "/metadata/ContentDescriptiveMetadata/originallanguage");
mappings.addBookmarkForXpath("Language used (opt)", "metadata/ObjectDescriptiveMetadata/LanguageInformation/languageUsed");
mappings.addBookmarkForXpath("Local Keywords (opt)", "/metadata/ContentDescriptiveMetadata/LocalKeyword");
mappings.addBookmarkForXpath("Contributpor (opt)", "metadata/ObjectDescriptiveMetadata/contributor");
mappings.addBookmarkForXpath("Metadata Language (opt)", "metadata/ObjectDescriptiveMetadata/LanguageInformation/metadatalanguage");
mappings.addBookmarkForXpath("Landing Page URL (opt)", "/metadata/AdministrativeMetadata/landingPageURL");
