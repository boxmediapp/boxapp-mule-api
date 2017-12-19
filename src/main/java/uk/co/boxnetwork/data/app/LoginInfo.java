package uk.co.boxnetwork.data.app;

import uk.co.boxnetwork.model.BoxUser;

public class LoginInfo {
	private String username;
    private String roles;       
    private String clientId;       
    private String clientSecret;
    private Long expiresAt;
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
	public LoginInfo(){
		super();
	}
	public LoginInfo(BoxUser user){
		this.username=user.getUsername();
		this.clientId=user.getClientId();
		this.clientSecret=user.getClientSecret();
		this.roles=user.getRoles();
		this.expiresAt=user.getSecretExpiresAt();		
	}
}
