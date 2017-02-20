package uk.co.boxnetwork.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity(name="compliance_information")
public class ComplianceInformation {
	private static final Logger logger=LoggerFactory.getLogger(ComplianceInformation.class);
	
	@Id
	private String id;
	
	@Column(name="issue_number")
	private String issueNumber;

	@Column(name="material_id")
	private String materialId;
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "programme_certification_id", nullable = true )
	private ProgrammeCertification programmeCertification;
    
	@Column(name="programme_version_certification")
	private String programmeVersionCertification;
	
	@Column(name="transmission_date")
    private String transmissionDate;
    
	@Column(name="asset_id")
    private String assetId;
	
	@Column(name="viewer_user_key")
    private String viewerUserKey;
	
	@Column(name="certification_finished")
    private String certificationFinished;
	
	@Column(name="edited_on")
    private String editedOn;
	
	public String getIssueNumber() {
		return issueNumber;
	}
	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}
	public String getMaterialId() {
		return materialId;
	}
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	public ProgrammeCertification getProgrammeCertification() {
		return programmeCertification;
	}
	public void setProgrammeCertification(ProgrammeCertification programmeCertification) {
		this.programmeCertification = programmeCertification;
	}
	public String getProgrammeVersionCertification() {
		return programmeVersionCertification;
	}
	public void setProgrammeVersionCertification(String programmeVersionCertification) {
		this.programmeVersionCertification = programmeVersionCertification;
	}
	public String getTransmissionDate() {
		return transmissionDate;
	}
	public void setTransmissionDate(String transmissionDate) {
		this.transmissionDate = transmissionDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getViewerUserKey() {
		return viewerUserKey;
	}
	public void setViewerUserKey(String viewerUserKey) {
		this.viewerUserKey = viewerUserKey;
	}
	public String getCertificationFinished() {
		return certificationFinished;
	}
	public void setCertificationFinished(String certificationFinished) {
		this.certificationFinished = certificationFinished;
	}
	public String getEditedOn() {
		return editedOn;
	}
	public void setEditedOn(String editedOn) {
		this.editedOn = editedOn;
	}
	public void addProgrammeCertification(ProgrammeCertification certification){
		if(this.programmeCertification!=null && certification!=null){
			logger.warn("More than one ProgrammeCertification is set:"+toString());			
		}
		this.programmeCertification=certification;
	}
	
    public void update(ComplianceInformation obj){    	
    	this.issueNumber=obj.getIssueNumber();
    	this.materialId=obj.getMaterialId();
    	this.programmeCertification=obj.getProgrammeCertification();
    	this.transmissionDate=obj.getTransmissionDate();    	
    	this.programmeVersionCertification=obj.getProgrammeVersionCertification();
    	this.transmissionDate=obj.getTransmissionDate();
    	this.assetId=obj.getAssetId();
    	this.viewerUserKey=obj.getViewerUserKey();    	
        this.certificationFinished=obj.getCertificationFinished();
        this.editedOn=obj.getEditedOn();
    }
    public void adjustBeforeSave(Episode episode){    	
    	if(programmeCertification!=null){
    		programmeCertification.adjustBeforeSave(episode);    		
    	}
    	
    }
    
}
