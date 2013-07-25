/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.client.image.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.annotation.SemanticTagGroup;
import at.ait.dme.yuma.client.image.annotation.handler.CancelImageAnnotationClickHandler;
import at.ait.dme.yuma.client.image.annotation.handler.SaveImageAnnotationClickHandler;
import at.ait.dme.yuma.client.image.annotation.handler.UpdateImageAnnotationClickHandler;
import at.ait.dme.yuma.client.image.annotation.handler.tag.ImageAnnotationKeyDownHandler;
import at.ait.dme.yuma.client.image.annotation.handler.tag.SelectImageAnnotationTagClickHandler;

import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * form to create and update annotations
 * 
 * @author Christian Sadilek
 * @author Manuel Gay
 * @author Rainer Simon
 */
public class StandardImageAnnotationForm extends ImageAnnotationForm {
	public class Checkbox extends com.google.gwt.user.client.ui.CheckBox {
	    
	    private Object resource;

	    public Object getResource() {
	        return resource;
	    }

	    public void setResource(Object resource) {
	        this.resource = resource;
	    }
	}
	
	private static final String SCOPE_RADIO_GROUP_NAME = "scope";
	
	private VerticalPanel formPanel = new VerticalPanel();
	private TextBox titleTextBox = new TextBox();	
	protected TextArea textArea = new TextArea();
	private RadioButton rdPublic,rdPrivate = null;

	private List<SemanticTag> tags = new ArrayList<SemanticTag>();
    private VerticalPanel linksStack;
    private VerticalPanel suggestedLinksStack;
	  
    public StandardImageAnnotationForm() {}
    
	public StandardImageAnnotationForm(ImageAnnotationComposite annotationComposite, 
			ImageAnnotationTreeNode annotationTreeNode, boolean fragmentAnnotation, boolean update) {
		
    	formPanel.setStyleName("imageAnnotation-form");		
		formPanel.add(createTitlePanel(update,annotationTreeNode));
		formPanel.add(createTextPanel(update,annotationTreeNode,annotationComposite));
		formPanel.add(createRadioPanel(update,annotationTreeNode));
		formPanel.add(createLinksPanel(update,annotationTreeNode));
		formPanel.add(createSemanticLinksPanel(update,annotationTreeNode));
		formPanel.add(createButtonsPanel(update, annotationTreeNode, annotationComposite));	
	 	initWidget(formPanel);
	}

	/**
	 * Workaround: for some reason, radio buttons are reset during GUI layout.
	 * This workaround restores their state directly after layout.
	 */
	@Override
	public void layout() {
		if (rdPublic != null) {
			final boolean pub = rdPublic.getValue();
			final boolean prv = rdPrivate.getValue();
			DeferredCommand.addCommand(new Command() {
				@Override
				public void execute() {
					rdPublic.setValue(pub);
					rdPrivate.setValue(prv);
				}
			});
		}
	}
	
	@Override
	public ImageAnnotationForm createNew(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, boolean fragmentAnnotation, boolean update) {
		return new StandardImageAnnotationForm(annotationComposite, annotationTreeNode, 
				fragmentAnnotation, update);
	}
	
	private HorizontalPanel createTitlePanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode) {
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		Label titleLabel = new Label(Application.getConstants().annotationTitle());
		titleLabel.setStyleName("imageAnnotation-form-label");
		titleTextBox.setStyleName("imageAnnotation-form-title");
		// in case of an update
		if(update) titleTextBox.setText(annotationTreeNode.getTitle());
		//in case of an reply
		if(!update&&annotationTreeNode!=null)
			titleTextBox.setText(Application.getConstants().annotationReplyTitlePrefix()+
					annotationTreeNode.getTitle());
	
		titlePanel.add(titleLabel);		
		titlePanel.add(titleTextBox);
		
		return titlePanel;		
	}
	
	private HorizontalPanel createTextPanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode, ImageAnnotationComposite annotationComposite) {
		
		HorizontalPanel textPanel = new HorizontalPanel();
		Label textLabel = new Label(Application.getConstants().annotationText());
		textLabel.setStyleName("imageAnnotation-form-label");		
		textArea.setStyleName("imageAnnotation-form-text");
		if(update) textArea.setText(annotationTreeNode.getText());
		
		textPanel.add(textLabel);
		textPanel.add(textArea);
		
		textArea.addKeyDownHandler(createKeyDownHandler(annotationComposite));
		
		return textPanel;
	}
	
	private HorizontalPanel createRadioPanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode) {
	
		Label scopeLabel = new Label();
		scopeLabel.setStyleName("imageAnnotation-form-label");         
	     
	    HorizontalPanel radioPanel = new HorizontalPanel();
        radioPanel.add(scopeLabel);
        radioPanel.add(rdPublic = new RadioButton(SCOPE_RADIO_GROUP_NAME,
                " " + Application.getConstants().publicScope()));
        radioPanel.add(rdPrivate = new RadioButton(SCOPE_RADIO_GROUP_NAME,
                " " + Application.getConstants().privateScope()));
        rdPublic.setStyleName("imageAnnotation-form-radiobutton");
        rdPrivate.setStyleName("imageAnnotation-form-radiobutton");     
        		
		if(update&&annotationTreeNode.getAnnotation().getScope()==ImageAnnotation.Scope.PRIVATE)
			rdPrivate.setValue(true, true);		
		else
			rdPublic.setValue(true, true);		
		
		return radioPanel;
	}
	
	protected Panel createLinksPanel(boolean update, ImageAnnotationTreeNode annotationTreeNode) {

	    HorizontalPanel linksPanel = new HorizontalPanel();
	    Label linksLabel = new Label(Application.getConstants().annotationLinks());
	    linksLabel.setStyleName("imageAnnotation-form-label");      
        linksPanel.add(linksLabel);
	    
        linksStack = new VerticalPanel();
        linksStack.setStyleName("imageAnnotation-form-links");
        linksPanel.add(linksStack);
        
        if(update&&annotationTreeNode.getAnnotation().hasSemanticTags()) {
            for(SemanticTag t: annotationTreeNode.getAnnotation().getSemanticTags()) {
                addLink(t, linksStack, true);
                if(!this.tags.contains(t)) {
                    this.tags.add(t);
                }
            }
        }
	    
	    return linksPanel;
	}
	
	protected Panel createSemanticLinksPanel(boolean update, ImageAnnotationTreeNode annotationTreeNode) {

        HorizontalPanel linksPanel = new HorizontalPanel();
        Label linksLabel = new Label(Application.getConstants().annotationSuggestedLinks());
        linksLabel.setStyleName("imageAnnotation-form-label");      
        linksPanel.add(linksLabel);
        
        suggestedLinksStack = new VerticalPanel();
        suggestedLinksStack.setStyleName("imageAnnotation-form-links");

        linksPanel.add(suggestedLinksStack);
        
        return linksPanel;
    }
	
	protected KeyDownHandler createKeyDownHandler(ImageAnnotationComposite annotationComposite) {
		return new ImageAnnotationKeyDownHandler(this,annotationComposite);
	}
	
	
	private HorizontalPanel createButtonsPanel(boolean update, 
			ImageAnnotationTreeNode annotationTreeNode, 
			ImageAnnotationComposite annotationComposite) {
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		PushButton saveButton = new PushButton(Application.getConstants().actionSave());
		if(update) {
			saveButton.addClickHandler(new UpdateImageAnnotationClickHandler(annotationComposite, 
					annotationTreeNode, this));
		} else {
			saveButton.addClickHandler(new SaveImageAnnotationClickHandler(annotationComposite, 
					annotationTreeNode, this));
		}
		saveButton.setStyleName("imageAnnotation-form-button");
		buttonsPanel.add(saveButton);
		
		PushButton cancelButton = new PushButton(Application.getConstants().actionCancel());
		cancelButton.setStyleName("imageAnnotation-form-button");
		cancelButton.addClickHandler(new CancelImageAnnotationClickHandler(annotationComposite,
				annotationTreeNode));
		buttonsPanel.add(cancelButton);
		
		return buttonsPanel;
	}
	
	@Override
	public String getAnnotationText() {
		return textArea.getText();
	}
	
	@Override
	public String getAnnotationTitle() {
		return titleTextBox.getText();
	}
	
	@Override
	public ImageAnnotation.Scope getAnnotationScope() {
		return (rdPublic.getValue())?ImageAnnotation.Scope.PUBLIC:ImageAnnotation.Scope.PRIVATE;
	}
	
	public void addTag(SemanticTag t) {
	    this.tags.add(t);
	}
	
	public void removeTag(SemanticTag t) {
	    this.tags.remove(t);
	}
	
	@Override
	public List<SemanticTag> getSemanticTags() {
	    return this.tags;
	}
	
	/**
	 * Adds a resource link (checkbox with text)
	 * @param t a {@link LinkedAnnotationResource}
	 * @param checked whether the checkbox is checked when the link is added
	 */
	private void addLink(SemanticTag t, VerticalPanel p, boolean checked) {
        Checkbox linkBox = new Checkbox();
        linkBox.setValue(checked);
        
        String shortDescription = t.getDescription();
        if(shortDescription.length() > 50) {
            shortDescription = shortDescription.substring(0, 50) + "...";
        }
        linkBox.setHTML("<a title=\"" + t.getDescription() + "\"href=\"" + t.getURI() + "\">" + shortDescription + "</a>");
        linkBox.setStyleName("imageAnnotation-form-checkbox");
        linkBox.setResource(t);
        linkBox.addClickHandler(new SelectImageAnnotationTagClickHandler(this));
	    p.add(linkBox);
	}
	
	/**
	 * Displays suggested entities that can be linked to the annotation
	 * @param entities a collection of {@link GroupOfAmbiguousSemanticTags}-s
	 */
	public void displaySuggestedLinks(Collection<SemanticTagGroup> entities) {
	    suggestedLinksStack.clear();
	    for(SemanticTagGroup e : entities) {
	        suggestedLinksStack.add(new Label(e.getTitle()));
	        for(SemanticTag t : e.getAmbiguousTags()) {
	            if(!this.tags.contains(t)) {
	                addLink(t, suggestedLinksStack, false);
	            }
	        }
	    }
	}

}
