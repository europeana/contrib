package harvesterUI.shared.externalServices;

import com.extjs.gxt.ui.client.data.BaseModel;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 07-12-2011
 * Time: 23:50
 */
public class ExternalServiceUI extends BaseModel {
    
    private List<ServiceParameterUI> serviceParameters;

    public ExternalServiceUI() {}

    public ExternalServiceUI(String id,String name,String uri, String statusUri,String type, List<ServiceParameterUI> serviceParameters) {
        set("id",id);
        set("name",name);
        set("uri",uri);
        set("statusUri",statusUri);
        set("type",type);
        setServiceParameters(serviceParameters);
    }

    public List<ServiceParameterUI> getServiceParameters() {
        return serviceParameters;
    }

    public void setServiceParameters(List<ServiceParameterUI> serviceParameters) {
        this.serviceParameters = serviceParameters;
    }

    public void setId(String id){set("id", id);}
    public String getId(){return (String) get("id");}

    public void setName(String name){set("name", name);}
    public String getName(){return (String) get("name");}

    public void setUri(String uri){set("uri", uri);}
    public String getUri(){return (String) get("uri");}

    public void setStatusUri(String statusUri){set("statusUri", statusUri);}
    public String getStatusUri(){return (String) get("statusUri");}

    public void setType(String type){set("type", type);}
    public String getType(){return (String) get("type");}
}
