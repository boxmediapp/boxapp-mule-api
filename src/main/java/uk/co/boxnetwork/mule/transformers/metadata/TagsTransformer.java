package uk.co.boxnetwork.mule.transformers.metadata;




import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;


public class TagsTransformer  extends BoxRestTransformer{
	
	@Autowired
	BoxMedataRepository boxMedataRepository;
			
	@Override
	protected  Object processGET(MuleMessage message, String outputEncoding){					
			String[] tags=boxMedataRepository.getAllTags();
			if(tags==null){
				tags=new String[0];				
			}			
			return tags;
	}      
	@Override
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
		String tag=MuleRestUtil.getPathPath(message);
		if(tag==null || tag.length()==0){
			logger.info("tag is not provided to delete");
			return returnError("Do not support delete all tags",message);
		}
		else{
			logger.info("receive to delete request tag="+tag);
			return boxMedataRepository.removeTag(tag);
		}			 
	}
	protected Object processPOST(MuleMessage message, String outputEncoding){
		try{	
    		    String tagInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****Posted a new tag:"+tagInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   uk.co.boxnetwork.model.MediaTag mediaTag = objectMapper.readValue(tagInJson, uk.co.boxnetwork.model.MediaTag.class);
			   boxMedataRepository.saveTag(mediaTag.getName());
			   return mediaTag;
		}
		catch(Exception e){
			throw new RuntimeException("proesing post:"+e,e);    			
		}
    }
   
}