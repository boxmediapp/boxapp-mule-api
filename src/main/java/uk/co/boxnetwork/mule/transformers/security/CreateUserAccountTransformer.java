package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.STMPEmailService;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;


public class CreateUserAccountTransformer extends BoxRestTransformer{	
	@Autowired
	private STMPEmailService smtpEmailService;
	
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
	   return true;		   
	}
	
	private boolean isValidAccount(BoxUser user){
		if(user.getFirstName().trim().length()==0){
			return false;			
		}
		if(user.getLastName().trim().length()==0){
			return false;			
		}
		if(user.getEmail().trim().length()==0){
			return false;			
		}
		if(user.getPassword().trim().length()<5){
			return false;			
		}
		if(user.getCompany().trim().length()==0){
			return false;			
		}
		return true;
	}
    @Override	
	 protected Object processPOST(MuleMessage message, BoxOperator operator, String outputEncoding){    	
    	try{
		    	com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				String requestInJson = (String)message.getPayloadAsString();							
				BoxUser boxuser = objectMapper.readValue(requestInJson, BoxUser.class);
				if(!isValidAccount(boxuser)){
						return returnError("Missing required fields",message,400);
				}
				if(boxuser.getUsername()==null|| boxuser.getUsername().trim().length()==0){
					boxuser.setUsername(boxuser.getEmail());
				}
				BoxUser existingUser=boxUserService.getUserByUserName(boxuser.getUsername());
				if(existingUser!=null){
					return returnError("User name with "+boxuser.getUsername()+" already exists",message,409);
				}
				boxuser.setRoles("user");
				boxuser.setRoles("user");
				boxUserService.setPassword(boxuser, boxuser.getPassword());
				
				boxUserService.createNewUser(boxuser);
				boxuser.setRoles("user");
				boxuser.setPassword("*******");
				smtpEmailService.sendApprovalNotification(boxuser);
				return boxuser;
    	}
    	catch(Exception e){
	    	logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
			throw new RuntimeException(e+" whule processing the payload",e);
    	}
					 
	}	

	
}
