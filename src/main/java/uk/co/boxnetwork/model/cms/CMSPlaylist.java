package uk.co.boxnetwork.model.cms;


import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity(name="cms_playlist")
public class CMSPlaylist {
	@Id
	private String id;
	
	private String title;
	
	@Column(name="synced_at")
	private Date syncedAt;

	
	@ManyToMany
	@JoinTable(
	      name="cms_playlist_to_episode",
	      joinColumns=@JoinColumn(name="cms_playlist_id", referencedColumnName="id"),
	      inverseJoinColumns=@JoinColumn(name="cms_episode_id", referencedColumnName="id"))

	private List<CMSEpisode> episodes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<CMSEpisode> getEpisodes() {
		return episodes;
	}
	public void setEpisodes(List<CMSEpisode> episodes) {
		this.episodes = episodes;
	}
	public Date getSyncedAt() {
		return syncedAt;
	}
	public void setSyncedAt(Date syncedAt) {
		this.syncedAt = syncedAt;
	}
	
	
}
