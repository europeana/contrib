package harvesterUI.shared.externalServices;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 27/01/12
 * Time: 13:36
 */
public class ExternalServiceResultUI extends BaseModel {

    public ExternalServiceResultUI() {}

    public ExternalServiceResultUI(String state) {
        set("state",state);
//        set("recordsValidated",recordsValidated);
//        set("resultFilePath", resultFilePath);
    }

    public void setState(String state){set("state", state);}
    public String getState(){return (String) get("state");}

//    public void setRecordsValidated(String recordsValidated){set("recordsValidated", recordsValidated);}
//    public String getRecordsValidated(){return (String) get("recordsValidated");}

//    public void setResultFilePath(String resultFilePath){set("resultFilePath", resultFilePath);}
//    public String getResultFilePath(){return (String) get("resultFilePath");}

}
