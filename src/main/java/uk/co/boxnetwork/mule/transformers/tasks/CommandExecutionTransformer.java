package uk.co.boxnetwork.mule.transformers.tasks;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.util.GenericUtilities;

public class CommandExecutionTransformer extends BoxRestTransformer{
	
	
	@Autowired
	MetadataService metataService;
	
	
	protected Object processPOST(MuleMessage message, String outputEncoding){
		try{	
    		    String commandInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****Posted a new command:"+commandInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   uk.co.boxnetwork.model.MediaCommand command = objectMapper.readValue(commandInJson, uk.co.boxnetwork.model.MediaCommand.class);
			   return metataService.processCommand(command);			   
		}
		catch(Exception e){
			throw new RuntimeException("proesing post:"+e,e);    			
		}
    }
	
}