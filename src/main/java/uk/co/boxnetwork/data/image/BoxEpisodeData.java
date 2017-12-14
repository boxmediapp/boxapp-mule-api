package uk.co.boxnetwork.data.image;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.boxnetwork.BoxScheduleEvent;
import uk.co.boxnetwork.model.ImageStatus;


public class BoxEpisodeData {
	
		
	private Long id;
	private String  title;
	private String contractNumber;
	private String episodeNumber;
	private String programmeNumber;
	private BoxScheduleEventData schedule;
	
	private Date lastModifiedAt;
	
	private List<ImageSet> imageSets;	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	
	public String getProgrammeNumber() {
		return programmeNumber;
	}
	public void setProgrammeNumber(String programmeNumber) {
		this.programmeNumber = programmeNumber;
	}
	
	
	public List<ImageSet> getImageSets() {
		return imageSets;
	}
	public void setImageSets(List<ImageSet> imageSets) {
		this.imageSets = imageSets;
	}
	
	
	
	
	public BoxScheduleEventData getSchedule() {
		return schedule;
	}
	public void setSchedule(BoxScheduleEventData schedule) {
		this.schedule = schedule;
	}
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	public BoxEpisodeData(uk.co.boxnetwork.model.BoxEpisode episode){
		this.id=episode.getId();
		this.title = episode.getTitle();	
		this.programmeNumber=episode.getProgrammeNumber();
		if(programmeNumber!=null&& programmeNumber.length()>0){
			String matParts[]=programmeNumber.split("/");
			this.contractNumber=matParts[0];
			if(matParts.length>1){
				this.episodeNumber=matParts[1];				
			}
		}
		if(episode.getBoxSchedule()!=null){
			this.schedule=new BoxScheduleEventData(episode.getBoxSchedule());
		}
				
		this.lastModifiedAt=episode.getLastModifiedAt();
	}
	
	
	
}
