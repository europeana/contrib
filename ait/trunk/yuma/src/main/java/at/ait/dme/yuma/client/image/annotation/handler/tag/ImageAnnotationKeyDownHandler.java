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

package at.ait.dme.yuma.client.image.annotation.handler.tag;

import java.util.Collection;

import at.ait.dme.yuma.client.annotation.SemanticTagGroup;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationComposite;
import at.ait.dme.yuma.client.image.annotation.StandardImageAnnotationForm;
import at.ait.dme.yuma.client.server.SemanticEnrichmentService;
import at.ait.dme.yuma.client.server.SemanticEnrichmentServiceAsync;
import at.ait.dme.yuma.client.server.exception.SemanticEnrichmentServiceException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.TextArea;

public class ImageAnnotationKeyDownHandler implements KeyDownHandler {

    private StandardImageAnnotationForm annotationForm;
    private ImageAnnotationComposite annotationComposite;
    private Collection<SemanticTagGroup> entities = null;
    private boolean serviceEnabled = true;
    
	/**
	 * Enrichment service type constant
	 */
	private static final String WHICH_ENRICHMENT_SERVICE = 
		SemanticEnrichmentService.OPENCALAIS_DBPEDIA_LOOKUP; 


    public ImageAnnotationKeyDownHandler(StandardImageAnnotationForm form, 
    		ImageAnnotationComposite annotationComposite) {
        this.annotationForm = form;
        this.annotationComposite = annotationComposite;
    }

    public void onKeyDown(KeyDownEvent event) {
        if(serviceEnabled) {
            int b = 0;
            if((event.getNativeKeyCode() == 32) || 
            		(event.getNativeKeyCode()==KeyCodes.KEY_BACKSPACE && b % 2 == 0)) {
                b++;
                // annotationComposite.enableLoadingImage();
                TextArea w = (TextArea) event.getSource();
                try {
                    loadEntities(w.getText());
                } catch (SemanticEnrichmentServiceException e) {
                    // die, silently, to not annoy the user
                    //Alert.error("Error while running semantic enrichment service");
                }
            }
        }
    }

    private void loadEntities(String text) throws SemanticEnrichmentServiceException {
        SemanticEnrichmentServiceAsync service = (SemanticEnrichmentServiceAsync) 
        	GWT.create(SemanticEnrichmentService.class);
        service.getTagSuggestions(text, WHICH_ENRICHMENT_SERVICE, 
        		new AsyncCallback<Collection<SemanticTagGroup>>() {
        	
            public void onFailure(Throwable caught) {
                // annotationComposite.disableLoadingImage();
                if(caught instanceof StatusCodeException && 
                		((StatusCodeException)caught).getStatusCode() == 404) {
                    // the servlet is disabled, don't try to load entities anymore
                    serviceEnabled = false;
                } else {
                    // die, silently, to not annoy the user
                    //Alert.error("Semantic enrichment service failed");
                    //caught.printStackTrace();
                }
            }

            public void onSuccess(Collection<SemanticTagGroup> result) {
                entities = result;
                if(entities != null) {
                    annotationForm.displaySuggestedLinks(entities);
                }
                // annotationComposite.disableLoadingImage();
                annotationComposite.layout();
            }
        });
    }
}
