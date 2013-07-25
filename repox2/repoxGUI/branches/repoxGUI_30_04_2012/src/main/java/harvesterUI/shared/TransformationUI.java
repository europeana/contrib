package harvesterUI.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class TransformationUI extends BaseModel {

    public TransformationUI() {}

    public TransformationUI(String identifier, String description, String srcFormat, String destFormat,
                            String schema, String mtdNamespace, String xslFilePath, boolean isXslVersion2) {
        set("identifier",identifier);
        set("description",description);
        set("srcFormat", srcFormat);
        set("destFormat",destFormat);
        set("schema",schema);
        set("mtdNamespace",mtdNamespace);
        set("xslFilePath",xslFilePath);
        set("isXslVersion2",isXslVersion2);
        createDSString(identifier,srcFormat,destFormat);
    }

    public void setIdentifier(String identifier){set("identifier", identifier);}
    public String getIdentifier(){return (String) get("identifier");}

    public void setDescription(String description){set("description", description);}
    public String getDescription(){return (String) get("description");}

    public void setSrcFormat(String srcFormat){set("srcFormat", srcFormat);}
    public String getSrcFormat(){return (String) get("srcFormat");}

    public void setDestFormat(String destFormat){set("destFormat", destFormat);}
    public String getDestFormat(){return (String) get("destFormat");}

    public void setSchema(String schema){set("schema", schema);}
    public String getSchema(){return (String) get("schema");}

    public void setMetadataNamespace(String mtdNamespace){set("mtdNamespace", mtdNamespace);}
    public String getMetadataNamespace(){return (String) get("mtdNamespace");}

    public void setIsXslVersion2(boolean isXslVersion2){set("isXslVersion2", isXslVersion2);}
    public boolean getIsXslVersion2(){return (Boolean) get("isXslVersion2");}

    public void setXslFilePath(int xslFilePath){set("xslFilePath", xslFilePath);}
    public String getXslFilePath(){return (String) get("xslFilePath");}

    public void createDSString(String identifier, String srcFormat ,String destFormat) {
        if(identifier.equals("-"))
            set("dsStringFormat","-");
        else {
            String dsStringFormat = srcFormat + " to " + destFormat + ": " + identifier;
            set("dsStringFormat",dsStringFormat);
        }
    }

    public String getDSStringFormat() { return (String) get("dsStringFormat");}
}
