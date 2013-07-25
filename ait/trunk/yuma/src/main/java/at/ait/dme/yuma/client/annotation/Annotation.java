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

package at.ait.dme.yuma.client.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import at.ait.dme.yuma.client.image.annotation.ImageAnnotation;

/**
 * This is the base class of all annotation classes. At the moment, we only need
 * {@link ImageAnnotation} but in the future there will be audio and video
 * based implementations.
 * 
 * For now, we only share the semantic tags but most of the fields of {@link ImageAnnotation}
 * can be shared between different implementations of Annotation {@link Annotation}.
 * 
 * @author Christian Sadilek
 */
// TODO complete when implementing video and/or audio annotation! 
public abstract class Annotation implements Serializable {
	private static final long serialVersionUID = 5702749187171740401L;

	/** annotation linked resources **/
	protected List<SemanticTag> semanticTags;
	
	public Collection<SemanticTag> getSemanticTags() {
	    return semanticTags;
	}
	
	public void addSemanticTag(SemanticTag semanticTag) {
	    if(semanticTags == null) {
	        semanticTags = new ArrayList<SemanticTag>();
	    }
	    semanticTags.add(semanticTag);
	}
	
	public void setSemanticTags(List<SemanticTag> semanticTags) {
	    this.semanticTags = semanticTags;
	}
	
	public boolean hasSemanticTags() {
		return (semanticTags!=null && !semanticTags.isEmpty());
	}
}
