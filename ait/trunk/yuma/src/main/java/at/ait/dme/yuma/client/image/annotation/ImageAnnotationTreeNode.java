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

import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.image.ImageFragment;
import at.ait.dme.yuma.client.image.annotation.handler.CreateImageAnnotationClickHandler;
import at.ait.dme.yuma.client.image.annotation.handler.DeleteImageAnnotationClickHandler;
import at.ait.dme.yuma.client.map.annotation.GoogleMapsComposite;
import at.ait.dme.yuma.client.util.MinMaxWindowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * composite that represents an annotation within the annotation tree
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ImageAnnotationTreeNode extends Composite implements HasMouseOutHandlers,
		HasMouseOverHandlers {
	
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
	private Image flag = new Image("images/empty.gif");
	private Image fragment = new Image("images/empty.gif");
	private Image privateAnnotation = new Image("images/empty.gif");
	private Image mapAnnotation = new Image("images/empty.gif");

	private HorizontalPanel annotationActionsPanel = new HorizontalPanel();
	private PushButton actionReply = new PushButton();
	private PushButton actionReplyFragment = new PushButton();
	private PushButton actionEdit = new PushButton();
	private PushButton actionDelete = new PushButton();

	private Label title = new Label();
	private InlineHTML text = new InlineHTML();

	private boolean flagged = false;

	public ImageAnnotationTreeNode(ImageAnnotationComposite annotationComposite,
			ImageAnnotation annotation, ImageAnnotation parentAnnotation) {
		initWidget(annotationPanel);

		this.annotation = annotation;
		this.parentAnnotation = parentAnnotation;

		addHeader();
		addBody();
		addActions(annotationComposite);
	}

	/**
	 * adds the header information: user, date and rates
	 */
	public void addHeader() {
		headerPanel.setStyleName("imageAnnotation-header");

		if (annotation.getScope() == ImageAnnotation.Scope.PRIVATE) {
			privateAnnotation.setStyleName("imageAnnotation-private-annotation-image");
			headerPanel.add(privateAnnotation);
		}

		headerUser.setText(annotation.getCreatedBy());
		headerUser.setStyleName("imageAnnotation-header-user");
		headerPanel.add(headerUser);

		if (annotation.getCreated() != null) {
			headerDate.setText("("
					+ DateTimeFormat.getShortDateTimeFormat().format(annotation.getModified())
					+ ")");
			headerDate.setStyleName("imageAnnotation-header-date");
		}
		headerPanel.add(headerDate);

		if (annotation.hasFragment()) {
			fragment.setStyleName("imageAnnotation-header-fragment");
			headerPanel.add(fragment);
		}

		if (annotation.hasFragment() && (Application.getBbox() != null || Application.isInTileMode())) {
			mapAnnotation.setStyleName("imageAnnotation-header-map");
			mapAnnotation.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(550, 300, 500, 300);
					window.setWidget(new GoogleMapsComposite(annotation));
					window.show();
				}
			});
			headerPanel.add(mapAnnotation);
		}

		// rate information not yet implemented
		/*
		 * if(annotation.getRate()==null || annotation.getRate()==0)
		 * rateValue.setStyleName("imageAnnotation-header-rate"); else
		 * if(annotation.getRate()>0)
		 * rateValue.setStyleName("imageAnnotation-header-rate-plus"); else
		 * rateValue.setStyleName("imageAnnotation-header-rate-minus");
		 */
		rateValue.setStyleName("imageAnnotation-header-rate");
		rateValue.setText("+0");
		annotationRatePanel.add(rateValue);

		rateMinus.setStyleName("imageAnnotation-header-rate-image-minus");
		annotationRatePanel.add(rateMinus);
		ratePlus.setStyleName("imageAnnotation-header-rate-image-plus");
		annotationRatePanel.add(ratePlus);
		
		if(Application.getFlaggedId()!=null && 
				Application.getFlaggedId().equals(annotation.getId())) {
			flag.setStyleName("imageAnnotation-header-flag-red-image");
			flagged=true;
		} else {
			flag.setStyleName("imageAnnotation-header-flag-blue-image");
		}
		Anchor flagAnchor = new Anchor();
		flagAnchor.setHref(createFlagLink());
		flagAnchor.setTarget("_blank");
		flagAnchor.getElement().appendChild(flag.getElement());
		annotationRatePanel.add(flagAnchor);
		
		headerPanel.add(annotationRatePanel);
		annotationPanel.add(headerPanel);
		
		// Init CSS style
		deselect();
	}

	/**
	 * adds the body (title and text)
	 */
	public void addBody() {
		title.setText(annotation.getTitle());
		title.setStyleName("imageAnnotation-title");

		text.setStyleName("imageAnnotation-text");
		text.setHTML(annotation.getText());

		if (title.getText() != null)
			annotationPanel.add(title);
		annotationPanel.add(text);
		
		// Semantic tags
		if (annotation.hasSemanticTags()) {
			FlowPanel tagPanel = new FlowPanel();
			tagPanel.setStyleName("imageAnnotation-taglist");
			for (SemanticTag t : annotation.getSemanticTags()) {
				InlineHTML span = new InlineHTML("<a target=\"_blank\" href=\""
						+ t.getURI() + "\" title=\"" 
						+ t.getDescription() + "\">" 
						+ t.getTitle() + "</a>"
				);
				tagPanel.add(span);
			}
			annotationPanel.add(tagPanel);
		}
	}

	/**
	 * add the action buttons
	 */
	public void addActions(ImageAnnotationComposite annotationComposite) {
		// add reply action
		actionReply.setText(Application.getConstants().actionReply());
		actionReply.setStyleName("imageAnnotation-action");
		actionReply.addClickHandler(
				new CreateImageAnnotationClickHandler(annotationComposite, this, false, false));
		actionReply.setEnabled(Application.getUser() != null);
		annotationActionsPanel.add(actionReply);

		// add reply w/ fragment action
		actionReplyFragment.setText(Application.getConstants().actionReplyFragment());
		actionReplyFragment.setStyleName("imageAnnotation-action");
		actionReplyFragment.addClickHandler(
				new CreateImageAnnotationClickHandler(annotationComposite, this, true, false));
		actionReplyFragment.setEnabled(Application.getUser() != null);
		annotationActionsPanel.add(actionReplyFragment);

		// add edit action
		actionEdit.setText(Application.getConstants().actionEdit());
		actionEdit.setStyleName("imageAnnotation-action");
		actionEdit.setEnabled(Application.isAuthenticatedUser(annotation.getCreatedBy())
				&& !annotation.hasReplies());
		actionEdit.addClickHandler(new CreateImageAnnotationClickHandler(annotationComposite, this,
				annotation.hasFragment(), true));
		annotationActionsPanel.add(actionEdit);

		// add delete action
		actionDelete.setText(Application.getConstants().actionDelete());
		actionDelete.setStyleName("imageAnnotation-action");
		actionDelete.setEnabled(Application.isAuthenticatedUser(annotation.getCreatedBy())
				&& !annotation.hasReplies());
		actionDelete.addClickHandler(
				new DeleteImageAnnotationClickHandler(annotationComposite, this));
		annotationActionsPanel.add(actionDelete);

		annotationActionsPanel.setStyleName("imageAnnotation-actions");
		annotationPanel.add(annotationActionsPanel);
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
		this.annotationForm = annotationForm;
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
		return annotation.getFragment();
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
		return (parentAnnotation != null) ? parentAnnotation.getId() : null;
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
	 * we consider annotation tree nodes as equal if the represent the same
	 * annotation
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImageAnnotationTreeNode))
			return false;
		if (this == obj)
			return true;

		ImageAnnotationTreeNode node = (ImageAnnotationTreeNode) obj;
		return annotation.equals(node.getAnnotation());
	}

	/**
	 * we consider annotation tree nodes as equal if the represent the same
	 * annotation
	 */
	@Override
	public int hashCode() {
		return annotation.getId().hashCode();
	}

	/** is annotation marked as offensive
	 * 
	 * @return true if flagged, otherwise false
	 */
	public boolean isFlagged() {
		return flagged;
	}
	
	/**
	 * create the mailto link for flagging an annotation as inappropriate
	 * 
	 * @return mailto link
	 */
	private String createFlagLink() {
		ErrorMessages msg = (ErrorMessages) GWT.create(ErrorMessages.class);

		// TODO refactor (just a prototype)!
		String baseUrl = Application.getBaseUrl().replace("image-annotation-frontend",
				"image-annotation-frontend-admin");

		return "mailto:"
				+ msg.flagRecipient()
				+ "?subject="
				+ msg.flagSubject()
				+ "&body="
				+ msg.flagBody(annotation.getTitle(), baseUrl + "annotate.jsp?objectURL="
						+ annotation.getImageUrl() + "%26flaggedId=" + annotation.getId(), 
						Application.getUser());
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
