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

package at.ait.dme.yuma.suite.apps.image.core.client.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.suite.apps.core.client.I18NConstants;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.events.AbstractKeyboardHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.CancelClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.SaveClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.UpdateClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.client.widgets.MinMaxWindowPanel;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.model.User;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation.MediaType;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation.Scope;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.NERService;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.NERServiceAsync;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.TagCloud;
import at.ait.dme.yuma.suite.apps.image.core.client.tagging.TagSuggestBox;
import at.ait.dme.yuma.suite.apps.image.core.client.tagging.TagTreePanel;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageAnnotation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Image-specific sub-class of the AnnotationEdit form. The media type
 * can be configured (so that it can be used for Images and Maps); and 
 * ti is compatible with the Tag Cloud.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ImageAnnotationEditForm extends AnnotationEditForm {
	
	private static final String SCOPE_RADIO_GROUP_NAME = "scope";
	
	private VerticalPanel formPanel = new VerticalPanel();

	private TextBox titleTextBox = new TextBox();	
	private TextArea textArea = new TextArea();
	
	private static final String DELETE_TAG_ICON = "images/remove_tag.png";
	
	private RadioButton rdPublic, rdPrivate = null;

	public HashMap<SemanticTag, Widget> tags = new HashMap<SemanticTag, Widget>(); 
	
	private FlowPanel tagPanel;
    
    private MediaType mediaType;
    
	private TagCloud tagCloud;
	
	private I18NConstants i18n = YUMACoreProperties.getConstants();
	
	private NERServiceAsync enrichmentService = 
		(NERServiceAsync) GWT.create(NERService.class);

    public ImageAnnotationEditForm(MediaType mediaType, TagCloud tagCloud) {
    	super();
    	
    	this.mediaType = mediaType;
    	this.tagCloud = tagCloud;
    }
    
	public ImageAnnotationEditForm(AnnotationPanel panel, 
			AnnotationTreeNode annotation, AnnotationTreeNode parent,
			MediaType mediaType, TagCloud tagCloud) {
		
		super(panel, annotation, parent);
		
		this.mediaType = mediaType;
		this.tagCloud = tagCloud;
		
    	formPanel.setStyleName("annotationEditForm");		
		formPanel.add(createTitlePanel());
		formPanel.add(createTextPanel());
		formPanel.add(createAddNewTagPanel());
		formPanel.add(createScopePanel());
		formPanel.add(createTagsPanel());
		formPanel.add(createButtonsPanel());
		
		panel.getMediaViewer().setAnnotationEditForm(this);
		if (annotation != null && annotation.getAnnotation().hasTags()) {
			for (SemanticTag t : annotation.getAnnotation().getTags()) {
				addTag(t);
			}
		}
		
	 	initWidget(formPanel);
	}
	
	@Override
	public AnnotationEditForm newInstance(AnnotationPanel panel, AnnotationTreeNode annotation,
			AnnotationTreeNode parent) {
		
		return new ImageAnnotationEditForm(panel, annotation, parent, mediaType, tagCloud);
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
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					rdPublic.setValue(pub);
					rdPrivate.setValue(prv);			
				}
			});
		}
	}

	protected FlowPanel createTitlePanel() {
		FlowPanel titlePanel = new FlowPanel();
		
		Label titleLabel = new Label(i18n.title());
		titleLabel.setStyleName("annotationEditForm-label");
		
		titleTextBox.setStyleName("annotationEditForm-title-input");
		if (annotation == null && parent != null) {
			titleTextBox.setText("RE: " + parent.getAnnotation().getTitle());
		} else if (annotation != null) { 
			titleTextBox.setText(annotation.getAnnotation().getTitle());
		}
			
		titlePanel.add(titleLabel);		
		titlePanel.add(titleTextBox);
		return titlePanel;		
	}
	
	protected FlowPanel createTextPanel() {
		FlowPanel textPanel = new FlowPanel();
		
		Label textLabel = new Label(i18n.text());
		textLabel.setStyleName("annotationEditForm-label");		
		
		textArea.setStyleName("annotationEditForm-text-input");
		if (annotation != null)
			textArea.setText(annotation.getAnnotation().getText());
		textArea.addKeyDownHandler(new AbstractKeyboardHandler(1000) {
			@Override
			public void onSpace() {
				getTagSuggestions();
			}
			
			@Override
			public void onIdle() {
				getTagSuggestions();
			}
		});
		
		textPanel.add(textLabel);
		textPanel.add(textArea);
		
		return textPanel;
	}
	
	protected FlowPanel createAddNewTagPanel() {
		FlowPanel container = new FlowPanel();
		container.setStyleName("annotationEditForm-tag");
		
		Label addTagLabel = new Label(i18n.addTag());
		addTagLabel.setStyleName("annotationEditForm-label");	
		
		HorizontalPanel addTagPanel = new HorizontalPanel();
		final TagSuggestBox newTagTextBox = new TagSuggestBox(10);	
		newTagTextBox.setStyleName("annotationEditForm-tag-input");
		newTagTextBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> evt) {
				addTag(newTagTextBox.getTag());
				newTagTextBox.clear();
			}
		});
		addTagPanel.add(newTagTextBox);
		
		PushButton btnAdd = new PushButton(i18n.add());
		btnAdd.addStyleName("annotationEditForm-tag-btn-add");
		btnAdd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent evt) {
				addTag(newTagTextBox.getTag());
				newTagTextBox.clear();
			}
		});
		addTagPanel.add(btnAdd);
		
		PushButton btnBrowse = new PushButton(i18n.browse());
		btnBrowse.addStyleName("annotationEditForm-tag-btn-browse");
		
		final TagTreePanel tagTreePanel = new TagTreePanel(this);
		btnBrowse.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(200, 200, 350, 450);
				window.setWidget(tagTreePanel);
				window.getHeader().setText(i18n.vocabularyBrowser());
				window.show();
			}
		});
		addTagPanel.add(btnBrowse);
		
		container.add(addTagLabel);
		container.add(addTagPanel);
		
		return container;
	}
	
	protected HorizontalPanel createScopePanel() {
	    HorizontalPanel radioPanel = new HorizontalPanel();

		Label scopeLabel = new Label();
		scopeLabel.setStyleName("imageAnnotation-form-label");         
	     
        rdPublic = new RadioButton(SCOPE_RADIO_GROUP_NAME,
                " " + i18n.publicScope());
        rdPublic.setStyleName("imageAnnotation-form-radiobutton");
        
        rdPrivate = new RadioButton(SCOPE_RADIO_GROUP_NAME,
                " " + i18n.privateScope());
        rdPrivate.setStyleName("imageAnnotation-form-radiobutton");     
        		
		if (annotation != null && annotation.getAnnotation().getScope() == Scope.PRIVATE) {
			rdPrivate.setValue(true, true);
		} else {
			rdPublic.setValue(true, true);
		}       

        radioPanel.add(scopeLabel);
        radioPanel.add(rdPublic);
        radioPanel.add(rdPrivate);
        
		return radioPanel;
	}
	
	protected Panel createTagsPanel() {
	    tagPanel = new FlowPanel();
		tagPanel.setStyleName("imageAnnotation-taglist");
		return tagPanel;
	}
	
	protected HorizontalPanel createButtonsPanel() {
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		
		PushButton btnSave = new PushButton(i18n.save());
		btnSave.setStyleName("imageAnnotation-form-button");
		if (annotation == null) {
			btnSave.addClickHandler(new SaveClickHandler(panel, parent, this));	
		} else {
			btnSave.addClickHandler(new UpdateClickHandler(panel, annotation, parent, this));
		}
		buttonsPanel.add(btnSave);
		
		PushButton btnCancel = new PushButton(i18n.cancel());
		btnCancel.setStyleName("imageAnnotation-form-button");
		if (annotation == null) {
			btnCancel.addClickHandler(new CancelClickHandler(panel, parent));
		} else {
			btnCancel.addClickHandler(new CancelClickHandler(panel, annotation));
		}
		buttonsPanel.add(btnCancel);
		
		return buttonsPanel;
	}
	
	@Override
	public Annotation getAnnotation() {
		Date timestamp = new Date();
		
		Annotation a = new ImageAnnotation();
		a.setObjectUri(YUMACoreProperties.getObjectURI());
		
		if (parent != null) {
			a.setParentId(parent.getAnnotation().getId());
		
			String rootId = parent.getAnnotation().getRootId();
			if (rootId == null)
				rootId = parent.getAnnotation().getId();
			a.setRootId(rootId);
		}
		
		if (annotation == null) {
			a.setCreated(timestamp);
		} else {
			a.setCreated(annotation.getAnnotation().getCreated());
		}
		
		a.setLastModified(timestamp);
		a.setCreatedBy(User.get());
		a.setMediaType(mediaType);
		a.setTitle(titleTextBox.getText());
		a.setText(textArea.getText());

		if (rdPublic.getValue()) {
			a.setScope(Scope.PUBLIC);
		} else {
			a.setScope(Scope.PRIVATE);
		}
		
		a.setTags(new ArrayList<SemanticTag>(tags.keySet()));			
		a.setFragment(panel.getMediaViewer().getActiveMediaFragment());		
		return a;
	}
	
	@Override
	public void addTag(final SemanticTag tag) {
		if (tag == null)
			return;
		
		if (tags.containsKey(tag))
			return;
		
		FlowPanel container = new FlowPanel();
		
		InlineHTML span = new InlineHTML("<a target=\"_blank\" href=\""
				+ tag.getURI() + "\" title=\"" 
				+ tag.getPrimaryDescription() + "\">" 
				+ tag.getPrimaryLabel() + "</a>"
		);
		container.add(span);
		
		PushButton x = new PushButton(new Image(DELETE_TAG_ICON));
		x.setTitle("Remove Tag '" + tag.getPrimaryLabel() + "'");
		x.setStyleName("annotationEditForm-tag-btn-delete");
		x.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				removeTag(tag);
			}
		});
		container.add(x);
		
		tagPanel.add(container);
		tags.put(tag, container);
		panel.layout();
	}
	
	@Override
	public void removeTag(SemanticTag tag) {
		tagPanel.remove(tags.get(tag));
		tags.remove(tag);
		panel.layout();
	}
	
	private void getTagSuggestions() {
    	enrichmentService.getTagSuggestions(textArea.getText(),
    			new AsyncCallback<Collection<SemanticTag>>() {
					@Override
					public void onFailure(Throwable t) {
						// Do nothing...
					}

					@Override
					public void onSuccess(Collection<SemanticTag> result) {
						if (result.size() > 0 && !tagCloud.isVisible()) tagCloud.show();
						
						for (SemanticTag tag : result) {
							tagCloud.addTag(tag, 22, "#ffd77d");
						}
					}
				}
    	);
	}

}
