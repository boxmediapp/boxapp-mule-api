package uk.co.boxnetwork.mule.transformers.cms;


import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;


import uk.co.boxnetwork.components.cms.CMSService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.cms.CMSMenuData;

import uk.co.boxnetwork.model.MediaApplicationID;

import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;


public class ManageCMSMenuTransformer  extends BoxRestTransformer{

	@Autowired
	CMSService cmsService;
	
	
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){	
		MediaApplicationID applicationId=getApplicationId(operator);			
		String menuid=MuleRestUtil.getPathPath(message);
		if(menuid==null || menuid.length()==0){
			return getAllCMSMenus(message,outputEncoding,applicationId);
		}
		else{
			return getAnCMSMenu(menuid, message,outputEncoding);				
		}
	}
	
	private  Object getAllCMSMenus(MuleMessage message, String outputEncoding,MediaApplicationID applicationId){		
	    return cmsService.findAllCMSMenu(applicationId);					    
	}
	
     private Object getAnCMSMenu(String menuid, MuleMessage message, String outputEncoding){
	  		Long id=Long.valueOf(menuid);
		    return cmsService.getCMSMenuById(id);									
	}		
	
     @Override
   protected Object processPUT(MuleMessage message, BoxOperator operator, String outputEncoding) throws Exception{
	   String menuid=MuleRestUtil.getPathPath(message);
	   MediaApplicationID applicationId=getApplicationId(operator);
	   if(menuid==null||menuid.length()==0){
		   return new ErrorMessage("The menuid is missing in PUT");
	   }
	   Long id=Long.valueOf(menuid);
	   CMSMenuData  cmsmenu=null;
	   
	   if(message.getPayload() instanceof uk.co.boxnetwork.data.Episode){
		   cmsmenu=(CMSMenuData)message.getPayload();			   
	   }
	   else{			   
		   		String cmsMenuInJson=(String)message.getPayloadAsString();		   
			    logger.info("*****updating cmsmenu"+cmsMenuInJson+"****");
			    com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				cmsmenu = objectMapper.readValue(cmsMenuInJson, CMSMenuData.class);
	   }	   	  
	   cmsService.updateCMSMenu(cmsmenu,applicationId);
	   return cmsmenu;
	}  
     
     @Override         
	protected Object processPOST(MuleMessage message, BoxOperator operator,String outputEncoding){
    	 MediaApplicationID applicationId=getApplicationId(operator);
		String cmsMenuInJson=null;
		try{	
			cmsMenuInJson=(String)message.getPayloadAsString();
		}
		catch(Exception e){
			logger.error(e +" while getting the payload",e);
			return returnError("failed to get the request payload", message);	
		}
			   logger.info("*****Posted a new cmsmenu:"+cmsMenuInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   CMSMenuData cmsmenu=null;
			   try {
				   cmsmenu = objectMapper.readValue(cmsMenuInJson, CMSMenuData.class);
			} catch (Exception e1) {
				logger.error("failed to parse into the cmsmenu:"+cmsMenuInJson,e1);
				return returnError("failed to parse the episode payload, wrong format", message);	
			}			   			   			 
			if(cmsmenu.getId()!=null){
				 return returnError("failed to create, id is set",message);
			}
			cmsService.createCMSMenu(cmsmenu, applicationId);
			return cmsmenu;			   	
    }
	@Override
	protected Object processDELETE(MuleMessage message, BoxOperator operator,String outputEncoding){	
		String menuid=MuleRestUtil.getPathPath(message);
		if(menuid==null || menuid.length()==0){
			return returnError("Do not support delete all cmsmenu",message);
		}
		else{
			Long id=Long.valueOf(menuid);
			MediaApplicationID applicationId=getApplicationId(operator);
			CMSMenuData cmsmenu=cmsService.removeCMSMenuById(id,applicationId);
			if(cmsmenu!=null){
				logger.info("Episode is deleted successfully id="+id);
				return cmsmenu;
			}
			else{
				return returnError("failed to delete", message);
			}
			
		}			 
	}      
	
	
     
}
