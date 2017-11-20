package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import uk.co.boxnetwork.data.s3.VideoFileItem;

@Entity(name="s3_video_file_item")
public class S3VideoFileItem {
	@Id
	@GeneratedValue
    private Long id;
	
	
	private  String file;    
	
	@Column(name="episode_title")
    private  String episodeTitle;
	
	@Column(name="programme_number")
    private  String programmeNumber;
	
	@Column(name="episode_id")
    private  Long episodeId;
	
	@Column(name="last_modified_date")
    private  Date lastModifiedDate;
	
	@Column(name="duration_error")
    private  Long durationError;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getProgrammeNumber() {
		return programmeNumber;
	}

	public void setProgrammeNumber(String programmeNumber) {
		this.programmeNumber = programmeNumber;
	}

	public Long getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(Long episodeId) {
		this.episodeId = episodeId;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Long getDurationError() {
		return durationError;
	}

	public void setDurationError(Long durationError) {
		this.durationError = durationError;
	}
	
	public void copyFrom(VideoFileItem file){
		this.file=file.getFile();
	    this.episodeTitle=file.getEpisodeTitle();	    
	    this.programmeNumber=file.getProgrammeNumber();	    
	    this.episodeId=file.getEpisodeId();		
	    this.lastModifiedDate=file.getLastModifidDate();		
	    this.durationError=file.getDurationError();
	}
	public void exportTo(VideoFileItem file){
		file.setFile(this.file);
	    file.setEpisodeTitle(this.episodeTitle);	    
	    file.setProgrammeNumber(this.programmeNumber);	    
	    file.setEpisodeId(this.episodeId);	    
	    file.setLastModifidDate(this.lastModifiedDate);		
	    file.setDurationError(this.durationError);
	}
}
