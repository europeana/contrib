package gr.ntua.ivml.awareness.persistent;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.hibernate.validator.constraints.NotBlank;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PrePersist;


/* author Anna */

@Entity("europeanaimages")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE)
@JsonSerialize(include = Inclusion.NON_NULL)
public class EuropeanaImage {
	@Id
    public ObjectId id;
	
	public Date dateCreated;
	
	@NotNull
	@NotBlank
	public String thumbnail;
	
	@NotNull
	@NotBlank
	public String coverImage;
	
	
	@JsonIgnore
	public ObjectId getObjectId() {
			return id;
		}
	    
     
	public String getId(){
	    	return id.toString();
	    }
	    
	
		
	public String getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
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
	
	
	
	
}
