package uk.co.boxnetwork.mule.transformers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.BCPlayList;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.UpdatePraram;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;

public class BCPlaylistTranscormer extends BoxRestTransformer{

	@Autowired
	MetadataService metadataService;
	
	@Autowired
	MetadataMaintainanceService metadataMaintainanceService;
	
	@Autowired
	AppConfig appConfig;
	
	
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		String playlistid=MuleRestUtil.getPathPath(message);
		if(playlistid==null || playlistid.length()==0){
			return getAllPlaylists(message,outputEncoding);
		}
		else{
			return getAPlaylist(playlistid, message,outputEncoding);				
		}
	}
	
	private  Object getAllPlaylists(MuleMessage message, String outputEncoding){
		SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.BCPLAYLIST);
	    return metadataService.findBCPlayListItems(searchParam);				    
	}
	
     private Object getAPlaylist(String pathpart, MuleMessage message, String outputEncoding){
	  		if(pathpart.endsWith("/videos")){
	  			String playlistid=pathpart.substring(0,pathpart.length()-"/videos".length());
	  			SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.BCITEMINPLAYLIST);
	  			
	  			return getVideosInPlayList(playlistid,searchParam);
	  		}
	  		else{
	  			return metadataService.getAPlaylist(pathpart);
	  		}	  			
	 }
     private Object getVideosInPlayList(String playlistid,SearchParam searchParam){
    	 return metadataService.getVideoDataInPlaylist(playlistid, searchParam);
     }
     @Override
     protected Object processPATCH(MuleMessage message, String outputEncoding) throws Exception{	   					
    	 String playlistid=MuleRestUtil.getPathPath(message);
 		if(playlistid==null || playlistid.length()==0){
 			return returnError("patch not supported for this path",message); 			
 		}
 		else{
 			return patchAPlaylist(playlistid, message,outputEncoding);				
 		}
 	 }
     private Object patchAPlaylist(String playlistid,MuleMessage message,String outputEncoding) throws Exception{
    	 BCPlayList playlist=null;
    	 if(message.getPayload() instanceof BCPlayList){
    		 playlist=(BCPlayList)message.getPayload();			   
		   }
		   else{			   
			   		String playListInJson=(String)message.getPayloadAsString();		   
				   logger.info("*****updating playlist:"+playListInJson+"****");
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
					
					
					objectMapper.setSerializationInclusion(Include.NON_NULL);
					playlist = objectMapper.readValue(playListInJson, BCPlayList.class);
					metadataService.patchPlayList(playlistid,playlist);
		   } 
    	 return playlist;
     }
     
     protected Object processDELETE(MuleMessage message, String outputEncoding){			
    	 String playlistid=MuleRestUtil.getPathPath(message);
 		if(playlistid==null || playlistid.length()==0){
 			return returnError("delete not supported",message);
 		}
 		else{
 			return metadataService.deletePlayList(playlistid);	
 			
 		}
 	}
     protected Object processPOST(MuleMessage message, String outputEncoding){
    	 BCPlayList playlist=null;
    	 if(message.getPayload() instanceof BCPlayList){
    		 playlist=(BCPlayList)message.getPayload();			   
		   }
		   else{	
			   try{
			   		String playListInJson=(String)message.getPayloadAsString();		   
				   logger.info("*****create a new playlist:"+playListInJson+"****");
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
					objectMapper.setSerializationInclusion(Include.NON_NULL);
					playlist = objectMapper.readValue(playListInJson, BCPlayList.class);
					metadataService.createPlayList(playlist);
			   }
			   catch(Exception e){
				   logger.error("error creating the playlist:"+e,e);
				   return returnError("failed to create playlist:"+e,message);
			   }
		   } 
    	 return playlist;					 
	}
	   
   
}