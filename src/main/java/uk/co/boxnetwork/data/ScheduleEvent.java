package uk.co.boxnetwork.data;

import java.util.Date;


public class ScheduleEvent {
    private Long id;

	

	private String duration;
	

	private Date scheduleDay;
	

	private Date scheduleTimestamp;
			

	private Date lastModifiedAt;

	private Date createdAt;

	private String episdeoTitle;
	private String programmeNumber;
	private Long episodeId;
	
	
	public ScheduleEvent(){
		
	}
	public ScheduleEvent(uk.co.boxnetwork.model.ScheduleEvent evt){
		this.id=evt.getId();
		this.duration=evt.getDuration();
		this.scheduleDay=evt.getScheduleDay();
		this.scheduleTimestamp=evt.getScheduleTimestamp();
		this.lastModifiedAt=evt.getLastModifiedAt();
		this.createdAt=evt.getCreatedAt();
		if(evt.getEpisode()!=null){
			this.episdeoTitle=evt.getEpisode().getTitle();
			this.programmeNumber=evt.getEpisode().getCtrPrg();
			this.episodeId=evt.getEpisode().getId();
		}
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Date getScheduleDay() {
		return scheduleDay;
	}

	public void setScheduleDay(Date scheduleDay) {
		this.scheduleDay = scheduleDay;
	}

	public Date getScheduleTimestamp() {
		return scheduleTimestamp;
	}

	public void setScheduleTimestamp(Date scheduleTimestamp) {
		this.scheduleTimestamp = scheduleTimestamp;
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
	public String getEpisdeoTitle() {
		return episdeoTitle;
	}
	public void setEpisdeoTitle(String episdeoTitle) {
		this.episdeoTitle = episdeoTitle;
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
	
}
