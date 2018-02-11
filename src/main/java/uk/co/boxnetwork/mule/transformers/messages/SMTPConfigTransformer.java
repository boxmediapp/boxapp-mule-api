package uk.co.boxnetwork.mule.transformers.messages;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.STMPEmailService;
import uk.co.boxnetwork.model.SMTPConfig;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class SMTPConfigTransformer extends BoxRestTransformer{	
	
	@Autowired
	private STMPEmailService smtpEmailService;
	
	protected boolean checkGETAccess(BoxOperator operator){
		return operator.checkAdminAccess();
	}
@Override
protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){
	String configid=MuleRestUtil.getPathPath(message);
	if(STMPEmailService.isValidConfigId(configid)){
		SMTPConfig smtpConfig= smtpEmailService.getSMTPConfig();
		if(smtpConfig==null){
			smtpConfig=new SMTPConfig();
		}
		return smtpConfig;
	}
	else{
		return returnError("Invalid configID", message);
	}
 							
}


@Override
protected boolean checkPOSTAccess(BoxOperator operator){
	 return operator.checkAdminAccess();				
}

@Override
protected Object processPOST(MuleMessage message, BoxOperator operator, String outputEncoding) {
	String configid=MuleRestUtil.getPathPath(message);
	if(STMPEmailService.isValidConfigId(configid)){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		  objectMapper.setSerializationInclusion(Include.NON_NULL);
		  try{
			  	String requestInJson = (String)message.getPayloadAsString();							
			  	SMTPConfig smtpConfig = objectMapper.readValue(requestInJson, SMTPConfig.class);
			  	
			  	smtpEmailService.updateSMTPConfig(smtpConfig);
			  	return smtpConfig;
		  }
		  catch(Exception e){
			  logger.error(e+"while updating the smtpconfig",e);
			  return returnError(e.toString(),message);
		  }
		  		
	}
	else{
		return returnError("Invalid configID", message);
	} 
	   	 	   
}
	

}
