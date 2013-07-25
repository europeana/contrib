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

package at.ait.dme.yuma.suite.apps.core.client.treeview;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ait.dme.yuma.suite.apps.core.client.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * The annotation tree.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class AnnotationTree extends Tree {
	
	private AnnotationPanel panel;
	
    private HandlerManager handlerManager;	
    
    private AnnotationTreeNode prototype;
    
    private Map<Annotation, AnnotationTreeNode> nodes = new HashMap<Annotation, AnnotationTreeNode>();
    private Map<TreeItem, Annotation> annotations = new HashMap<TreeItem, Annotation>();
    
    public AnnotationTree(AnnotationPanel panel, HandlerManager handlerManager, AnnotationTreeNode prototype) {
    	this.panel = panel;
    	this.handlerManager = handlerManager;
    	this.prototype = prototype;
    	this.setStyleName("imageAnnotation-tree");
    }
    
    /**
     * Workaround for http://code.google.com/p/google-web-toolkit/issues/detail?id=369
     */
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
	
	public void appendChild(AnnotationTreeNode parent, Annotation child) {
		AnnotationTreeNode childNode = prototype.newInstance(panel, child, parent);
		addAnnotation(childNode, parent);
	}

	public void addAnnotation(Annotation annotation) {
		addAnnotation(prototype.newInstance(panel, annotation, null), null);
	}
	
	private void addAnnotation(final AnnotationTreeNode annotation, AnnotationTreeNode parent) {
		TreeItem treeItem;
		if (parent == null) {
			treeItem = this.insertItem(0, annotation);
		} else {
			treeItem = parent.getTreeItem().addItem(annotation);
		}
		annotation.setTreeItem(treeItem);
		
		if (annotation.getAnnotation().hasFragment())
			panel.getMediaViewer().showAnnotation(annotation.getAnnotation());
	
		annotation.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				annotation.select();
				handlerManager.fireEvent(new AnnotationSelectionEvent(annotation.getAnnotation(), true));
			}
		});
		annotation.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				annotation.deselect();
				handlerManager.fireEvent(new AnnotationSelectionEvent(annotation.getAnnotation(), false));
			}
		});
				
		setSelectedItem(annotation.getTreeItem());
		ensureSelectedItemVisible();
		
		if(annotation.getAnnotation().hasReplies()) {
			List<Annotation> replies = sort(annotation.getAnnotation().getReplies());
			
			for(Annotation reply : replies) {
				AnnotationTreeNode node = prototype.newInstance(panel, reply, annotation);	
				addAnnotation(node, annotation);				
			}			
		}
		
		nodes.put(annotation.getAnnotation(), annotation);
		annotations.put(treeItem, annotation.getAnnotation());
	}
	
	public void removeAnnotation(Annotation annotation) {
		AnnotationTreeNode node = nodes.get(annotation);
		TreeItem treeItem = node.getTreeItem();
		annotations.remove(treeItem);
		treeItem.remove();
		nodes.remove(annotation);
		node.clear();
	}
	
	public void showAnnotationEditForm(AnnotationTreeNode annotation, AnnotationEditForm editForm) {		
		annotation.showAnnotationForm(editForm);
	}
	
	public void hideAnnotationEditForm(AnnotationTreeNode annotation) {
		annotation.hideAnnotationForm();
	}
	
	private List<Annotation> sort(List<Annotation> annotations) {
		Collections.sort(annotations, new Comparator<Annotation>() {
			public int compare(Annotation o1, Annotation o2) {
				return o1.getCreated().compareTo(o2.getCreated());
			}					
		});
		return annotations;
	}
	
	public void selectAnnotation(Annotation annotation, boolean selected) {
		final AnnotationTreeNode node = nodes.get(annotation);
		if (node == null)
			return;
		
		if(selected) {
			node.select();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					setSelectedItem(node.getTreeItem());
					ensureSelectedItemVisible();
				}
			});
		} else {
			node.deselect();
		}
	}
	
	public Annotation getParentAnnotation(Annotation annotation) {		
		AnnotationTreeNode node = nodes.get(annotation);
		if (node == null)
			return null;
		
		TreeItem parentItem = node.getTreeItem().getParentItem();
		if (parentItem == null)
			return null;
		
		return annotations.get(parentItem);
	}
	
	@Override
	public void removeItems() {
		nodes.clear();
		annotations.clear();
		super.removeItems();
	}

}
