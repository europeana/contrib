package at.researchstudio.dme.imageannotation.client.annotation.listener;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationTreeNode;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * click listener to show the annotation form for annotation creation and update
 * 
 * @author Christian Sadilek
 */
public class CreateAnnotationClickListener extends AnnotationClickListener {
	/**
	 * indicates if the annotation has a fragment
	 */
	private boolean fragmentAnnotation;
	
	/**
	 * indicates whether an existing annotation should be updated
	 */
	private boolean update;
	
	public CreateAnnotationClickListener(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, boolean fragmentAnnotation, 
			boolean update) {
		super(annotationComposite, annotationTreeNode);
		this.fragmentAnnotation = fragmentAnnotation;
		this.update = update;
	}
	
	public void onClick(ClickEvent event) {
		getAnnotationComposite().showAnnotationForm(getAnnotationTreeNode(),fragmentAnnotation, 
				update);
	}	
}
