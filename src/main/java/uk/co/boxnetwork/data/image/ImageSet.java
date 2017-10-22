package uk.co.boxnetwork.data.image;

import java.util.Date;


public class ImageSet {
	
    private Long id;
		
	
	private Date lastModifiedAt;
		
	
	private Date createdAt;
	
	private String tags;
	
	
	private Long episodeId;	
	
	private String programmeNumber;
	
	private String title;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
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
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
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
	public ImageSet(){
		
	}
	public ImageSet(uk.co.boxnetwork.model.ImageSet imageSet){
		this.id=imageSet.getId();
		this.lastModifiedAt=imageSet.getLastModifiedAt();
		this.createdAt=imageSet.getCreatedAt();
		this.tags=imageSet.getTags();
		this.programmeNumber=imageSet.getProgrammeNumber();	
		this.title=imageSet.getTitle();
	}
	public void update(uk.co.boxnetwork.model.ImageSet imageSet){		
		imageSet.setId(this.id);
		imageSet.setTags(this.tags);
		imageSet.setProgrammeNumber(this.programmeNumber);
		imageSet.setEpisodeId(this.episodeId);
		imageSet.setTitle(this.title);		
	}
}
