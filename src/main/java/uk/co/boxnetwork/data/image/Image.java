package uk.co.boxnetwork.data.image;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.ImageBoxMediaStatus;
import uk.co.boxnetwork.model.ImageStatus;

public class Image {
    private Long id;			
	private Date createdAt;	
	private Date lastModifiedAt;
	private String filename;	
	private String s3BaseURL;
	
	private Integer width;	
	private Integer height;
	private String tags;
	private ImageSet imageSet;
	private ImageStatus imageStatus;
	private String url;
	
	private ImageBoxMediaStatus imageBoxMediaStatus;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getS3BaseURL() {
		return s3BaseURL;
	}
	public void setS3BaseURL(String s3BaseURL) {
		this.s3BaseURL = s3BaseURL;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public Image(){
		
	}
	
	
	public ImageSet getImageSet() {
		return imageSet;
	}
	public ImageStatus getImageStatus() {
		return imageStatus;
	}
	public void setImageStatus(ImageStatus imageStatus) {
		this.imageStatus = imageStatus;
	}
	public void setImageSet(ImageSet imageSet) {
		this.imageSet = imageSet;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	
	
	public ImageBoxMediaStatus getImageBoxMediaStatus() {
		return imageBoxMediaStatus;
	}
	public void setImageBoxMediaStatus(ImageBoxMediaStatus imageBoxMediaStatus) {
		this.imageBoxMediaStatus = imageBoxMediaStatus;
	}
	public Image(uk.co.boxnetwork.model.Image image, AppConfig appConfig){
		this.id=image.getId();		
		this.createdAt=image.getCreatedAt();					
		this.filename=image.getFilename();		
		this.s3BaseURL=image.getS3BaseURL();
		this.width=image.getWidth();		
		this.height=image.getHeight();
		this.tags=image.getTags();
		this.imageStatus=image.getImageStatus();
		this.url=appConfig.getImageClientBaseURL()+"/"+image.getFilename();
		this.lastModifiedAt=image.getLastModifiedAt();
		this.imageBoxMediaStatus=image.getImageBoxMediaStatus();
	}	
	public void update(uk.co.boxnetwork.model.Image image){		
		image.setFilename(this.filename);		
		image.setS3BaseURL(this.s3BaseURL);
		image.setWidth(this.width);		
		image.setHeight(this.height);
		image.setTags(this.tags);
		image.setImageStatus(this.imageStatus);
	}
	public String toString(){
		return "id=["+id+"]createdAt=["+createdAt+"]filename=["+filename+"]s3BaseURL=["+s3BaseURL+"]tags=["+tags+"]imageBoxMediaStatus=["+imageBoxMediaStatus+"]";
	}
	
}
