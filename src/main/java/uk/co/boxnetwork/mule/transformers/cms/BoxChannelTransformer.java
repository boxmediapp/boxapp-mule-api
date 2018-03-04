package uk.co.boxnetwork.mule.transformers.cms;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImageRepository;
import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.components.boxdata.BoxDataRepository;
import uk.co.boxnetwork.components.cms.CMSService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.cms.BoxChannelData;
import uk.co.boxnetwork.data.cms.CMSMenuData;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MediaApplicationID;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxChannelTransformer extends BoxRestTransformer{

	@Autowired
	CMSService  cmsService;
	
	
	@Autowired
	AppConfig appConfig;
	
	 
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator, String outputEncoding){
				
		String channelid=MuleRestUtil.getPathPath(message);
		if(channelid==null||channelid.trim().length()==0){			
				return getAllBoxChannels(message,outputEncoding);
		}
		else{
			Long scheduleTimestampFrom=getLongQueryParameter(message,"scheduleTimestampFrom");
			Long scheduleTimestampTo=getLongQueryParameter(message,"scheduleTimestampTo");
			return cmsService.findBoxChannelById(channelid,scheduleTimestampFrom,scheduleTimestampTo);
		}
	}
	
	private  Object getAllBoxChannels(MuleMessage message, String outputEncoding){		
		Long scheduleTimestampFrom=getLongQueryParameter(message,"scheduleTimestampFrom");
		Long scheduleTimestampTo=getLongQueryParameter(message,"scheduleTimestampTo");
	    return cmsService.findAllBoxChannel(scheduleTimestampFrom,scheduleTimestampTo);		    			
				    
	}
	@Override
	protected Object processPOST(MuleMessage message, BoxOperator operator,String outputEncoding){
		    MediaApplicationID applicationId=getApplicationId(operator);
			String channelInJson=null;
			try{	
				channelInJson=(String)message.getPayloadAsString();
			}
			catch(Exception e){
				logger.error(e +" while getting the payload",e);
				return returnError("failed to get the request payload", message);	
			}
				   logger.info("*****Posted a new channel:"+channelInJson+"****");
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
				   objectMapper.setSerializationInclusion(Include.NON_NULL);
				   BoxChannelData channelData=null;
				   try {
					   channelData = objectMapper.readValue(channelInJson, BoxChannelData.class);
				} catch (Exception e1) {
					logger.error("failed to parse into the channel:"+channelInJson,e1);
					return returnError("failed to parse the channel payload, wrong format", message);	
				}			   			   			 
				if(channelData.getChannelId()==null){
					 return returnError("channelId cannot be null",message);
				}
				if(channelData.getChannelName()==null){
					 return returnError("channelName cannot be null",message);
				}
				cmsService.updateChannel(channelData);
				return channelData;				
		
	}
	@Override
	protected Object processDELETE(MuleMessage message, BoxOperator operator,String outputEncoding){
		
		String channelid=MuleRestUtil.getPathPath(message);
		if(channelid==null || channelid.length()==0){
			return returnError("DELETE not supoorted for pulural", message);
		}
		else{		
				return cmsService.deleteChannelById(channelid);					
				
			}
			
		
	}
	
	            
     
}
