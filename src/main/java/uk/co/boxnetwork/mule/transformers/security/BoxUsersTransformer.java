package uk.co.boxnetwork.mule.transformers.security;



import org.mule.api.MuleMessage;


import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.ErrorMessage;

import uk.co.boxnetwork.model.BoxUser;

import uk.co.boxnetwork.mule.model.BoxOperator;

import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxUsersTransformer extends BoxRestTransformer{	
	
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
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){
		if(operator.checkPOSTAccess()){
			return boxUserService.listUsers();
		}
		else{
				return deniedAccessMessage(message,outputEncoding);				
		}					
	}
	
	
	@Override
	protected Object processDELETE(MuleMessage message, BoxOperator operator, String outputEncoding){
		if(!operator.checkAdminAccess()){
			return deniedAccessMessage(message,outputEncoding);
		}
	    String username=MuleRestUtil.getPathPath(message);
	    logger.info("****** user name to delete:"+username);
 	   if(username==null||username.length()==0){
 		   return new ErrorMessage("The username is missing in DELETE");
 	   }
 	   BoxUser user=boxUserService.getUserByUserName(username);
 	   if(user==null){
 		  return new ErrorMessage("no such username with username");
 	   } 	   	  	 	  
	  if(operator.getUsername().equals(username)){
		  logger.error("User attempted to delete his/her account!!!!!:username=["+username+"]");
		  return new ErrorMessage("You cannot delete your own account!");
	  }	  
	   boxUserService.deleteUser(username);  
 	   return user;
	}
	@Override
	protected Object processPUT(MuleMessage message, BoxOperator operator, String outputEncoding) throws Exception{
		   String username=MuleRestUtil.getPathPath(message);
	 	   if(username==null||username.length()==0){
	 		   return new ErrorMessage("The username is missing in PUT");
	 	   }	 	   
	 	   BoxUser user=boxUserService.getUserByUserName(username);
	 	   if(user==null){
	 		 if(!operator.checkAdminAccess()){
	 			return deniedAccessMessage(message,outputEncoding);
	 		 }
	 		 else{
	 			return new ErrorMessage("no such username with username");
	 		 }	 		 
	 	  }
	 	  
	 	  com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		  objectMapper.setSerializationInclusion(Include.NON_NULL);
		  String requestInJson = (String)message.getPayloadAsString();							
		  BoxUser usedata = objectMapper.readValue(requestInJson, BoxUser.class);
		  if(!usedata.getUsername().equals(username)){
			  return new ErrorMessage("username does not match exactly in the db");
		  }
		  if(usedata.getRoles()!=null){
			  if(operator.checkAdminAccess()){				  
	 	 		  user.setRoles(usedata.getRoles());
			 	  boxUserService.updateUser(user);
			  }
			  else{
				  return deniedAccessMessage(message,outputEncoding);
			  }
		  }
		  else if(usedata.getPassword()!=null){
			  if(username.equals(operator.getUsername()) || operator.checkAdminAccess()){
				  boxUserService.setPassword(user, usedata.getPassword());
			 	  boxUserService.updateUser(user);
			  }
		  }	 	   	  
	 	  usedata.setPassword("******");
	 	  return usedata;	 	   
	}
		
	
    @Override	
	 protected Object processPOST(MuleMessage message, BoxOperator operator, String outputEncoding){
    	
    	try{
		    	com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				String requestInJson = (String)message.getPayloadAsString();							
				BoxUser boxuser = objectMapper.readValue(requestInJson, BoxUser.class);
				boxUserService.setPassword(boxuser, boxuser.getPassword());
				boxUserService.createNewUser(boxuser);
				boxuser.setRoles("user");
				boxuser.setPassword("*******");
				return boxuser;
    	}
    	catch(Exception e){
	    	logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
			throw new RuntimeException(e+" whule processing the payload",e);
    	}
					 
	}	

	
}
