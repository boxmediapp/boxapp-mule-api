package uk.co.boxnetwork.data;

public class ErrorMessage {
   private String message;
   public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public ErrorMessage(String mess){
	   this.message=mess;	   
   }
}
