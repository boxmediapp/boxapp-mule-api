package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.model.UserAccountData;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.security.BoxUserService;

public class UserAccountTransformer  extends BoxRestTransformer{	
	@Autowired
	 protected BoxUserService boxUserService;
	 
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
	 protected Object processPOST(MuleMessage message, BoxOperator operator, String outputEncoding){
   	
			   	try{
					    	com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
							objectMapper.setSerializationInclusion(Include.NON_NULL);
							String requestInJson = (String)message.getPayloadAsString();							
							UserAccountData userAccount = objectMapper.readValue(requestInJson, UserAccountData.class);
							if(userAccount.getUserStatus()!=null){
									BoxUser user=operator.getUser();
									user.setUserStatus(userAccount.getUserStatus());
									boxUserService.updateUser(user);
									if(operator.getLoginInfo()!=null){
										operator.getLoginInfo().setUserStatus(userAccount.getUserStatus());
									}
							}							
							return userAccount;
			   	}
			   	catch(Exception e){
				    	logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
						throw new RuntimeException(e+" whule processing the payload",e);
			   	}
					 
	}
	
	
}
