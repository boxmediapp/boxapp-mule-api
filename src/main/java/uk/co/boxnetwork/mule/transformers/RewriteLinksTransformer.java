package uk.co.boxnetwork.mule.transformers;

import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.util.LinkReplacer;



public class RewriteLinksTransformer extends AbstractMessageTransformer{
	private static final Logger logger=LoggerFactory.getLogger(RewriteLinksTransformer.class);
	 private String selectedConfigVar="selected";
	 
	 public void setSelectedConfigVar(String selectedConfigVar) {
		this.selectedConfigVar = selectedConfigVar;
	}

	



	public static String retrieveValue(Map<String,String> selectedConfig,Map<String,String> commonConfig,String key){
		  String value=null;
		  if(selectedConfig!=null)
		      value=selectedConfig.get(key);
		  if(value!=null)
			  return value;
		  if(commonConfig!=null)
		      return commonConfig.get(key);
		  else
			  return null;
	 }
	 
	

	
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		Map<String,Object> selectedConfig=message.getInvocationProperty(selectedConfigVar);
		String toBaseURL=(String)selectedConfig.get("baseURL");
		String protocol=(String)selectedConfig.get("protocol");
		String host=(String)selectedConfig.get("host");
		String fromBaseURL=protocol+"://"+host;
		List<Map<String,String>> replaceConfigs= (List<Map<String,String>>)selectedConfig.get("replace");		
		if(replaceConfigs==null||replaceConfigs.size()==0){
			logger.info("no replace carried out  selectedConfig=["+selectedConfig+"]");
			return message.getPayload();
		}
		String payload=(String)message.getPayload();
		for(Map<String,String> replaceConfig:replaceConfigs){			
		    String from=replaceConfig.get("from");
		    String to=replaceConfig.get("to");
		    if(from.charAt(0)=='/'){
		    	from=fromBaseURL+from;
		    }
		    if(to.charAt(0)=='/'){
		    	to=toBaseURL+to;		    	
		    }
		    payload=LinkReplacer.replace(payload, from, to);		
	    }
		return payload;
	}	

}
