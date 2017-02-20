package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class ClientDeciceTransformer  extends BoxRestTransformer{
	
	@Autowired
	BoxMedataRepository boxMedataRepository;
			
	@Override
	protected  Object processGET(MuleMessage message, String outputEncoding){					
		    List<uk.co.boxnetwork.model.ClientDevice> devices=boxMedataRepository.getAllClientDevices();		    
			return devices;
	}      
	@Override
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
		String device=MuleRestUtil.getPathPath(message);
		if(device==null || device.length()==0){
			logger.info("device is not provided to delete");
			return returnError("Do not support delete all device",message);
		}
		else{
			logger.info("receive to delete request device="+device);
			return boxMedataRepository.removeClientDevice(device);
		}			 
	}
	protected Object processPOST(MuleMessage message, String outputEncoding){
		try{	
    		    String deviceInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****Posted a new device:"+deviceInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   uk.co.boxnetwork.model.ClientDevice device = objectMapper.readValue(deviceInJson, uk.co.boxnetwork.model.ClientDevice.class);
			   boxMedataRepository.saveClientDevice(device.getName());
			   return device;
		}
		catch(Exception e){
			throw new RuntimeException("proesing post:"+e,e);    			
		}
    }
   
}
