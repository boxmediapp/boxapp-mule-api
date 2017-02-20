package uk.co.boxnetwork.mule.transformers.metadata;

import java.io.IOException;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.CuePoint;
import uk.co.boxnetwork.data.Episode;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;

public class CueTransformer extends BoxRestTransformer{

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
				return getAllCues(paths[0]);				
			}
			else{
				return getOneCuePoint(paths[0],paths[1]);
		     }
		}
	}
	public Object getAllCues(String episodeid){
		Long id=Long.valueOf(episodeid);
	    Episode episode=metadataService.getEpisodeById(id);
	    return episode.getCuePoints();	    
	}
	public Object getOneCuePoint(String episodeid, String cueid){		
		Long cid=Long.valueOf(cueid);
	    return metadataService.getCuePointById(cid);	    
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
				return createNewCue(paths[0],message,outputEncoding);				
			}
			else{
				return returnError("not spoorted",message);
		     }
		}
	}
	Object createNewCue(String episodeid,MuleMessage message, String outputEncoding){
		try {	
				String cueInJson=(String)message.getPayloadAsString();		   
				logger.info("*****Posted a new cue:"+cueInJson+"****:for:"+episodeid);
				Long id=Long.valueOf(episodeid);		   
				com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
				objectMapper.setSerializationInclusion(Include.NON_NULL);
			   	uk.co.boxnetwork.data.CuePoint cuepoint = objectMapper.readValue(cueInJson, uk.co.boxnetwork.data.CuePoint.class);
			   	metadataService.createNewCuepoint(id,cuepoint);
			   	return cuepoint;			   	
		   } catch (Exception e) {			   
			   logger.error("error when processing the new cuepoint",e);
			   return returnError("Error when processing the new cuepoint",message);			   
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
				return returnError("the curid is missing for delete", message);				
			}
			else{
				return deleteOneCuePoint(paths[0],paths[1]);
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
				return updateOneCuePoint(paths[0],paths[1],message, outputEncoding);
		     }
		}			 
	}
	Object updateOneCuePoint(String episodeid,String cueid, MuleMessage message, String outputEncoding){
		try {	
				String cueInJson=(String)message.getPayloadAsString();		   
				logger.info("*****UPDATE cue:"+cueInJson+"****:for:episodeid:"+episodeid+":cueid:"+cueid);
				Long epid=Long.valueOf(episodeid);
				Long cid=Long.valueOf(cueid);
				
				com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
				objectMapper.setSerializationInclusion(Include.NON_NULL);
			   	uk.co.boxnetwork.data.CuePoint cuepoint = objectMapper.readValue(cueInJson, uk.co.boxnetwork.data.CuePoint.class);
			   	metadataService.updateCuepoint(epid,cid,cuepoint);			   	
			   	return cuepoint;			   	
		   } catch (Exception e) {			   
			   logger.error("error when processing the new cuepoint",e);
			   return returnError("Error when processing the new cuepoint",message);			   
		   } 
		   
		   
		
	}
	
	
	
	
	Object deleteOneCuePoint(String epidodeid, String cueid){		
		Long epid=Long.valueOf(epidodeid);
		Long cid=Long.valueOf(cueid);
		return metadataService.deleteCuepoint(epid,cid);
	}
	
}
