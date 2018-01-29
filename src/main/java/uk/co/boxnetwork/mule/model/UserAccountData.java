package uk.co.boxnetwork.mule.model;

import javax.persistence.Column;
import javax.persistence.Id;

import uk.co.boxnetwork.model.BoxUser;

public class UserAccountData {
	
	 	
    private String username;
  
    private String roles;
    
    
    
    
    private String clientId;
    
    
    private String clientSecret;
    
   
    private String firstName;
    
    private String lastName;   
    
    private String email;
    
    private String company;
    
    private String userStatus;
    
    private  String password;
    
    
    private UserAccountDataAction action;
    
    

	
	public UserAccountDataAction getAction() {
		return action;
	}

	public void setAction(UserAccountDataAction action) {
		this.action = action;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserAccountData(BoxUser user){
	    this.username=user.getUsername();
	    this.roles=user.getRoles();
	    this.clientId=user.getClientId();
	    this.clientSecret=user.getClientSecret();
	    this.firstName=user.getFirstName();
	    this.lastName=user.getLastName();
	    this.email=user.getEmail();
	    this.company=user.getCompany();
	    this.userStatus=user.getUserStatus();
	}
	public UserAccountData(){
		
	}
	
}
