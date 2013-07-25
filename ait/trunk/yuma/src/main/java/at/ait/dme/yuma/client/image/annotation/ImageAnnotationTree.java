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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ait.dme.yuma.client.image.ImageComposite;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionEvent;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * A tree widget displaying an annotation thread.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationTree extends Tree {
	
	// map an annotation to its node
    private Map<ImageAnnotation, ImageAnnotationTreeNode> annotationNodes = new HashMap<ImageAnnotation, ImageAnnotationTreeNode>();
	
    private HandlerManager handlerManager;	
    private ImageComposite imageComposite;
    private ImageAnnotationComposite annotationComposite;

    public ImageAnnotationTree(List<ImageAnnotation> annotations, 
    		HandlerManager handlerManager, ImageComposite imageComposite, 
    		ImageAnnotationComposite annotationComposite) {
    	this.handlerManager = handlerManager;
    	this.imageComposite = imageComposite;
    	this.annotationComposite = annotationComposite;
    	this.setStyleName("imageAnnotation-tree");
    	this.build(annotations);
    }
    
    // workaround for http://code.google.com/p/google-web-toolkit/issues/detail?id=369
	public void onBrowserEvent(Event event) {
    	if (DOM.eventGetType(event) == Event.ONMOUSEDOWN
        		|| DOM.eventGetType(event) == Event.ONMOUSEUP
        		|| DOM.eventGetType(event) == Event.ONCLICK
        		|| DOM.eventGetType(event) == Event.ONKEYDOWN
        		|| DOM.eventGetType(event) == Event.ONKEYUP
        		|| DOM.eventGetType(event) == Event.ONKEYPRESS)
        		
        	return;

         super.onBrowserEvent(event);
     }
    
	
	/**
	 * build the annotation tree
	 * root annotations are sorted descending by creation date, replies are 
	 * sorted ascending by creation date.
	 * 
	 * @param annotations
	 */
	public void build(List<ImageAnnotation> annotations) {
		removeItems();	
		setVisible(false);

		// sort the annotations descending by creation date		
		Collections.sort(annotations, new Comparator<ImageAnnotation>() {
			public int compare(ImageAnnotation o1, ImageAnnotation o2) {
				return o2.getCreated().compareTo(o1.getCreated());
			}					
		});
		
		for(ImageAnnotation annotation : annotations) {
			addAnnotation(annotation, null, null);
		}
		
		setVisible(true);
	}
	
	/**
	 * add the given annotation and all replies to the annotation tree
	 * 
	 * @param annotation
	 * @param parentAnnotation
	 * @param parent
	 */
	private void addAnnotation(final ImageAnnotation annotation, ImageAnnotation parentAnnotation,
			TreeItem parent) {		
			
		// create the tree node
		final ImageAnnotationTreeNode node = 
			new ImageAnnotationTreeNode(annotationComposite, annotation, parentAnnotation);		
		node.setAnnotationTreeItem((parent==null)?this.addItem(node):parent.addItem(node));
		node.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				node.setStyleName("imageAnnotation");
				handlerManager.fireEvent(new ImageAnnotationSelectionEvent(annotation, false));
			}
		});
		node.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				node.setStyleName("imageAnnotation-selected");
				handlerManager.fireEvent(new ImageAnnotationSelectionEvent(annotation, true));
			}
		});
		annotationNodes.put(annotation, node);
		
		// show the fragment
		if(annotation.hasFragment()) {
			DeferredCommand.addCommand(new Command(){
				public void execute() { 		
					imageComposite.showFragment(node.getAnnotation());
				}
			});
		}
				
		setSelectedItem(node.getAnnotationTreeItem());
		ensureSelectedItemVisible();
		
		// process replies
		if(annotation.hasReplies()) {
			// sort replies ascending by creation date
			List<ImageAnnotation> replies = annotation.getReplies();
			Collections.sort(replies, new Comparator<ImageAnnotation>() {
				public int compare(ImageAnnotation o1, ImageAnnotation o2) {
					return o1.getCreated().compareTo(o2.getCreated());
				}					
			});
			// add replies to the tree
			for(ImageAnnotation reply : replies) {
				addAnnotation(reply, annotation, node.getAnnotationTreeItem());				
			}			
		}
	}
	
    public ImageAnnotationTreeNode getAnnotationNode(ImageAnnotation annotation) {
    	return annotationNodes.get(annotation);
    }
    

	@Override
	public void removeItems() {
		annotationNodes.clear();
		super.removeItems();
	}

	public void selectAnnotationTreeNode(final ImageAnnotationTreeNode node, boolean selected) {
		if(selected) {
			node.setStyleName("imageAnnotation-selected");
			DeferredCommand.addCommand(new Command(){
				public void execute() { 		
					setSelectedItem(node.getAnnotationTreeItem());
					ensureSelectedItemVisible();
				}
			});
		} else {
			node.setStyleName("imageAnnotation");
		}
	}    
}
