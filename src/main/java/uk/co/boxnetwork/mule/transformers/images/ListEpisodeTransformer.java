package uk.co.boxnetwork.mule.transformers.images;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.UpdatePraram;

import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.PublishedStatus;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;


public class ListEpisodeTransformer extends BoxRestTransformer{

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
		SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.GENERIC);		
	    return imageService.findBoxEpisodes(searchParam);		    			
				    
	}
	            
     
}
