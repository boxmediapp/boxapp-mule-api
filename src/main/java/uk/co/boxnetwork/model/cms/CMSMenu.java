package uk.co.boxnetwork.model.cms;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;


import uk.co.boxnetwork.model.MediaApplicationID;

@Entity(name="cms_menu")
public class CMSMenu {
	
	@Id
	@GeneratedValue
    private Long id;

	@Column(name="application_id")
	private MediaApplicationID applicationId=MediaApplicationID.MEDIA_APP;
	
	private String title;
	
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
	 
	
	@ManyToMany
	@JoinTable(
	      name="cms_menu_to_playlist",
	      joinColumns=@JoinColumn(name="cms_menu_id", referencedColumnName="id"),
	      inverseJoinColumns=@JoinColumn(name="cms_playlist_id", referencedColumnName="id"))

	private List<CMSPlaylist> playlist;
	
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
	public MediaApplicationID getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(MediaApplicationID applicationId) {
		this.applicationId = applicationId;
	}
	public List<CMSPlaylist> getPlaylist() {
		return playlist;
	}
	public void setPlaylist(List<CMSPlaylist> playlist) {
		this.playlist = playlist;
	}
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}
	
	
	
}
