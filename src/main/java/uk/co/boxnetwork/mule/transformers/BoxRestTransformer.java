package uk.co.boxnetwork.mule.transformers;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;

import org.mule.api.transformer.TransformerException;
import org.mule.module.http.internal.ParameterMap;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;

import uk.co.boxnetwork.data.app.LoginInfo;
import uk.co.boxnetwork.data.bc.BCErrorMessage;
import uk.co.boxnetwork.data.generic.RestResponseMessage;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.BoxUserRole;
import uk.co.boxnetwork.model.MediaApplicationID;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.model.IdentityType;
import uk.co.boxnetwork.security.BoxUserService;

public class BoxRestTransformer  extends AbstractMessageTransformer{
	 static final protected Logger logger=LoggerFactory.getLogger(BoxRestTransformer.class);
	 
	 public static  Long getLongQueryParameter(MuleMessage message,String parametername){		
		 	ParameterMap queryparams=message.getInboundProperty("http.query.params");
		 	String pvalue=null;
			if(queryparams!=null){				
				pvalue=queryparams.get(parametername);
				try{
					return Long.valueOf(pvalue);
				}
				catch(Exception error){
					logger.error("failed convert "+parametername+" input parameter to long parameter:"+pvalue+":"+error);
					return null;
				}
			}
			else{
				return null;
			}
	 }

			

	 @Autowired
	 protected BoxUserService boxUserService;
	 
			
	 public MediaApplicationID getApplicationId(BoxOperator operator){		 
		 List<BoxUserRole> roles=operator.getRoles();
		 return roles.get(0).getApplicationId();
		 
	 }
	protected String convertObjectToJson(Object obj) throws JsonProcessingException{
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();		
		objectMapper.setSerializationInclusion(Include.NON_NULL);					
		return objectMapper.writeValueAsString(obj);			
	}
	
	public String returnError(String desc,MuleMessage message){
		return returnError(desc,message,500);
	}
	public String returnError(String desc,MuleMessage message, int code){
		
		message.setOutboundProperty("http.status", code);		
		return "{\"error\":\""+desc+"\"}";		
	}
	
	public  BoxOperator getOperator(MuleMessage message){		   
			BoxOperator operator=new BoxOperator(message);
			if(operator.getUsername()!=null){
				BoxUser user=null;
				LoginInfo loginInfo=boxUserService.findLoginInfoByClientId(operator.getUsername());				
				if(loginInfo!=null){ //User Logged In User.
					operator.setLoginInfo(loginInfo);					
					user=boxUserService.getUserByUserName(loginInfo.getUsername());
					if(user!=null){
						operator.setIdentityType(IdentityType.TEMPCLIENTID);
					}		
					else{
							throw new RuntimeException("logged in user cannot be found from the database:"+operator.getUsername());
					}
					
				}
				else{
						user=boxUserService.getUserByClientId(operator.getUsername());
						if(user==null){
							user=boxUserService.getUserByUserName(operator.getUsername());
							if(user!=null){
								operator.setIdentityType(IdentityType.USERNAME); 
							}
							else{
								throw new RuntimeException("username cannot be found from the database:"+operator.getUsername());
							}
						}
						else{
							operator.setIdentityType(IdentityType.CLIENTID);
						}
				}					
				List<BoxUserRole> roles=boxUserService.findBoxUserRole(user);
				operator.setRoles(roles);
				operator.setUser(user);
			}			
			return operator;
	}	
	protected boolean checkGETAccess(BoxOperator operator){
		    return operator.checkGETAccess();		    
	}
	 protected boolean checkPOSTAccess(BoxOperator operator){
		   return operator.checkPOSTAccess();				 
	}	
	protected boolean checkPUTAccess(BoxOperator operator){
		
		   return operator.checkPUTAccess();	  
	}
	protected boolean checkDELETEAccess(BoxOperator operator){			
		   return operator.checkDELETEAccess();
		   
	}	
	protected boolean checkPATCHAccess(BoxOperator operator){	   					
		   return operator.checkPATCHAccess();		   
	}

	public  Object deniedAccessMessage(MuleMessage message, String outputEncoding){
		message.setOutboundProperty("http.status", 401);		
		return "{\"error\":\"The access denied for the role\"}";
	}
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
			BoxOperator operator=getOperator(message); 
		    logger.info(operator+":"+this.getClass().getName());
	try{	
				addCORS(message, outputEncoding);
				String inboudMethod=message.getInboundProperty("http.method");
				Object returnObject=null;		
				if(inboudMethod.equals("GET")){
					if(checkGETAccess(operator)){					
							returnObject=processGET(message, operator,outputEncoding);
					}
					else{
						    return deniedAccessMessage(message,outputEncoding);
					}
				}
				else if(inboudMethod.equals("PUT")){
					if(checkPUTAccess(operator)){
							returnObject=processPUT(message, operator,outputEncoding);
					}
					else{
					    return deniedAccessMessage(message,outputEncoding);
					}
				}
				else if(inboudMethod.equals("POST")){
					if(checkPOSTAccess(operator)){
						returnObject=processPOST(message, operator,outputEncoding);
					}
					else{
						 return deniedAccessMessage(message,outputEncoding);
					}
					
				}
		
				else if(inboudMethod.equals("DELETE")){
					if(checkPOSTAccess(operator)){
						returnObject=processDELETE(message, operator,outputEncoding);
					}
					else{
						return deniedAccessMessage(message,outputEncoding);
					}
					
				}
				else if(inboudMethod.equals("OPTIONS")){
					returnObject=processOPTIONS(message, operator,outputEncoding);
				}
				else if(inboudMethod.equals("PATCH")){
					if(checkPATCHAccess(operator)){
						returnObject=processPATCH(message, operator,outputEncoding);
					}
					else{
						return deniedAccessMessage(message,outputEncoding);
					}
					
				}
				else 
					returnObject=processDefault(message,operator,outputEncoding,inboudMethod);	
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
				return exceptionToErrorResponse(e,message);		
		 }
	}
	private Object exceptionToErrorResponse(Exception error, MuleMessage message){
		logger.error(error+" in transformer",error);
		try{
			com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			BCErrorMessage[] erromessages = objectMapper.readValue(error.getMessage(), BCErrorMessage[].class);
			return returnError(error.getMessage(), message, 500);
		}
		catch(Exception e1){
			try{
				RestResponseMessage responseMEssage=RestResponseMessage.exceptionToRestResponseMessage(e1);
				return returnError(responseMEssage.getMessage(), message,responseMEssage.getCode());
			}
			catch(Exception e2){
				return returnError(error.getMessage(), message, 500);
			}
		}
	}
	
	 protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){
		 return returnError("The Method GET not supported",message);
	}
	 protected Object processPOST(MuleMessage message, BoxOperator operator,String outputEncoding){
		 return returnError("The Method POST not supported",message);					 
	}	
	protected Object processPUT(MuleMessage message, BoxOperator operator,String outputEncoding) throws Exception{
	   		
	   return returnError("The Method PUT not supported",message);
	}
	protected Object processDELETE(MuleMessage message, BoxOperator operator,String outputEncoding){			
		return returnError("The Method DELETE not supported",message);
	}
	protected Object processOPTIONS(MuleMessage message, BoxOperator operator,String outputEncoding){			
	   return message.getPayload();
	   
	}
	protected Object processDefault(MuleMessage message, BoxOperator operator,String outputEncoding, String method){	   					
		return returnError("The Method "+method+" not supported",message);
	}
	protected Object processPATCH(MuleMessage message, BoxOperator operator,String outputEncoding) throws Exception{	   					
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
