package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.mule.model.ClientRequestInfo;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.security.BoxUserService;

public class LoginUserTransformer extends BoxRestTransformer{

	@Autowired
	BoxUserService boxUserService;
	
		
	
    @Override	
	 protected Object processPOST(MuleMessage message, String outputEncoding){
    	
    	try{
    			ClientRequestInfo userinfo=new ClientRequestInfo(message);
		    	com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				String requestInJson = (String)message.getPayloadAsString();							
				BoxUser boxuser = objectMapper.readValue(requestInJson, BoxUser.class);				
				return boxUserService.createClientIdAndSecret(boxuser);
    	}
    	catch(Exception e){
	    	logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
			throw new RuntimeException(e+" whule processing the payload",e);
    	}
					 
	}	

	
}
