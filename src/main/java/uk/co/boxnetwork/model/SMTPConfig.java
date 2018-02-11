package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;

@Entity(name="smtp_config")
public class SMTPConfig {

	@Id
	
    private String id;
	
	@Column(name="smtp_host")
	private String smtpHost;
	
	@Column(name="smtp_port")
	private String smtpPort;
		
	
	@Column(name="smtp_username")
	private String username;
	
	@Column(name="smtp_password")
	private String password;
	
	@Column(name="from_email_address")
	private String fromEmailAddress="";
	
	@Column(name="from_name")
	private String fromName;
	
	@Column(name="account_admins")
	private String accountAdmins;
	
	

	@Column(name="modified_at")
	private Date modifiedAt;

	
	


	public String getSmtpHost() {
		return smtpHost;
	}



	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}



	public String getSmtpPort() {
		return smtpPort;
	}



	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getFromEmailAddress() {
		return fromEmailAddress;
	}



	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}



	public String getFromName() {
		return fromName;
	}



	public void setFromName(String fromName) {
		this.fromName = fromName;
	}



	
	public Date getModifiedAt() {
		return modifiedAt;
	}



	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getId() {
		return id;
	}



	public String getAccountAdmins() {
		return accountAdmins;
	}



	public void setAccountAdmins(String accountAdmins) {
		this.accountAdmins = accountAdmins;
	}



	
	
	
	

}
