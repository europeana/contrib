identifier = template.findFirst("//eus:identifier").addConstantMapping("EUS_00000000000000000000000000000000").setFixed(true);
recordType = template.findFirst("//eus:recordType").addConstantMapping("SERIES/COLLECTION").setFixed(true);

//broadcastDate = cache.duplicate(template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:broadcastDate").getId());
//broadcastDate.setLabel("broadcastDate (dd/mm/yyyy)");
//productionYear = cache.duplicate(template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:productionYear").getId());
//productionYear.setLabel("productionYear (yyyy)");
//lastBroadcastDate = cache.duplicate(template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:lastBroadCastDate").getId());
//lastBroadcastDate.setLabel("lastBroadCastDate (dd/mm/yyyy)");
//lastProductionYear = cache.duplicate(template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:lastProductionYear").getId());
//lastProductionYear.setLabel("lastProductionYear (yyyy)");


broadcastDate = template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:broadcastDate");
broadcastDate.setLabel("broadcastDate (dd/mm/yyyy)");
productionYear = template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:productionYear");
productionYear.setLabel("productionYear (yyyy)");
lastBroadcastDate = template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:lastBroadCastDate");
lastBroadcastDate.setLabel("lastBroadCastDate (dd/mm/yyyy)");
lastProductionYear = template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:lastProductionYear");
lastProductionYear.setLabel("lastProductionYear (yyyy)");

////////////////////
// Bookmarks
////////////////////
//mappings.addBookmarkForXpath("Identifier", "/metadata/AdministrativeMetadata/identifier");
//mappings.addBookmarkForXpath("Series/collection title", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/seriesOrCollectionTitle");
//mappings.addBookmarkForXpath("Series/collection title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInEnglish/seriesOrCollectionTitle");
//mappings.addBookmarkForXpath("Alternative Series/collection title", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/alternativeSeriesOrCollectionTitle");
//mappings.addBookmarkForXpath("Alternative Series/collection title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInEnglish/alternativeSeriesOrCollectionTitle");
//mappings.addBookmarkForXpath("Summary", "/metadata/ContentDescriptiveMetadata/summary");
//mappings.addBookmarkForXpath("Summary in English", "/metadata/ContentDescriptiveMetadata/summaryInEnglish");
//mappings.addBookmarkForXpath("Provider", "/metadata/AdministrativeMetadata/provider");
//mappings.addBookmarkForXpath("Publisher/Broadcaster", "/metadata/AdministrativeMetadata/publisherbroadcaster");
//mappings.addBookmarkForXpath("Broadcast Date (mandatory if broadcast)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/broadcastDate");
//mappings.addBookmarkForXpath("Production Year", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/productionYear");
//mappings.addBookmarkForXpath("Material Type", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/materialType");


//Optional
//mappings.addBookmarkForXpath("First Broadcast Channel (opt)", "/metadata/AdministrativeMetadata/firstBroadcastChannel");
//mappings.addBookmarkForXpath("Extended Description (opt)", "/metadata/ContentDescriptiveMetadata/extendedDescription");
//mappings.addBookmarkForXpath("Information (opt)", "/metadata/ObjectDescriptiveMetadata/information");
//mappings.addBookmarkForXpath("Last Broadcast Date (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/lastBroadCastDate");
//mappings.addBookmarkForXpath("Last Production Year (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/lastProductionYear");
//mappings.addBookmarkForXpath("Original Language (opt)", "/metadata/ContentDescriptiveMetadata/originallanguage");
//mappings.addBookmarkForXpath("Series Season Number", "/metadata/ContentDescriptiveMetadata/SeriesSeasonNumber");
//mappings.addBookmarkForXpath("Episode Number", "/metadata/ContentDescriptiveMetadata/episodeNumber");
//mappings.addBookmarkForXpath("Language used (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/languageUsed");
//mappings.addBookmarkForXpath("Local Keywords (opt)", "/metadata/ContentDescriptiveMetadata/LocalKeyword");
//mappings.addBookmarkForXpath("Contributpor (opt)", "/metadata/ObjectDescriptiveMetadata/contributor");
//mappings.addBookmarkForXpath("Metadata Language (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/metadatalanguage");
//mappings.addBookmarkForXpath("Landing Page URL (opt)", "/metadata/AdministrativeMetadata/landingPageURL");



//////////////////////////////////
// Bookmarks sorted alphabetically
//////////////////////////////////
mappings.addBookmarkForXpath("Alternative Series/collection title", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/alternativeSeriesOrCollectionTitle");
mappings.addBookmarkForXpath("Alternative Series/collection title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInEnglish/alternativeSeriesOrCollectionTitle");
mappings.addBookmarkForXpath("Broadcast Date (mandatory if broadcast)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/broadcastDate");
mappings.addBookmarkForXpath("Contributpor (opt)", "/metadata/ObjectDescriptiveMetadata/contributor");
mappings.addBookmarkForXpath("Country of production (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/SpatialInformation/CountryofProduction");
mappings.addBookmarkForXpath("Episode Number", "/metadata/ContentDescriptiveMetadata/episodeNumber");
mappings.addBookmarkForXpath("Extended Description (opt)", "/metadata/ContentDescriptiveMetadata/extendedDescription");
mappings.addBookmarkForXpath("Filename (opt)", "/metadata/AdministrativeMetadata/filename");
mappings.addBookmarkForXpath("First Broadcast Channel (opt)", "/metadata/AdministrativeMetadata/firstBroadcastChannel");
mappings.addBookmarkForXpath("Identifier", "/metadata/AdministrativeMetadata/identifier");
mappings.addBookmarkForXpath("Information (opt)", "/metadata/ObjectDescriptiveMetadata/information");
mappings.addBookmarkForXpath("Landing Page URL (opt)", "/metadata/AdministrativeMetadata/landingPageURL");
mappings.addBookmarkForXpath("Language used (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/languageUsed");
mappings.addBookmarkForXpath("Local Keywords (opt)", "/metadata/ContentDescriptiveMetadata/localKeyword");
mappings.addBookmarkForXpath("Last Broadcast Date (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/lastBroadCastDate");
mappings.addBookmarkForXpath("Last Production Year (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/lastProductionYear");
mappings.addBookmarkForXpath("Local Keywords (opt)", "/metadata/ContentDescriptiveMetadata/LocalKeyword");
mappings.addBookmarkForXpath("Material Type", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/materialType");
mappings.addBookmarkForXpath("Metadata Language (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/metadatalanguage");
mappings.addBookmarkForXpath("Original identifier", "/metadata/AdministrativeMetadata/originalIdentifier");
mappings.addBookmarkForXpath("Original Language (opt)", "/metadata/ContentDescriptiveMetadata/originallanguage");
mappings.addBookmarkForXpath("Production Year", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/productionYear");
mappings.addBookmarkForXpath("Provider", "/metadata/AdministrativeMetadata/provider");
mappings.addBookmarkForXpath("Publisher/Broadcaster", "/metadata/AdministrativeMetadata/publisherbroadcaster");
mappings.addBookmarkForXpath("Relation identifier (opt)", "/metadata/ObjectDescriptiveMetadata/relationIdentifier");
mappings.addBookmarkForXpath(" @relationType (opt)", "/metadata/ObjectDescriptiveMetadata/relationIdentifier/@relationtype");
mappings.addBookmarkForXpath("Series/collection title", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/seriesOrCollectionTitle");
mappings.addBookmarkForXpath("Series/collection title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInEnglish/seriesOrCollectionTitle");
mappings.addBookmarkForXpath("Series Season Number", "/metadata/ContentDescriptiveMetadata/SeriesSeasonNumber");
mappings.addBookmarkForXpath("Summary", "/metadata/ContentDescriptiveMetadata/summary");
mappings.addBookmarkForXpath("Summary in English", "/metadata/ContentDescriptiveMetadata/summaryInEnglish");




