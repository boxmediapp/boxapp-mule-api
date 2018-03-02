package uk.co.boxnetwork.model.cms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import uk.co.boxnetwork.data.bc.BCVideoData;

@Entity(name="cms_episode")
public class CMSEpisode {
	@Id
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
	
	
	@Column(name="synced_at")
	private Date syncedAt;
	
	
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
	
	
	
	public Date getSyncedAt() {
		return syncedAt;
	}
	public void setSyncedAt(Date syncedAt) {
		this.syncedAt = syncedAt;
	}
	public void importFrom(BCVideoData bcVideoData){
		this.id=bcVideoData.getId();	
		this.title=bcVideoData.getName();
		this.description=bcVideoData.getDescription();		
		this.long_description=bcVideoData.getLong_description();
		if(bcVideoData.getCustom_fields()!=null){
			this.contenttype=bcVideoData.getCustom_fields().getContenttype();
			this.txchannel=bcVideoData.getCustom_fields().getTxchannel();
			this.seriesnumber=bcVideoData.getCustom_fields().getSeriesnumber();
			this.seriestitle=bcVideoData.getCustom_fields().getSeriestitle();
			this.certificationtype=bcVideoData.getCustom_fields().getCertificationtype();
			this.programmetitle=bcVideoData.getCustom_fields().getProgrammetitle();
			this.programmesynopsis=bcVideoData.getCustom_fields().getProgrammesynopsis();
			this.warningtext=bcVideoData.getCustom_fields().getWarningtext();
			this.episodenumber=bcVideoData.getCustom_fields().getEpisodenumber();
			this.drm=bcVideoData.getCustom_fields().getDrm();
			this.excludeddevices=bcVideoData.getCustom_fields().getExcludeddevices();
			this.sponsorship=bcVideoData.getCustom_fields().getSponsorship();
			this.artist=bcVideoData.getCustom_fields().getArtist();
			this.productplacement=bcVideoData.getCustom_fields().getProductplacement();
			this.numberOfAdsInPreRoll=bcVideoData.getCustom_fields().getNumberOfAdsInPreRoll();
		}
		if(bcVideoData.getDuration()!=null){
			this.duration=Long.valueOf(bcVideoData.getDuration());
		}
		
		this.economics=bcVideoData.getEconomics();
		
		if(bcVideoData.getSchedule()!=null){
			this.availability_start=bcVideoData.getSchedule().getStarts_at();
			this.availability_end=bcVideoData.getSchedule().getEnds_at();
		}
		if(bcVideoData.getImages()!=null){
			if(bcVideoData.getImages().getThumbnail()!=null){
				this.thumbnail=bcVideoData.getImages().getThumbnail().getSrc();
			}
			if(bcVideoData.getImages().getPoster()!=null){
				this.poster=bcVideoData.getImages().getPoster().getSrc();
			}
			
		}		
	}
	
	
}
