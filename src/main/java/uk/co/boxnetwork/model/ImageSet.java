package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="image_set")
public class ImageSet {
	@Id
	@GeneratedValue
    private Long id;
		
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
		
	@Column(name="created_at")
	private Date createdAt;
	
	
	private String title;
	
	@Column(name="file_counter")
	private Integer fileCounter;
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "box_episode_id", nullable = true)
	private BoxEpisode boxEpisode;	

	@Column(name="image_set_type")
	private ImageSetType imageSetType=ImageSetType.DEFAULT;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getFileCounter() {
		return fileCounter;
	}

	public void setFileCounter(Integer fileCounter) {
		this.fileCounter = fileCounter;
	}
	

	public BoxEpisode getBoxEpisode() {
		return boxEpisode;
	}

	public void setBoxEpisode(BoxEpisode boxEpisode) {
		this.boxEpisode = boxEpisode;
	}

	public ImageSetType getImageSetType() {
		return imageSetType;
	}

	public void setImageSetType(ImageSetType imageSetType) {
		this.imageSetType = imageSetType;
	}

	@Override
	public String toString(){
		return "id=["+id+"]createdAt=["+createdAt+"]lastModifiedAt=["+lastModifiedAt+"]title=["+title+"]imageSetType=["+imageSetType+"]";
		
	}
	
		
}
