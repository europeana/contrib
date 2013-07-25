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

/**
 * A group of {@link SemanticTag}s (with a name and type). 
 *
 * @author Rainer Simon
 */
public class SemanticTagGroup implements Serializable {
	private static final long serialVersionUID = 5225121871824438123L;

	/**
	 * The title
	 */
    private String title;
    
    /**
     * The type
     */
    private String type;

    private Collection<SemanticTag> tags = new ArrayList<SemanticTag>();
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public Collection<SemanticTag> getAmbiguousTags() {
        return tags;
    }

    public void addTag(SemanticTag t) {
        this.tags.add(t);
    }

    public void setTags(Collection<SemanticTag> tags) {
        this.tags = tags;
    }

}
