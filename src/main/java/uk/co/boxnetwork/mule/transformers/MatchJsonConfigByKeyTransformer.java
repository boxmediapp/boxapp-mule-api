package uk.co.boxnetwork.mule.transformers;



import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.util.ConfigSelector;





public class MatchJsonConfigByKeyTransformer extends AbstractMessageTransformer{
	
	private static final Logger logger=LoggerFactory.getLogger(MatchJsonConfigByKeyTransformer.class);
	
	public enum KeyLocation {	
		header,
		query_parameter,
    }
	public enum OutputLocation{
		payload,
		invocation
	}
	
	private ConfigSelector configSelector;
	
    
	

	public void setConfigSelector(ConfigSelector configSelector) {
		this.configSelector = configSelector;
	}

	private KeyLocation keyLocation=KeyLocation.header;
    private String keyVariable="apikey";
    
    private OutputLocation outputLocation= OutputLocation.invocation;
    
	private String outputVar="selected";
	
	public void setOutputVar(String outputVar) {
		this.outputVar = outputVar;
	}

	public void setOutputLocation(OutputLocation outputLocation) {
		this.outputLocation = outputLocation;
	}

	public void setKeyLocation(KeyLocation keyLocation) {
		this.keyLocation = keyLocation;
	}
	public void setKeyVariable(String keyVariable) {
		this.keyVariable = keyVariable;
	}
	
	private String retrieveKeyValue(MuleMessage message){
		Map<String,String> maps=null;;
		switch(keyLocation){
		case header:
			      maps= message.getInboundProperty("http.headers");
			      break;
		case query_parameter:
			      maps=message.getInvocationProperty("http.query.params");
			      break;
		}
		if(maps==null){
			return null;			
		}
		return maps.get(keyVariable);
	}
	
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		 String keyValue=retrieveKeyValue(message);
		
		 Map<String,Object> selectedConfig=configSelector.selectConfig(keyValue);
		 if(selectedConfig==null){
			 selectedConfig=configSelector.selectConfig("default");			 
		 }
		 
		logger.info("return selectedConfig=["+selectedConfig+"]for keyValue=["+keyValue+"]");
		
		if(outputLocation==OutputLocation.payload){
			return selectedConfig;
		}
		else if(outputLocation==OutputLocation.invocation){						
			message.setInvocationProperty(outputVar, selectedConfig);
			
			return message.getPayload();
		}
		else
			throw new TransformerException(this,new Exception("outputLocation is not set to valid value"));
	}
}
