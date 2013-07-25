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

package at.ait.dme.yuma.client.map.annotation;

import java.util.Set;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import at.ait.dme.yuma.client.Application;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotationComposite;
import at.ait.dme.yuma.client.image.annotation.handler.CreateImageAnnotationClickHandler;
import at.ait.dme.yuma.client.map.TiledImageComposite;

/**
 * Composite used to manage control points
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ControlPointComposite extends ImageAnnotationComposite {
	
	/**
	 * 'Create control point' button
	 */
	private PushButton createButton;

	public ControlPointComposite(TiledImageComposite imageComposite, 
			ControlPointForm imageAnnotationForm, Set<String> shapeTypes)   {
	
		super(imageComposite, imageAnnotationForm, shapeTypes);
	}

	@Override
	protected Widget createHeader() {
		// The parent header panel
		FlowPanel header = new FlowPanel();
		
		// 'Help geo-Reference' label
		Label addAnnotationLabel = new Label(Application.getConstants().helpGeoreference());
		addAnnotationLabel.setStyleName("imageAnnotation-add-annotation");
		header.add(addAnnotationLabel);
		
		// 'Help' link
		HTML help = new HTML("<a target=\"_blank\" href=\"userguide_" + 
				LocaleInfo.getCurrentLocale().getLocaleName()+".html\">" + 
				Application.getConstants().help() + "</a>" );
		help.setStyleName("imageAnnotation-help");
		header.add(help);		
		
		// Instructions text
		Label addAnnotationHint = new Label(Application.getConstants().helpGeoreferenceHint()); 
		addAnnotationHint.setStyleName("imageAnnotation-add-annotation-hint");
		header.add(addAnnotationHint);
		
		// Button panel
		HorizontalPanel buttons = new HorizontalPanel();
		
		// 'Create Control Point' button
		createButton = new PushButton(Application.getConstants().actionCreateCP());
		createButton.setStyleName("imageAnnotation-button");
		createButton.addClickHandler(new CreateImageAnnotationClickHandler(this,null,false,false));
		createButton.setEnabled(!Application.getUser().isEmpty());
		buttons.add(createButton);
		
		header.add(buttons);
		
		// Placeholder for the annotation form 
		header.add(annotationFormPanel);
		
		return header;
	}
}
