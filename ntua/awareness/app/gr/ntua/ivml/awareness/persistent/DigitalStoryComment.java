package gr.ntua.ivml.awareness.persistent;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.annotations.PrePersist;

@Entity("storyComments")
@Indexes( @Index("userId, -dateCreated, storyId") )
public class DigitalStoryComment {
	@Id
    public ObjectId id;
	@NotNull
	@NotBlank
	public String userId;
	@NotNull
	@NotBlank
	public String storyId;
	@NotNull
	@NotBlank
	public String text;
	public Date dateCreated;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	@PrePersist
	public void prePersist() {
		if(this.dateCreated == null){
			this.dateCreated = new Date();			
		}
	}
}
