package uk.co.boxnetwork.data.app;

import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.BoxUserRole;

public class LoginInfo {
	private String username;
    private String roles;       
    private String clientId;       
    private String clientSecret;
    private Long expiresAt;
    private Long durationInSeconds;
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
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
	
	public Long getDurationInSeconds() {
		return durationInSeconds;
	}
	public void setDurationInSeconds(Long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}
	public LoginInfo(){
		super();
	}
	public LoginInfo(BoxUser user, BoxUserRole role){
		this.username=user.getUsername();
		this.clientId=user.getClientId();
		this.clientSecret=user.getClientSecret();
		this.roles=user.getRoles();
		this.expiresAt=user.getSecretExpiresAt();	
		this.durationInSeconds=role.getSecretDuration();
	}
}
