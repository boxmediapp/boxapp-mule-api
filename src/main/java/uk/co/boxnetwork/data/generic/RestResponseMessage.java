package uk.co.boxnetwork.data.generic;

public class RestResponseMessage {
  private String code;
  private String message;
  public RestResponseMessage(){
	  
  }
public RestResponseMessage(String code, String message) {
	super();
	this.code = code;
	this.message = message;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}  
  
}
