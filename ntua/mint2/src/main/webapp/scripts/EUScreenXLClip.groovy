identifier = template.findFirst("//eus:identifier").addConstantMapping("EUS_00000000000000000000000000000000").setFixed(true);

broadcastDate = template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:broadcastDate");
broadcastDate.setLabel("broadcastDate (dd/mm/yyyy)");
productionYear = template.findFirst("//eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:productionYear");
productionYear.setLabel("productionYear (yyyy)");

////////////////////
// Bookmarks
////////////////////
//mappings.addBookmarkForXpath("Record Type", "/metadata/ObjectDescriptiveMetadata/recordType");
//mappings.addBookmarkForXpath("Identifier", "/metadata/AdministrativeMetadata/identifier");
//mappings.addBookmarkForXpath("Title", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/title");
//mappings.addBookmarkForXpath("Title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInEnglish/title");
//mappings.addBookmarkForXpath("Summary", "/metadata/ContentDescriptiveMetadata/summary");
//mappings.addBookmarkForXpath("Summary in English", "/metadata/ContentDescriptiveMetadata/summaryInEnglish");
//mappings.addBookmarkForXpath("Provider", "/metadata/AdministrativeMetadata/provider");
//mappings.addBookmarkForXpath("Publisher/Broadcaster", "/metadata/AdministrativeMetadata/publisherbroadcaster");
//mappings.addBookmarkForXpath("Broadcast Date (mandatory if broadcast)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/broadcastDate");
//mappings.addBookmarkForXpath("Production Year", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/productionYear");
//mappings.addBookmarkForXpath("IPR restrictions", "/metadata/AdministrativeMetadata/iprRestrictions");
//mappings.addBookmarkForXpath("Rights terms and conditions", "/metadata/AdministrativeMetadata/rightsTermsAndConditions");
//mappings.addBookmarkForXpath("Genre", "/metadata/ContentDescriptiveMetadata/genre");
//mappings.addBookmarkForXpath("Topic", "/metadata/ContentDescriptiveMetadata/topic");
//mappings.addBookmarkForXpath("Thesaurus terms", "/metadata/ContentDescriptiveMetadata/ThesaurusTerm");
//mappings.addBookmarkForXpath("Material Type", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/materialType");
//mappings.addBookmarkForXpath("Item duration", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/itemDuration");
//mappings.addBookmarkForXpath("Original identifier", "/metadata/AdministrativeMetadata/originalIdentifier");
//mappings.addBookmarkForXpath("Filename", "/metadata/AdministrativeMetadata/filename");

//Optional
//mappings.addBookmarkForXpath("Clip Title (opt)", "/metadata/ContentDescriptiveMetadata/clipTitle");
//mappings.addBookmarkForXpath("Extended Description (opt)", "/metadata/ContentDescriptiveMetadata/extendedDescription");
//mappings.addBookmarkForXpath("Information (opt)", "/metadata/ObjectDescriptiveMetadata/information");
//mappings.addBookmarkForXpath("First broadcast channel (opt)", "/metadata/AdministrativeMetadata/firstBroadcastChannel");
//mappings.addBookmarkForXpath("Geographical coverage (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/SpatialInformation/GeographicalCoverage");
//mappings.addBookmarkForXpath("Country of production (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/SpatialInformation/CountryofProduction");
//mappings.addBookmarkForXpath("Original Language (opt)", "/metadata/ContentDescriptiveMetadata/originallanguage");
//mappings.addBookmarkForXpath("Local Keywords (opt)", "/metadata/ContentDescriptiveMetadata/LocalKeyword");
//mappings.addBookmarkForXpath("Contributpor (opt)", "/metadata/ObjectDescriptiveMetadata/contributor");
//mappings.addBookmarkForXpath("Item Colour (opt)", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/itemColor");
//mappings.addBookmarkForXpath("Item Sound (opt)", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/itemSound");
//mappings.addBookmarkForXpath("Apsect Ratio (opt)", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/aspectRatio");
//mappings.addBookmarkForXpath("Language used (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/languageUsed");
//mappings.addBookmarkForXpath("Subtitle Language (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/subtitleLanguage");
//mappings.addBookmarkForXpath("Metadata Language (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/metadatalanguage");
//mappings.addBookmarkForXpath("Relation identifier (opt)", "/metadata/ObjectDescriptiveMetadata/relationIdentifier");
//mappings.addBookmarkForXpath("  @relationType (opt)", "/metadata/ObjectDescriptiveMetadata/relationIdentifier@relationtype");
//mappings.addBookmarkForXpath("Digital item URL (opt)", "/metadata/AdministrativeMetadata/digitalItemURL");
//mappings.addBookmarkForXpath("Landing Page URL (opt)", "/metadata/AdministrativeMetadata/landingPageURL");



mappings.addBookmarkForXpath("Aspect Ratio (opt)", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/aspectRatio");
mappings.addBookmarkForXpath("Broadcast Date (mandatory if broadcast)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/broadcastDate");
mappings.addBookmarkForXpath("Clip Title (opt)", "/metadata/ContentDescriptiveMetadata/clipTitle");
mappings.addBookmarkForXpath("Contributpor (opt)", "/metadata/ObjectDescriptiveMetadata/contributor");
mappings.addBookmarkForXpath("Country of production (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/SpatialInformation/CountryofProduction");
mappings.addBookmarkForXpath("Digital item URL (opt)", "/metadata/AdministrativeMetadata/digitalItemURL");
mappings.addBookmarkForXpath("Episode number", "/metadata/ContentDescriptiveMetadata/episodeNumber");
mappings.addBookmarkForXpath("Extended Description (opt)", "/metadata/ContentDescriptiveMetadata/extendedDescription");
mappings.addBookmarkForXpath("Filename", "/metadata/AdministrativeMetadata/filename");
mappings.addBookmarkForXpath("First broadcast channel (opt)", "/metadata/AdministrativeMetadata/firstBroadcastChannel");
mappings.addBookmarkForXpath("Genre", "/metadata/ContentDescriptiveMetadata/genre");
mappings.addBookmarkForXpath("Geographical coverage (opt)", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/SpatialInformation/GeographicalCoverage");
mappings.addBookmarkForXpath("Identifier", "/metadata/AdministrativeMetadata/identifier");
mappings.addBookmarkForXpath("Information (opt)", "/metadata/ObjectDescriptiveMetadata/information");
mappings.addBookmarkForXpath("IPR restrictions", "/metadata/AdministrativeMetadata/iprRestrictions");
mappings.addBookmarkForXpath("Item Colour (opt)", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/itemColor");
mappings.addBookmarkForXpath("Item duration", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/itemDuration");
mappings.addBookmarkForXpath("Item Sound (opt)", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/itemSound");
mappings.addBookmarkForXpath("Landing Page URL (opt)", "/metadata/AdministrativeMetadata/landingPageURL");
mappings.addBookmarkForXpath("Language used (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/languageUsed");
mappings.addBookmarkForXpath("Local Keywords (opt)", "/metadata/ContentDescriptiveMetadata/localKeyword");
mappings.addBookmarkForXpath("Material Type", "/metadata/ObjectDescriptiveMetadata/TechnicalInformation/materialType");
mappings.addBookmarkForXpath("Metadata Language (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/metadatalanguage");
mappings.addBookmarkForXpath("Original identifier", "/metadata/AdministrativeMetadata/originalIdentifier");
mappings.addBookmarkForXpath("Original Language (opt)", "/metadata/ContentDescriptiveMetadata/originallanguage");
mappings.addBookmarkForXpath("Production Year", "/metadata/ObjectDescriptiveMetadata/SpatioTemporalInformation/TemporalInformation/productionYear");
mappings.addBookmarkForXpath("Provider", "/metadata/AdministrativeMetadata/provider");
mappings.addBookmarkForXpath("Publisher/Broadcaster", "/metadata/AdministrativeMetadata/publisherbroadcaster");
mappings.addBookmarkForXpath("Record Type", "/metadata/ObjectDescriptiveMetadata/recordType");
mappings.addBookmarkForXpath("Relation identifier (opt)", "/metadata/ObjectDescriptiveMetadata/relationIdentifier");
mappings.addBookmarkForXpath(" @relationType (opt)", "/metadata/ObjectDescriptiveMetadata/relationIdentifier/@relationtype");
mappings.addBookmarkForXpath("Rights terms and conditions", "/metadata/AdministrativeMetadata/rightsTermsAndConditions");
mappings.addBookmarkForXpath("Series/Collection title", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/seriesOrCollectionTitle");
mappings.addBookmarkForXpath("Series/Collection title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInEnglish/seriesOrCollectionTitle");
mappings.addBookmarkForXpath("Series/Season number", "/metadata/ContentDescriptiveMetadata/seriesSeasonNumber");
mappings.addBookmarkForXpath("Subtitle Language (opt)", "/metadata/ObjectDescriptiveMetadata/LanguageInformation/subtitleLanguage");
mappings.addBookmarkForXpath("Summary", "/metadata/ContentDescriptiveMetadata/summary");
mappings.addBookmarkForXpath("Summary in English", "/metadata/ContentDescriptiveMetadata/summaryInEnglish");
mappings.addBookmarkForXpath("Thesaurus terms", "/metadata/ContentDescriptiveMetadata/ThesaurusTerm");
mappings.addBookmarkForXpath("Title", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInOriginalLanguage/title");
mappings.addBookmarkForXpath("Title in English", "/metadata/ContentDescriptiveMetadata/TitleSet/TitleSetInEnglish/title");
mappings.addBookmarkForXpath("Topic", "/metadata/ContentDescriptiveMetadata/topic");









