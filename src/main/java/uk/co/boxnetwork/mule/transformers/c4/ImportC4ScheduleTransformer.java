package uk.co.boxnetwork.mule.transformers.c4;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.http.internal.ParameterMap;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ImportC4ScheduleTransformer extends AbstractMessageTransformer{
	
	
	@Autowired
	ImportC4ScheduleService importC4ScheduleService;
	
	

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		String payload=null;
		try {
			payload = message.getPayloadAsString();
			logger.info("recived the import schedule request:"+payload);
			
			com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
			
			objectMapper.setSerializationInclusion(Include.NON_NULL);	    
			ImportScheduleRequest request;
			
			request = objectMapper.readValue(payload, ImportScheduleRequest.class);
			importC4ScheduleService.importSchedule(request);
			return request;			
		} catch (Exception e) {
			logger.error("error while parsing the import schedule request:"+payload,e);			
			 message.setOutboundProperty("http.status", 500);
			 return "{\"message\":\"Error:"+e+"\"}";
		}
	}
	  
   
}
