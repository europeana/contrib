package gr.ntua.ivml.awareness.persistent;

import gr.ntua.ivml.awareness.search.SolrHelper;
import gr.ntua.ivml.awareness.util.MongoDB;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.hibernate.validator.constraints.NotBlank;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PostPersist;
import com.google.code.morphia.annotations.PrePersist;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


/* author Anna */

@Entity("storyimages")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE)
@JsonSerialize(include = Inclusion.NON_NULL)
public class StoryImage {
	@Id
    public ObjectId id;
	
	@NotNull
	@NotBlank
	public String original;
	public Date dateCreated;
	
	@NotNull
	@NotBlank
	public String thumbnail;
	
	@NotNull
	@NotBlank
	public String coverImage;
	
	@NotNull
	@NotBlank
	public String objectPreview;
	
	@NotNull
	@NotBlank
	public String objectThumbnail;
	
	@NotNull
	@NotBlank
	public String userid;
	
	public String title;
	public String description;
	
	@JsonIgnore
	public ObjectId getObjectId() {
			return id;
		}
	    
     
	public String getId(){
	    	return id.toString();
	    }
	    
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String uid) {
		this.userid = uid;
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	
	
	public String getObjectThumbnail() {
		return objectThumbnail;
	}
	public void setObjectThumbnail(String objectThumbnail) {
		this.objectThumbnail = objectThumbnail;
	}
	
	public String getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}
	
	public String getObjectPreview() {
		return objectPreview;
	}
	
	public void setObjectPreview(String objectPreview) {
		this.objectPreview = objectPreview;
	}
	
	
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	@PrePersist
	public void prePersist() {
		if(this.dateCreated == null){
			this.dateCreated = new Date();
		}
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	
	
	public BSONObject getBsonForSolr() {
		try {
			return MongoDB.getMorphia().toDBObject(this);
		} catch( Exception e ) {
			return new BasicDBObject();
		}
	}

	@PostPersist
	public DBObject solrize( DBObject json ) {
		SolrHelper.solrize(json, this);
		return json;
	}

}
