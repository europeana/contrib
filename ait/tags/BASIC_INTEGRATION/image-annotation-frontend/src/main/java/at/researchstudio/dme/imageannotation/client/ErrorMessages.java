package at.researchstudio.dme.imageannotation.client;

import com.google.gwt.i18n.client.Messages;

public interface ErrorMessages extends Messages {
	public String imageNotFound();
	public String annotationConflict();
	public String failedToSaveAnnotation();	
	public String failedToReadAnnotations();
	public String failedToDeleteAnnotation();	
	public String failedToAuthenticate();
}
