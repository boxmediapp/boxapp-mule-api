package uk.co.boxnetwork.mule.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.mule.transformers.MapPayloadValidator;



public class MapPayloadConfig {
	private static final Logger logger=LoggerFactory.getLogger(MapPayloadConfig.class);
    public static MapPayloadConfig load(String configFile){
	  InputStream input=MapPayloadValidator.class.getClassLoader().getResourceAsStream(configFile);
	  if(input==null){
			throw new RuntimeException("Failed to read  configFile=["+configFile+"]");
	  }
		try{
			
			com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
			
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			try{
			     return objectMapper.readValue(input, MapPayloadConfig.class);
			}
			catch(Exception e){
				
				throw new RuntimeException("Failed to load json data from configFile=["+configFile+"]",e);
			}
		}
		finally{
			try {
				input.close();
			} catch (IOException e) {
				logger.warn("while closing the resoure input stream:"+e,e);				
			}
		}
  }
	
  private List<KeyValueRule> rules;

public List<KeyValueRule> getRules() {
	return rules;
}

public void setRules(List<KeyValueRule> rules) {
	this.rules = rules;
}
 public boolean match(Map<String, String> payload){
	for(KeyValueRule rule:rules){
		if(rule.match(payload)){
			return true;
		}
	}
	return false;
	 
 }
 
}
