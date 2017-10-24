package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="image")
public class Image {
	@Id
	@GeneratedValue
    private Long id;
		
		
	@Column(name="created_at")
	private Date createdAt;
	
	@Column(name="file_name")
	private String filename;
	
	@Column(name="s3_base_url")
	private String s3BaseURL;
	
	
	private int width;
	
	private int height;
	 
	
	private String tags;
 	    	
	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn( name = "image_set_id", nullable = false )	
	private ImageSet imageSet;

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

	public ImageSet getImageSet() {
		return imageSet;
	}

	public void setImageSet(ImageSet imageSet) {
		this.imageSet = imageSet;
	}

	public String getS3BaseURL() {
		return s3BaseURL;
	}

	public void setS3BaseURL(String s3BaseURL) {
		this.s3BaseURL = s3BaseURL;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
	
}
