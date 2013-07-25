package europeana.geoparser.service;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.ProcessingInstruction;

import europeana.geoparser.Geoparser;
import europeana.geoparser.NamedEntityRecognitionException;
import europeana.geoparser.NerResult;
import europeana.geoparser.NerResultOfEuropeanaRecord;
import europeana.geoparser.ResolutionException;
import europeana.geoparser.europeanaMetadata.EuropeanaRecord;
import europeana.geoparser.europeanaMetadata.InvalidMetadataRecordException;

/**
 * The controler for the web interface of the Geoparser
 *
 * @author nfreire
 */
public class GeoparsingControl {
    private Geoparser geoparser;
    
    public GeoparsingControl(Geoparser geoparser) {
		super();
		this.geoparser = geoparser;
	}

    
    /**
     * @param freeText some text to parse
     * @param language the language of the text
     * @param stylesheet attach a stylesheet to the xml result, may be null
     * @return an xml response, containing the text anotated with the entities   
     * @throws NamedEntityRecognitionException If an error occurs during the entity recognition phase
     * @throws ResolutionException if an error occurs during the resolution phase
     * @throws IOException if an error occurs while generating the xml response
     */
    @SuppressWarnings("unchecked")
	public Document geoparseFreeText(String freeText, String language, String stylesheet
    ) throws NamedEntityRecognitionException, ResolutionException, IOException {
        if (freeText == null)
            freeText = "";
        NerResult geoResult = geoparser.geoparse(freeText, language);
        Document responseDom = geoResult.toXml();
        if (stylesheet != null && !stylesheet.isEmpty()) {
            ProcessingInstruction instr = DocumentHelper.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"" + stylesheet + "\"");
            responseDom.content().add(0, instr);
        }
        return responseDom;
    }


    /**
     * @param metadataXml a metadata record to parse, in ESE format
     * @param stylesheet attach a stylesheet to the xml result, may be null
     * @return an xml response, containing the metadat record anotated with the entities   
     * @throws InvalidMetadataRecordException if the record is not un a recognizable format
     * @throws DocumentException if an error occurs while generating the xml response
     * @throws NamedEntityRecognitionException If an error occurs during the entity recognition phase
     * @throws ResolutionException if an error occurs during the resolution phase
     * @throws IOException if an error occurs while generating the xml response
     */
    @SuppressWarnings("unchecked")
	public Document geoparseMetadata(String metadataXml, String stylesheet) 
    throws InvalidMetadataRecordException, DocumentException, NamedEntityRecognitionException, ResolutionException, IOException {
        if (metadataXml == null)
            metadataXml = "";
        Document recordDom = DocumentHelper.parseText(metadataXml);
        EuropeanaRecord record = new EuropeanaRecord(recordDom);
        NerResultOfEuropeanaRecord geoResult = geoparser.geoparse(record);
        Document responseDom = geoResult.toXml();
        if (stylesheet != null && !stylesheet.isEmpty()) {
            ProcessingInstruction instr = DocumentHelper.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"" + stylesheet + "\"");
            responseDom.content().add(0, instr);
        }
        return responseDom;
    }



}
