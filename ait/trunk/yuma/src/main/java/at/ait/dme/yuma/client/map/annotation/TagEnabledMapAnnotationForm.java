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

package at.ait.dme.yuma.client.map.annotation;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.gwt.mosaic.ui.client.MessageBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.annotation.SemanticTagGroup;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationComposite;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationForm;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationTreeNode;
import at.ait.dme.yuma.client.image.annotation.StandardImageAnnotationForm;
import at.ait.dme.yuma.client.server.SemanticEnrichmentService;
import at.ait.dme.yuma.client.server.SemanticEnrichmentServiceAsync;

/**
 * A sub-class of {@link StandardImageAnnotationForm} that works with the {@link AnnotationLayer}'s
 * tag cloud.
 * 
 * @author Rainer Simon
 *
 */
public class TagEnabledMapAnnotationForm extends StandardImageAnnotationForm {
	
	/**
	 * Reference to the annotation layer
	 */
	private AnnotationLayer lAnnotations;
	
	/**
	 * Reference to the annotationComposite
	 */
	private ImageAnnotationComposite annotationComposite;
	
	/**
	 * The FlowPanel displaying the current tags  
	 */
	private FlowPanel tagPanel;
	
	/**
	 * The tags for this annotation
	 */
	public HashMap<SemanticTag, Widget> tags = new HashMap<SemanticTag, Widget>(); 
	
	/**
	 * Semantic enrichment service
	 */
	private SemanticEnrichmentServiceAsync enrichmentService = 
		(SemanticEnrichmentServiceAsync) GWT.create(SemanticEnrichmentService.class);
	
	/**
	 * Enrichment service type constant
	 */
	private static final String WHICH_ENRICHMENT_SERVICE = 
		SemanticEnrichmentService.OPENCALAIS_DBPEDIA_LOOKUP; 

	public TagEnabledMapAnnotationForm(AnnotationLayer annotationLayer) {
		this.lAnnotations = annotationLayer;
	}
	
	public TagEnabledMapAnnotationForm(ImageAnnotationComposite annotationComposite,
			AnnotationLayer annotationLayer, ImageAnnotationTreeNode annotationTreeNode,
			boolean fragmentAnnotation, boolean update) {
	
		super(annotationComposite, annotationTreeNode, fragmentAnnotation, update);
		
		this.annotationComposite = annotationComposite;
		this.lAnnotations = annotationLayer;
	
		annotationLayer.setAnnotationForm(this);
		if (update && annotationTreeNode.getAnnotation().hasSemanticTags()) {
			for (SemanticTag t : annotationTreeNode.getAnnotation().getSemanticTags()) {
				addTag(t);
			}
		}
	}
	
	@Override
	public ImageAnnotationForm createNew(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, boolean fragmentAnnotation, boolean update) {
		return new TagEnabledMapAnnotationForm(annotationComposite, lAnnotations, annotationTreeNode,
				fragmentAnnotation, update);
	}
	
	@Override
	protected Panel createLinksPanel(boolean update, ImageAnnotationTreeNode annotationTreeNode) {
		tagPanel = new FlowPanel();
		tagPanel.setStyleName("imageAnnotation-taglist");
		return tagPanel;
	}
	
	@Override
	protected Panel createSemanticLinksPanel(boolean update, ImageAnnotationTreeNode annotationTreeNode) {
		return new FlowPanel();
	}
	
	@Override
	protected KeyDownHandler createKeyDownHandler(ImageAnnotationComposite annotationComposite) {
		return new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
	            if (event.getNativeKeyCode() == ' ' || event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
	            	enrichmentService.getTagSuggestions(textArea.getValue(), WHICH_ENRICHMENT_SERVICE, 
	            			new AsyncCallback<Collection<SemanticTagGroup>>() {
						@Override
						public void onSuccess(Collection<SemanticTagGroup> result) {
							for (SemanticTagGroup group : result) {
								for (SemanticTag tag : group.getAmbiguousTags()) {
									lAnnotations.modifyTagCloud(new SemanticTag[] { tag });
								}
							}
						}
						
						@Override
						public void onFailure(Throwable t) {
							ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
							MessageBox.error(errorMessages.error(), t.getMessage());
						}
					});
	            }
			}
		};
	}	
	
	@Override
	public void addTag(SemanticTag tag) {
		InlineHTML span = new InlineHTML("<a target=\"_blank\" href=\""
				+ tag.getURI() + "\" title=\"" 
				+ tag.getDescription() + "\">" 
				+ tag.getTitle() + "</a>"
		);
		tagPanel.add(span);
		tags.put(tag, span);
		annotationComposite.layout();
	}
	
	@Override
	public void removeTag(SemanticTag tag) {
		tagPanel.remove(tags.get(tag));
		tags.remove(tag);
		annotationComposite.layout();
	}
	
	@Override
	public List<SemanticTag> getSemanticTags() {
	    return new ArrayList<SemanticTag>(tags.keySet());
	}

}
