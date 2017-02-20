package uk.co.boxnetwork.mule.util;


import java.util.Set;

import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuMessageUtil {
	private static final Logger logger=LoggerFactory.getLogger(MenuMessageUtil.class);
 public static void inspectInboundProperties(MuleMessage message){
	 Set<String> inboundPropertyNames = message.getInboundPropertyNames();
	    
	    for (String inboundPropertyName : inboundPropertyNames) {
	        Object inboundProperty = message.getInboundProperty(inboundPropertyName);
	        if(inboundProperty==null){
	        	logger.info(" inboundProperty["+inboundPropertyName+"]=null");
	        }
	        else{
	        	logger.info(" inboundProperty["+inboundPropertyName+"]="+inboundProperty+":type:"+inboundProperty.getClass().getName());
	        }
	    }
 }
}
