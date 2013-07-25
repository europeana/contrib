package at.researchstudio.dme.imageannotation.client.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import at.researchstudio.dme.imageannotation.client.AnnotationConstants;
import at.researchstudio.dme.imageannotation.client.Application;
import at.researchstudio.dme.imageannotation.client.ErrorMessages;
import at.researchstudio.dme.imageannotation.client.annotation.listener.CreateAnnotationClickListener;
import at.researchstudio.dme.imageannotation.client.annotation.map.MapAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.dnd.DraggableWindowComposite;
import at.researchstudio.dme.imageannotation.client.image.ImageComposite;
import at.researchstudio.dme.imageannotation.client.image.ImageFragmentContainer;
import at.researchstudio.dme.imageannotation.client.image.ImageFragmentSelectionListener;
import at.researchstudio.dme.imageannotation.client.server.ImageAnnotationService;
import at.researchstudio.dme.imageannotation.client.server.ImageAnnotationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * image annotation composite shows the annotation tree and allows
 * to create, update, and delete annotations.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationComposite extends DraggableWindowComposite 
	implements ImageFragmentSelectionListener {
	
	// reference to the image composite
	private ImageComposite imageComposite = null;
	
	// the annotation tree is embedded in a scroll panel
	private AnnotationTree annotationTree = new AnnotationTree();
	private ScrollPanel scrollPanel = new ScrollPanel();
	
	// panel for the annotation form
	private VerticalPanel annotationFormPanel = new VerticalPanel();
	
	// image to be displayed while loading
	private Image loadingImage = new Image("images/loading.gif");
	
	// action buttons
	private PushButton annotateButton = new PushButton();
	private PushButton annotateFragmentButton = new PushButton();
	private PushButton showOnMapButton = new PushButton();
	
	// all currently displayed annotations
	private ArrayList<ImageAnnotation> annotations = null;
	
	// saves the scroll position when the annotation form is displayed
	private int scrollPosition = 0;
	
	// annotation constants
	private AnnotationConstants annotationConstants=
		(AnnotationConstants)GWT.create(AnnotationConstants.class);	
	
	/**
	 * the image annotation composite constructor. needs an image composite
	 * to show and hide fragments.
	 * 
	 * @param imageComposite
	 */
	public ImageAnnotationComposite(ImageComposite imageComposite) {	
		this.imageComposite = imageComposite;
		this.setStyleName("imageAnnotation-composite");
					
		lbTitle.setText(annotationConstants.annotations());
		closeWindowImage.setStyleName("imageAnnotation-window-bar-close");
		
		showHints();			
		showButtons();
		
		// place holder for the annotation form
		compositePanel.add(annotationFormPanel);
		
		// annotation tree	
		scrollPanel.add(annotationTree);
		scrollPanel.setPixelSize(400, 400);
		scrollPanel.setAlwaysShowScrollBars(false);
		scrollPanel.setHorizontalScrollPosition(0);
		populateTree();		
		compositePanel.add(scrollPanel);
		makeNorthResizable();
	}
	
	/**
	 * show hints and create link to help page
	 */
	private void showHints() {
		AbsolutePanel addAnnotationPanel = new AbsolutePanel();
		Label addAnnotationLabel = new Label(annotationConstants.addAnnotation());
		addAnnotationLabel.setStyleName("imageAnnotation-add-annotation");
		addAnnotationPanel.add(addAnnotationLabel);
						
		HTML help = new HTML("<a target=\"_blank\" href=\"userguide_" + 
				LocaleInfo.getCurrentLocale().getLocaleName()+".html\">" + 
				annotationConstants.help() + "</a>" );
		help.setStyleName("imageAnnotation-help");
		addAnnotationPanel.add(help);				
		loadingImage.setStyleName("imageAnnotation-loading");
		addAnnotationPanel.add(loadingImage);			
		compositePanel.add(addAnnotationPanel);
		
		Label addAnnotationHint = new Label(annotationConstants.addAnnotationHint()); 
		addAnnotationHint.setStyleName("imageAnnotation-add-annotation-hint");
		compositePanel.add(addAnnotationHint);		
	}
	
	/**
	 * create all buttons
	 */
	private void showButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		annotateButton.setStyleName("imageAnnotation-button");
		annotateButton.setText(annotationConstants.actionCreate());	
		annotateButton.addClickHandler(
				new CreateAnnotationClickListener(this,null,false,false));
		annotateButton.setEnabled(!Application.getUser().isEmpty());
		buttons.add(annotateButton);
		
		annotateFragmentButton.setStyleName("imageAnnotation-button");
		annotateFragmentButton.setText(annotationConstants.actionCreateFragment());
		annotateFragmentButton.addClickHandler(
				new CreateAnnotationClickListener(this,null,true,false));		
		annotateFragmentButton.setEnabled(!Application.getUser().isEmpty());
		buttons.add(annotateFragmentButton);				
		
		showOnMapButton.setStyleName("imageAnnotation-button");
		showOnMapButton.setText(annotationConstants.actionShowOnMap());
		showOnMapButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopupPanel popup = new PopupPanel();
				popup.add(new MapAnnotationComposite(annotations));
				popup.setPopupPosition(550, 300);
				popup.show();
				popup.setStyleName("imageAnnotation-popup-map");
			}			
		});			
		showOnMapButton.setVisible(Application.getBbox()!=null);
		buttons.add(showOnMapButton);				
	
		compositePanel.add(buttons);		
	}
	
	/**
	 * shows the annotation form either underneath a tree node in case of an
	 * update or reply to an existing annotation, or above the annotation tree
	 * in case of an new annotation. if it is a fragment annotation it will also
	 * show the active fragment panel.
	 * 
	 * @param annotationTreeNode
	 * @param fragmentAnnotation true if annotation has a fragment, otherwise false
	 * @param update true if the user updates an existing annotation, otherwise false
	 */
	public void showAnnotationForm(final ImageAnnotationTreeNode annotationTreeNode, 
			boolean fragmentAnnotation, boolean update) {					

		final ImageAnnotationForm annotationForm = new ImageAnnotationForm(this, 
				annotationTreeNode, fragmentAnnotation, update);
		
		scrollPosition = scrollPanel.getScrollPosition();	
		
		if(annotationTreeNode!=null) {
			annotationTreeNode.showAnnotationForm(annotationForm);
			DeferredCommand.addCommand(new Command(){
				public void execute() { 
					scrollPanel.ensureVisible(annotationForm);
			    }
			});
			
			if(fragmentAnnotation) {
				// if we update an existing annotation we first have to remove the
				// existing fragment and show an active fragment panel instead.
				if(update && annotationTreeNode.getImageFragment()!=null) {					
					imageComposite.showActiveFragmentPanel(annotationTreeNode, true);
					imageComposite.hideFragment(annotationTreeNode);
				} else {
					imageComposite.showActiveFragmentPanel(null, true);
				}
			}
		} else {
			annotateButton.setEnabled(false);
			annotateFragmentButton.setEnabled(false);
			annotationFormPanel.add(annotationForm);
			if(fragmentAnnotation) imageComposite.showActiveFragmentPanel(null, true);
		}
	}
	
	/**
	 *  hides the annotation form, again either from above the annotation tree or from
	 *  underneath a tree node, also hides the active fragment panel. if in case of
	 *  an update the operation was canceled we have to restore a possible fragment.
	 *  
	 *  @param annotationTreeNode
	 * 	@param canceled true if the user canceled the operation, otherwise false
	 *  @see #showAnnotationForm(ImageAnnotationTreeNode, boolean, boolean)
	 */
	public void hideAnnotationForm(final ImageAnnotationTreeNode annotationTreeNode, 
			boolean canceled) {
	
		if(annotationTreeNode==null) {
			annotationFormPanel.clear();
			annotateButton.setEnabled(true);
			annotateFragmentButton.setEnabled(true);
		} else {
			annotationTreeNode.hideAnnotationForm();
			DeferredCommand.addCommand(new Command(){
				public void execute() { 
					scrollPanel.setScrollPosition(scrollPosition);
					annotationTree.setSelectedItem(annotationTreeNode.getAnnotationTreeItem());
			    }
			});
		}
		
		imageComposite.hideActiveFragmentPanel();
		if(canceled && annotationTreeNode != null && 
				annotationTreeNode.getImageFragment()!=null) { 
			imageComposite.showFragment(annotationTreeNode);
		}
	}
	
	/**
	 * mark the corresponding annotation node for the given fragment as selected
	 * 
	 * @param node
	 */
	public void onSelectImageFragment(ImageFragmentContainer fragmentContainer) {
		if(!(fragmentContainer instanceof ImageAnnotationTreeNode)) return;
		final ImageAnnotationTreeNode node = (ImageAnnotationTreeNode) fragmentContainer;
		node.setStyleName("imageAnnotation-selected");
		DeferredCommand.addCommand(new Command(){
			public void execute() { 		
				annotationTree.setSelectedItem(node.getAnnotationTreeItem());
				annotationTree.ensureSelectedItemVisible();
				scrollPanel.ensureVisible(node.getAnnotationTreeItem());
			}
		});
	}
	
	/**
	 * remove selection from the corresponding annotation node
	 * 
	 * @param node
	 */
	public void onDeselectImageFragment(ImageFragmentContainer fragmentContainer) {
		if(!(fragmentContainer instanceof ImageAnnotationTreeNode)) return;
		ImageAnnotationTreeNode node = (ImageAnnotationTreeNode) fragmentContainer;
		node.setStyleName("imageAnnotation");
	}
	
	/**
	 * build the annotation tree
	 */
	public void buildAnnotationTree() {
		buildAnnotationTree(annotations);
	}
	
	/**
	 * adds an new annotation to the annotation tree
	 * 
	 * @param annotation
	 * @param parent
	 */
	public void addAnnotation(ImageAnnotation annotation, ImageAnnotation parent) {
		if(annotations==null) annotations = new ArrayList<ImageAnnotation>();
		
		if(parent!=null) {
			parent.addReply(annotation);
		} else {
			annotations.add(annotation);
		}
		
		buildAnnotationTree(annotations);
	}
	
	/**
	 * removes an annotation from the annotation tree
	 * 
	 * @param annotationNode
	 */
	public void removeAnnotationFromTree(ImageAnnotationTreeNode annotationNode) {
		TreeItem parent = annotationNode.getAnnotationTreeItem().getParentItem();
		if(parent==null) {
			annotationTree.removeItem(annotationNode.getAnnotationTreeItem());
			annotations.remove(annotationNode.getAnnotation());
		} else {
			parent.removeItem(annotationNode.getAnnotationTreeItem());
			annotationNode.getParentAnnotation().removeReply(annotationNode.getAnnotation());
		}		
		imageComposite.hideFragment(annotationNode);
		
		buildAnnotationTree(annotations);
	}
	
	/**
	 * build the annotation tree
	 * root annotations are sorted descending by creation date, replies are 
	 * sorted ascending by creation date.
	 * 
	 * @param annotations
	 */
	private void buildAnnotationTree(ArrayList<ImageAnnotation> annotations) {
		annotationTree.removeItems();	
		annotationTree.setVisible(false);

		// sort the annotations descending by creation date		
		Collections.sort(annotations, new Comparator<ImageAnnotation>() {
			public int compare(ImageAnnotation o1, ImageAnnotation o2) {
				return o2.getCreated().compareTo(o1.getCreated());
			}					
		});
		
		for(ImageAnnotation annotation : annotations) {
			addAnnotationToTree(annotation, null, null);
		}
		
		annotationTree.setVisible(true);
	}
	
	/**
	 * add the given annotation and all replies to the annotation tree
	 * 
	 * @param annotation
	 * @param parentAnnotation
	 * @param parent
	 */
	private void addAnnotationToTree(ImageAnnotation annotation, ImageAnnotation parentAnnotation,
			TreeItem parent) {		

		if(annotation.getScope()==ImageAnnotation.Scope.PRIVATE && 
				!Application.isAuthenticatedUser(annotation.getCreatedBy())) return;
		
		final ImageAnnotationTreeNode node = 
			new ImageAnnotationTreeNode(this,annotation,parentAnnotation);		
		node.setAnnotationTreeItem((parent==null)?annotationTree.addItem(node):parent.addItem(node));
		if(annotation.getAnnotatedFragment()!=null) {
			DeferredCommand.addCommand(new Command(){
				public void execute() { 		
					imageComposite.showFragment(node);
				}
			});
		}
				
		annotationTree.setSelectedItem(node.getAnnotationTreeItem());
		annotationTree.ensureSelectedItemVisible();
		
		if(annotation.getReplies()!=null) {
			// sort the replies ascending by creation date
			List<ImageAnnotation> replies = annotation.getReplies();
			Collections.sort(replies, new Comparator<ImageAnnotation>() {
				public int compare(ImageAnnotation o1, ImageAnnotation o2) {
					return o1.getCreated().compareTo(o2.getCreated());
				}					
			});
			// add the replies to the tree
			for(ImageAnnotation reply : replies) {
				addAnnotationToTree(reply, annotation, node.getAnnotationTreeItem());				
			}			
		}
	}
	
	/**
	 * used to initially populate the annotation tree.
	 * makes a server call to retrieve all annotations for the image.
	 */
	private void populateTree() {
		ImageAnnotationServiceAsync imageAnnotationService = (ImageAnnotationServiceAsync) GWT
				.create(ImageAnnotationService.class);

		imageAnnotationService.listAnnotations(Application.getImageUrl(),
			new AsyncCallback<ArrayList<ImageAnnotation>>() {
				public void onFailure(Throwable caught) {
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
					Window.alert(errorMessages.failedToReadAnnotations());
				}

				public void onSuccess(ArrayList<ImageAnnotation> foundAnnotations) {
					annotations = foundAnnotations;
					buildAnnotationTree(annotations);
					disableLoadingImage();
				}
			});	
	}
	
	/**
	 * refresh the annotation tree
	 */
	public void refreshTree() {
		enableLoadingImage();
		annotationTree.removeItems();
		annotations.clear();
		populateTree();
	}
	
	/**
	 * show the loading image
	 */
	public void enableLoadingImage() {
		this.setStyleName("imageAnnotation-composite-loading");
		loadingImage.setVisible(true);
	}
	
	/**
	 * hide the loading image
	 */
	public void disableLoadingImage() {
		loadingImage.setVisible(false);
		this.setStyleName("imageAnnotation-composite");
	}
	
	/**
	 * used by the click listeners to retrieve the image composite
	 * 
	 * @return image composite
	 */
	public ImageComposite getImageComposite() {
		return imageComposite;
	}
	
	/**
	 * set the size of this composite
	 */
	@Override
	public void setSize(int width, int height) {
		if(height<15) return;
		if (Application.getUserAgent().contains("firefox")) height = height - 2;
		this.setSize(new Integer(width).toString(), new Integer(height).toString());		
		compositePanel.setSize(new Integer(width).toString(), new Integer(height).toString());
		scrollPanel.setSize(new Integer(width).toString(), new Integer(
				Math.max(0, height-150)).toString());		
	}
}
