package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.app.LoginInfo;

import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class LogoutTransformer  extends BoxRestTransformer{	
	
	protected boolean checkGETAccess(BoxOperator operator){
	    return true;		    
    }
	protected boolean checkPOSTAccess(BoxOperator operator){
	   return true;				 
	}	
	protected boolean checkPUTAccess(BoxOperator operator){	
	   return true;	  
	}
	protected boolean checkDELETEAccess(BoxOperator operator){			
	   return true;	   
	}	
	protected boolean checkPATCHAccess(BoxOperator operator){	   					
	   return operator.checkPATCHAccess();		   
	}
	@Override
	protected Object processPOST(MuleMessage message, BoxOperator operator,String outputEncoding){
		try{    			
	    	com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			String requestInJson = (String)message.getPayloadAsString();							
			LoginInfo loginInfo = objectMapper.readValue(requestInJson, LoginInfo.class);
			  if(operator.getLoginInfo()!=null){
						boxUserService.removeLoginInfoByClientId(operator.getLoginInfo().getClientId());
			  }			  
			  return loginInfo;
			
			}
		catch(Exception e){
			logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
			throw new RuntimeException(e+" whule processing the payload",e);
		}
	}
	
}
