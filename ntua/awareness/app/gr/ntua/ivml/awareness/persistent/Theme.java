package gr.ntua.ivml.awareness.persistent;


import gr.ntua.ivml.awareness.util.MongoDB;

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
import com.mongodb.BasicDBObject;



/* author Anna */

@Entity("themes")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE)
@JsonSerialize(include = Inclusion.NON_NULL)
public class Theme{
	
		
 	    @Id
	    public ObjectId id;

	   
 	    @NotNull(message="Title is mandatory")
		@NotBlank
	    public String title;
 	    
 	    @NotNull
		@NotBlank
	    public String description;
	    
	    @NotNull
		@NotBlank
		public String wallpaper;
	    
	    @NotNull
		@NotBlank
		public String banner;
	    
	    
	    @NotNull
		@NotBlank
		public String minibanner;
	    
	    
	    @NotNull
		@NotBlank
		public String background;
	    
	    public boolean defaultTheme=false;
	    
	    
	    @JsonIgnore
	    public ObjectId getObjectId() {
			return id;
		}
	    
        
	    public String getId(){
	    	return id.toString();
	    }
	    
	    public boolean getDefaultTheme(){
	    	return defaultTheme;
	    }
		
	    public void setDefaultTheme(boolean isdefault){
	    	defaultTheme=isdefault;
	    }
	    
	    public void setId(ObjectId id) {
			this.id = id;
		}
		
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getWallpaper() {
			return wallpaper;
		}
		public void setWallpaper(String wallpaper) {
			this.wallpaper = wallpaper;
		}
		public void setBanner(String banner){
			this.banner=banner;
			
		}
		
		public String getBanner() {
			return banner;
		}
		
		public void setMinibanner(String minibanner){
			this.minibanner=minibanner;
			
		}
		
		public String getMinibanner() {
			return minibanner;
		}
		
		public void setBackground(String background){
			this.background=background;
			
		}
		
		public String getBackground() {
			return background;
		}


		public BSONObject getBsonForSolr() {
			try {
				return MongoDB.getMorphia().toDBObject(this);
			} catch( Exception e ) {
				return new BasicDBObject();
			}
		}
}