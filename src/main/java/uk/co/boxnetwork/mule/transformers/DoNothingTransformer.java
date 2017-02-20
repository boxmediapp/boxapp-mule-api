package uk.co.boxnetwork.mule.transformers;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.mule.util.ConfigSelector;



public class DoNothingTransformer extends BoxRestTransformer{
	private static final Logger logger=LoggerFactory.getLogger(DoNothingTransformer.class);
	protected Object processGET(MuleMessage message, String outputEncoding){
		logger.info("cors is added on GET");
		return message.getPayload();
	}
	 protected Object processPOST(MuleMessage message, String outputEncoding){
		 
		 
		 logger.info("cors is added on POST");
		 return message.getPayload();			 
	}	
	protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
		
		logger.info("cors is added on PUT");
		return message.getPayload();					 
	}
	protected Object processDELETE(MuleMessage message, String outputEncoding){
		
		logger.info("cors is added on DELETE");
		return message.getPayload();			 
	}
	protected Object processOPTIONS(MuleMessage message, String outputEncoding){
		logger.info("cors is added on OPTIONS");
		
	   return message.getPayload();
	   
	}
	protected Object processDefault(MuleMessage message, String outputEncoding, String method){
		logger.info("cors is added on DEFAULT");
		
		return message.getPayload();		 
	}
		

}
