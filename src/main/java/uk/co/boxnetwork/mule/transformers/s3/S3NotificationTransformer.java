package uk.co.boxnetwork.mule.transformers.s3;

import java.io.IOException;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.components.NotificationReceiver;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.s3.S3Notifivcation;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class S3NotificationTransformer extends BoxRestTransformer{

	@Autowired
	private NotificationReceiver notificationReceiver; 
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){		
		try{
			
			com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			String requestInJson = (String)message.getPayloadAsString();							
			logger.info("S3NotificationTransformer: Notification Message is received:"+requestInJson);
			Map<String, Object> msg = (Map<String, Object>)objectMapper.readValue(requestInJson, Map.class);
			notificationReceiver.notify(msg);			
			//S3Notifivcation s3Notification = objectMapper.readValue(requestInJson, S3Notifivcation.class);
			//SNSSubscriptionMessage snsM3ssage=objectMapper.readValue(requestInJson, SNSSubscriptionMessage.class);
			return requestInJson;
			//return snsM3ssage;			
		}
		catch(Exception e){
			throw new RuntimeException("failed on processing S3VideoConcatTransformer request",e);
		}
		  		  	    			 
	}
}
