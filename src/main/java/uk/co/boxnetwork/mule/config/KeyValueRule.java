package uk.co.boxnetwork.mule.config;

import java.util.List;
import java.util.Map;





public class KeyValueRule {
private List<KeyValueRuleItem> input;








public List<KeyValueRuleItem> getInput() {
	return input;
}








public void setInput(List<KeyValueRuleItem> input) {
	this.input = input;
}



private boolean isKeyDefined(String key){
	for(KeyValueRuleItem item:input){
		 if(item.getKey().equals(key))
			 return true;
	}
	
	  return false;
}




public boolean match(Map<String, String> payload){
	for(String key:payload.keySet()){
		if(!isKeyDefined(key)){
			return false;
		}
	}
	for(KeyValueRuleItem item:input){
		 if(!item.match(payload))
			 return false;
	  }
	  
	  return true;
}
}
