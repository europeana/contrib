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
import java.util.List;
import java.util.Set;

import org.gwt.mosaic.core.client.Dimension;
import org.gwt.mosaic.ui.client.MessageBox;
import org.gwt.mosaic.ui.client.WindowPanel;
import org.gwt.mosaic.ui.client.layout.BorderLayout;
import org.gwt.mosaic.ui.client.layout.BorderLayoutData;
import org.gwt.mosaic.ui.client.layout.HasLayoutManager;
import org.gwt.mosaic.ui.client.layout.LayoutPanel;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.image.ImageComposite;
import at.ait.dme.yuma.client.image.annotation.handler.CreateImageAnnotationClickHandler;
import at.ait.dme.yuma.client.image.annotation.handler.selection.HasImageAnnotationSelectionHandlers;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionEvent;
import at.ait.dme.yuma.client.image.annotation.handler.selection.ImageAnnotationSelectionHandler;
import at.ait.dme.yuma.client.map.annotation.GoogleMapsComposite;
import at.ait.dme.yuma.client.server.ImageAnnotationService;
import at.ait.dme.yuma.client.server.ImageAnnotationServiceAsync;
import at.ait.dme.yuma.client.util.MinMaxWindowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Image annotation composite shows the annotation tree and allows
 * to create, update, and delete annotations.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationComposite extends Composite implements HasLayoutManager, 
	HasImageAnnotationSelectionHandlers {
	
	// this panel's handler manager 
	final protected HandlerManager handlerManager = new HandlerManager(this);	
	
	// the container panel for all GUI elements
	private LayoutPanel containerPanel = new LayoutPanel(new BorderLayout());
	
	// the parent panel for the loading and scrollpanel
	private DeckPanel deckPanel = new DeckPanel();
	
	// panel for displaying the loading icon
	private DockPanel loadingPanel = new DockPanel();
	private Image loadingImage = new Image("images/loading.gif");

	// the scroll panel
	private ScrollPanel scrollPanel = new ScrollPanel();	
	private int scrollPosition = 0;

	// panel for the annotation form
	protected LayoutPanel annotationFormPanel = new LayoutPanel();	
	private ImageAnnotationForm imageAnnotationForm = null;
	
	// the annotation tree
	private ImageAnnotationTree annotationTree;

	// the shape types of this tree's annotations
	private Set<String> shapeTypes = null;	
	
	// action buttons
	private PushButton annotateButton = new PushButton();
	private PushButton annotateFragmentButton = new PushButton();
	private PushButton showOnMapButton = new PushButton();
	
	// all currently displayed annotations
	private List<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>();
	
	// reference to the image composite
	private ImageComposite imageComposite = null;
	
	/**
	 * the image annotation composite constructor. needs an image composite
	 * to show and hide fragments.
	 * 
	 * @param imageComposite
	 */
	public ImageAnnotationComposite(ImageComposite imageComposite, 
			ImageAnnotationForm imageAnnotationForm, Set<String> shapeTypes) {	
		this.imageComposite = imageComposite;
		this.imageAnnotationForm = imageAnnotationForm;
		this.shapeTypes = shapeTypes;
		
		// Container panel for annotation tree & controls
		containerPanel.setStyleName("imageAnnotation-composite");
		containerPanel.add(createHeader(), new BorderLayoutData(BorderLayout.Region.NORTH));
		
		// 'Loading' panel
		loadingPanel.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		loadingPanel.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
		loadingPanel.add(loadingImage, DockPanel.CENTER);
		
		// Annotation tree
		createTree(shapeTypes);		
		
		// Register annotation selection handler on image composite
		imageComposite.addImageAnnotationSelectionHandler(new ImageAnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(ImageAnnotationSelectionEvent event) {
				final ImageAnnotationTreeNode node = 
					annotationTree.getAnnotationNode(event.getAnnotation());
				if(node==null) return;
				
				annotationTree.selectAnnotationTreeNode(node, event.isSelected());
				if(event.isSelected()) {
					DeferredCommand.addCommand(new Command(){
						public void execute() { 		
							scrollPanel.ensureVisible(node.getAnnotationTreeItem());
						}
					});
				} 
			}
		});
		
		deckPanel.add(loadingPanel);
		deckPanel.add(scrollPanel);

		enableLoadingImage();

		containerPanel.add(deckPanel);
		initWidget(containerPanel);
	}
	
	@Override
	public void layout() {
		invalidate();
		if (imageAnnotationForm != null) imageAnnotationForm.layout();
		containerPanel.layout();
	}
	
	/**
	 * show hints and create link to help page
	 */
	protected Widget createHeader() {
		// The parent header panel
		FlowPanel header = new FlowPanel();
		
		// 'Add your Annotation' label
		Label addAnnotationLabel = new Label(Application.getConstants().addAnnotation());
		addAnnotationLabel.setStyleName("imageAnnotation-add-annotation");
		header.add(addAnnotationLabel);
		
		// 'Help' link
		HTML help = new HTML("<a target=\"_blank\" href=\"userguide_" + 
				LocaleInfo.getCurrentLocale().getLocaleName()+".html\">" + 
				Application.getConstants().help() + "</a>" );
		help.setStyleName("imageAnnotation-help");
		header.add(help);		
		
		// Instructions text
		Label addAnnotationHint = new Label(Application.getConstants().addAnnotationHint()); 
		addAnnotationHint.setStyleName("imageAnnotation-add-annotation-hint");
		header.add(addAnnotationHint);
		
		// Button panel
		HorizontalPanel buttons = new HorizontalPanel();
		
		// 'Annotate' button
		annotateButton.setStyleName("imageAnnotation-button");
		annotateButton.setText(Application.getConstants().actionCreate());	
		annotateButton.addClickHandler(
				new CreateImageAnnotationClickHandler(this,null,false,false));
		annotateButton.setEnabled(!Application.getUser().isEmpty());
		buttons.add(annotateButton);
		
		// 'Annotate Fragment' button
		annotateFragmentButton.setStyleName("imageAnnotation-button");
		annotateFragmentButton.setText(Application.getConstants().actionCreateFragment());
		annotateFragmentButton.addClickHandler(
				new CreateImageAnnotationClickHandler(this,null,true,false));		
		annotateFragmentButton.setEnabled(!Application.getUser().isEmpty());
		buttons.add(annotateFragmentButton);
		
		// 'Show on Map' button
		showOnMapButton.setStyleName("imageAnnotation-button");
		showOnMapButton.setText(Application.getConstants().actionShowOnMap());
		showOnMapButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(550, 300, 500, 300);
				window.getHeader().setText("Map");
				window.setWidget(new GoogleMapsComposite(annotations));
				window.setResizable(false);
				window.show();
			}			
		});			
		showOnMapButton.setVisible(Application.getBbox()!=null||Application.isInTileMode());
		buttons.add(showOnMapButton);	
		
		header.add(buttons);	
		header.add(annotationFormPanel);
		return header;
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

		imageAnnotationForm=imageAnnotationForm.createNew(this, annotationTreeNode, 
				fragmentAnnotation, update);
		
		scrollPosition = scrollPanel.getScrollPosition();	
		
		if(annotationTreeNode!=null) {
			annotationTreeNode.showAnnotationForm(imageAnnotationForm);
			DeferredCommand.addCommand(new Command(){
				public void execute() { 
					scrollPanel.ensureVisible(imageAnnotationForm);
			    }
			});
			
			if(fragmentAnnotation) {
				// if we update an existing annotation we first have to remove the
				// existing fragment and show an active fragment panel instead.
				if(update && annotationTreeNode.getImageFragment()!=null) {					
					imageComposite.showActiveFragmentPanel(annotationTreeNode.getAnnotation(), true);
					imageComposite.hideFragment(annotationTreeNode.getAnnotation());
				} else {
					imageComposite.showActiveFragmentPanel(null, true);
				}
			}
		} else {
			annotateButton.setEnabled(false);
			annotateFragmentButton.setEnabled(false);
			annotationFormPanel.add(imageAnnotationForm);
			if(fragmentAnnotation) imageComposite.showActiveFragmentPanel(null, true);
		}
		
		layout();
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
			imageComposite.showFragment(annotationTreeNode.getAnnotation());
		}

		layout();
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
		
		annotationTree.build(annotations);
	}
	
	/**
	 * removes an annotation from the annotation tree
	 * 
	 * @param annotationNode
	 */
	public void removeAnnotation(ImageAnnotationTreeNode annotationNode) {
		TreeItem parent = annotationNode.getAnnotationTreeItem().getParentItem();
		if(parent==null) {
			annotationTree.removeItem(annotationNode.getAnnotationTreeItem());
			annotations.remove(annotationNode.getAnnotation());
		} else {
			parent.removeItem(annotationNode.getAnnotationTreeItem());
			annotationNode.getParentAnnotation().removeReply(annotationNode.getAnnotation());
		}		
		imageComposite.hideFragment(annotationNode.getAnnotation());
		
		annotationTree.build(annotations);
	}
	
	/**
	 * makes a server call to retrieve all annotations for the image and displays the using a tree
	 */
	private void createTree(Set<String> shapeTypes) {
		ImageAnnotationServiceAsync imageAnnotationService = (ImageAnnotationServiceAsync) GWT
				.create(ImageAnnotationService.class);

		imageAnnotationService.listAnnotations(Application.getExternalObjectId(), 
				Application.getImageUrl(), shapeTypes,
			new AsyncCallback<List<ImageAnnotation>>() {
				public void onFailure(Throwable caught) {
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
					MessageBox.error(errorMessages.error(), errorMessages.failedToReadAnnotations());
				}

				public void onSuccess(List<ImageAnnotation> foundAnnotations) {
					// remove annotations the user is not allowed to see
					for(ImageAnnotation annotation : foundAnnotations) { 
						if(annotation.getScope()==ImageAnnotation.Scope.PRIVATE && 
								!Application.isAuthenticatedUser(annotation.getCreatedBy()))
							continue;
						annotations.add(annotation);

					}

					annotationTree = new ImageAnnotationTree(annotations, handlerManager,
							imageComposite, ImageAnnotationComposite.this);
					
					scrollPanel.add(annotationTree);
				
					disableLoadingImage();
					layout();
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
		createTree(shapeTypes);
	}
	
	/**
	 * show the loading image
	 */
	public void enableLoadingImage() {
		deckPanel.showWidget(0);
	}
	
	/**
	 * hide the loading image
	 */
	public void disableLoadingImage() {
		deckPanel.showWidget(1);
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
	public void setSize(int width, int height) {
		if(height<15) return;
		if (Application.getUserAgent().contains("firefox")) height = height - 2;
		this.setSize(new Integer(width).toString(), new Integer(height).toString());
		scrollPanel.setSize(new Integer(width).toString(), new Integer(
				Math.max(0, height-150)).toString());		
	}

	@Override
	public HandlerRegistration addImageAnnotationSelectionHandler(
			ImageAnnotationSelectionHandler handler) {
		return handlerManager.addHandler(ImageAnnotationSelectionEvent.getType(), handler);
	}

	@Override
	public Dimension getPreferredSize() {
		return null;
	}

	@Override
	public void invalidate() {
		containerPanel.invalidate();
	}

	@Override
	public void invalidate(Widget widget) {
		containerPanel.invalidate(widget);
	}

	@Override
	public boolean needsLayout() {
		return false;
	}

	@Override
	public void onResize() {
		layout();		
	}
}
