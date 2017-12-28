package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import uk.co.boxnetwork.util.GenericUtilities;

@Entity(name="user")
public class BoxUser {
	@Id 	
    private String username;
  
    private String password;
  
    private String roles;
    
    
    
    @Column(name="client_id")
    private String clientId;
    
    @Column(name="client_secret")
    private String clientSecret;
    
   
    private String firstName;
    
    private String lastName;   
    
    private String email;
    
    private String company;
    
    private String userStatus;
    
    
           
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
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
	
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String toString(){
		return "username=["+username+"clientId=["+clientId+"]roles=["+roles+"]firstName=["+firstName+"]lastName["+lastName+"]company=["+company+"]userStatus=["+userStatus+"]";
	}
}
