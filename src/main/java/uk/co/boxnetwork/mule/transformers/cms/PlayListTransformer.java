package uk.co.boxnetwork.mule.transformers.cms;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;



import uk.co.boxnetwork.components.cms.CMSService;
import uk.co.boxnetwork.model.MediaApplicationID;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class PlayListTransformer extends BoxRestTransformer{

	@Autowired
	CMSService cmsService;
	
	
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){	
		MediaApplicationID applicationId=getApplicationId(operator);			
		String playlistid=MuleRestUtil.getPathPath(message);
		if(playlistid==null || playlistid.length()==0){
			return getAllPlaylists(message,outputEncoding,applicationId);
		}
		else{
			return getAPlaylist(playlistid, message,outputEncoding);				
		}
	}
	
	private  Object getAllPlaylists(MuleMessage message, String outputEncoding,MediaApplicationID applicationId){
		Long episodeid=getLongQueryParameter(message,"episodeid");
		if(episodeid!=null){
				return returnError("episodeis is missing", message);
		}
		else{
			return cmsService.findCMSPlaylistByEpisodeId(episodeid);
		}
	}
	
     private Object getAPlaylist(String playlistid, MuleMessage message, String outputEncoding){
	  		
		   // return cmsService.getPlaylistById(playlistid);
    	 return null;
	}		
	
     	
     
}
