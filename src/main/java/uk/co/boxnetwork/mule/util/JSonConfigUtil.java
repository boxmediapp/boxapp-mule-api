package uk.co.boxnetwork.mule.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class JSonConfigUtil {
private static final Logger logger=LoggerFactory.getLogger(JSonConfigUtil.class);
	@SuppressWarnings("unchecked")
	public  static Map<String, Object> loadConfig(String configFile){
		  InputStream input=JSonConfigUtil.class.getClassLoader().getResourceAsStream(configFile);
		  if(input==null){
				throw new RuntimeException("Failed to read  configFile=["+configFile+"]");			
		  }
			try{
				
				com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				
				
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				try{
								 
					return (Map<String, Object>)objectMapper.readValue(input, Map.class);
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
	 
	private static void extendMapContent(Map<String,Object>  selectedConfig,Map<String,Object>  commonConfig){
		if(commonConfig==null)
			return;
		
		for(Map.Entry<String, Object> entry:commonConfig.entrySet()){
			if(selectedConfig.get(entry.getKey())==null){
				selectedConfig.put(entry.getKey(), entry.getValue());
			}
		}
	}
	@SuppressWarnings("unchecked")
	public static Map<String,Map<String,Object>> buildMapFromConfig(Map<String, Object> config,String selectKeyName,String lookupKeyName,String commonKeyName){
		  if(config==null){
			  logger.warn("input of buildMapFromConfig is null");
			  return null;
		  }
		  Map<String,Object> commonConfig=(Map<String,Object>)config.get(commonKeyName);
		  Map<String,Map<String,Object>> configMaps=new HashMap<String,Map<String,Object>>();		  
		  List<Map<String,Object>> configs=(List<Map<String,Object>>)config.get(selectKeyName);		  
		  for(Map<String,Object> cf:configs){
				  String kv=(String)cf.get(lookupKeyName);
				  extendMapContent(cf,commonConfig);				  
				  configMaps.put(kv, cf);
		  }
		  return configMaps;
	  }
	
}
