package uk.co.boxnetwork.mule.transformers.bc;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.model.BCNotification;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BCIngestNotificationTransformer extends BoxRestTransformer{
	
	@Autowired
	BCVideoService bcVideoService;
	
	@Autowired
	MetadataService medataService;
	
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){
					
			try {
					com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
							objectMapper.setSerializationInclusion(Include.NON_NULL);
							String notificationInJson = (String)message.getPayloadAsString();
							logger.info("Received bc notification:"+notificationInJson);
							if(notificationInJson.indexOf("entity=ASSET")!=-1){
								logger.info("Trying to correct the json format:"+notificationInJson);
								notificationInJson=notificationInJson.replaceAll("\\{", "{\"");
								notificationInJson=notificationInJson.replaceAll("\\}", "\"}");
								notificationInJson=notificationInJson.replaceAll("\\=", "\":\"");
								notificationInJson=notificationInJson.replaceAll("\\, ", "\", \"");
								logger.info("corrected to:"+notificationInJson);								
							}
							BCNotification bcNotification = objectMapper.readValue(notificationInJson, BCNotification.class);
							bcNotification.setId(null);
							bcVideoService.persist(bcNotification);
							medataService.notifyTranscode(bcNotification);							
							return bcNotification;
												
				} catch (Exception e) {
					logger.error("error is processing ingest:"+message.getPayload().getClass().getName());
					throw new RuntimeException(e+" whule processing the payload",e);
				}		   
				   
					
	}
	protected Object processGET(MuleMessage message, String outputEncoding){
		
		String jobid=MuleRestUtil.getPathPath(message);
		if(jobid==null || jobid.length()==0){
			return bcVideoService.findAllBCNotification();
			
		}
		else{
			return bcVideoService.findBCNotificationByJobId(jobid);
		}
	}
		  		  
		  		  	    			 

}


