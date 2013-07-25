package at.researchstudio.dme.annotation.db.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;

/**
 * Represents an annotation entity. Annotations of different types (image, audio, video, etc)
 * differ only in the representation of the addressed fragment (svg, mpeg21 pointers, etc).
 * We decided not to model the fragment representation but use the string representation here.
 * Therefore, we can use this entity class for all types of annotations. 
 * 
 * @author Christian Sadilek
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "annotation.subtree", 
				query = "from Annotation a where a.rootId = :rootId"), 						
	@NamedQuery(name = "annotation.list",
				query = "from Annotation a where a.objectUrl = :objectUrl"),				
	@NamedQuery(name = "annotation.list.linked",
				query = "from Annotation a where externalObjectId = :id"),				
	@NamedQuery(name = "annotation.count",
				query = "select count(*) from Annotation a where a.objectUrl = :objectUrl"),
	@NamedQuery(name = "annotation.count.linked",
				query = "select count(*) from Annotation a where a.externalObjectId = :id"),
	@NamedQuery(name = "annotation.search",
				query = "from Annotation a where (lower(a.title) like concat('%',:term,'%') or " +
						"lower(a.text) like concat('%',:term,'%'))")
})
public class Annotation implements Serializable {
	private static final long serialVersionUID = 2167432601402234311L;
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	@Column
	private Long parentId;
	
	@Column
	private Long rootId;			
		
    @Column(length = 512, nullable = false)
    @Index(name = "objecturl")
	private String objectUrl;
	
    @Column(length = 512, nullable = true)    
	private String externalObjectId;
	
    @Column
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
	private Date modified;
    
    @Column(length = 64)
	private String createdBy;
    
    @Column(length = 128)	
	private String title;
	
    @Column(length = 2048)		
    private String text;	
    
    @Column(length = 32)    
	private String mimeType;
	
    @Column(length = 2048)
    private String annotatedFragment;
    
    @OneToMany
    @JoinColumn(name="parentId")
	private List<Annotation> replies;		
    
    @CollectionOfElements 
    private List<String> links;	
		
	@Enumerated(EnumType.STRING)
	private Scope scope;
	
	public enum Scope {PUBLIC, PRIVATE};		

	public Annotation() {
		this.created = new Date();
		this.modified = new Date();
		this.scope = Scope.PUBLIC;
		this.replies = new ArrayList<Annotation>();
	}
	
	public Annotation(String objectUrl, String externalObjectId, Long parentId, 
			Long rootId, String createdBy, String title, String text, Scope scope) {
		this();
		this.objectUrl = objectUrl;
		this.externalObjectId = externalObjectId;
		this.parentId = parentId;
		this.rootId = rootId;
		this.createdBy = createdBy;
		this.title = title;
		this.text = text;
		this.scope = scope;
	}

	public Annotation(Long id, String objectUrl, String externalObjectId, 
			Long parentId, Long rootId, String createdBy, String title, String text, 
			Scope scope) {
		this(objectUrl, externalObjectId, parentId, rootId, createdBy, title, text, scope);
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Long getRootId() {
		return rootId;
	}

	public void setRootId(Long rootId) {
		this.rootId = rootId;
	}
	
	public String getObjectUrl() {
		return objectUrl;
	}
	
	public void setObjectUrl(String objectUrl) {
		this.objectUrl = objectUrl;
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
	
	public List<Annotation> getReplies() {
		return replies;
	}
	
	public void addReply(Annotation reply) {
		if(replies==null) 
			replies = new ArrayList<Annotation>();
		replies.add(reply);
	}
	
	public void removeReply(Annotation reply) {
		if(replies!=null) {
			replies.remove(reply);
		}
	}
	
	public void setReplies(List<Annotation> replies) {
		this.replies = replies;
	}

	public List<String> getLinks() {
		return links;
	}

	public void addLink(String link) {
		if(links==null) 
			links = new ArrayList<String>();
		links.add(link);
	}
	
	public void setLinks(List<String> links) {
		this.links = links;
	}
	
	public String getAnnotatedFragment() {
		return annotatedFragment;
	}

	public void setAnnotatedFragment(String annotatedFragment) {
		this.annotatedFragment = annotatedFragment;
	}

	public Scope getScope() {
		return scope;
	}
	
	public String getScopeAsString() {
		return scope.name();
	}
	
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("id:").append(getId())
		.append("\nobjectId:").append(getObjectUrl())
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
		if(!(obj instanceof Annotation)) return false;
		if(this==obj) return true;
		
		Annotation annotation = (Annotation)obj;
		
		if (id == null) {
			if (annotation.getId() != null)
				return false;
		} else if (!id.equals(annotation.getId()))
			return false;
		
		if (objectUrl == null) {
			if (annotation.getObjectUrl() != null)
				return false;
		} else if (!objectUrl.equals(annotation.getObjectUrl()))
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