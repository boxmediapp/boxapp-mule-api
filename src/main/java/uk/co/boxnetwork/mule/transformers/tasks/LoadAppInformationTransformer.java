package uk.co.boxnetwork.mule.transformers.tasks;



import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.data.app.AppInfo;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MediaApplicationID;

import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;


public class LoadAppInformationTransformer extends BoxRestTransformer{
	

	
	@Autowired
	AppConfig appConfig;
	
	
	
	@Autowired
	private BoxMedataRepository repository;

	
	
	
	@Override
	 protected Object processGET(MuleMessage message, BoxOperator operator, String outputEncoding){
		MediaApplicationID applicationId=getApplicationId(operator);		
		if(applicationId==MediaApplicationID.IMAGE_CLIENT_APP){
			applicationId=MediaApplicationID.MEDIA_APP;
		}
		AppInfo appInfo=new AppInfo();
		AppConfig appconfig=repository.findAppConfigByApplication(applicationId);		
		appInfo.setAppconfig(appconfig);
	    return appInfo;
	 }
	
	@Override
	protected boolean checkPUTAccess(BoxOperator operator){				
		return operator.checkAdminAccess();
	}
	 @Override
     protected Object processPUT(MuleMessage message, BoxOperator operator, String outputEncoding) throws Exception{
		 	AppInfo  appInfo=null;		   
		   if(message.getPayload() instanceof AppInfo){
			   appInfo=(AppInfo)message.getPayload();			   
		   }
		   else{			   
			   		String appInfoInJSon=(String)message.getPayloadAsString();		   
				   logger.info("*****updating appinfo"+appInfoInJSon+"****");
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
					objectMapper.setSerializationInclusion(Include.NON_NULL);
					appInfo = objectMapper.readValue(appInfoInJSon, AppInfo.class);
		   }
		MediaApplicationID applicationId=getApplicationId(operator);
		AppConfig appconfigInDb=repository.findAppConfigByApplication(applicationId);
		if(appconfigInDb.getId()!=appInfo.getAppconfig().getId()){
						return returnError("The config does not match the application id", message);
		}
		if(appInfo.getAppconfig().getApplicationId()!=applicationId){
						return returnError("Cannot changed the application id", message);
		}
		appconfigInDb.importConfig(appInfo.getAppconfig());
		repository.updateAppConfig(appconfigInDb);			
		appconfigInDb.exportConfig(appConfig);
		return appInfo; 
	}  
       
   
}