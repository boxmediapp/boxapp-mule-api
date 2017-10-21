package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="image_set")
public class ImageSet {
	@Id
	@GeneratedValue
    private Long id;
		
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
		
	@Column(name="created_at")
	private Date createdAt;
	
	private String tags;
	
	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn( name = "episode_id", nullable = false )
	private Episode episode;	
}
