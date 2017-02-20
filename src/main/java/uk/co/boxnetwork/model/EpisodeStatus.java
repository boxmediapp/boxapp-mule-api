package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="episode_status")
public class EpisodeStatus {
   
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="metadata_status")
	private MetadataStatus  metadataStatus=MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER;
	
	@Column(name="video_status")
	private VideoStatus videoStatus=VideoStatus.MISSING_VIDEO;

	@Column(name="numberOfTranscodedFiles")
	private Integer numberOfTranscodedFiles=0;

	@Column(name="transcode_job_id")			
	private String transcodeJobId;
	
	@Column(name="published_status")
	private PublishedStatus publishedStatus=PublishedStatus.NOT_APPROVED;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public VideoStatus getVideoStatus() {
		return videoStatus;
	}

	public void setVideoStatus(VideoStatus videoStatus) {
		this.videoStatus = videoStatus;
	}

	public MetadataStatus getMetadataStatus() {
		return metadataStatus;
	}

	public void setMetadataStatus(MetadataStatus metadataStatus) {
		this.metadataStatus = metadataStatus;
	}

	public Integer getNumberOfTranscodedFiles() {
		return numberOfTranscodedFiles;
	}

	public void setNumberOfTranscodedFiles(Integer numberOfTranscodedFiles) {
		this.numberOfTranscodedFiles = numberOfTranscodedFiles;
	}

	public String getTranscodeJobId() {
		return transcodeJobId;
	}

	public void setTranscodeJobId(String transcodeJobId) {
		this.transcodeJobId = transcodeJobId;
	}

	public PublishedStatus getPublishedStatus() {
		return publishedStatus;
	}

	public void setPublishedStatus(PublishedStatus publishedStatus) {
		this.publishedStatus = publishedStatus;
	}
	
	public boolean  update(EpisodeStatus episodeStatus){
		boolean changed=false;
		
		if(this.metadataStatus!=null){
			episodeStatus.setMetadataStatus(this.metadataStatus);
			changed=true;
		}
		if(this.publishedStatus!=null){
			episodeStatus.setPublishedStatus(this.publishedStatus);
			changed=true;
		}
		if(this.videoStatus!=null){
			episodeStatus.setVideoStatus(this.videoStatus);
			changed=true;
		}
		return changed;
	}
	
	public boolean canActivate(){
		return this.publishedStatus==PublishedStatus.ACTIVE || this.publishedStatus==PublishedStatus.INACTIVE || this.publishedStatus==PublishedStatus.APPROVED;		
	}
}
