package at.ait.dme.yuma.suite.apps.image.core.client.tagging;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

public class TagTreeNode extends Composite {
	
	/**
	 * The label
	 */
	private Label label;
	
	private String uri = null;
	
	private SemanticTag tag = null;
	
	/**
	 * The this tag's tree item
	 */
	protected TreeItem treeItem;
	
	public TagTreeNode(String uri) {
		this.uri = uri;
		this.label = new Label(uri);
		this.label.setStyleName("vocabularyBrowser-treeNode");
		initWidget(label);
	}
	
	public TagTreeNode(SemanticTag tag) {
		this.tag = tag;
		this.label = new Label(tag.getPrimaryLabel());
		this.label.setStyleName("vocabularyBrowser-treeNode");
		initWidget(label);
	}
	
	String getURI() {
		return uri;
	}
	
	SemanticTag getTag() {
		return tag;
	}
	
	TreeItem getTreeItem() {
		return treeItem;
	}
	
	void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

}
