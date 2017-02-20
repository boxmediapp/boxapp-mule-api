package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.security.BoxUserService;

public class BoxUsersTransformer extends BoxRestTransformer{

	@Autowired
	BoxUserService boxUserService;
	
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){
		
		return boxUserService.listUsers();		
	}
	
	@Override
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
	   String username=MuleRestUtil.getPathPath(message);
	   logger.info("****** user name to delrte:"+username);
 	   if(username==null||username.length()==0){
 		   return new ErrorMessage("The username is missing in DELETE");
 	   }
 	   BoxUser user=boxUserService.getUserByUserName(username);
 	   if(user==null){
 		  return new ErrorMessage("no such username with username");
 	   }
 	  boxUserService.deleteUser(username);
 	  return user;
	}
	@Override
	protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
		   String username=MuleRestUtil.getPathPath(message);
	 	   if(username==null||username.length()==0){
	 		   return new ErrorMessage("The username is missing in DELETE");
	 	   }
	 	  BoxUser user=boxUserService.getUserByUserName(username);
	 	  if(user==null){
	 		 return new ErrorMessage("no such username with username");
	 	  }
	 	  
	 	  com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		  objectMapper.setSerializationInclusion(Include.NON_NULL);
		  String requestInJson = (String)message.getPayloadAsString();							
		  BoxUser boxuser = objectMapper.readValue(requestInJson, BoxUser.class);
		  if(!boxuser.getUsername().equals(username)){
			  return new ErrorMessage("username does not match exactly in the db");
		  }
	 	   
	 	  user.setPassword(boxuser.getPassword()); 
	 	  boxUserService.updateUser(user);
	 	 boxuser.setPassword("******");
	 	 return boxuser;	 	   
	}
		
	
    @Override	
	 protected Object processPOST(MuleMessage message, String outputEncoding){
    	
    	try{
		    	com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				String requestInJson = (String)message.getPayloadAsString();							
				BoxUser boxuser = objectMapper.readValue(requestInJson, BoxUser.class);
				boxUserService.createNewUser(boxuser);
				boxuser.setPassword("*******");
				return boxuser;
    	}
    	catch(Exception e){
	    	logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
			throw new RuntimeException(e+" whule processing the payload",e);
    	}
					 
	}	

	
}
