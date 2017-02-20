package uk.co.boxnetwork.data;

public class CuepointMetadata {
	private String materialId;
	private String mediaType;
	private String duration;
	private Integer numberOfAds;
	
	private String artist;
	private String track;	
	private String certification;
	
	
	public CuepointMetadata(){
		
	}
	
	public CuepointMetadata(String materiaId, String mediaType, String duration, Integer numberOfAds,String artist, String track, String certification) {
		super();
		this.materialId = materiaId;
		this.mediaType = mediaType;
		this.duration = duration;
		this.numberOfAds=numberOfAds;
		this.artist=artist;
		this.track=track;
		this.certification=certification;		
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

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

		
}
