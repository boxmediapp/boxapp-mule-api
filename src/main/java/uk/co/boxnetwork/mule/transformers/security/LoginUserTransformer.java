package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;


import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.mule.model.BoxOperator;

import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

import uk.co.boxnetwork.security.BoxUserService;

public class LoginUserTransformer extends BoxRestTransformer{

	@Autowired
	BoxUserService boxUserService;
	
		
	protected boolean checkPOSTAccess(BoxOperator operator){
		   return true;				 
	}
	
    @Override	
	 protected Object processPOST(MuleMessage message, BoxOperator operator, String outputEncoding){
    	
    	try{    			
		    	com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				String requestInJson = (String)message.getPayloadAsString();							
				BoxUser boxuser = objectMapper.readValue(requestInJson, BoxUser.class);				
				return boxUserService.createLoginInfo(operator.getUsername()); 
    	}
    	catch(Exception e){
	    	logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
			throw new RuntimeException(e+" whule processing the payload",e);
    	}
					 
	}	

	
}
