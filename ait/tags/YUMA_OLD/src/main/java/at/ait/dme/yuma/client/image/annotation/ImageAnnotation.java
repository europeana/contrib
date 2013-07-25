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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import at.ait.dme.yuma.client.annotation.Annotation;
import at.ait.dme.yuma.client.annotation.SemanticTag;
import at.ait.dme.yuma.client.image.ImageFragment;

/**
 * represents an image annotation with an unique addressable URI as id.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotation extends Annotation {
	private static final long serialVersionUID = -8296122947267690316L;

	public enum Scope {PUBLIC, PRIVATE};		
	
	// these id's must be addressable URLs	
	/** the id of this annotation */
	private String id;
	/** the parent annotation id of this annotation */
	private String parentId;
	/** the root annotation id of this annotation */
	private String rootId;			
	/** the id of the annotated object (e.g. the url to the image)*/
	private String imageUrl;
	/** an optional id of an external object this annotation is linked to. this
	 * id does not have to be an addressable URI */
	private String externalObjectId;
	
	/** the creation date of the annotation */
	private Date created;
	/** the last modification date of the annotation */
	private Date modified;
	/** the user who created the annotation */
	private String createdBy;
	
	/** the title of the annotation */
	private String title;
	/** the body text of the annotation */
	private String text;	
	/** the mime type of the annotated object */
	private String mimeType;
	
	/** annotation replies */
	private List<ImageAnnotation> replies;	
	/** annotation links (LOD) */
	private List<String> links;
	
	/** the annotated image fragment */
	private ImageFragment fragment;
	/** scope of the annotation */
	private Scope scope;
		
	public ImageAnnotation() {
		this.created = new Date();
		this.modified = new Date();
		this.scope = Scope.PUBLIC;
		this.mimeType = "image/jpeg";
		// initialized with a void fragment
		this.fragment = new ImageFragment();
	}
	
	public ImageAnnotation(String objectId, String createdBy) {
		this();
		this.imageUrl = objectId;
		this.createdBy = createdBy;
	}
	
	public ImageAnnotation(String id, String objectId, String createdBy) {
		this(objectId, createdBy);
		this.id = id;
	}
	
	public ImageAnnotation(String objectId, String externalObjectId, String parentId, 
			String rootId, String createdBy, String title, String text, Scope scope, 
			List<SemanticTag> semanticTags) {
		this(objectId, createdBy);
		this.title=title;
		this.text=text;
		this.externalObjectId = externalObjectId;
		this.parentId = parentId;
		this.rootId = rootId;
		this.scope = scope;
		this.semanticTags = semanticTags;
	}

	public ImageAnnotation(String id, String objectId, String externalObjectId, 
			String parentId, String rootId, String createdBy, String title, String text, 
			Scope scope, List<SemanticTag> semanticTags) {
		this(objectId, externalObjectId, parentId, rootId, createdBy, title, text, scope, 
				semanticTags);
		this.id = id;
	}

	public ImageAnnotation(String id, String objectId, String externalObjectId, 
			String parentId, String rootId, String createdBy, String title, String text, 
			Scope scope) {
		this(id, objectId, externalObjectId, parentId, rootId, createdBy, title, text, scope, null);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getRootId() {
		return rootId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getExternalObjectId() {
		return externalObjectId;
	}
	
	public void setExternalObjectId(String externalObjectId) {
		this.externalObjectId = externalObjectId;
	}

	public Date getModified() {
		return new Date(modified.getTime());
	}
	
	public void setModified(Date modified) {
		this.modified = new Date(modified.getTime());
	}
	
	public Date getCreated() {
		return new Date(created.getTime());
	}
	
	public void setCreated(Date created) {
		this.created = new Date(created.getTime());
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}		
	
	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}	
	
	public List<ImageAnnotation> getReplies() {
		return replies;
	}
	
	public void addReply(ImageAnnotation reply) {
		if(replies==null) 
			replies = new ArrayList<ImageAnnotation>();
		replies.add(reply);
	}
	
	public void removeReply(ImageAnnotation reply) {
		if(replies!=null) {
			replies.remove(reply);
		}	
	}
	
	public void setReplies(List<ImageAnnotation> replies) {
		this.replies = replies;
	}
	
	public boolean hasReplies() {
		return (replies!=null && !replies.isEmpty());
	}
	
	public Collection<String> getLinks() {
		return links;
	}
	
	public void addLink(String link) {
		if(links==null) 
			links = new ArrayList<String>();
		links.add(link);
	}
	
	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}
	
	public boolean hasLinks() {
		return (links!=null && !links.isEmpty());
	}
	
	public ImageFragment getFragment() {
		return fragment;
	}

	public void setFragment(ImageFragment annotatedFragment) {
		this.fragment = annotatedFragment;
	}
	
	public boolean hasFragment() {
		return (fragment!=null && !fragment.isVoid());
	}

	public Scope getScope() {
		return scope;
	}
	
	public String getScopeAsString() {
		return scope.name();
	}
	
	/**
	 * sets the scope for the given string
	 * defaults to PUBLIC in case of an unknown or empty scope string
	 * 
	 * @param scopeString
	 */
	public void setScopeFromString(String scopeString) {
		scope = Scope.PUBLIC;
		try {
			if(scopeString!=null)
				scope=Scope.valueOf(scopeString.toUpperCase());
		} catch(IllegalArgumentException iae) {
			// can be ignored, we default to public
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("id:").append(getId())
		.append("\nobjectId:").append(getImageUrl())
		.append("\nexternalObjectId:").append(getExternalObjectId())		
		.append("\nrootId:").append(getRootId())	
		.append("\nparentId:").append(getParentId())
		.append("\ncreated:").append(getCreated())
		.append("\ncreated by:").append(getCreatedBy())	
		.append("\nmodified:").append(getModified())		
		.append("\ntitle:").append(getTitle())
		.append("\ntext:").append(getText())
		.append("\nmimeType:").append(getMimeType())		
		.append("\nscope:").append(scope.name());	
		if(hasLinks())
			for(String link : links) buf.append("\nlink:").append(link);
		
		return buf.toString();
	}

	public String toHtml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>")		
		.append("<head><title>")
		.append(getTitle())
		.append("</title></head>")
		.append("<body>")	
		.append(getText())	
		.append("</body>")		
		.append("</html>");			
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ImageAnnotation)) return false;
		if(this==obj) return true;
		
		ImageAnnotation annotation = (ImageAnnotation)obj;
		
		if (id == null) {
			if (annotation.id != null)
				return false;
		} else if (!id.equals(annotation.id))
			return false;
		
		if (imageUrl == null) {
			if (annotation.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(annotation.imageUrl))
			return false;
		
		if (externalObjectId == null) {
			if (annotation.externalObjectId != null)
				return false;
		} else if (!externalObjectId.equals(annotation.externalObjectId))
			return false;
		
		if (parentId == null) {
			if (annotation.parentId != null)
				return false;
		} else if (!parentId.equals(annotation.parentId))
			return false;
		
		if (rootId == null) {
			if (annotation.rootId != null)
				return false;
		} else if (!rootId.equals(annotation.rootId))
			return false;
		
		if (created == null) {
			if (annotation.created != null)
				return false;
		} else {
			// compare creation date w/o milliseconds
			Date thisDate = new Date((created.getTime()/1000)*1000);
			Date compareDate = new Date((annotation.created.getTime()/1000)*1000);			
			if (!thisDate.equals(compareDate))
				return false;
		}
		
		if (modified == null) {
			if (annotation.modified != null)
				return false;
		} else {
			// compare modification date w/o milliseconds
			Date thisDate = new Date((modified.getTime()/1000)*1000);
			Date compareDate = new Date((annotation.modified.getTime()/1000)*1000);			
			if (!thisDate.equals(compareDate))
				return false;
		}
		
		if (createdBy == null) {
			if (annotation.createdBy != null)
				return false;
		} else if (!createdBy.equals(annotation.createdBy))
			return false;
		
		if (title == null) {
			if (annotation.title != null)
				return false;
		} else if (!title.equals(annotation.title))
			return false;
	
		if (text == null) {
			if (annotation.text != null)
				return false;
		} else if (!text.equals(annotation.text))
			return false;
	
		if (mimeType == null) {
			if (annotation.mimeType != null)
				return false;
		} else if (!mimeType.equals(annotation.mimeType))
			return false;
		
		if (replies == null) {
			if (annotation.replies != null)
				return false;
		} else if (!replies.equals(annotation.replies))
			return false;
		
		if (links == null) {
			if (annotation.links != null)
				return false;
		} else if (!links.equals(annotation.links))
			return false;
		
		if (fragment == null) {
			if (annotation.fragment != null)
				return false;
		} else if (!fragment.equals(annotation.fragment))
			return false;
		
		if (semanticTags == null) {
			if (annotation.semanticTags != null)
				return false;
		} else if (!semanticTags.equals(annotation.semanticTags))
			return false;
		
		if(!scope.equals(annotation.scope)) return false;
		
		return true;	
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
