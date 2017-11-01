package uk.co.boxnetwork.mule.transformers.images;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.image.Image;
import uk.co.boxnetwork.data.image.ImageSet;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class AllClientImageTransformer extends BoxRestTransformer{
	@Autowired
	ImageService imageService;
	
	
	@Autowired
	AppConfig appConfig;

	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		String imgid=MuleRestUtil.getPathPath(message);
		if(imgid==null || imgid.length()==0){
			return findImages(message,outputEncoding);
			}
		else{
			return imageService.findClientImageById(Long.valueOf(imgid));					
		}
	}
	
	private  Object findImages(MuleMessage message, String outputEncoding){
	   SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.EPISODE);
	   return imageService.findClientImages(searchParam);	  	   
	}
	
	 
}
