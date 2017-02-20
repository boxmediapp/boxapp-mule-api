package uk.co.boxnetwork.mule.util;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigSelector {
	private static final Logger logger=LoggerFactory.getLogger(ConfigSelector.class);
	  private Map<String,Map<String,Object>> configMap;	  
	  
	  public void setConfig(String configFile ){
		  Map<String, Object> config=JSonConfigUtil.loadConfig(configFile);		  
		  configMap=JSonConfigUtil.buildMapFromConfig(config,"select","key","common");		  
		  logger.info("loaded the configuration configMap=["+configMap+"]");
	  }
	  public Map<String,Object> selectConfig(String key){
		  Map<String,Object> ret= configMap.get(key);		  
		  return ret;
	  }
	  
	
   public String toString(){
	   return String.valueOf(configMap);
   }
	  
}
