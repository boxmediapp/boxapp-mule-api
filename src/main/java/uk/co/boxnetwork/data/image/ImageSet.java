package uk.co.boxnetwork.data.image;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ImageSet {
	
    private Long id;
		
	
	private Date lastModifiedAt;
		
	
	private Date createdAt;
	
	private Long episodeId;	
	
	private String programmeNumber;
	
	private String contractNumber;
	private String episodeNumber;
	
	
	private String title;
	private List<Image> images=new ArrayList<Image>();
	
	
	private Integer fileCounter;
	
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
	
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	
	public Integer getFileCounter() {
		return fileCounter;
	}
	public void setFileCounter(Integer fileCounter) {
		this.fileCounter = fileCounter;
	}
	
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public String getEpisodeNumber() {
		return episodeNumber;
	}
	public void setEpisodeNumber(String episodeNumber) {
		this.episodeNumber = episodeNumber;
	}
	public ImageSet(){
		
	}
	
	public ImageSet(uk.co.boxnetwork.model.ImageSet imageSet){
		this.id=imageSet.getId();
		this.lastModifiedAt=imageSet.getLastModifiedAt();
		this.createdAt=imageSet.getCreatedAt();

		this.programmeNumber=imageSet.getProgrammeNumber();	
		this.title=imageSet.getTitle();
		this.fileCounter=imageSet.getFileCounter();
		
		if(programmeNumber!=null&& programmeNumber.length()>0){
			String matParts[]=programmeNumber.split("/");
			this.contractNumber=matParts[0];
			if(matParts.length>1){
				this.episodeNumber=matParts[1];				
			}
		}	
	
	}
	public void update(uk.co.boxnetwork.model.ImageSet imageSet){		
		imageSet.setId(this.id);		
		imageSet.setProgrammeNumber(this.programmeNumber);
		imageSet.setEpisodeId(this.episodeId);
		imageSet.setTitle(this.title);		
		imageSet.setFileCounter(fileCounter);
	}
}
