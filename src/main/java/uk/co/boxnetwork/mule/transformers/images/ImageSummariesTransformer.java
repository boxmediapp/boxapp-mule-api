package uk.co.boxnetwork.mule.transformers.images;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ImageSummariesTransformer extends BoxRestTransformer{
	@Autowired
	ImageService imageService;
	
	
	@Autowired
	AppConfig appConfig;

	
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		String setid=MuleRestUtil.getPathPath(message);
		if(setid==null || setid.length()==0){
			return buildImageSummaries(message,outputEncoding);
		}
		else{
			return returnError("does not support", message);						
		}
	}
	protected Object buildImageSummaries(MuleMessage message, String outputEncoding){
		
		return imageService.buildImageSummaries();
		
	}
	
}
