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
	public void encrypt(String encryptionKey){
		this.password=GenericUtilities.encrypt(encryptionKey, password);		
	}
	public void decrypt(String encryptionKey){
		this.password=GenericUtilities.decrypt(encryptionKey, password);		
	} 
}
