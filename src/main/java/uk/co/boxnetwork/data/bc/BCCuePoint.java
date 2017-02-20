package uk.co.boxnetwork.data.bc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.co.boxnetwork.data.CuepointMetadata;
import uk.co.boxnetwork.util.GenericUtilities;

public class BCCuePoint {
	private static final Logger logger=LoggerFactory.getLogger(BCCuePoint.class);
	  private String id;
	  private String name;
	  private String type;
	  private Double time;
	  private String metadata;
	  private Boolean force_stop;
	  public BCCuePoint(){
		  
	  }
	  public BCCuePoint(uk.co.boxnetwork.data.CuePoint cuepoint, String numberOfBraks,Integer advertDuration){
		  this.name=cuepoint.getName();
		  this.type=cuepoint.getType();
		  this.time=cuepoint.getTime();
		  this.force_stop=cuepoint.getForce_stop();
		  if(cuepoint.getMetadata()!=null && cuepoint.getMetadata().getNumberOfAds()!=null){			  
				this.metadata="{\"numberOfAds\":"+cuepoint.getMetadata().getNumberOfAds()+"}";				
		  }
		  else if(numberOfBraks!=null && numberOfBraks.trim().length()>0){
			  this.metadata="{\"numberOfAds\":"+numberOfBraks+"}";			  
		  }
		  if(advertDuration!=null){
			  
		  }
		  
		  
		  		  
	  }
	  public void export(uk.co.boxnetwork.model.CuePoint cuepoint){
		  cuepoint.setName(this.name);
		  cuepoint.setType(this.type);
		  cuepoint.setTime(this.time);
		  cuepoint.setForce_stop(this.force_stop);
		  if(!GenericUtilities.isEmpty(this.metadata)){
			  com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
			  try {
				CuepointMetadata metadata = objectMapper.readValue(this.metadata, CuepointMetadata.class);
				cuepoint.setNumberOfAds(metadata.getNumberOfAds());
			} catch (Exception e) {
				logger.error(e+" while parsing the cue metadta:"+this.metadata,e);
			} 
			  
		  }
		  
	  }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getTime() {
		return time;
	}
	public void setTime(Double time) {
		this.time = time;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	public Boolean getForce_stop() {
		return force_stop;
	}
	public void setForce_stop(Boolean force_stop) {
		this.force_stop = force_stop;
	}
	  
}
