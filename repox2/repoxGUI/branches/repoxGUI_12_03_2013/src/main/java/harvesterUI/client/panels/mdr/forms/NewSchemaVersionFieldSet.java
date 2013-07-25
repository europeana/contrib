package harvesterUI.client.panels.mdr.forms;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 10-01-2012
 * Time: 18:25
 */
public class NewSchemaVersionFieldSet extends FieldSet {

    private int fieldCount = 1;

    public NewSchemaVersionFieldSet() {
        setHeading("Schema Versions");
    }

    public void addNewField(){
        NewSchemaVersionField NewSchemaVersionField = new NewSchemaVersionField(this,fieldCount);
        add(NewSchemaVersionField);
        layout();
        fieldCount++;
    }

    public void addNewField(SchemaVersionUI schemaVersionUI){
        add(new NewSchemaVersionField(this,schemaVersionUI,fieldCount));
        layout();
        fieldCount++;
    }

    public List<SchemaVersionUI> getAllSchemaVersions(){
        List<SchemaVersionUI> schemaVersionUIList = new ArrayList<SchemaVersionUI>();
        for(Component component : getItems()){
            if(component instanceof NewSchemaVersionField){
                NewSchemaVersionField schemaVersionField = (NewSchemaVersionField)component;
                String xsdLink = schemaVersionField.getXsdLink();
                double version = schemaVersionField.getVersion().doubleValue();
                SchemaVersionUI schemaVersionUI = new SchemaVersionUI(version,xsdLink,null);
                schemaVersionUIList.add(schemaVersionUI);
            }
        }
        return schemaVersionUIList;
    }

    public void resize() {
        for(Component component : getItems()){
            if(component instanceof NewSchemaVersionField){
                ((NewSchemaVersionField) component).layout(true);
            }
        }
        layout(true);
    }

    public void edit(SchemaUI schemaUI){
        for(SchemaVersionUI schemaVersionUI : schemaUI.getSchemaVersions()){
            addNewField(schemaVersionUI);
        }
    }

    public void reset(){
        fieldCount = 1;
        for(int i=getItems().size()-1 ; i >=0 ; i--){
            if(getItem(i) instanceof NewSchemaVersionField){
                getItem(i).removeFromParent();
            }
        }
    }

    public void subtractFieldCount() {
        fieldCount--;
    }
}
