package gr.ntua.ivml.awareness.persistent;

import gr.ntua.ivml.awareness.util.MongoDB;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.utils.IndexDirection;
import com.mongodb.BasicDBObject;

@Entity("digitalObjects")
public class StoryObject {
	private static final Logger log = Logger.getLogger( StoryObject.class );
	
	@Id
    public ObjectId id;
	
	public String title;
	public String description;
	public String additionalInfo;
	public String provider; //Europeana provider
	public String dataProvider; //Europeana data provider
	public String dcCreator; //User who created the item
	@NotNull
	@NotBlank
	public String creator;
	@NotNull
	@NotBlank
	public String source;
	public List<String> tags;
	@NotNull
	@NotBlank
	public String type;
	@NotNull
	@NotBlank
	public String url;
	public String language;
	public Date dateCreated;
	@Indexed(value=IndexDirection.GEO2D, name="geo")
    protected double[] loc = null;
	@NotNull
	@NotBlank
	public String sosource; 
	public String thumbnail;
	public String storyImage;

	public String license;
	
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
	
	
	public String getStoryImage() {
		return storyImage;
	}
	public void setStoryImage(String sid) {
		this.storyImage = sid;
	}
	
	public String getLicense() {
		return license;
	}
	
	public void setLicense(String license) {
		this.license = license;
		if(!sosource.equalsIgnoreCase("Europeana") && (license==null || license.length()==0)){
			license="http://creativecommons.org/publicdomain/zero/1.0/";
		}
		
	}
	
	public void setSosource(String sosource){
		
		if(sosource==null){
			if(source.indexOf("www.youtube.com")>-1){
				sosource="YouTube";
			}
			else if(source.indexOf("europeana.eu")>-1){
				sosource="Europeana";
			}
			else{sosource="local";}
		}
		this.sosource=sosource;
	}
	
	
	public String getSosource(){
		return sosource;
	}
	
	

	public String getThumbnail() {
		if(thumbnail==null){
			if(sosource.equalsIgnoreCase("Europeana")){
				if(type.equalsIgnoreCase("video")){
					thumbnail="images/cubes/video-eu.png";
				}
				else if(type.equalsIgnoreCase("sound")){
					thumbnail="images/cubes/sound-eu.png";
				}
				else if(type.equalsIgnoreCase("image")){
					thumbnail="images/cubes/image-eu.png";
				}
				else if(type.equalsIgnoreCase("text")){
					thumbnail="images/cubes/text-eu.png";
				}
			}
			else if(sosource.equalsIgnoreCase("Youtube")){
				thumbnail="images/cubes/youtube.png";
			}
			else{
				if(type.equalsIgnoreCase("video")){
					thumbnail="images/cubes/video.png";
				}
				else if(type.equalsIgnoreCase("sound")){
					thumbnail="images/cubes/sound.png";
				}
				else if(type.equalsIgnoreCase("image")){
					thumbnail="images/cubes/image.png";
				}
				else if(type.equalsIgnoreCase("text")){
					thumbnail="images/cubes/text.png";
				}
			}
			
		}	
		else{ thumbnail="../images/"+thumbnail;}
		return thumbnail;
	}
	
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getDataProvider() {
		return dataProvider;
	}
	public void setDataProvider(String dprovider) {
		this.dataProvider = dprovider;
	}
	
	public String getDcCreator() {
		return dcCreator;
	}
	public void setDcCreator(String dccreator) {
		this.dcCreator = dccreator;
	}
	
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		if(type.equalsIgnoreCase("image") && sosource.equalsIgnoreCase("local"))
			url= "../images/"+url;
		else if (type.equalsIgnoreCase("image") && sosource.equalsIgnoreCase("europeana") && this.storyImage!=null){
			EuropeanaImage euImage=MongoDB.getEuropenaImageDAO().get(new ObjectId(storyImage));
			if(euImage!=null){
				if(euImage.getCoverImage().startsWith("http://")){
					url=euImage.getCoverImage();
				}else{
				url= "../images/"+euImage.getCoverImage();}
			}
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public double[] getLoc() {
		return loc;
	}
	public void setLoc(double[] loc) {
		this.loc = loc;
	}
	
	/**
	 *TODO what else do we need to check before we save the document apart from the creation date?
	 */
	@PrePersist
	public void prePersist() {
		if(this.dateCreated == null){
			this.dateCreated = new Date();
		}
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public BSONObject getBsonForSolr() {
		try {
			return MongoDB.getMorphia().getMapper().toDBObject(this);
		} catch( Exception e ) {
			log.debug( "Couldnt get BSON from StoryObject " + this.toString(), e );
			return new BasicDBObject();
		}
	}
}
