package uk.co.boxnetwork.mule.transformers.bc;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCVideoIngestRequest;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BCIngestTransformer  extends BoxRestTransformer{
	
	@Autowired
	BCVideoService bcVideoService;
	
	
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){
			String requestInJson=null;
			
			try {
					FileIngestRequest ingestrequest=null;
				    if(message.getPayload() instanceof FileIngestRequest){
					   ingestrequest=(FileIngestRequest)message.getPayload();			   
					}
					else{			   									
							com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
							objectMapper.setSerializationInclusion(Include.NON_NULL);
							requestInJson = (String)message.getPayloadAsString();							
							ingestrequest = objectMapper.readValue(requestInJson, FileIngestRequest.class);
					}
				    
					return bcVideoService.ingestVideoToBrightCove(ingestrequest);
												
				} catch (Exception e) {
					logger.error("error is processing ingest:"+message.getPayload().getClass().getName()+" requestInJson=["+requestInJson+"]");
					throw new RuntimeException(e+" whule processing the payload",e);
				}		   
				   
					
		   }		  
		  		  
		  		  	    			 
	}
	  
