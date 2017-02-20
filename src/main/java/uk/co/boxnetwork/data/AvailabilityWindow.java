package uk.co.boxnetwork.data;

import java.util.Date;



public class AvailabilityWindow {
	
    private Long id;
 
	private Long start;
	private Long end;
	
	public AvailabilityWindow(){
	
	}
public AvailabilityWindow(uk.co.boxnetwork.model.AvailabilityWindow availability){
	
	this.id=availability.getId();
	this.start=availability.getStart();
	this.end=availability.getEnd();
	
}
public void update(uk.co.boxnetwork.model.AvailabilityWindow availability){
	availability.setStart(this.start);
	availability.setEnd(this.end);

	
}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
    public boolean valid(){
    	if(start==null || end==null)
    		return false;
    	if(start<=0 || end<=0){
    		return false;
    	}
    	return true;
    }
}
