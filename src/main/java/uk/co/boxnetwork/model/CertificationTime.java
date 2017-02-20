package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="certification_time")
public class CertificationTime {
	@Id
	private String id;
	
	@Column(name="check_end_time")
	private String checkEndTime;
	
	@Column(name="check_start_time")
	private String checkStartTime;
	
	@Column(name="distribution_id")
	private String distributionId;
	
	@Column(name="end_time")
	private String endTime;
	
	@Column(name="schedule_warning_flag")
	private String scheduleWarningFlag;
	
	@Column(name="start_time")
	private String startTime;
	
	@Column(name="warning")
	private String warning;
	
	public void update(CertificationTime certificationTime){
		
		this.checkEndTime=certificationTime.getCheckEndTime();
		
		this.checkStartTime=certificationTime.getCheckStartTime();
		
		this.distributionId=certificationTime.getDistributionId();
		
		this.endTime=certificationTime.getEndTime();
		
		this.scheduleWarningFlag=certificationTime.getScheduleWarningFlag();
		
		this.startTime=certificationTime.getStartTime();
		
		this.warning=certificationTime.getWarning();
	}
	public String getCheckEndTime() {
		return checkEndTime;
	}
	public void setCheckEndTime(String checkEndTime) {
		this.checkEndTime = checkEndTime;
	}
	public String getCheckStartTime() {
		return checkStartTime;
	}
	public void setCheckStartTime(String checkStartTime) {
		this.checkStartTime = checkStartTime;
	}
	public String getDistributionId() {
		return distributionId;
	}
	public void setDistributionId(String distributionId) {
		this.distributionId = distributionId;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScheduleWarningFlag() {
		return scheduleWarningFlag;
	}
	public void setScheduleWarningFlag(String scheduleWarningFlag) {
		this.scheduleWarningFlag = scheduleWarningFlag;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getWarning() {
		return warning;
	}
	public void setWarning(String warning) {
		this.warning = warning;
	}
	@Override
	public String toString() {
		return "CertificationTime [checkEndTime=" + checkEndTime + ", checkStartTime=" + checkStartTime
				+ ", distributionId=" + distributionId + ", endTime=" + endTime + ", id=" + id
				+ ", scheduleWarningFlag=" + scheduleWarningFlag + ", startTime=" + startTime + ", warning=" + warning
				+ "]";
	}	
	public void adjustBeforeSave(Episode episode){
		episode.setCertType(CertType.POST_WATERSHED);
		episode.setWarningText(warning);
	}	
}
