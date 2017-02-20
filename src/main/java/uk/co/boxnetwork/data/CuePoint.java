package uk.co.boxnetwork.data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.co.boxnetwork.model.Episode;

public class CuePoint implements Comparable<CuePoint>{

	private Long id;
	
	private String name;
	
	private String type;
	
	private Double time;
	
	
	
	private String code;
	
	private Boolean force_stop;
	private CuepointMetadata metadata;
	
	
	public CuePoint(){
		
	}
    public CuePoint(uk.co.boxnetwork.model.CuePoint cuePoint){
    	
    	this.name=cuePoint.getName();
    	this.type=cuePoint.getType();
    	this.time=cuePoint.getTime();    
    	this.code=cuePoint.getCode();    
    	this.id=cuePoint.getId();
    	this.metadata=cuePoint.creatMetadata();
    	
    }
    public void update(uk.co.boxnetwork.model.CuePoint cuePoint){
    	cuePoint.setName(this.name);
    	cuePoint.setType(this.type);
    	cuePoint.setTime(this.time);
    	cuePoint.setCode(this.code);
    	cuePoint.receiveMetadata(this.metadata);    		
    	
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

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getForce_stop() {
		return force_stop;
	}

	public void setForce_stop(Boolean force_stop) {
		this.force_stop = force_stop;
	}
	@Override
	public int compareTo(CuePoint o) {
		return (int)(this.time-o.getTime());
	}
	public void setMetadata(CuepointMetadata metadata) {
		this.metadata = metadata;
	}
	public CuepointMetadata getMetadata() {
		return metadata;
	}
	
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "CuePoint [name=" + name + ", type=" + type + ", time=" + time + ", code=" + code + ", force_stop="
				+ force_stop + ", metadata=" + metadata + "]";
	}
	
	
	
		
}
