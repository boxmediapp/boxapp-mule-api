package uk.co.boxnetwork.mule.transformers.images;

import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class AllClientImageTransformer extends BoxRestTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(AllClientImageTransformer.class);
	@Autowired
	ImageService imageService;
	
	
	@Autowired
	AppConfig appConfig;

	
	private String[] clientIds={"virgin","freesat","ee","sky","freeview"};
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		String path=MuleRestUtil.getPathPath(message);
		
		if(path==null || path.length()==0){
			return findImages(message,outputEncoding);
			}
		else{
			   int ib=path.indexOf("/");
			   String clientIdPath=null;
			   String imgid=null;
			   if(ib==0){
				   return returnError("wrong path", message);
			   }
			   else if(ib>0){
				   clientIdPath=path.substring(0,ib);	
				   if((ib+1)<path.length()){
					   imgid=path.substring(ib+1);
				   }
			   }
			   else{
				   clientIdPath=path;
			   }
			   String clientId=null;
			   for(String id:clientIds){
				   if(id.equals(clientIdPath)){
					   clientId=id;
					   break;
				   }
			   }
			   if(clientId==null){
				   if(imgid!=null){
					   return returnError("wrong client id:"+path, message);
				   }
				   else{
					   Long imid=null;
					   try{
						   imid=Long.valueOf(clientIdPath);
						   return imageService.findClientImageById(imid);
						   
					   }
					   catch(Exception e){
						   return returnError("wrong client id"+path, message);
					   }
				   }
				   
			   }
			   else{
				   return findImages(clientId,imgid,message,outputEncoding);
			   }
			   
			   
								
		}
	}
	private Object findImages(String clientid,String imgid,MuleMessage message, String outputEncoding){
		logger.info("Processing client image request clientid=["+clientid+"]imgid=["+imgid+"]");
		if(imgid==null){
			return findImages(message,outputEncoding);
		}
		else{
			try{
				  Long imid=Long.valueOf(imgid);
				   return imageService.findClientImageById(imid);
				   
			   }
			   catch(Exception e){
				   return returnError("wrong imgid id"+imgid, message);
			   }
		}
	}
	
	private  Object findImages(MuleMessage message, String outputEncoding){
	   SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.EPISODE);
	   return imageService.findClientImages(searchParam);	  	   
	}
	
	 
}
