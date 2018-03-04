package uk.co.boxnetwork.data.image;

import java.util.Date;


import uk.co.boxnetwork.model.BoxChannel;
import uk.co.boxnetwork.model.BoxEpisode;
import uk.co.boxnetwork.model.BoxScheduleEvent;

public class BoxScheduleEventData {
	
    private Long id;	
	private BoxEpisode boxEpisode;		
	private Date scheduleTimestamp;	
	private BoxChannel boxChannel;
	private String title;
	private String programmeNumber;
	

	 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BoxEpisode getBoxEpisode() {
		return boxEpisode;
	}
	public void setBoxEpisode(BoxEpisode boxEpisode) {
		this.boxEpisode = boxEpisode;
	}
	public Date getScheduleTimestamp() {
		return scheduleTimestamp;
	}
	public void setScheduleTimestamp(Date scheduleTimestamp) {
		this.scheduleTimestamp = scheduleTimestamp;
	}
	public BoxChannel getBoxChannel() {
		return boxChannel;
	}
	public void setBoxChannel(BoxChannel boxChannel) {
		this.boxChannel = boxChannel;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProgrammeNumber() {
		return programmeNumber;
	}
	public void setProgrammeNumber(String programmeNumber) {
		this.programmeNumber = programmeNumber;
	}
	public BoxScheduleEventData(BoxScheduleEvent scheduleEvent){
		this.id=scheduleEvent.getId();
		this.scheduleTimestamp=scheduleEvent.getScheduleTimestamp();
		this.boxChannel=scheduleEvent.getBoxChannel();
		if(scheduleEvent.getBoxEpisode()!=null){
			BoxEpisode episode=scheduleEvent.getBoxEpisode();
			
			this.programmeNumber=episode.getProgrammeNumber();
			this.title=episode.getTitle();
		}
	
	}
}
