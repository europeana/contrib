package pt.utl.ist.repox.externalServices;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ExternalRestService {
    private static final Logger log = Logger.getLogger(ExternalRestService.class);

    private String id;
    private String name;
    private String uri;
    private String statusUri;
    private String type;
    private List<ServiceParameter> serviceParameters;

    public ExternalRestService(String id, String name, String uri, String statusUri, String type) {
        super();
        this.id = id;
        this.uri = uri;
        this.type = type;
        this.name = name;
        this.statusUri = statusUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatusUri() {
        return statusUri;
    }

    public List<ServiceParameter> getServiceParameters() {
        if(serviceParameters == null)
            serviceParameters = new ArrayList<ServiceParameter>();
        return serviceParameters;
    }

    public void setServiceParameters(List<ServiceParameter> serviceParameters) {
        this.serviceParameters = serviceParameters;
    }

//    public String transform(String identifier, String xmlSourceString, String dataProviderName) throws DocumentException, TransformerException {
//        Transformer transformer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().loadStylesheet(this);
//
//        Document sourceDocument = DocumentHelper.parseText(xmlSourceString);
//        DocumentSource source = new DocumentSource(sourceDocument);
//        DocumentResult result = new DocumentResult();
//
//        transformer.clearParameters();
//        transformer.setParameter("recordIdentifier", identifier);
//        transformer.setParameter("dataProvider", dataProviderName);
//
//        // Transform the source XML to System.out.
//        transformer.transform(source, result);
//        Document transformedDoc = result.getDocument();
//
//        return transformedDoc.asXML();
//    }
}
