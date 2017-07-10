package uk.co.boxnetwork.data.bc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.co.boxnetwork.data.CuepointMetadata;
import uk.co.boxnetwork.util.GenericUtilities;

class BCCuePointMetadata{
	
	private Integer numberOfAds;
	private Integer advertDuration;
	private String artist;
	private String track;
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public Integer getNumberOfAds() {
		return numberOfAds;
	}
	public void setNumberOfAds(Integer numberOfAds) {
		this.numberOfAds = numberOfAds;
	}
	public void setNumberOfAds(String numberOfAds) {
		try{
			this.numberOfAds = Integer.valueOf(numberOfAds);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public Integer getAdvertDuration() {
		return advertDuration;
	}
	public void setAdvertDuration(Integer advertDuration) {
		this.advertDuration = advertDuration;
	}	
	
}
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
		  
		  
		  BCCuePointMetadata md=null;
		  
		  if(cuepoint.getMetadata()!=null || (numberOfBraks!=null && numberOfBraks.trim().length()>0)){
			  md=new BCCuePointMetadata();
			  if(numberOfBraks!=null && numberOfBraks.trim().length()>0){
				  md.setNumberOfAds(numberOfBraks);
			  }
			  if(cuepoint.getMetadata()!=null && cuepoint.getMetadata().getNumberOfAds()!=null){
				  md.setNumberOfAds(cuepoint.getMetadata().getNumberOfAds());
		      }
			  if(advertDuration!=null && md!=null && md.getNumberOfAds()!=null){
					 md.setAdvertDuration(advertDuration);
		      }
		  }
		  if(cuepoint.getMetadata()!=null){
			  if(cuepoint.getMetadata().getArtist()!=null && cuepoint.getMetadata().getArtist().trim().length()>0){
				  md.setArtist(cuepoint.getMetadata().getArtist().trim());
			  }
			  if(cuepoint.getMetadata().getTrack()!=null && cuepoint.getMetadata().getTrack().trim().length()>0){
				  md.setTrack(cuepoint.getMetadata().getTrack().trim());
			  }			  
		  }
		  
		  
		  if(md!=null){
			  buildMetadata(md);
		  }
		  
		  
		  		  
	  }
	  private void buildMetadata(BCCuePointMetadata md){
		  com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
		  try {
				this.metadata = objectMapper.writeValueAsString(md);
			} catch (JsonProcessingException e) {
				
				logger.error(e+" while parsing the BCCuePointMetadata",e);
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
