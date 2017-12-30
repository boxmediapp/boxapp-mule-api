package uk.co.boxnetwork.mule.transformers.images.clients;

import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class AllClientImageTransformer extends BoxRestTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(AllClientImageTransformer.class);
	@Autowired
	ImageService imageService;
	
	
	@Autowired
	AppConfig appConfig;

	
	
	@Override
	protected boolean checkGETAccess(BoxOperator operator){
	    return true;		    
    }
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator, String outputEncoding){				
		String path=MuleRestUtil.getPathPath(message);
		
		if(path==null || path.length()==0){
				return findImages(message,outputEncoding);
	     }
		else{			   
				Long imid=null;
				try{
						   imid=Long.valueOf(path);
						   return imageService.findClientImageById(imid);
						   
			    }
					   catch(Exception e){
						   return returnError("wrong client id"+path, message);
					   }
		}

	}
	private  Object findImages(MuleMessage message, String outputEncoding){
	   SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.EPISODE);
	   return imageService.findClientImages(searchParam);	  	   
	}
	
	 
}
