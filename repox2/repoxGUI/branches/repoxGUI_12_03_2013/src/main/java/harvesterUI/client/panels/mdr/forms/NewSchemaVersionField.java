package harvesterUI.client.panels.mdr.forms;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.mdr.SchemaVersionUI;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 10-01-2012
 * Time: 18:57
 */
public class NewSchemaVersionField extends FieldSet{

    private NumberField versionField;
    private TextField<String> xsdLinkField;
    private FormData formData;

    public NewSchemaVersionField(final NewSchemaVersionFieldSet parent, int fieldNumber) {
        formData = new FormData("95%");

        setHeading("Version Field " + fieldNumber);
        setLayout(new EditableFormLayout(120));

        LayoutContainer addContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.END);
        addContainer.setLayout(operationsLayout);

        if(parent.getItemCount() > 0){
            Button removeButton = new Button(HarvesterUI.CONSTANTS.delete(), new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent be) {
                    removeFromParent();
                    parent.subtractFieldCount();
                }
            });
            removeButton.setIcon(HarvesterUI.ICONS.delete());
            addContainer.add(removeButton, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        }

        add(addContainer);

        versionField = new NumberField();
        versionField.setFieldLabel("Version (Number)" + HarvesterUI.REQUIRED_STR);
        versionField.setId("versionField");
        versionField.setAllowBlank(false);

        xsdLinkField = new TextField<String>();
        xsdLinkField.setFieldLabel("Xsd Link" + HarvesterUI.REQUIRED_STR);
        xsdLinkField.setId("xsdLinkField");
        xsdLinkField.setAllowBlank(false);

        add(versionField,formData);
        add(xsdLinkField,formData);
    }

    public NewSchemaVersionField(final NewSchemaVersionFieldSet parent, SchemaVersionUI schemaVersionUI, int fieldNumber) {
        formData = new FormData("95%");

        setHeading("Version Field " + fieldNumber);
        setLayout(new EditableFormLayout(120));

        LayoutContainer addContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.END);
        addContainer.setLayout(operationsLayout);

        if(parent.getItemCount() > 0){
            Button removeButton = new Button(HarvesterUI.CONSTANTS.delete(), new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent be) {
                    removeFromParent();
                    parent.subtractFieldCount();
                }
            });
            removeButton.setIcon(HarvesterUI.ICONS.delete());
            addContainer.add(removeButton, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        }

        add(addContainer);

        versionField = new NumberField();
        versionField.setFieldLabel("Version" + HarvesterUI.REQUIRED_STR);
        versionField.setId("versionField");
        versionField.setAllowBlank(false);
        versionField.setValue(schemaVersionUI.getVersion());

        xsdLinkField = new TextField<String>();
        xsdLinkField.setFieldLabel("Xsd Link" + HarvesterUI.REQUIRED_STR);
        xsdLinkField.setId("xsdLinkField");
        xsdLinkField.setValue(schemaVersionUI.getXsdLink());

        add(versionField,formData);
        add(xsdLinkField,formData);
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
        super.layout();
        layout();
    }

    public String getXsdLink(){
        return xsdLinkField.getValue();
    }

    public Number getVersion(){
        return versionField.getValue();
    }
}
