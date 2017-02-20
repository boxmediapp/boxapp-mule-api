package uk.co.boxnetwork.mule.transformers;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.config.MapPayloadConfig;





public class MapPayloadValidator extends AbstractMessageTransformer{
	private static final Logger logger=LoggerFactory.getLogger(MapPayloadValidator.class);
	private static final String MAP_PAYLOAD_VALIDATION="MapValidationResult";
	
	public String configurationFile;
	public MapPayloadConfig config;
	public String getConfigurationFile() {
		return configurationFile;
	}
	public void setConfigurationFile(String configurationFile) {
		this.configurationFile = configurationFile;
	}
	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		Object payload=message.getPayload();
		if(payload instanceof Map){
			if(config==null)
				config=MapPayloadConfig.load(configurationFile);
			boolean validationResult=config.match((Map<String,String>)payload);
			logger.info("Validation Result validationResult=["+validationResult+"]session:"+message.getProperty(LogMessageTransformer.EVENT_NOTIFICATION_SESSION_ID,PropertyScope.SESSION));
			message.setInvocationProperty(MAP_PAYLOAD_VALIDATION, validationResult);
		}
		else{
			logger.info("Payload is not the type of Map.session:"+message.getProperty(LogMessageTransformer.EVENT_NOTIFICATION_SESSION_ID,PropertyScope.SESSION));
			message.setInvocationProperty(MAP_PAYLOAD_VALIDATION, false);			
		}
		return payload;
	}
	

}
