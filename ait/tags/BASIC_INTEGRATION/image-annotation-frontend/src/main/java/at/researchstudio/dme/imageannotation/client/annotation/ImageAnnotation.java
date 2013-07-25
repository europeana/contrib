package at.researchstudio.dme.imageannotation.client.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import at.researchstudio.dme.imageannotation.client.image.ImageFragment;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * represents an image annotation with an unique addressable URI as id.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotation implements IsSerializable {
	private static final long serialVersionUID = 2167432601402234311L;
	private static final String DEFAULT_MIME_TYPE = "image/jpeg";
	
	public enum Scope {PUBLIC, PRIVATE};		
	
	// these id's must be addressable URLs	
	/** the id of this annotation */
	private String id;
	/** the parent annotation id of this annotation */
	private String parentId;
	/** the root annotation id of this annotation */
	private String rootId;			
	/** the id of the annotated object (e.g. the url to the image)*/
	private String objectId;
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
	private List<ImageAnnotation> replies = new ArrayList<ImageAnnotation>();	
	/** annotation links (LOD) */
	private List<String> links = new ArrayList<String>();
	
	/** the annotated image fragment */
	private ImageFragment annotatedFragment;
	/** scope of the annotation */
	private Scope scope;
		
	public ImageAnnotation() {
		this.created = new Date();
		this.modified = new Date();
		this.scope = Scope.PUBLIC;
	}
	
	public ImageAnnotation(String objectId, String externalObjectId, String parentId, 
			String rootId, String createdBy, String title, String text, Scope scope) {
		this();
		this.objectId = objectId;
		this.externalObjectId = externalObjectId;
		this.parentId = parentId;
		this.rootId = rootId;
		this.createdBy = createdBy;
		this.title = title;
		this.text = text;
		this.scope = scope;
	}

	public ImageAnnotation(String id, String objectId, String externalObjectId, 
			String parentId, String rootId, String createdBy, String title, String text, 
			Scope scope) {
		this(objectId, externalObjectId, parentId, rootId, createdBy, title, text, scope);
		this.id = id;
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
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
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
		return (mimeType!=null)?mimeType:DEFAULT_MIME_TYPE;
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

	public Collection<String> getLinks() {
		return links;
	}

	public void addLink(String link) {
		if(link==null) 
			links = new ArrayList<String>();
		links.add(link);
	}
	
	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}

	public ImageFragment getAnnotatedFragment() {
		return annotatedFragment;
	}

	public void setAnnotatedFragment(ImageFragment annotatedFragment) {
		this.annotatedFragment = annotatedFragment;
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
		.append("\nobjectId:").append(getObjectId())
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
			if (annotation.getId() != null)
				return false;
		} else if (!id.equals(annotation.getId()))
			return false;
		
		if (objectId == null) {
			if (annotation.getObjectId() != null)
				return false;
		} else if (!objectId.equals(annotation.getObjectId()))
			return false;
		
		if (externalObjectId == null) {
			if (annotation.getExternalObjectId() != null)
				return false;
		} else if (!externalObjectId.equals(annotation.getExternalObjectId()))
			return false;
		
		if (parentId == null) {
			if (annotation.getParentId() != null)
				return false;
		} else if (!parentId.equals(annotation.getParentId()))
			return false;
		
		if (rootId == null) {
			if (annotation.getRootId() != null)
				return false;
		} else if (!rootId.equals(annotation.getRootId()))
			return false;
		
		if (created == null) {
			if (annotation.getCreated() != null)
				return false;
		} else {
			// compare creation date w/o milliseconds
			Date thisDate = new Date((created.getTime()/1000)*1000);
			Date compareDate = new Date((annotation.getCreated().getTime()/1000)*1000);			
			if (!thisDate.equals(compareDate))
				return false;
		}
		
		if (modified == null) {
			if (annotation.getModified() != null)
				return false;
		} else {
			// compare modification date w/o milliseconds
			Date thisDate = new Date((modified.getTime()/1000)*1000);
			Date compareDate = new Date((annotation.getModified().getTime()/1000)*1000);			
			if (!thisDate.equals(compareDate))
				return false;
		}
		
		if (createdBy == null) {
			if (annotation.getCreatedBy() != null)
				return false;
		} else if (!createdBy.equals(annotation.getCreatedBy()))
			return false;
		
		if (title == null) {
			if (annotation.getTitle() != null)
				return false;
		} else if (!title.equals(annotation.getTitle()))
			return false;
	
		if (text == null) {
			if (annotation.getText() != null)
				return false;
		} else if (!text.equals(annotation.getText()))
			return false;
	
		if (mimeType == null) {
			if (annotation.getMimeType() != null)
				return false;
		} else if (!mimeType.equals(annotation.getMimeType()))
			return false;
		
		if (replies == null) {
			if (annotation.getReplies() != null)
				return false;
		} else if (!replies.equals(annotation.getReplies()))
			return false;
		
		if (links == null) {
			if (annotation.getLinks() != null)
				return false;
		} else if (!links.equals(annotation.getLinks()))
			return false;
		
		if (annotatedFragment == null) {
			if (annotation.getAnnotatedFragment() != null)
				return false;
		} else if (!annotatedFragment.equals(annotation.getAnnotatedFragment()))
			return false;
		
		if(!scope.equals(annotation.getScope())) return false;
		
		return true;	
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
