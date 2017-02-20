package uk.co.boxnetwork.data;

import org.apache.commons.codec.binary.Base64;

public class BasicAuthenticatedURLConfiguration {
 
private String url;
 private String user;
 private String password;
 
 public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getUser() {
	return user;
}
public void setUser(String user) {
	this.user = user;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String basicAuthentication(){
	String plainCreds = user+":"+password;
	byte[] plainCredsBytes = plainCreds.getBytes();
	byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	String base64Creds = new String(base64CredsBytes);
	return base64Creds;
	
}
@Override
public String toString() {
	return "BasicAuthenticatedURLConfiguration [url=" + url + ", user=" + user + ", password=" + password + "]";
}
 
}
