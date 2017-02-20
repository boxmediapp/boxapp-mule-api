package uk.co.boxnetwork.mule.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;


import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuleRestUtil {
	private static final Logger logger=LoggerFactory.getLogger(MuleRestUtil.class);
  public static String getPathPath(MuleMessage message){
	  String listenerPath=message.getInboundProperty("http.listener.path");
	  int ind=listenerPath.indexOf('*');
	  if(ind>=0){
		  listenerPath=listenerPath.substring(0,ind);		  		  
	  }
	  String requesturi=message.getInboundProperty("http.request.uri");
	  
	  if(requesturi.length()<=listenerPath.length()){
		  return null;		  
	  }
	  else{
		  String restpath=requesturi.substring(listenerPath.length());
		  String basepath=requesturi.substring(0,listenerPath.length());
		  if(basepath.endsWith("?")){
			  return null;
		  }
		  ind=restpath.indexOf("?");
		  if(ind!=-1){
			  restpath=restpath.substring(0,ind);
		  }
		  
			 try {
				return URLDecoder.decode(restpath,"utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e+ "while decoding restpath:"+restpath,e);
				return restpath;
			}			  		  
	  }
  }
  public static void printAllInboundProperties(MuleMessage message){
	  Set<String> keys=message.getInboundPropertyNames();
		for(String k:keys){
			logger.info("::::"+k+":"+message.getInboundProperty(k));
		}
		
  }
  public static void addCORS(MuleMessage message, String outputEncoding){
	  message.setOutboundProperty("Access-Control-Allow-Origin", "*");
      message.setOutboundProperty("Access-Control-Max-Age", "600");
	  message.setOutboundProperty("Access-Control-Allow-Methods","GET, PUT, POST, OPTIONS, DELETE");
	  message.setOutboundProperty("Access-Control-Allow-Headers","X-Requested-With, Content-Type, Accept,apikey");	  
  }
}
