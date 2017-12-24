package uk.co.boxnetwork.mule.transformers.images;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.ImageRepository;
import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxChannelTransformer extends BoxRestTransformer{

	@Autowired
	ImageRepository  imageRepository;
	
	
	@Autowired
	AppConfig appConfig;
	
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator, String outputEncoding){				
		String channelid=MuleRestUtil.getPathPath(message);
		if(channelid==null||channelid.trim().length()==0){			
				return getAllBoxChannels(message,outputEncoding);
		}
		else{
			return imageRepository.findBoxChannelById(channelid);
		}
	}
	
	private  Object getAllBoxChannels(MuleMessage message, String outputEncoding){
		SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.EPISODE);		
	    return imageRepository.findAllBoxChannel(searchParam);		    			
				    
	}
	            
     
}
