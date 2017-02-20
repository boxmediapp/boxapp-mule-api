package uk.co.boxnetwork.data;


import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.model.AppConfig;

public class UpdatePraram{ 
static final protected Logger logger=LoggerFactory.getLogger(UpdatePraram.class);
private String updateType=null;



   public UpdatePraram(MuleMessage message, AppConfig appConfig){
			
	ParameterMap queryparams=message.getInboundProperty("http.query.params");
	if(queryparams!=null){				
			this.updateType=queryparams.get("update");					
	}
   }
   public boolean isSwitchEpisodeSeries(){
		return this.updateType!=null && this.updateType.equals("switchseries");
   }
   public boolean isSwitchEpisodeSeriesGroup(){
		return this.updateType!=null && this.updateType.equals("switchseriesgroup");
   }
}
