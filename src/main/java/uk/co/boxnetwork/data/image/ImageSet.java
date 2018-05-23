package uk.co.boxnetwork.data.image;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.boxnetwork.model.ImageSetType;


public class ImageSet {
	
    private Long id;
		
	
	private Date lastModifiedAt;
		
	
	private Date createdAt;
	
		
	
	private String programmeNumber;
	
	private String contractNumber;
	private String episodeNumber;
	
	
	private String title;
	private List<Image> images=new ArrayList<Image>();
	
	private ImageSetType imageSetType=ImageSetType.DEFAULT;
	
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
	
	public ImageSetType getImageSetType() {
		return imageSetType;
	}
	public void setImageSetType(ImageSetType imageSetType) {
		this.imageSetType = imageSetType;
	}
	public ImageSet(uk.co.boxnetwork.model.ImageSet imageSet){
		this.id=imageSet.getId();
		this.lastModifiedAt=imageSet.getLastModifiedAt();
		this.createdAt=imageSet.getCreatedAt();
		if(imageSet.getBoxEpisode()!=null){
			this.programmeNumber=imageSet.getBoxEpisode().getProgrammeNumber();
		}
			
		this.title=imageSet.getTitle();
		this.fileCounter=imageSet.getFileCounter();
		this.imageSetType=imageSet.getImageSetType();
		
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
		imageSet.setTitle(this.title);		
		imageSet.setFileCounter(fileCounter);
		imageSet.setImageSetType(imageSetType);
	}
	@Override
	public String toString(){
		return "id=["+this.id+"]programmeNumber=["+programmeNumber+"]title=["+this.title+"]imageSetType=["+imageSetType+"]";
	}
}
