package harvesterUI.shared.mdr;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class SchemaUI extends SchemaTreeUI implements IsSerializable{

    private List<SchemaVersionUI> schemaVersions;

    public SchemaUI() {}

    public SchemaUI(String designation, String shortDesignation, String description,
                    String namespace, String notes) {
        super(shortDesignation,null,"","",null);
        set("designation",designation);
        set("shortDesignation", shortDesignation);
        set("description",description);
//        set("creationDate",creationDate);
//        set("xsdLink",xsdLink);
        set("namespace",namespace);
        set("notes",notes);
    }

    public void setDesignation(String designation){set("designation", designation);}
    public String getDesignation(){return (String) get("designation");}

    public void setShortDesignation(String shortDesignation){set("shortDesignation", shortDesignation);}
    public String getShortDesignation(){return (String) get("shortDesignation");}

    public void setDescription(String description){set("description", description);}
    public String getDescription(){return (String) get("description");}

//    public void setCreationDate(Date creationDate){set("creationDate", creationDate);}
//    public Date getCreationDate(){return (Date) get("creationDate");}

//    public void setXsdLink(String xsdLink){set("xsdLink", xsdLink);}
//    public String getXsdLink(){return (String) get("xsdLink");}

    public void setNamespace(String namespace){set("namespace", namespace);}
    public String getNamespace(){return (String) get("namespace");}

    public void setNotes(int notes){set("notes", notes);}
    public String getNotes(){return (String) get("notes");}

    public List<SchemaVersionUI> getSchemaVersions() {
        if(schemaVersions == null)
            schemaVersions = new ArrayList<SchemaVersionUI>();
        return schemaVersions;
    }

    public void createTreeChildren(){
        if(schemaVersions != null){
            for(SchemaVersionUI schemaVersionUI : schemaVersions){
                add(schemaVersionUI);
            }
        }
    }
}
