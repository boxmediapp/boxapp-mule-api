package uk.co.boxnetwork.mule.transformers.tasks;

import java.util.List;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.TimedTaskService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.app.AppInfo;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.TimedTask;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class LoadAppInformationTransformer extends BoxRestTransformer{
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	MetadataMaintainanceService maintainanceService;
	
	@Override
	 protected Object processGET(MuleMessage message, String outputEncoding){
		AppInfo appInfo=new AppInfo();
		appInfo.setAppconfig(appConfig);
			return appInfo;
	 }
	
	
	 @Override
     protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
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
					maintainanceService.updateAppConfig(appInfo.getAppconfig());
					
		   }
		   
		   return appInfo; 
		}  
       
   
}