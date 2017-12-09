package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="box_channel")
public class BoxChannel {
	
	public BoxChannel() {
		
	}
	public BoxChannel(String channelId, String channelName) {
		super();
		this.channelId = channelId;
		this.channelName = channelName;
	}


	@Id
	@Column(name="channel_id")
    private String channelId;
  
	
	@Column(name="channel_name")
	private String channelName;


	public String getChannelId() {
		return channelId;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getChannelName() {
		return channelName;
	}


	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	
}
