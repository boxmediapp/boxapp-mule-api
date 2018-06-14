package uk.co.boxnetwork.mule.transformers.images;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ListScheduleEpisodeTransformer extends BoxRestTransformer{

	@Autowired
	ImageService imageService;
	
	
	@Autowired
	AppConfig appConfig;
	
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){				
		String episodeid=MuleRestUtil.getPathPath(message);
		if(episodeid==null || episodeid.length()==0){
			return getAllEpisodes(message,outputEncoding);
		}
		else{
			return imageService.findEpisodeById(Long.valueOf(episodeid));			
		}
	}
	
	private  Object getAllEpisodes(MuleMessage message, String outputEncoding){
		SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.BOXEPISODE);		
	    return imageService.findBoxEpisodesByBoxScheduleEvent(searchParam);		    							    
	}
	            
     
}
