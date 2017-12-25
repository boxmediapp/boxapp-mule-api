package uk.co.boxnetwork.data.app;

import java.util.Date;
import java.util.List;

import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.BoxUserRole;

public class LoginInfo {
	private String username;
    private List<BoxUserRole> roles;       
    private String clientId;       
    private String clientSecret;
    private Long expiresAt;
    private Long durationInSeconds=Long.valueOf(3600);
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public Long getDurationInSeconds() {
		return durationInSeconds;
	}
	public void setDurationInSeconds(Long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
    
	public Long getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	
	public LoginInfo(){
		super();
	}
	public void refreshExpiresAt(){
		Date now=new Date();    	
    	long nowInMilliseconds=now.getTime();    	    	    	
    	expiresAt=nowInMilliseconds+durationInSeconds*1000; 		
	}
	public boolean expired(){
		Date now=new Date();
		if(now.getTime()>this.expiresAt){
			return true;
		}
		else{
			return false;
		}
	}
	
	public List<BoxUserRole> getRoles() {
		return roles;
	}
	public void setRoles(List<BoxUserRole> roles) {
		this.roles = roles;
		if(this.roles!=null && this.roles.size()>0){
			this.durationInSeconds=roles.get(0).getSecretDuration();
		}
	}
	public LoginInfo(BoxUser user){
		this.username=user.getUsername();
		this.clientId=user.getClientId();
		this.clientSecret=user.getClientSecret();							
	}
	
}
