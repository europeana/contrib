package at.researchstudio.dme.imageannotation.client.annotation;

import at.researchstudio.dme.imageannotation.client.AnnotationConstants;
import at.researchstudio.dme.imageannotation.client.annotation.listener.CancelAnnotationClickListener;
import at.researchstudio.dme.imageannotation.client.annotation.listener.SaveAnnotationClickListener;
import at.researchstudio.dme.imageannotation.client.annotation.listener.UpdateAnnotationClickListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * form to create and update annotations
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationForm extends Composite {
	private static final String SCOPE_RADIO_GROUP_NAME = "scope";
	private AnnotationConstants annotationConstants = null;
	
	private TextBox titleTextBox = new TextBox();	
	private TextArea textArea = new TextArea();
	private RadioButton rdPublic,rdPrivate = null;
	
	public ImageAnnotationForm(ImageAnnotationComposite annotationComposite, 
			ImageAnnotationTreeNode annotationTreeNode, boolean fragmentAnnotation, boolean update) {
		annotationConstants=(AnnotationConstants)GWT.create(AnnotationConstants.class);	
										
		VerticalPanel formPanel = new VerticalPanel();
		formPanel.setStyleName("imageAnnotation-form");		
		formPanel.add(createTitlePanel(update,annotationTreeNode));
		formPanel.add(createTextPanel(update,annotationTreeNode));
		formPanel.add(createRadioPanel(update,annotationTreeNode));
		formPanel.add(createButtonsPanel(update,annotationTreeNode, annotationComposite));	
		initWidget(formPanel);
	}
	
	private HorizontalPanel createTitlePanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode) {
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		Label titleLabel = new Label(annotationConstants.annotationTitle());
		titleLabel.setStyleName("imageAnnotation-form-title-label");
		titleTextBox.setStyleName("imageAnnotation-form-title");
		// in case of an update
		if(update) titleTextBox.setText(annotationTreeNode.getTitle());
		//in case of an reply
		if(!update&&annotationTreeNode!=null)
			titleTextBox.setText(annotationConstants.annotationReplyTitlePrefix()+
					annotationTreeNode.getTitle());
	
		titlePanel.add(titleLabel);		
		titlePanel.add(titleTextBox);
		
		return titlePanel;		
	}
	
	private HorizontalPanel createTextPanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode) {
		
		HorizontalPanel textPanel = new HorizontalPanel();
		Label textLabel = new Label(annotationConstants.annotationText());
		textLabel.setStyleName("imageAnnotation-form-text-label");		
		textArea.setStyleName("imageAnnotation-form-text");
		if(update) textArea.setText(annotationTreeNode.getText());
		
		textPanel.add(textLabel);
		textPanel.add(textArea);
		
		return textPanel;
	}
	
	private HorizontalPanel createRadioPanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode) {
		
		HorizontalPanel radioPanel = new HorizontalPanel();		
		radioPanel.add(rdPublic = new RadioButton(SCOPE_RADIO_GROUP_NAME,
				annotationConstants.publicScope()));
		radioPanel.add(rdPrivate = new RadioButton(SCOPE_RADIO_GROUP_NAME,
				annotationConstants.privateScope()));
		rdPublic.setStyleName("imageAnnotation-form-radiobutton");
		
		if(update&&annotationTreeNode.getAnnotation().getScope()==ImageAnnotation.Scope.PRIVATE)
			rdPrivate.setValue(true);			
		else
			rdPublic.setValue(true);
		
		return radioPanel;
	}
	
	private HorizontalPanel createButtonsPanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode, 
			ImageAnnotationComposite annotationComposite) {
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		PushButton saveButton = new PushButton(annotationConstants.actionSave());
		if(update) {
			saveButton.addClickHandler(new UpdateAnnotationClickListener(annotationComposite, 
					annotationTreeNode, this));
		} else {
			saveButton.addClickHandler(new SaveAnnotationClickListener(annotationComposite, 
					annotationTreeNode, this));
		}
		saveButton.setStyleName("imageAnnotation-form-button");
		buttonsPanel.add(saveButton);
		
		PushButton cancelButton = new PushButton(annotationConstants.actionCancel());
		cancelButton.setStyleName("imageAnnotation-form-button");
		cancelButton.addClickHandler(new CancelAnnotationClickListener(annotationComposite,
				annotationTreeNode));
		buttonsPanel.add(cancelButton);
		
		return buttonsPanel;
	}
	
	public String getText() {
		return textArea.getText();
	}
	
	public String getTitle() {
		return titleTextBox.getText();
	}
	
	public ImageAnnotation.Scope getScope() {
		return (rdPublic.getValue())?ImageAnnotation.Scope.PUBLIC:ImageAnnotation.Scope.PRIVATE;
	}
}
