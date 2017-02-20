package uk.co.boxnetwork.data.bc;

public class BCErrorMessage {
	
   private String error_code;
   private String message;
   
public String getError_code() {
	return error_code;
}
public void setError_code(String error_code) {
	this.error_code = error_code;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
   
public String toString(){
	return "error_code=["+error_code+"message=["+message+"]";
}
}
