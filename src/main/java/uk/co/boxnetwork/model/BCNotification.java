package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="bc_notification")
public class BCNotification {
	@Id
	@GeneratedValue
    private Long id;

	@Column(name="entity_type")
    private String entityType;
	
	@Column(name="entity")	
	private String entity;
	
	@Column(name="version")
    private String version;
	
	@Column(name="job_id")	
    private String jobId;
	
	@Column(name="account_id")
    private String accountId;
	
	@Column(name="action")
    private String action;
	
	
    private String status;
	
	@Column(name="video_id")
    private String videoId;
	
	@Column(name="profile_ref_id")
	private String profileRefId;
	
    @Column(name="error_message")
    private String errorMessage;
    
    @Column(name="error")
    private String error;
    
    @Column(name="referenceId")
    private String referenceId;
    
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getProfileRefId() {
		return profileRefId;
	}

	public void setProfileRefId(String profileRefId) {
		this.profileRefId = profileRefId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
  
}

