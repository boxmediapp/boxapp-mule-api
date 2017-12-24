package uk.co.boxnetwork.mule.transformers;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.util.ConfigSelector;



public class DoNothingTransformer extends BoxRestTransformer{
	private static final Logger logger=LoggerFactory.getLogger(DoNothingTransformer.class);
	
	
	protected boolean checkGETAccess(BoxOperator operator){
	    return true;		    
	}
	protected boolean checkPOSTAccess(BoxOperator operator){
	   return true;				 
	}	
	protected boolean checkPUTAccess(BoxOperator operator){
	
	   return true;	  
	}
	protected boolean checkDELETEAccess(BoxOperator operator){			
	   return true;
	   
	}	
	protected boolean checkPATCHAccess(BoxOperator operator){	   					
	   return true;		   
	}

	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator, String outputEncoding){
		logger.info("cors is added on GET");
		return message.getPayload();
	}
	@Override
	 protected Object processPOST(MuleMessage message, BoxOperator operator, String outputEncoding){
		 
		 
		 logger.info("cors is added on POST");
		 return message.getPayload();			 
	}
	@Override
	protected Object processPUT(MuleMessage message, BoxOperator operator, String outputEncoding) throws Exception{
		
		logger.info("cors is added on PUT");
		return message.getPayload();					 
	}
	@Override
	protected Object processDELETE(MuleMessage message, BoxOperator operator, String outputEncoding){
		
		logger.info("cors is added on DELETE");
		return message.getPayload();			 
	}
	
	@Override
	protected Object processOPTIONS(MuleMessage message, BoxOperator operator, String outputEncoding){
		logger.info("cors is added on OPTIONS");
		
	   return message.getPayload();
	   
	   
	}
	@Override
	protected Object processDefault(MuleMessage message,BoxOperator operator, String outputEncoding, String method){
		logger.info("cors is added on DEFAULT");
		
		return message.getPayload();		 
	}
		

}
