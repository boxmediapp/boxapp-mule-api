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




@Entity(name="certification_type")
public class CertificationType {
	private static final Logger logger=LoggerFactory.getLogger(CertificationType.class);
	
	@Id
	private String id;
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "certification_category_id", nullable = true )
	private CertificationCategory certificationCategory;
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "certification_time_id", nullable = true )
	private CertificationTime certificationTime;
	
	@Column(name="cli_key")
	private String cliKey;
	
	@Column(name="code")
    private String code;
	
	@Column(name="description")
    private String description;
    public void update(CertificationType obj){    	
    	
    	this.certificationCategory=obj.getCertificationCategory();
    	
    	this.certificationTime=obj.getCertificationTime();
    	
    	this.cliKey=obj.getCliKey();
    	
    	this.code=obj.getCode();
    	
    	this.description=obj.getDescription();
    }
	public void addCertificationTime(CertificationTime certtime){
		if(this.certificationTime!=null && certtime!=null){
				logger.warn("More than one ProgrammeCertification is set:"+toString());
		}
		this.certificationTime=certtime;
	}
    public CertificationCategory getCertificationCategory() {
		return certificationCategory;
	}
	
	public void setCertificationCategory(CertificationCategory certificationCategory) {
		this.certificationCategory = certificationCategory;
	}
	public CertificationTime getCertificationTime() {
		return certificationTime;
	}
	public void setCertificationTime(CertificationTime certificationTime) {
		this.certificationTime = certificationTime;
	}
	public String getCliKey() {
		return cliKey;
	}
	public void setCliKey(String cliKey) {
		this.cliKey = cliKey;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "CertificationType [certificationCategory=" + certificationCategory + ", certificationTimes="
				+ certificationTime + ", cliKey=" + cliKey + ", code=" + code + ", description=" + description
				+ ", id=" + id + "]";
	}   
	public void adjustBeforeSave(Episode episode){
		if(code!=null && code.equals("FA")){
			episode.setCertType(CertType.ALL_TIMES);
		}
		if(certificationCategory!=null){
			certificationCategory.adjustBeforeSave(episode);
		}
		if(certificationTime!=null){
			certificationTime.adjustBeforeSave(episode);			
		}
			
	}
}
