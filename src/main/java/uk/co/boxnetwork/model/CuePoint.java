package uk.co.boxnetwork.model;


import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import uk.co.boxnetwork.data.CuepointMetadata;
import uk.co.boxnetwork.util.GenericUtilities;

@Entity(name="cue_points")
public class CuePoint {
	@Id
	@GeneratedValue
    private Long id;
    
	private String name;
	
	private String type;
	
	private Double time;
	
	private String metadata;
	
	private String code;
	
	private Boolean force_stop;
	
	private String materiaId;
	private String mediaType;
	private String duration;
	private Integer numberOfAds;
	
	private String artist;
	private String track;
	
	private String certification;
	
	 
	
	
	@ManyToOne
    @JoinColumn(name="episode_id") 
    private Episode episode;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Episode getEpisode() {
		return episode;
	}

	public void setEpisode(Episode episode) {
		this.episode = episode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMateriaId() {
		return materiaId;
	}

	public void setMateriaId(String materiaId) {
		this.materiaId = materiaId;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getNumberOfAds() {
		return numberOfAds;
	}

	public void setNumberOfAds(Integer numberOfAds) {
		this.numberOfAds = numberOfAds;
	}

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

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}
	
	public boolean metadataNotEmpty(){
		return this.duration!=null || this.materiaId!=null || this.mediaType!=null || this.numberOfAds!=null || this.artist!=null || this.track!=null|| this.certification!=null;
	}
	public CuepointMetadata creatMetadata(){
		if(metadataNotEmpty()){
			return new CuepointMetadata(this.materiaId, this.mediaType, this.duration, this.numberOfAds, this.artist, this.track, this.certification);
		}
		else
			return null;
		
	}
	public void receiveMetadata(CuepointMetadata cueMetadata){
		if(cueMetadata==null){
			return;
		}
		this.duration=cueMetadata.getDuration();
		this.materiaId=cueMetadata.getMaterialId();
		this.mediaType=cueMetadata.getMediaType();
		this.numberOfAds=cueMetadata.getNumberOfAds();
		this.artist=cueMetadata.getArtist();
		this.track=cueMetadata.getTrack();
		this.certification=cueMetadata.getCertification();
	}
	
	public void makeSoundMouseFriendy(){
		this.name=GenericUtilities.makeSoundMouseFriendy(name);
		this.track=GenericUtilities.makeSoundMouseFriendy(track);
		this.artist=GenericUtilities.makeSoundMouseFriendy(artist);
		this.duration=GenericUtilities.makeDurationMouseFriendy(duration);
	}
	
}
