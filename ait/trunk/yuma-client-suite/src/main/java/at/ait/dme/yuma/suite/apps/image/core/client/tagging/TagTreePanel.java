package at.ait.dme.yuma.suite.apps.image.core.client.tagging;

import java.util.Collection;

import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagService;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class TagTreePanel extends Composite {
	
	/**
	 * The parent container panel
	 */
	private ScrollPanel containerPanel = new ScrollPanel();
	
	/**
	 * The tree
	 */
	private Tree tree;
	
	/**
	 * Reference to the tag service
	 */
	private TagServiceAsync tagService = (TagServiceAsync) GWT.create(TagService.class);
	
	public TagTreePanel(final AnnotationEditForm editForm) {
		tree = new Tree();
		tree.addOpenHandler(new OpenHandler<TreeItem>() {
			@Override
			public void onOpen(OpenEvent<TreeItem> evt) {
				TreeItem item = evt.getTarget();
				for (int i=0; i<item.getChildCount(); i++) {
					TagTreeNode node = (TagTreeNode) item.getChild(i).getUserObject();
					loadChildren(node);
				}
			}
		});
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> evt) {
				TagTreeNode node = (TagTreeNode) evt.getSelectedItem().getUserObject();
				if (node.getURI() == null)
					editForm.addTag(node.getTag());
			}
		});
		
		containerPanel.add(tree);
		containerPanel.setStyleName("vocabularyBrowser");
		
		initWidget(containerPanel);
		loadVocabularies();
	}
	
	private void addTag(TagTreeNode tag, TagTreeNode parent) {
		TreeItem treeItem;
		if (parent == null) {
			treeItem = tree.addItem(tag);
		} else {
			treeItem = parent.getTreeItem().addItem(tag);
		}
		treeItem.setUserObject(tag);
		tag.setTreeItem(treeItem);
	}
	
	private void loadVocabularies() {
		tagService.getVocabularies(new AsyncCallback<Collection<String>>() {
			@Override
			public void onSuccess(Collection<String> uris) {
				for (String uri : uris) {
					TagTreeNode rootNode = new TagTreeNode(uri);
					addTag(rootNode, null);
					loadChildren(rootNode);
				}
			}
			
			@Override
			public void onFailure(Throwable t) {
				// Ignore
			}
		});
	}
	
	private void loadChildren(final TagTreeNode parent) {
		String parentUri = parent.getURI();
		if (parentUri == null)
			parentUri = parent.getTag().getURI();
		
		tagService.getChildren(parentUri, new AsyncCallback<Collection<SemanticTag>>() {
			@Override
			public void onFailure(Throwable t) {
				// Ignore
			}

			@Override
			public void onSuccess(Collection<SemanticTag> tags) {
				for (SemanticTag t : tags) {
					TagTreeNode treeNode = new TagTreeNode(t); 
					addTag(treeNode, parent);
				}
			}
		});
	}

}
