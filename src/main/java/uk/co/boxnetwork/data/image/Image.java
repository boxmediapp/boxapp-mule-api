package uk.co.boxnetwork.data.image;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Image {
    private Long id;			
	private Date createdAt;	
	private String filename;	
	private String s3BaseURL;
	private int width;	
	private int height;
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
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public Image(){
		
	}
	public Image(uk.co.boxnetwork.model.Image image){
		this.id=image.getId();		
		this.createdAt=image.getCreatedAt();					
		this.filename=image.getFilename();		
		this.s3BaseURL=image.getS3BaseURL();
		this.width=image.getWidth();		
		this.height=image.getHeight();
	}	
	public void update(uk.co.boxnetwork.model.Image image){		
		image.setFilename(this.filename);		
		image.setS3BaseURL(this.s3BaseURL);
		image.setWidth(this.width);		
		image.setHeight(this.height);		
	}
	
}
