package at.researchstudio.dme.imageannotation.client.annotation.listener;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationComposite;
import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotationTreeNode;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * click listener to hide the annotation form 
 * 
 * @author Christian Sadilek
 */
public class CancelAnnotationClickListener extends AnnotationClickListener {
	
	public CancelAnnotationClickListener(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode) {
		super(annotationComposite, annotationTreeNode);
	}
		
	public void onClick(ClickEvent event) {
		getAnnotationComposite().hideAnnotationForm(getAnnotationTreeNode(),true);	
	}	
}
