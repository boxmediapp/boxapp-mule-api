package uk.co.boxnetwork.mule.config;

import java.util.Map;

public class KeyValueRuleItem {
	 private String key;
  	 private Boolean required;
     private String values[];
     
     public String getKey() {
 		return key;
 	 }
 	 public void setKey(String key) {
 		this.key = key;
 	 }
 	 public Boolean getRequired() {
 		return required;
 	 }
 	 public void setRequired(Boolean required) {
 		this.required = required;
 	 }
 	  	
 	 public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}
	public boolean match(Map<String, String> payload){
		String value=payload.get(key);
		if(required){
			if(value==null)
		        return false;
		}
		if(values!=null){
			if(value==null){
			    return true;
			}
			for(String v:values){
				if(value.equals(v)){
					return true;
				}
			}
			return false;
		}
		return true;		
	}


}
