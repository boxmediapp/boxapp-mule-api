package uk.co.boxnetwork.mule.transformers;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;

import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.bc.BCErrorMessage;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxRestTransformer  extends AbstractMessageTransformer{
	 static final protected Logger logger=LoggerFactory.getLogger(BoxRestTransformer.class);
	
	protected String convertObjectToJson(Object obj) throws JsonProcessingException{
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();		
		objectMapper.setSerializationInclusion(Include.NON_NULL);					
		return objectMapper.writeValueAsString(obj);			
	}
	public String returnError(String desc,MuleMessage message){
		
		message.setOutboundProperty("http.status", 500);		
		return "{error:\""+desc+"\"}";		
	}
	
	public String getClientInfo(MuleMessage message){
		try{			
			  			  
				String clientIPAdress=message.getProperty("http.remote.address", PropertyScope.INBOUND);
				String realip=message.getProperty("X-Real-IP", PropertyScope.INBOUND);
				String authorization=message.getProperty("authorization", PropertyScope.INBOUND);
				String username=null;				
				if(authorization!=null){
					try{
					 String base64Credentials = authorization.substring("Basic".length()).trim();
				        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
				                Charset.forName("UTF-8"));
				        // credentials = username:password
				        final String[] values = credentials.split(":",2);
				        username=values[0];
					}
					catch(Exception e){
						logger.error(e+ "while decoding",e);
					}
				        
				}
				
				String referer=message.getProperty("referer", PropertyScope.INBOUND);
				
				String requesturi=message.getProperty("http.request.uri", PropertyScope.INBOUND);
				
				String httpmethod=message.getProperty("http.method", PropertyScope.INBOUND);
				return "Received the "+httpmethod+" request on "+requesturi+" from "+clientIPAdress+":"+realip+":"+username+":referer:"+referer;								
		}
		catch(Exception e){
			logger.error(e+" whule logging client ip",e);
			return "";
		}
		
	}
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		    logger.info(getClientInfo(message)+":"+this.getClass().getName());
	try{	
				addCORS(message, outputEncoding);
				String inboudMethod=message.getInboundProperty("http.method");
				Object returnObject=null;		
				if(inboudMethod.equals("GET")){
					returnObject=processGET(message, outputEncoding);
				}
				else if(inboudMethod.equals("PUT")){			
					returnObject=processPUT(message, outputEncoding);			
				}
				else if(inboudMethod.equals("POST")){
					returnObject=processPOST(message, outputEncoding);
				}
		
				else if(inboudMethod.equals("DELETE")){
					returnObject=processDELETE(message, outputEncoding);
				}
				else if(inboudMethod.equals("OPTIONS")){
					returnObject=processOPTIONS(message, outputEncoding);
				}
				else if(inboudMethod.equals("PATCH")){
					returnObject=processPATCH(message, outputEncoding);
				}
				else 
					returnObject=processDefault(message,outputEncoding,inboudMethod);	
				if(returnObject ==null){
					return "{\"message\":\"ok, empty response\"}";					
				}
				else if(returnObject instanceof org.mule.transport.NullPayload){
					logger.info("Service returned NullPauyload");
					return returnObject;
				}
				else if(returnObject instanceof String || returnObject instanceof InputStream){
					return returnObject;
				}
				else {
					try {
						return convertObjectToJson(returnObject);
					} catch (JsonProcessingException e) {
						logger.error(e+":Unbale to convert the object to json: returnObject=p["+returnObject+"] class="+returnObject.getClass().getName() ,e);
						return returnError("Unable to convert to json",message);
					}
				}
	     }
		catch(Exception e) {									
						logger.error(e+" in transformer",e);
						try{
							com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
							BCErrorMessage[] erromessages = objectMapper.readValue(e.getMessage(), BCErrorMessage[].class);
							message.setOutboundProperty("http.status", 500);
							return e.getMessage();
						}
						catch(Exception e1){
							
							return returnError(e.toString(),message);
						}
						
		 }
		
		
	}
	
	 protected Object processGET(MuleMessage message, String outputEncoding){
		 return returnError("The Method GET not supported",message);
	}
	 protected Object processPOST(MuleMessage message, String outputEncoding){
		 return returnError("The Method POST not supported",message);					 
	}	
	protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
	   		
	   return returnError("The Method PUT not supported",message);
	}
	protected Object processDELETE(MuleMessage message, String outputEncoding){			
		return returnError("The Method DELETE not supported",message);
	}
	protected Object processOPTIONS(MuleMessage message, String outputEncoding){			
	   return message.getPayload();
	   
	}
	protected Object processDefault(MuleMessage message, String outputEncoding, String method){	   					
		return returnError("The Method "+method+" not supported",message);
	}
	protected Object processPATCH(MuleMessage message, String outputEncoding) throws Exception{	   					
		return returnError("The patch is not supported",message);
	}
	
   
   public void addCORS(MuleMessage message, String outputEncoding){
		String origin=null;		 
		Map<String,String> headers=message.getInboundProperty("http.headers");
		if(headers!=null){
			origin= headers.get("Origin");
		}
		else{
			origin= "*";
		}
	    message.setOutboundProperty("Access-Control-Allow-Origin", origin);
	    message.setOutboundProperty("Access-Control-Max-Age", "600");
	    message.setOutboundProperty("Access-Control-Allow-Methods","GET,POST, DELETE,PUT, PATCH, OPTIONS");
	    message.setOutboundProperty("Access-Control-Allow-Headers","X-Requested-With, Content-Type, Accept,apikey,accept, authorization");
   }
}
