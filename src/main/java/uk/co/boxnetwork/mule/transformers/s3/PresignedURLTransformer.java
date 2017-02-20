package uk.co.boxnetwork.mule.transformers.s3;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.data.s3.S3FileSignatureData;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class PresignedURLTransformer extends BoxRestTransformer{
	@Autowired
	private S3BucketService s3uckerService;
	
	protected Object processGET(MuleMessage message, String outputEncoding){
		Map<String, String> queryprarams=message.getInboundProperty("http.query.params");		
		String prefix=null;
		if(queryprarams!=null && queryprarams.get("url")!=null){
			String file=queryprarams.get("url").trim();
			logger.info("to sing:"+file);
			S3FileSignatureData item=new S3FileSignatureData();	
		    item.setFile(s3uckerService.generatedPresignedURL(file, 3600));
		    return item;
		}
		else{
			return new ErrorMessage("the parameter is missing");
		}	
	}
	 protected Object processPOST(MuleMessage message, String outputEncoding){
		 try{
			   String commandInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****Posted a new command:"+commandInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   S3FileSignatureData request = objectMapper.readValue(commandInJson, S3FileSignatureData.class);
			   return s3uckerService.createS3FileSignatureData(request);		   
		 }
		catch(Exception e){
			throw new RuntimeException("proesing post:"+e,e);    			
		}
		   
	}	

	
}