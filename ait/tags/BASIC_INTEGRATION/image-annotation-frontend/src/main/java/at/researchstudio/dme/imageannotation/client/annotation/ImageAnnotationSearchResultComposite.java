package at.researchstudio.dme.imageannotation.client.annotation;

import java.util.ArrayList;
import java.util.Date;

import at.researchstudio.dme.imageannotation.client.AnnotationConstants;
import at.researchstudio.dme.imageannotation.client.Application;
import at.researchstudio.dme.imageannotation.client.ErrorMessages;
import at.researchstudio.dme.imageannotation.client.dnd.DraggableWindowComposite;
import at.researchstudio.dme.imageannotation.client.server.ImageAnnotationService;
import at.researchstudio.dme.imageannotation.client.server.ImageAnnotationServiceAsync;
import at.researchstudio.dme.imageannotation.client.table.SortableTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

/**
 * this composite displays annotation search results. it's a prototype
 * and should not be part of the image annotation service in the future.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationSearchResultComposite extends DraggableWindowComposite {
	private AnnotationConstants annotationConstants=
		(AnnotationConstants)GWT.create(AnnotationConstants.class);					

	private String searchTerm = null;
	private SortableTable resultTable = new SortableTable();	
	
	/**
	 * inner class that represents a date within a search result. it is sortable
	 * and still displays an internationalized string.
	 */
	private static class SearchResultDate extends Date {
		private static final long serialVersionUID = -6525977328526197680L;

		public SearchResultDate(long date) {
			super(date);
		}

		@Override
		public String toString() {
			return DateTimeFormat.getShortDateTimeFormat().format(this);
		}
	}
	
	public ImageAnnotationSearchResultComposite(String searchTerm) {
		this.searchTerm = searchTerm;	
		this.setStyleName("imageAnnotation-searchresult-composite");

		lbTitle.setText(annotationConstants.annotationSearchResults());		
		closeWindowImage.setStyleName("imageAnnotation-searchresult-window-bar-close");
		minWindowImage.setStyleName("imageAnnotation-searchresult-window-bar-minmax");
		maxWindowImage.setStyleName("imageAnnotation-searchresult-window-bar-minmax");
	
		compositePanel.add(resultTable);		
	
		// the search result table
		resultTable.setWidth(800 + "px");
		resultTable.setStyleName("sortableTable");
		resultTable.setBorderWidth(0);
		resultTable.setCellPadding(1);
		resultTable.setCellSpacing(1);
		
		resultTable.addColumnHeader(annotationConstants.annotationCreator(),  0);
		resultTable.addColumnHeader(annotationConstants.annotationCreationDate(), 1);
		resultTable.addColumnHeader(annotationConstants.annotationImage(), 2);
		resultTable.addColumnHeader(annotationConstants.annotationTitle(), 3);
		resultTable.addColumnHeader(annotationConstants.annotationText(), 4);					
	
		RowFormatter rowFormatter = resultTable.getRowFormatter();
		rowFormatter.setStyleName(0, "tableHeader");
	
		CellFormatter cellFormatter = resultTable.getCellFormatter();
		for (int colIndex=0; colIndex<5; colIndex++){
			cellFormatter.setStyleName(0, colIndex, "headerStyle");
			cellFormatter.setAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER, 
					HasVerticalAlignment.ALIGN_MIDDLE);
		}
		
		resultTable.setValue(1, 0, annotationConstants.searching());
		
		// do the search
		search();			
	}

	/**
	 * execute the search
	 */
	private void search() {
		ImageAnnotationServiceAsync imageAnnotationService = (ImageAnnotationServiceAsync) GWT
				.create(ImageAnnotationService.class);

		imageAnnotationService.findAnnotations(searchTerm, 
			new AsyncCallback<ArrayList<ImageAnnotation>>() {
				public void onFailure(Throwable caught) {
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
					Window.alert(errorMessages.failedToReadAnnotations());
				}
	
				public void onSuccess(ArrayList<ImageAnnotation> annotations) {				
					// no hits
					if(annotations.isEmpty()) 
						resultTable.setValue(1, 0, annotationConstants.noresults());
					
					// and search result entries to the table
					int row = 1;
					for(ImageAnnotation annotation : annotations) {
						// remove private annotations of other users
						if (annotation.getScope() == ImageAnnotation.Scope.PRIVATE
								&& !Application.isAuthenticatedUser(annotation.getCreatedBy()))
							continue;
					
						if(annotation.getObjectId()==null) continue;
							
						String style = (row%2 == 0)?"tableRowEven":"tableRowOdd";
						RowFormatter rowFormatter = resultTable.getRowFormatter();
						rowFormatter.setStyleName(row, style);
	
						if (annotation.getCreatedBy() != null)
							resultTable.setValue(row, 0, annotation.getCreatedBy());
							resultTable.setValue(row, 1, new SearchResultDate(
								annotation.getCreated().getTime()).toString());
							resultTable.setValue(row, 2, buildLink(annotation));
						if (annotation.getTitle() != null)
							resultTable.setValue(row, 3, annotation.getTitle());
						if (annotation.getText() != null)
							resultTable.setValue(row, 4, annotation.getText());
						row++;
					}				
				resultTable.sort(1,true);
				makeNorthResizable();
				// IE looses the style here so we set it again
				windowBar.setStyleName("imageAnnotation-window-bar");	
			}
		});
	}
	
	/**
	 * build a link to the annotation
	 * 
	 * @param annotation
	 * @return link
	 */
	private String buildLink(ImageAnnotation annotation) {		
		StringBuilder builder = new StringBuilder();
		
		if(annotation.getMimeType()==null || annotation.getMimeType().startsWith("image"))			
			builder.append("<a href=\"").append(Application.getBaseUrl()).append("annotate.jsp?");
		else if(annotation.getMimeType().startsWith("video"))
			builder.append("<a href=\"").append("http://dme.arcs.ac.at/video-annotation-frontend")
				.append("/videoannotation.html?");
		
		builder.append("&objectURL=").append(annotation.getObjectId());
		if(annotation.getExternalObjectId()!=null)
			builder.append("&id=").append(annotation.getExternalObjectId());
		
		builder.append("&user=").append(Application.getUser()).
		append("&db=").append(Application.getDatabaseName()).
		append("\">").append(annotation.getObjectId()).append("</a>");					
		
		return builder.toString();
	}
}
