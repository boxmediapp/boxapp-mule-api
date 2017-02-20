package uk.co.boxnetwork.mule.transformers.metadata;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.Episode;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class AvailabilityTransformer extends BoxRestTransformer{

	@Autowired
	MetadataService metadataService;
	
	@Autowired
	MetadataMaintainanceService metadataMaintainanceService;
	
	
	
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		String pathpart=MuleRestUtil.getPathPath(message);		 
		if(pathpart==null || pathpart.length()==0){
			return returnError("the episodeId is missing", message);
		}
		else{
			String paths[]=pathpart.split("/");
			if(paths.length==1){
				return getAllAvailabilities(paths[0]);				
			}
			else{
				return getOneCueAvailability(paths[0],paths[1]);
		     }
		}
	}
	public Object getAllAvailabilities(String episodeid){
		Long id=Long.valueOf(episodeid);
	    Episode episode=metadataService.getEpisodeById(id);
	    return episode.getAvailabilities();	    
	}
	public Object getOneCueAvailability(String episodeid, String availabilityid){		
		Long avid=Long.valueOf(availabilityid);
	    return metadataService.getAvailabilityWindowId(avid);	    
	}
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){				
		String pathpart=MuleRestUtil.getPathPath(message);		 
		if(pathpart==null || pathpart.length()==0){
			return returnError("the episodeId is missing", message);
		}
		else{
			String paths[]=pathpart.split("/");
			if(paths.length==1){
				return createNewAvailability(paths[0],message,outputEncoding);				
			}
			else{
				return returnError("not spoorted",message);
		     }
		}
	}
	Object createNewAvailability(String episodeid,MuleMessage message, String outputEncoding){
		try {	
				String availabilityInJson=(String)message.getPayloadAsString();		   
				logger.info("*****Posted a new availability:"+availabilityInJson+"****:for:"+episodeid);
				Long id=Long.valueOf(episodeid);		   
				com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
				objectMapper.setSerializationInclusion(Include.NON_NULL);
			   	uk.co.boxnetwork.data.AvailabilityWindow availability = objectMapper.readValue(availabilityInJson, uk.co.boxnetwork.data.AvailabilityWindow.class);
			   	if(!availability.valid()){
			   		return returnError("Not valid AvailabilityWindow value submitted", message);
			   	}			   	
			   	metadataService.createNewAvailability(id,availability);			   	
			   	return availability;			   	
		   } catch (Exception e) {			   
			   logger.error("error when processing the new availability",e);
			   return returnError("Error when processing the new availability",message);			   
		   } 
		   
		   
		
	}
	
	@Override
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
		String pathpart=MuleRestUtil.getPathPath(message);		 
		if(pathpart==null || pathpart.length()==0){
			return returnError("the episodeId is missing", message);
		}
		else{
			String paths[]=pathpart.split("/");
			if(paths.length==1){
				return returnError("the availabilityid is missing for delete", message);				
			}
			else{
				return deleteOneAvailability(paths[0],paths[1]);
		     }
		}			 
	}
	
	@Override
	protected Object processPUT(MuleMessage message, String outputEncoding){	
		String pathpart=MuleRestUtil.getPathPath(message);		 
		if(pathpart==null || pathpart.length()==0){
			return returnError("the episodeId is missing", message);
		}
		else{
			String paths[]=pathpart.split("/");
			if(paths.length==1){
				return returnError("the curid is missing for update", message);				
			}
			else{
				return updateOneAvailability(paths[0],paths[1],message, outputEncoding);
		     }
		}			 
	}
	Object updateOneAvailability(String episodeid,String availabilityid, MuleMessage message, String outputEncoding){
		try {	
				String cueInJson=(String)message.getPayloadAsString();		   
				logger.info("*****UPDATE availability:"+cueInJson+"****:for:episodeid:"+episodeid+":avialabilityid:"+availabilityid);
				Long epid=Long.valueOf(episodeid);
				Long avid=Long.valueOf(availabilityid);
				
				com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
				objectMapper.setSerializationInclusion(Include.NON_NULL);
			   	uk.co.boxnetwork.data.AvailabilityWindow availabilitywindow = objectMapper.readValue(cueInJson, uk.co.boxnetwork.data.AvailabilityWindow.class);
			   	if(!availabilitywindow.valid()){
			   		return returnError("Not valid AvailabilityWindow value submitted", message);
			   	}
			   	metadataService.updateAvailabilityWindow(epid,avid,availabilitywindow);			   	
			   	return availabilitywindow;			   	
		   } catch (Exception e) {			   
			   logger.error("error when processing the new vailability",e);
			   return returnError("Error when processing the new vailability",message);			   
		   } 
		   
		   
		
	}
	
	
	
	
	Object deleteOneAvailability(String epidodeid, String availabilityid){		
		Long epid=Long.valueOf(epidodeid);
		Long avid=Long.valueOf(availabilityid);
		return metadataService.deleteAvailabilityWindow(epid,avid);
		
	}
	
}
