package uk.co.boxnetwork;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.co.boxnetwork.model.BoxChannel;
import uk.co.boxnetwork.model.BoxEpisode;

@Entity(name="box_schedule_event")
public class BoxScheduleEvent {
	@Id
	@GeneratedValue
    private Long id;

	
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "box_episode_id", nullable = true )
	private BoxEpisode boxEpisode;
	
	@Column(name="schedule_timestamp")
	private Date scheduleTimestamp;
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "box_channel_id", nullable = true )
	private BoxChannel boxChannel;

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
	
	
	
}
