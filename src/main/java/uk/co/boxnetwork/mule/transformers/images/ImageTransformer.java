package uk.co.boxnetwork.mule.transformers.images;

import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.image.Image;

import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ImageTransformer extends BoxRestTransformer{
	
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
			return imageService.findImageById(Long.valueOf(imgid));					
		}
	}
	
	private  Object findImages(MuleMessage message, String outputEncoding){
	   SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.EPISODE);
	   return imageService.findImages(searchParam);	  	   
	}
	
	private Object parseImage(MuleMessage message, String outputEncoding){
		String imageInJson=null;
		try{	
			imageInJson=(String)message.getPayloadAsString();
		}
		catch(Exception e){
			logger.error(e +" while getting the payload",e);
			return returnError("failed to get the request payload", message);	
		}
		 logger.info("*****Posted a new image:"+imageInJson+"****");
		   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
		   objectMapper.setSerializationInclusion(Include.NON_NULL);
		   Image image=null;
		   try {
			   image = objectMapper.readValue(imageInJson, Image.class);
		} catch (Exception e1) {
			logger.error("failed to parse into the Image:"+imageInJson,e1);
			return returnError("failed to parse the Image payload, wrong format", message);	
		}		
		return image;
	}
	@Override
	protected Object processPUT(MuleMessage message, String outputEncoding){
		String imgid=MuleRestUtil.getPathPath(message);
		if(imgid==null || imgid.length()==0){
			return returnError("PUT not supoorted for pulural", message);
		}
		else{
			Object obj=parseImage(message,outputEncoding);
			if(obj instanceof Image){
				Image image=(Image)obj;
				if(image.getId()==Long.valueOf(imgid)){
					return imageService.createImage(image);
				}
				else{
					return returnError("image id does not match", message);
				}
			}
			else{
				return obj;
			}
		}
	}
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){
		String imgid=MuleRestUtil.getPathPath(message);
		if(imgid==null || imgid.length()==0){
			Object obj=parseImage(message,outputEncoding);
			if(obj instanceof Image){
				Image image=(Image)obj;				
				return imageService.createImage(image);
			}						
			else{
				return obj;
			}
		}
		else{
			return returnError("POST on image id is not allowed", message);	
		}
	}
	
	
	            
	
}
