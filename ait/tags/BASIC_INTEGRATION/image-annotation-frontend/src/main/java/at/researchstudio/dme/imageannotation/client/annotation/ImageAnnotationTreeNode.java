package at.researchstudio.dme.imageannotation.client.annotation;

import at.researchstudio.dme.imageannotation.client.AnnotationConstants;
import at.researchstudio.dme.imageannotation.client.Application;
import at.researchstudio.dme.imageannotation.client.annotation.listener.CreateAnnotationClickListener;
import at.researchstudio.dme.imageannotation.client.annotation.listener.DeleteAnnotationClickListener;
import at.researchstudio.dme.imageannotation.client.annotation.map.MapAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.image.ImageFragment;
import at.researchstudio.dme.imageannotation.client.image.ImageFragmentContainer;
import at.researchstudio.dme.imageannotation.client.image.ImageFragmentSelectionListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * composite that represents an annotation within the annotation tree
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationTreeNode extends Composite implements ImageFragmentContainer {
	private ImageAnnotation annotation;
	private ImageAnnotation parentAnnotation;	
	private TreeItem annotationTreeItem;
	
	private VerticalPanel annotationPanel = new VerticalPanel();	
	private ImageAnnotationForm annotationForm = null;
	
	private HorizontalPanel headerPanel = new HorizontalPanel();	
	private Label headerUser = new Label();
	private Label headerDate = new Label();

	private HorizontalPanel annotationRatePanel = new HorizontalPanel();
	private Label rateValue = new Label();
	private Image rateMinus = new Image("images/empty.gif");
	private Image ratePlus = new Image("images/empty.gif");
	private Image fragment = new Image("images/empty.gif");
	private Image privateAnnotation = new Image("images/empty.gif");
	private Image mapAnnotation = new Image("images/empty.gif");	
	
	private HorizontalPanel annotationActionsPanel = new HorizontalPanel();
	private PushButton actionReply = new PushButton(); 
	private PushButton actionReplyFragment = new PushButton(); 
	private PushButton actionEdit = new PushButton(); 
	private PushButton actionDelete = new PushButton(); 
		
	private Label title = new Label();
	private HTML text = new HTML();
	
	// the corresponding fragment panel of this node's annotation
	private ImageFragmentSelectionListener imageFragmentSelectionListener = null;
	
	// internationalized annotation constants
	private AnnotationConstants annotationConstants=(AnnotationConstants) 
		GWT.create(AnnotationConstants.class);	
	
	/**
	 * mouse listener used to mark the node and corresponding fragment as selected
	 */
	public class SelectionMouseListener implements MouseOverHandler, MouseOutHandler {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			select();
			if(imageFragmentSelectionListener!=null)
				imageFragmentSelectionListener.onSelectImageFragment(ImageAnnotationTreeNode.this);
		}
		@Override
		public void onMouseOut(MouseOutEvent event) {
			deselect();
			if(imageFragmentSelectionListener!=null)
				imageFragmentSelectionListener.onDeselectImageFragment(ImageAnnotationTreeNode.this);
		}		
	}
	
	public ImageAnnotationTreeNode(ImageAnnotationComposite annotationComposite, 
			ImageAnnotation annotation, ImageAnnotation parentAnnotation) {				
		initWidget(annotationPanel);

		this.annotation = annotation;
		this.parentAnnotation = parentAnnotation;
		
		addHeader();				
		addBody();
		addActions(annotationComposite);		
				
		addSelectionMouseListeners();
	}
	
	/**
	 * adds the header information: user, date and rates
	 */
	public void addHeader() {
		headerPanel.setStyleName("imageAnnotation-header");
		
		if(annotation.getScope()==ImageAnnotation.Scope.PRIVATE) {
			privateAnnotation.setStyleName("imageAnnotation-private-annotation-image");			
			headerPanel.add(privateAnnotation);
		}
		
		headerUser.setText(annotation.getCreatedBy());
		headerUser.setStyleName("imageAnnotation-header-user");
		headerPanel.add(headerUser);
		
		if(annotation.getCreated()!=null) {
			headerDate.setText("("+DateTimeFormat.getShortDateTimeFormat().
					format(annotation.getModified())+")");
			headerDate.setStyleName("imageAnnotation-header-date");					
		}		
		headerPanel.add(headerDate);
		
		if(annotation.getAnnotatedFragment()!=null) {
			fragment.setStyleName("imageAnnotation-header-fragment");			
			headerPanel.add(fragment);
		}
		
		if(annotation.getAnnotatedFragment()!=null && Application.getBbox()!=null) {
			mapAnnotation.setStyleName("imageAnnotation-header-map");
			mapAnnotation.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					PopupPanel popup = new PopupPanel();
					popup.add(new MapAnnotationComposite(annotation));
					popup.setPopupPosition(550, 300);
					popup.show();
					popup.setStyleName("imageAnnotation-popup-map");
				}				
			});
			headerPanel.add(mapAnnotation);
		}
			
		// rate information not yet implemented
		/*if(annotation.getRate()==null || annotation.getRate()==0)
			rateValue.setStyleName("imageAnnotation-header-rate");
		else if(annotation.getRate()>0)
			rateValue.setStyleName("imageAnnotation-header-rate-plus");
		else
			rateValue.setStyleName("imageAnnotation-header-rate-minus");*/
		rateValue.setStyleName("imageAnnotation-header-rate");
		rateValue.setText("+0");		
		annotationRatePanel.add(rateValue);
		
		rateMinus.setStyleName("imageAnnotation-header-rate-image-minus");
		annotationRatePanel.add(rateMinus);		
		ratePlus.setStyleName("imageAnnotation-header-rate-image-plus");		
		annotationRatePanel.add(ratePlus);
		headerPanel.add(annotationRatePanel);		
		
		annotationPanel.add(headerPanel);				
	}
	
	/**
	 * adds the body (title and text)
	 */
	public void addBody() {
		title.setText(annotation.getTitle());
		title.setStyleName("imageAnnotation-title");					
		
		text.setStyleName("imageAnnotation-text");							
		text.setText(annotation.getText());
		
		if(title.getText()!=null) annotationPanel.add(title);		
		annotationPanel.add(text);
	}
	
	/**
	 * add the action buttons
	 */
	public void addActions(ImageAnnotationComposite annotationComposite) {		
		// add reply action
		actionReply.setText(annotationConstants.actionReply());
		actionReply.setStyleName("imageAnnotation-action");
		actionReply.addClickHandler(new CreateAnnotationClickListener(annotationComposite,
				this,false,false));
		actionReply.setEnabled(Application.getUser()!=null);
		annotationActionsPanel.add(actionReply);	

		// add reply w/ fragment action
		actionReplyFragment.setText(annotationConstants.actionReplyFragment());
		actionReplyFragment.setStyleName("imageAnnotation-action");
		actionReplyFragment.addClickHandler(new CreateAnnotationClickListener(annotationComposite,
				this,true,false));		
		actionReplyFragment.setEnabled(Application.getUser()!=null);
		annotationActionsPanel.add(actionReplyFragment);	

		// add edit action
		actionEdit.setText(annotationConstants.actionEdit());
		actionEdit.setStyleName("imageAnnotation-action");
		actionEdit.setEnabled(Application.isAuthenticatedUser(annotation.getCreatedBy())&&
				annotation.getReplies().isEmpty());		
		actionEdit.addClickHandler(new CreateAnnotationClickListener(annotationComposite,
				this,annotation.getAnnotatedFragment()!=null,true));				
		annotationActionsPanel.add(actionEdit);	
		
		// add delete action
		actionDelete.setText(annotationConstants.actionDelete());
		actionDelete.setStyleName("imageAnnotation-action");
		actionDelete.setEnabled(Application.isAuthenticatedUser(annotation.getCreatedBy())&&
				annotation.getReplies().isEmpty());
		actionDelete.addClickHandler(new DeleteAnnotationClickListener(annotationComposite,this));				
		annotationActionsPanel.add(actionDelete);	
				
		annotationActionsPanel.setStyleName("imageAnnotation-actions");
		annotationPanel.add(annotationActionsPanel);	
	}
	
	/**
	 * add the mouse listeners from the tree node selection
	 */
	private void addSelectionMouseListeners() {
		SelectionMouseListener sml = new SelectionMouseListener();
		text.addMouseOverHandler(sml);
		text.addMouseOutHandler(sml);
		title.addMouseOverHandler(sml);
		title.addMouseOutHandler(sml);
		headerUser.addMouseOverHandler(sml);
		headerUser.addMouseOutHandler(sml);
		headerDate.addMouseOverHandler(sml);
		headerDate.addMouseOutHandler(sml);
		fragment.addMouseOverHandler(sml);
		fragment.addMouseOutHandler(sml);		
	}
	
	/**
	 * select the node
	 */
	public void select() {
		setStyleName("imageAnnotation-selected");
	}
	
	/**
	 * deselect the node
	 */
	public void deselect() {
		setStyleName("imageAnnotation");
	}
	
	/**
	 * shows the annotation form underneath this node
	 * 
	 * @param annotationForm
	 */
	public void showAnnotationForm(ImageAnnotationForm annotationForm) {
		this.annotationForm=annotationForm;
		annotationPanel.add(annotationForm);		
		actionReply.setEnabled(false);
		actionReplyFragment.setEnabled(false);
		actionEdit.setEnabled(false);
	}
	
	/**
	 * hides the annotation form
	 */
	public void hideAnnotationForm() {
		annotationForm.setVisible(false);
		actionReply.setEnabled(true);
		actionReplyFragment.setEnabled(true);
		actionEdit.setEnabled(true);
	}
	
	/**
	 * get the corresponding annotation of this node 
	 * 
	 * @return image annotation
	 */
	public ImageAnnotation getAnnotation() {
		return annotation;
	}
	
	/**
	 * get the annotated image fragment
	 * 
	 * @return image fragment
	 */
	public ImageFragment getImageFragment() {
		return annotation.getAnnotatedFragment();
	}
	
	/**
	 * get the corresponding annotation id of this node
	 * 
	 * @return annotation id
	 */
	public String getAnnotationId() {
		return annotation.getId();
	}

	/**
	 * get the corresponding parent annotation of this node
	 * 
	 * @return image annotation
	 */
	public ImageAnnotation getParentAnnotation() {
		return parentAnnotation;
	}

	/**
	 * get the corresponding parent annotation id of this node
	 *  
	 * @return annotation id
	 */
	public String getParentAnnotationId() {
		return (parentAnnotation!=null)?parentAnnotation.getId():null;
	}

	/**
	 * get the root id of this node's annotation
	 * 
	 * @return id of the root annotation
	 */
	public String getAnnotationRootId() {
		return annotation.getRootId();
	}
	
	/**
	 * get the external object id of this node's annotations
	 * 
	 * @return id of the external object
	 */
	public String getExternalObjectId() {
		return annotation.getExternalObjectId();
	}
	
	/**
	 * get the annotation text
	 * 
	 * @return text
	 */
	public String getText() {
		return text.getText();
	}
	
	/**
	 * get the annotation title
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title.getText();
	}

	/**
	 * get the wrapped TreeItem
	 * 
	 * @return TreeItem
	 */
	public TreeItem getAnnotationTreeItem() {
		return annotationTreeItem;
	}

	/**
	 * set the TreeItem
	 * 
	 * @param annotatonTreeItem
	 */
	public void setAnnotationTreeItem(TreeItem annotatonTreeItem) {
		this.annotationTreeItem = annotatonTreeItem;
	}
	
	/**
	 * set the corresponding shape panel of this node's annotation
	 * 
	 * @param listener
	 */
	public void setImageFragmentSelectionListener(ImageFragmentSelectionListener listener) {
		imageFragmentSelectionListener = listener;
	}

	/**
	 * we consider annotation tree nodes as equal if the represent the same annotation
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ImageAnnotationTreeNode)) return false;
		if(this==obj) return true;
		
		ImageAnnotationTreeNode node = (ImageAnnotationTreeNode)obj;
		return annotation.equals(node.getAnnotation());
	}

	/**
	 * we consider annotation tree nodes as equal if the represent the same annotation
	 */
	@Override
	public int hashCode() {		
		return annotation.getId().hashCode();
	}
}
