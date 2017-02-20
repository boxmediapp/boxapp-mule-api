package uk.co.boxnetwork.mule.transformers.soundmouse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import freemarker.template.TemplateException;
import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;

public class SoundmouseTransformer extends BoxRestTransformer{

	@Autowired
	MetadataService metadataService;
	
	@Autowired
	MetadataMaintainanceService metadataMaintainanceService;
	
	@Autowired
	AppConfig appConfig;
	
	
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		String uripath=MuleRestUtil.getPathPath(message);
		if(uripath==null || uripath.length()==0){
			return returnError("not supported",message);
		}
		else{
			try{	
				   String episodeid=uripath;
				   int ib=uripath.indexOf("/");
				   String type="header";				   
				   if(ib!=-1){
					   episodeid=uripath.substring(0,ib);
					   type=uripath.substring(ib+1);					   
				   }
				   Long id=Long.valueOf(episodeid);				   
				   if(type==null|| type.equals("header")){					   					   	
						return metadataService.getSoundMouseHeaderFile(id);
				   }
				   else if(type.equals("smurf")){					   	  
					   return metadataService.getSoundMouseSmurfFile(id);
				   }
				   else{
					   return returnError("not supported",message);
				   }
				
					
				}
			catch(Exception e){
				logger.error("Error when getting the soundmouse header file",e);
				return returnError("Failed to create the soundmouse header file:"+e, message);
			}
		}
	}
	
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){
		try{
		    String commandInJson=(String)message.getPayloadAsString();		   
		   logger.info("*****Received soudmouse command:"+commandInJson+"****");
		   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
		   objectMapper.setSerializationInclusion(Include.NON_NULL);
		   MediaCommand mediaCommand = objectMapper.readValue(commandInJson, MediaCommand.class);
		   if(MediaCommand.DELIVER_SOUND_MOUSE_SMURF_FILE.equals(mediaCommand.getCommand())){
			   metadataMaintainanceService.scheduleToDeliverSoundmouseSmurfFile();
		   }
		   else{
			   throw new RuntimeException("not supported command:"+mediaCommand);
		   }
		   return mediaCommand;
		}
		   catch(Exception e){
   			throw new RuntimeException("proesing post:"+e,e);    			
   		}
		   
		
	}
     	
	
             
     
}
