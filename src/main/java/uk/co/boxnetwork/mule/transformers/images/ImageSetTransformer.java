package uk.co.boxnetwork.mule.transformers.images;

import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImageService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.image.Image;
import uk.co.boxnetwork.data.image.ImageSet;
import uk.co.boxnetwork.model.AppConfig;

import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;

public class ImageSetTransformer extends BoxRestTransformer{
	
	@Autowired
	ImageService imageService;
	
	
	@Autowired
	AppConfig appConfig;


	
	
	
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		String setid=MuleRestUtil.getPathPath(message);
		if(setid==null || setid.length()==0){
			return findImageSets(message,outputEncoding);
		}
		else{
			return imageService.findImageSetById(Long.valueOf(setid));						
		}
	}
	
	private  Object findImageSets(MuleMessage message, String outputEncoding){
	   SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.EPISODE);
	   return imageService.findImageSets(searchParam);	   
	}
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){
		String setid=MuleRestUtil.getPathPath(message);
		if(setid==null || setid.length()==0){
			return createImageSet(message,outputEncoding);
		}
		else{
			return returnError("single post not supportede", message);
		}
	}
	private Object createImageSet(MuleMessage message, String outputEncoding){
		String imageseteInJson=null;
		try{	
			imageseteInJson=(String)message.getPayloadAsString();
		}
		catch(Exception e){
			logger.error(e +" while getting the payload",e);
			return returnError("failed to get the request payload", message);	
		}
		 logger.info("*****Posted a new imageset:"+imageseteInJson+"****");
		   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
		   objectMapper.setSerializationInclusion(Include.NON_NULL);
		   ImageSet imageSet=null;
		   try {
			   imageSet = objectMapper.readValue(imageseteInJson, ImageSet.class);
		} catch (Exception e1) {
			logger.error("failed to parse into the ImageSet:"+imageseteInJson,e1);
			return returnError("failed to parse the ImageSet payload, wrong format", message);	
		}		  
		if(imageSet.getId()==null){
			imageSet=imageService.createImageSet(imageSet);
		}	    					   
		return imageSet;
	}
	            
	 
}
