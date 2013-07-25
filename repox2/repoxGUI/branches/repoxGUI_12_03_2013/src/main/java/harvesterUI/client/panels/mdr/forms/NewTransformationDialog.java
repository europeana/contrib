package harvesterUI.client.panels.mdr.forms;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.mdr.TransformationUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 17:19
 */
public class NewTransformationDialog extends FormDialog {

    private DefaultFormPanel newTransformationFormPanel;
    private TextField<String> identifierField, descriptionField, sourceFormatField,
            destinationFormatField, schema, metadataNamespace;
    private FileUploadField xsdUploadField;
    private CheckBoxGroup isXslVersion2;
    private TransformationUI associatedTransformation = null;

    public NewTransformationDialog() {
        super(0.55,0.5);
        createNewUSerDialog();
    }

    private void createNewUSerDialog() {
        FormData formData = new FormData("95%");
        setHeading(HarvesterUI.CONSTANTS.addTransformation());
        setIcon(HarvesterUI.ICONS.add());

        newTransformationFormPanel = new DefaultFormPanel();
        newTransformationFormPanel.setHeaderVisible(false);
        newTransformationFormPanel.setLayout(new EditableFormLayout(160));

        newTransformationFormPanel.setMethod(FormPanel.Method.POST);
        newTransformationFormPanel.setEncoding(FormPanel.Encoding.MULTIPART);
        newTransformationFormPanel.setAction(GWT.getModuleBaseURL() + "transformationfileupload");

        identifierField = new TextField<String>();
        identifierField.setFieldLabel(HarvesterUI.CONSTANTS.identifier()+ HarvesterUI.REQUIRED_STR);
        identifierField.setId("schm_identifier");
        identifierField.setName("transformationSubmitID");
        identifierField.setAllowBlank(false);
        newTransformationFormPanel.add(identifierField, formData);

        descriptionField = new TextField<String>();
        descriptionField.setFieldLabel(HarvesterUI.CONSTANTS.description()+ HarvesterUI.REQUIRED_STR);
        descriptionField.setId("schm_desc");
        descriptionField.setAllowBlank(false);
        newTransformationFormPanel.add(descriptionField, formData);

        sourceFormatField = new TextField<String>();
        sourceFormatField.setFieldLabel(HarvesterUI.CONSTANTS.sourceFormat()+ HarvesterUI.REQUIRED_STR);
        sourceFormatField.setId("schm_srcFormat");
        sourceFormatField.setAllowBlank(false);
        newTransformationFormPanel.add(sourceFormatField, formData);

        schema = new TextField<String>();
        schema.setId("schema_mtdschema");
        schema.setFieldLabel(HarvesterUI.CONSTANTS.schema() + HarvesterUI.REQUIRED_STR);
        schema.setAllowBlank(false);

        metadataNamespace = new TextField<String>();
        metadataNamespace.setId("schema_mtdnams");
        metadataNamespace.setFieldLabel(HarvesterUI.CONSTANTS.metadataNamespace()+ HarvesterUI.REQUIRED_STR);
        metadataNamespace.setAllowBlank(false);

        destinationFormatField = new TextField<String>();
        destinationFormatField.setFieldLabel(HarvesterUI.CONSTANTS.destinationFormat()+ HarvesterUI.REQUIRED_STR);
        destinationFormatField.setId("schm_destFormat");
        destinationFormatField.setAllowBlank(false);
        destinationFormatField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if(destinationFormatField.getValue().startsWith("ese")) {
                    schema.setValue(UtilManager.getSchema("ese"));
                    metadataNamespace.setValue(UtilManager.getNamespace("ese"));
                } else if(destinationFormatField.getValue().startsWith("MarcXchange")) {
                    schema.setValue(UtilManager.getSchema("MarcXchange"));
                    metadataNamespace.setValue(UtilManager.getNamespace("MarcXchange"));
                }else if(destinationFormatField.getValue().startsWith("tel")) {
                    schema.setValue(UtilManager.getSchema("tel"));
                    metadataNamespace.setValue(UtilManager.getNamespace("tel"));
                }else if(destinationFormatField.getValue().startsWith("oai_dc")) {
                    schema.setValue(UtilManager.getSchema("oai_dc"));
                    metadataNamespace.setValue(UtilManager.getNamespace("oai_dc"));
                }else if(destinationFormatField.getValue().startsWith("NLM-AI")) {
                    schema.setValue(UtilManager.getSchema("NLM-AI"));
                    metadataNamespace.setValue(UtilManager.getNamespace("NLM-AI"));
                }else if(destinationFormatField.getValue().startsWith("NLM-Book")) {
                    schema.setValue(UtilManager.getSchema("NLM-Book"));
                    metadataNamespace.setValue(UtilManager.getNamespace("NLM-Book"));
                }else if(destinationFormatField.getValue().startsWith("lido")) {
                    schema.setValue(UtilManager.getSchema("lido"));
                    metadataNamespace.setValue(UtilManager.getNamespace("lido"));
                }else if(destinationFormatField.getValue().startsWith("ISO2709")) {
                    schema.setValue(UtilManager.getSchema("ISO2709"));
                    metadataNamespace.setValue(UtilManager.getNamespace("ISO2709"));
                }else if(destinationFormatField.getValue().startsWith("edm")) {
                    schema.setValue(UtilManager.getSchema("edm"));
                    metadataNamespace.setValue(UtilManager.getNamespace("edm"));
                }
            }
        });
        newTransformationFormPanel.add(destinationFormatField, formData);

        newTransformationFormPanel.add(schema,formData);
        newTransformationFormPanel.add(metadataNamespace,formData);

        isXslVersion2 = new CheckBoxGroup();
        CheckBox isXslVersion2CB = new CheckBox();
        isXslVersion2.setFieldLabel(HarvesterUI.CONSTANTS.xslVersion2() + "?" + HarvesterUI.REQUIRED_STR);
        isXslVersion2CB.setValue(false);
        isXslVersion2.add(isXslVersion2CB);
        newTransformationFormPanel.add(isXslVersion2,formData);

        xsdUploadField = new FileUploadField();
        xsdUploadField.setId("upload");
        xsdUploadField.setName("upload");
        xsdUploadField.setAllowBlank(false);
        xsdUploadField.setFieldLabel(HarvesterUI.CONSTANTS.transformationFile());
        newTransformationFormPanel.add(xsdUploadField,formData);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                AsyncCallback<String> callback = new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(String id) {
                        hide();
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveTransformation(), HarvesterUI.CONSTANTS.saveTransformationSuccess());
                        Dispatcher.forwardEvent(AppEvents.ReloadTransformations);
                    }
                };
                newTransformationFormPanel.submit();
                String identifier = identifierField.getValue();
                String description = descriptionField.getValue();
                String srcFormat = sourceFormatField.getValue();
                String destFormat = destinationFormatField.getValue();
                String stylesheet= xsdUploadField.getValue();
                String schemaStr = schema.getValue();
                String mtdNamespace = metadataNamespace.getValue();
                TransformationUI transformationUI = new TransformationUI(identifier,description,srcFormat,destFormat,
                        schemaStr,mtdNamespace,stylesheet,(Boolean)isXslVersion2.get(0).getValue());
                TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
                service.saveTransformation(transformationUI,associatedTransformation == null? "":associatedTransformation.getIdentifier(), callback);
            }
        });

        newTransformationFormPanel.addButton(saveButton);
        newTransformationFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
                Dispatcher.forwardEvent(AppEvents.ReloadTransformations);
            }
        }));

        newTransformationFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(newTransformationFormPanel);
        binding.addButton(saveButton);

        add(newTransformationFormPanel);
    }

    public void edit(TransformationUI transformationUI){
        associatedTransformation = transformationUI;
        setHeading(HarvesterUI.CONSTANTS.editTransformation()+": " + transformationUI.getIdentifier());
        identifierField.setValue(transformationUI.getIdentifier());
        descriptionField.setValue(transformationUI.getDescription());
        sourceFormatField.setValue(transformationUI.getSrcFormat());
        destinationFormatField.setValue(transformationUI.getDestFormat());
        schema.setValue(transformationUI.getSchema());
        metadataNamespace.setValue(transformationUI.getMetadataNamespace());

        xsdUploadField.setValue(transformationUI.getXslFilePath());
        ((CheckBox)isXslVersion2.get(0)).setValue(transformationUI.getIsXslVersion2());
    }

    public void resetValues() {
        associatedTransformation = null;
        identifierField.clear();
        descriptionField.clear();
        sourceFormatField.clear();
        destinationFormatField.clear();
        schema.clear();
        metadataNamespace.clear();

        xsdUploadField.clear();
        ((CheckBox)isXslVersion2.get(0)).setValue(false);

        setHeading(HarvesterUI.CONSTANTS.addTransformation());
    }
}
