package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="image_set")
public class ImageSet {
	@Id
	@GeneratedValue
    private Long id;
		
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
		
	@Column(name="created_at")
	private Date createdAt;
	
	
	
	@Column(name="episode_id")
	private Long episodeId;	
	
	
	@Column(name="programme_number")	
	private String programmeNumber;
	
	private String title;
	
	@Column(name="file_counter")
	private Integer fileCounter;
	

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

	

	public Long getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(Long episodeId) {
		this.episodeId = episodeId;
	}

	
	public String getProgrammeNumber() {
		return programmeNumber;
	}

	public void setProgrammeNumber(String programmeNumber) {
		this.programmeNumber = programmeNumber;
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

	
	
		
}
