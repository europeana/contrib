package gr.ntua.ivml.awareness.persistent;

import gr.ntua.ivml.awareness.search.SolrHelper;
import gr.ntua.ivml.awareness.util.HasEuropeanaResource;
import gr.ntua.ivml.awareness.util.MongoDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;
import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import play.Play;


import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PostPersist;
import com.google.code.morphia.annotations.PrePersist;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;



@Entity("digitalStories")
public class DigitalStory {
	public static final Logger log = Logger.getLogger( DigitalStory.class );
	
	@Id
    public ObjectId id;	
	@NotNull
	@NotBlank
	public String title;
	@NotNull
	@NotBlank
	public String description;
	public ArrayList<String> tags;
	@NotNull
	@NotBlank
	public String creator; 
	
	public String coverImage;
	

	public boolean isPublished;
	public boolean isPublishable;
	public boolean forReview;
	@NotNull
	@Size(max=12, min=1)
	@HasEuropeanaResource
	@Embedded
	public List<StoryObjectPlaceHolder> storyObjects;
	@NotNull
	@NotBlank
	public String theme;
	@NotNull
	@NotBlank
	public String language="en";
	public Date dateCreated;
	public String license="http://creativecommons.org/publicdomain/zero/1.0/";
	
	public ObjectId getId() {
		return id;
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
	
	public String getLicense() {
		return license;
	}
	
	public void setLicense(String license) {
		this.license = license;
		if(license==null){
			license="http://creativecommons.org/publicdomain/zero/1.0/";
		}
		
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ArrayList<String> getTags() {
		return tags;
	}
	
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getCoverImage() {
		
		return coverImage;
	}
	
	public void setCoverImage(String coverImage) {
		
		this.coverImage = coverImage;
		
	}
	
	@JsonIgnore
	public String getUrlImage(){
		if(coverImage!=null){
			if(coverImage.indexOf("http://", 0)==0){
				return coverImage;
			}
			else{
				return Play.application().configuration().getString("application.baseUrl")+"/images/"+coverImage;
			}
		}
		return null;
	}
	
	@JsonIgnore
   public void updateCoverImage() {
		
	
		//if(coverImage==null || coverImage.isEmpty()){
			List<StoryObjectPlaceHolder> los=this.getStoryObjects();
			boolean setim=false;
			if(los!=null){
				for(StoryObjectPlaceHolder sop:los){
					StoryObject so=MongoDB.getStoryObjectDAO().get(new ObjectId(sop.getStoryObjectID()));
					if(so!=null){
					if(so.getSosource().equalsIgnoreCase("local") && so.getStoryImage()!=null){
						StoryImage si=MongoDB.getStoryImageDAO().get(new ObjectId(so.getStoryImage()));
						if(si!=null){
							this.setCoverImage(si.getCoverImage());
							setim=true;
							break;
						}
					}
					}
				}
				if(setim==false){
					
					for(StoryObjectPlaceHolder sop:los){
						StoryObject so=MongoDB.getStoryObjectDAO().get(new ObjectId(sop.getStoryObjectID()));
						 if(so.getSosource().equalsIgnoreCase("europeana") && so.getStoryImage()!=null){
							EuropeanaImage eui=MongoDB.getEuropenaImageDAO().get(new ObjectId(so.getStoryImage()));
							if(eui!=null){
								this.setCoverImage(eui.getCoverImage());
								setim=true;
								break;
							}
							
						
						}
					}
				}
				if(setim==false){this.coverImage=null;}
			}
		//}
	}
	
	
	public boolean isPublished() {
		return isPublished;
	}
	
	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}
	
	public boolean isPublishable() {
		return isPublishable;
	}
	
	public void setPublishable(boolean isPublishable) {
		this.isPublishable = isPublishable;
	}
	
	public boolean isForReview() {
		return forReview;
	}
	
	public void setForReview(boolean forReview) {
		this.forReview = forReview;
	}
	
	
	public String getTheme() {
		return theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	/**
	 * somehow we need to figure out at this point if the linked story objects are eligible or not, apart from the list size. E.g. one of the object has to be a europeana object.
	 * Also boolean values do not have to be instantiated, the system does this automatically.
	 */
	@PrePersist
	public void prePersist() {
		if(this.dateCreated == null){
			this.dateCreated = new Date();			
		}
		
		ValidatorFactory factory =
		Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<DigitalStory>>  res = validator.validate(this);
		
		if(res.size() > 0){
			this.isPublishable = false;
		}else{
			this.isPublishable = true;
		}
	}
	
	@PostPersist
	public DBObject solrize( DBObject json ) {
		SolrHelper.solrize( getBsonForSolr(), this);
		return json;
	}

	public List<StoryObjectPlaceHolder> getStoryObjects() {
		return storyObjects;
	}

	public void setStoryObjects(List<StoryObjectPlaceHolder> storyObjects) {
		this.storyObjects = storyObjects;
		
	}
	
	public DBObject getBsonForSolr() {
		try {
			DBObject bsonDs = MongoDB.getMorphia().getMapper().toDBObject(this);
			List<StoryObject> lso = MongoDB.getStoryObjectDAO().getStoryObjectsByPlaceHolders(getStoryObjects());
			BasicDBList bsonStories = new BasicDBList();
			for( StoryObject so: lso ) {
				if( so != null ) {
					bsonStories.add( so.getBsonForSolr());
				}
			}
			bsonDs.removeField("storyObjects");
			bsonDs.put("storyObjects", bsonStories );

			String themeId = getTheme();
			if( themeId != null && themeId.length()>0 ) {
				// replace theme link with json
				Theme th = MongoDB.getThemeDAO().get( new ObjectId( themeId  ));
				BSONObject themeJson = th.getBsonForSolr();
				bsonDs.removeField( "theme");
				bsonDs.put( "theme", themeJson );
			}
			return bsonDs;
		} catch( Exception e ) {
			log.error( "Couldnt get BSON from Morphia", e );
			return new BasicDBObject();
		}
	}
}
