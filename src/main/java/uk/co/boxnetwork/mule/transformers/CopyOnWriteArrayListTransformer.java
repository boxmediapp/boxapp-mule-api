package uk.co.boxnetwork.mule.transformers;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.payload.MultiplePayloadsInputStream;




public class CopyOnWriteArrayListTransformer  extends AbstractMessageTransformer{
    
	private static final Logger logger=LoggerFactory.getLogger(CopyOnWriteArrayListTransformer.class);
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		Object payload=message.getPayload();
		if(payload==null){
		   logger.warn(" Input payload is null");
			return payload;
		}			
		if(payload instanceof  CopyOnWriteArrayList){			
			return createMultiplePayloadsInputStream((CopyOnWriteArrayList)payload,outputEncoding,message);
			
		}
		else if(payload instanceof String){
			logger.info("executed one single branch:the type is string:"+payload);
			return payload;
		}
		else if(payload instanceof InputStream){
			logger.info("executed one single branch:the type is inputstream"+payload);
			return createPayloadForInputStream((InputStream)payload,outputEncoding,message);
		}
		else{
			
			Message errorMEssage = MessageFactory.createStaticMessage("none of the branches are executed:"+payload.getClass().getName()+"string value:"+payload);
	        throw new TransformerException(errorMEssage, this);
			
		    	
		}
	}
	public Object createPayloadForInputStream(InputStream payload,String outputEncoding,MuleMessage message) throws TransformerException{
	   return payload;
	   
	}
	public Object createMultiplePayloadsInputStream(CopyOnWriteArrayList payload,String outputEncoding,MuleMessage message) throws TransformerException{
		return new MultiplePayloadsInputStream(getInputStreamListFromCopyOnWriteArrayList(payload,outputEncoding));		
	}
	public List<InputStream> getInputStreamListFromCopyOnWriteArrayList(CopyOnWriteArrayList payload,String outputEncoding){
		
		List<InputStream> streams=new ArrayList<InputStream>();			
		for(Object obj:payload){
    		 if(obj instanceof InputStream){	    			 
    			 streams.add((InputStream)obj);
    		 }
    		 else if(obj instanceof String){    			 
    			 String ch=(String)obj;
    			 ch=ch.trim();
    			 if(ch.length()==0){
    				 logger.info("zero length string is one of the payloads, ignored");
    				
    			 }
    			 else{    				     				  
    				   logger.info("One of the payload is type of string converting to stream ch=["+ch+"]"); 
    				   try {
							streams.add(new ByteArrayInputStream(ch.getBytes(outputEncoding)));
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException("Cannot convert string to inputstream:"+obj,e);
						}
    			 }
    		 }
    		 else{
    			logger.error("Unexpected type:"+obj.getClass().getName());
    		    throw new RuntimeException("Unexpected type, cannot Cannot convert to inputstream:"+obj.getClass().getName());
    		 }
    	 }
		return streams;
		
	
	}
    	
}
