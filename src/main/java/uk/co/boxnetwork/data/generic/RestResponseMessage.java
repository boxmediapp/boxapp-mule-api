package uk.co.boxnetwork.data.generic;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.co.boxnetwork.util.GenericUtilities;

public class RestResponseMessage {
	private static final Logger logger=LoggerFactory.getLogger(RestResponseMessage.class);
 
	 
	public static RuntimeException toRuntmeException(int code, String message){
	    String jsonString=createJsonString(code,message);
		return new RuntimeException(jsonString);
   }
	public static RuntimeException toRuntmeException(int code, String message, Exception exception){
		    String jsonString=createJsonString(code,message);
			return new RuntimeException(jsonString, exception);
	}
	public static RestResponseMessage exceptionToRestResponseMessage(Exception e) throws JsonParseException, JsonMappingException, IOException{
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		return objectMapper.readValue(e.getMessage(),RestResponseMessage.class);				
	}
	private  static String createJsonString(int code, String message){
	 RestResponseMessage responseObj=new RestResponseMessage(code, message);
	 com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();		
		try {
			return objectMapper.writeValueAsString(responseObj);
		} catch (JsonProcessingException e1) {
			logger.error("error create the json object from RestResponseMessage:"+e1,e1);			
			return 	"{code:\""+code+"\", message:\""+message+"\"}";		
		}
 }
  private int code;
  private String message;
  public RestResponseMessage(){
	  
  }
  
public RestResponseMessage(int code, String message) {
	super();
	this.code = code;
	this.message = message;
}
public int getCode() {
	return code;
}
public void setCode(int code) {
	this.code = code;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}  
  
}
