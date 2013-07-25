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

import java.util.List;

import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.image.annotation.ImageAnnotation.Scope;

import com.google.gwt.user.client.ui.Composite;

/**
 * Base class for image annotation forms
 * 
 * @author Christian Sadilek
 */
public abstract class ImageAnnotationForm extends Composite {
	
	public abstract ImageAnnotationForm createNew(
			ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, 
			boolean fragmentAnnotation, 
			boolean update);
	
	public void layout() {}

	public abstract String getAnnotationTitle();
	public abstract String getAnnotationText();
	public abstract Scope getAnnotationScope();
	public abstract List<SemanticTag> getSemanticTags(); 
}
