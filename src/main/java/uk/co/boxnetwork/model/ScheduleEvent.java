package uk.co.boxnetwork.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.co.boxnetwork.util.GenericUtilities;


@Entity(name="schedule_event")


public class ScheduleEvent {
	
	@Id
	@GeneratedValue
    private Long id;

	
	@Column(name="group_name")
	private String groupName;
	
	@Column(name="group_id")
	private String groupId;
	
	
	@Column(name="asset_id")
	private String assetId;
	
	@Column(name="asset_name")
	private String assetName;
	
	@Column(name="asset_type")
	private String assetType;
	
	@Column(name="distribution_id")
	private String distributionId;
	
	@Column(name="distribution_type")
	private String distributionType;
	
	@Column(name="duration")
	private String duration;
	
	@Column(name="schedule_event_id")
	private String scheduleEventID;
	
	@Column(name="schedule_event_type")
	private String scheduleEventType;
	
	@Column(name="schedule_day")
	private Date scheduleDay;
	
	@Column(name="schedule_timestamp")
	private Date scheduleTimestamp;
			
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "episode_id", nullable = true )
	private Episode episode;
	
	
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
	
	
	@Column(name="created_at")
	private Date createdAt;
	
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getGroupName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getGroupId() {
		return groupId;
	}


	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	public String getAssetId() {
		return assetId;
	}


	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}


	public String getAssetName() {
		return assetName;
	}


	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}


	public String getAssetType() {
		return assetType;
	}


	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}


	public String getDistributionId() {
		return distributionId;
	}


	public void setDistributionId(String distributionId) {
		this.distributionId = distributionId;
	}


	public String getDistributionType() {
		return distributionType;
	}


	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}


	public String getDuration() {
		return duration;
	}


	public void setDuration(String duration) {
		this.duration = duration;
	}


	public String getScheduleEventID() {
		return scheduleEventID;
	}


	public void setScheduleEventID(String scheduleEventID) {
		this.scheduleEventID = scheduleEventID;
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


	public Episode getEpisode() {
		return episode;
	}


	public void setEpisode(Episode episode) {
		this.episode = episode;
	}


	public String getScheduleEventType() {
		return scheduleEventType;
	}


	public void setScheduleEventType(String scheduleEventType) {
		this.scheduleEventType = scheduleEventType;
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


	public void merge(ScheduleEvent event){
    	if(GenericUtilities.isNotValidTitle(this.groupName)){
			this.groupName=event.getGroupName();
		}
    	if(GenericUtilities.isNotAValidId(this.groupId)){
			this.groupId=event.getGroupId();
		}
    	if(GenericUtilities.isNotValidTitle(this.assetName)){
			this.assetName=event.getAssetName();			
		}
    	if(GenericUtilities.isNotValidTitle(this.assetType)){
			this.assetType=event.getAssetType();			
		}
    	if(GenericUtilities.isNotAValidId(this.distributionId)){
			this.distributionId=event.getDistributionId();			
		}
    	if(GenericUtilities.isNotValidTitle(this.distributionType)){
			this.distributionType=event.getDistributionType();			
		}
    	if(this.duration==null){
			this.duration=event.getDuration();			
		}
    	if(GenericUtilities.isNotAValidId(this.scheduleEventID)){
			this.scheduleEventID=event.getScheduleEventID();			
		}
    	if(GenericUtilities.isNotValidTitle(this.scheduleEventType)){
			this.scheduleEventType=event.getScheduleEventType();			
		}
    	if(this.scheduleDay==null){
    		this.scheduleDay=event.getScheduleDay();
    	}
    	if(this.scheduleTimestamp==null){
    		this.scheduleTimestamp=event.getScheduleTimestamp();
    	}
    	if(this.episode==null){
    		this.episode=event.getEpisode();
    	}    	
    }



	@Override
	public String toString(){
		return "id=[+"+id+"]scheduleDay=["+scheduleDay+"]scheduleTimestamp=["+scheduleTimestamp+"]distributionType=["+distributionType+"]";
	}
	
}
