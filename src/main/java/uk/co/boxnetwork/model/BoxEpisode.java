package uk.co.boxnetwork.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name="box_episode")
public class BoxEpisode {
	@Id
	@GeneratedValue
    private Long id;

	
	private String title;
	
	
	@Column(name="programme_number")
	private String programmeNumber;
	
	
	@Column(name="created_at")
	private Date createdAt;
	
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
		
	
	@OneToMany(mappedBy="boxEpisode", fetch=FetchType.EAGER)	
	private Set<ImageSet> imageSets;
	
	@Column(name="schedule_timestamp")
	private Date scheduleTimestamp;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public void copyFrom(Episode episode){
		this.title=episode.getTitle();
		if(this.title==null|| this.title.trim().length()==0){
			this.title=episode.getName();
		}
		this.programmeNumber=episode.getCtrPrg();		
	}

	public Set<ImageSet> getImageSets() {
		return imageSets;
	}

	public void setImageSets(Set<ImageSet> imageSets) {
		this.imageSets = imageSets;
	}

	public Date getScheduleTimestamp() {
		return scheduleTimestamp;
	}

	public void setScheduleTimestamp(Date scheduleTimestamp) {
		this.scheduleTimestamp = scheduleTimestamp;
	}
	
}
