package uk.co.boxnetwork.data.s3;

import java.util.Date;

public class VideoFileItem {
	private  String file;    
    private  String episodeTitle;
    private  String programmeNumber; 
    private  Long episodeId;
    private  Date lastModifidDate;
    private  Long durationError;
    
    
    
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	public String getEpisodeTitle() {
		return episodeTitle;
	}
	public void setEpisodeTitle(String episodeTitle) {
		this.episodeTitle = episodeTitle;
	}
	public Long getEpisodeId() {
		return episodeId;
	}
	public void setEpisodeId(Long episodeId) {
		this.episodeId = episodeId;
	}
	public Date getLastModifidDate() {
		return lastModifidDate;
	}
	public void setLastModifidDate(Date lastModifidDate) {
		this.lastModifidDate = lastModifidDate;
	}
	public Long getDurationError() {
		return durationError;
	}
	public void setDurationError(Long durationError) {
		this.durationError = durationError;
	}
	public String getProgrammeNumber() {
		return programmeNumber;
	}
	public void setProgrammeNumber(String programmeNumber) {
		this.programmeNumber = programmeNumber;
	}
	
    
}
