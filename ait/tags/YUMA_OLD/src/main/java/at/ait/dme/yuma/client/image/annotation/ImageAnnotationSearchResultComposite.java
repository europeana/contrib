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

import java.util.Date;
import java.util.List;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.ErrorMessages;
import at.ait.dme.yuma.client.server.ImageAnnotationService;
import at.ait.dme.yuma.client.server.ImageAnnotationServiceAsync;
import at.ait.dme.yuma.client.table.SortableTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

/**
 * this composite displays annotation search results. it's a prototype
 * and should not be part of the image annotation service in the future.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationSearchResultComposite extends Composite {
	// panel of this composite
	private ScrollPanel compositePanel = new ScrollPanel();
	
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
		initWidget(compositePanel);
		
		this.searchTerm = searchTerm;	
		this.setStyleName("imageAnnotation-searchresult-composite");		
		compositePanel.add(resultTable);		
		
		// the search result table
		resultTable.setStyleName("sortableTable");
		resultTable.setBorderWidth(0);
		resultTable.setCellPadding(1);
		resultTable.setCellSpacing(1);
		
		resultTable.addColumnHeader(Application.getConstants().annotationCreator(),  0);
		resultTable.addColumnHeader(Application.getConstants().annotationCreationDate(), 1);
		resultTable.addColumnHeader(Application.getConstants().annotationImage(), 2);
		resultTable.addColumnHeader(Application.getConstants().annotationTitle(), 3);
		resultTable.addColumnHeader(Application.getConstants().annotationText(), 4);					
	
		RowFormatter rowFormatter = resultTable.getRowFormatter();
		rowFormatter.setStyleName(0, "tableHeader");
	
		CellFormatter cellFormatter = resultTable.getCellFormatter();
		for (int colIndex=0; colIndex<5; colIndex++){
			cellFormatter.setStyleName(0, colIndex, "headerStyle");
			cellFormatter.setAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER, 
					HasVerticalAlignment.ALIGN_MIDDLE);
		}
		
		resultTable.setValue(1, 0, Application.getConstants().searching());
			
		// do the search
		search();			
	}

	/**
	 * execute the search
	 */
	private void search() {
		ImageAnnotationServiceAsync imageAnnotationService = (ImageAnnotationServiceAsync) GWT
				.create(ImageAnnotationService.class);
/*
		imageAnnotationService.findAnnotations(searchTerm, 
			new AsyncCallback<List<ImageAnnotation>>() {
				public void onFailure(Throwable caught) {
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
					Window.alert(errorMessages.failedToReadAnnotations());
				}
	
				public void onSuccess(List<ImageAnnotation> annotations) {				
					// no hits
					if(annotations.isEmpty()) 
						resultTable.setValue(1, 0, Application.getConstants().noresults());
					
					// and search result entries to the table
					int row = 1;
					for(ImageAnnotation annotation : annotations) {
						// remove private annotations of other users
						// TODO should not be done in the user interface but we don't have another
						// option at the moment (FAST)
						if (annotation.getScope() == ImageAnnotation.Scope.PRIVATE
								&& !Application.isAuthenticatedUser(annotation.getCreatedBy()))
							continue;
					
						if(annotation.getImageUrl()==null) continue;
							
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
			}		
		});
		*/
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
		
		builder.append("&objectURL=").append(annotation.getImageUrl());
		if(annotation.getExternalObjectId()!=null)
			builder.append("&id=").append(annotation.getExternalObjectId());
		
		builder.append("&user=").append(Application.getUser()).
		append("&db=").append(Application.getDatabaseName()).
		append("\">").append(annotation.getImageUrl()).append("</a>");					
		
		return builder.toString();
	}
}
