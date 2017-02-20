package uk.co.boxnetwork.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="programme_certification")
public class ProgrammeCertification {
	@Id
	private String id;
	
	@Column(name="certification_date")
	private String certificationDate;
	
	 @Column(name="certification_end_date")
	 private String certificationEndDate;
	
	 @ManyToOne(optional=true, fetch=FetchType.EAGER)
	 @JoinColumn( name = "certification_type_id", nullable = true )
	 private CertificationType certificationType;
	
	 @Column(name="compliance_observation_id")
	 private String complianceObservationId;
	 
	 @Column(name="created_by")
	 private String createdBy;
	 
	 @Column(name="created_on")
	 private String createdOn;
	 
	 @Column(name="modified_by")
	 private String modifiedBy;
	 
	 @Column(name="modified_on")
	 private String modifiedOn;
	 
	 public void update(ProgrammeCertification programmeCertification){
		 
		 this.certificationDate=programmeCertification.getCertificationDate();
			
		 this.certificationEndDate=programmeCertification.getCertificationEndDate();
			
		 this.certificationType=programmeCertification.getCertificationType();
			
		 this.complianceObservationId=programmeCertification.getComplianceObservationId();
			 
		 this.createdBy=programmeCertification.getCreatedBy();
			 
		 this.createdOn=programmeCertification.getCreatedOn();
			 
		 this.modifiedBy=programmeCertification.getModifiedBy();
			 
		 this.modifiedOn=programmeCertification.getModifiedOn();
	 }
	public String getCertificationDate() {
		return certificationDate;
	}
	public void setCertificationDate(String certificationDate) {
		this.certificationDate = certificationDate;
	}
	public String getCertificationEndDate() {
		return certificationEndDate;
	}
	public void setCertificationEndDate(String certificationEndDate) {
		this.certificationEndDate = certificationEndDate;
	}
	public CertificationType getCertificationType() {
		return certificationType;
	}
	public void setCertificationType(CertificationType certificationType) {
		this.certificationType = certificationType;
	}
	public String getComplianceObservationId() {
		return complianceObservationId;
	}
	public void setComplianceObservationId(String complianceObservationId) {
		this.complianceObservationId = complianceObservationId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	@Override
	public String toString() {
		return "ProgrammeCertification [certificationDate=" + certificationDate + ", certificationEndDate="
				+ certificationEndDate + ", certificationType=" + certificationType + ", complianceObservationId="
				+ complianceObservationId + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", id=" + id
				+ ", modifiedBy=" + modifiedBy + ", modifiedOn=" + modifiedOn + "]";
	}
	public void adjustBeforeSave(Episode episode){
		if(certificationType!=null){
			certificationType.adjustBeforeSave(episode);	
		}
	}
     
}
