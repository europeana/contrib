package at.ait.dme.yuma.suite.apps.core.client.treeview;

import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;

import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Base class for annotation tree nodes.
 * 
 * @author Rainer Simon
 */
public abstract class AnnotationTreeNode
						extends Composite 
						implements HasMouseOutHandlers, HasMouseOverHandlers {
	
	/**
	 * Reference to the annotation panel
	 */
	protected AnnotationPanel panel;
	
	/**
	 * The annotation
	 */
	protected Annotation annotation;
	
	/**
	 * The parent annotation tree node (if any)
	 */
	protected AnnotationTreeNode parent;

	/**
	 * The this annotation's tree item
	 */
	protected TreeItem treeItem;
	
	public AnnotationTreeNode() { }
	
	public AnnotationTreeNode(AnnotationPanel panel,
					Annotation annotation, AnnotationTreeNode parent) {
		
		this.panel = panel;
		this.annotation = annotation;
		this.parent = parent;
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}
	
	TreeItem getTreeItem() {
		return treeItem;
	}
	
	void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}
	
	public void select() {
		removeStyleName("imageAnnotation");
		addStyleName("imageAnnotation-selected");
	}

	public void deselect() {
		removeStyleName("imageAnnotation-selected");
		addStyleName("imageAnnotation");
	}
	
	public abstract AnnotationTreeNode newInstance(AnnotationPanel panel, 
			Annotation annotation, AnnotationTreeNode parent);
	
	public abstract void showAnnotationForm(AnnotationEditForm editForm);
	
	public abstract void hideAnnotationForm();
	
	public abstract void refresh();
	
	public abstract void clear();
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AnnotationTreeNode))
			return false;
		
		if (this == other)
			return true;

		AnnotationTreeNode node = (AnnotationTreeNode) other;
		return annotation.equals(node.getAnnotation());
	}
	
	@Override
	public int hashCode() {
		return annotation.hashCode();
	}
	
	
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

}
