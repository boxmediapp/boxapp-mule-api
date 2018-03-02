package uk.co.boxnetwork.data.cms;

import uk.co.boxnetwork.model.cms.CMSEpisode;

public class CMSPlayListEpisodeData {
	private String id;	
	private String title;	
	private String description;
	private String long_description;		
	
	private String contenttype;
	private String txchannel;
	private String seriesnumber;
	private String seriestitle;
	  
	private String certificationtype;
	private String programmetitle;
	private String programmesynopsis;
	private String warningtext;
	private String episodenumber;
	private String drm;
	private String excludeddevices;
	private String sponsorship;
	private String artist;
	private String productplacement;
	private String numberOfAdsInPreRoll;
	 	
	private Long duration;
	private String economics;
	
	  
	private String availability_start;
	private String availability_end;
	
	private String thumbnail;
	private String poster;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLong_description() {
		return long_description;
	}
	public void setLong_description(String long_description) {
		this.long_description = long_description;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	public String getTxchannel() {
		return txchannel;
	}
	public void setTxchannel(String txchannel) {
		this.txchannel = txchannel;
	}
	public String getSeriesnumber() {
		return seriesnumber;
	}
	public void setSeriesnumber(String seriesnumber) {
		this.seriesnumber = seriesnumber;
	}
	public String getSeriestitle() {
		return seriestitle;
	}
	public void setSeriestitle(String seriestitle) {
		this.seriestitle = seriestitle;
	}
	public String getCertificationtype() {
		return certificationtype;
	}
	public void setCertificationtype(String certificationtype) {
		this.certificationtype = certificationtype;
	}
	public String getProgrammetitle() {
		return programmetitle;
	}
	public void setProgrammetitle(String programmetitle) {
		this.programmetitle = programmetitle;
	}
	public String getProgrammesynopsis() {
		return programmesynopsis;
	}
	public void setProgrammesynopsis(String programmesynopsis) {
		this.programmesynopsis = programmesynopsis;
	}
	public String getWarningtext() {
		return warningtext;
	}
	public void setWarningtext(String warningtext) {
		this.warningtext = warningtext;
	}
	public String getEpisodenumber() {
		return episodenumber;
	}
	public void setEpisodenumber(String episodenumber) {
		this.episodenumber = episodenumber;
	}
	public String getDrm() {
		return drm;
	}
	public void setDrm(String drm) {
		this.drm = drm;
	}
	public String getExcludeddevices() {
		return excludeddevices;
	}
	public void setExcludeddevices(String excludeddevices) {
		this.excludeddevices = excludeddevices;
	}
	public String getSponsorship() {
		return sponsorship;
	}
	public void setSponsorship(String sponsorship) {
		this.sponsorship = sponsorship;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getProductplacement() {
		return productplacement;
	}
	public void setProductplacement(String productplacement) {
		this.productplacement = productplacement;
	}
	public String getNumberOfAdsInPreRoll() {
		return numberOfAdsInPreRoll;
	}
	public void setNumberOfAdsInPreRoll(String numberOfAdsInPreRoll) {
		this.numberOfAdsInPreRoll = numberOfAdsInPreRoll;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public String getEconomics() {
		return economics;
	}
	public void setEconomics(String economics) {
		this.economics = economics;
	}
	public String getAvailability_start() {
		return availability_start;
	}
	public void setAvailability_start(String availability_start) {
		this.availability_start = availability_start;
	}
	public String getAvailability_end() {
		return availability_end;
	}
	public void setAvailability_end(String availability_end) {
		this.availability_end = availability_end;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public void importData(CMSEpisode episode){
		this.id=episode.getId();	
		this.title=	episode.getTitle();
		this.description=episode.getDescription();
		this.long_description=episode.getLong_description();
		this.contenttype=episode.getContenttype();
		this.txchannel=episode.getTxchannel();
		this.seriesnumber=episode.getSeriesnumber();
		this.seriestitle=episode.getSeriestitle();		  
		this.certificationtype=episode.getCertificationtype();
		this.programmetitle=episode.getProgrammetitle();
		this.programmesynopsis=episode.getProgrammesynopsis();
		this.warningtext=episode.getWarningtext();
		this.episodenumber=episode.getEpisodenumber();
		this.drm=episode.getDrm();
		this.excludeddevices=episode.getExcludeddevices();
		this.sponsorship=episode.getSponsorship();
		this.artist=episode.getArtist();
		this.productplacement=episode.getProductplacement();
		this.numberOfAdsInPreRoll=episode.getNumberOfAdsInPreRoll();		
		this.duration=episode.getDuration();
		this.economics=episode.getEconomics();
		this.availability_start=episode.getAvailability_start();
		this.availability_end=episode.getAvailability_end();		
		this.thumbnail=episode.getThumbnail();
		this.poster=episode.getPoster();
	}
	
}
