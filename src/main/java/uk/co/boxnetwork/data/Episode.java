package uk.co.boxnetwork.data;



import java.util.ArrayList;

import java.util.Collections;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.model.AdSuport;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.CertType;
import uk.co.boxnetwork.model.ComplianceInformation;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.ProgrammeContentType;
import uk.co.boxnetwork.util.GenericUtilities;






public class Episode {
	private static final Logger logger=LoggerFactory.getLogger(Episode.class);
	
    private Long id;

	private String  title;
	private String  name;
	
	
	
	private String assetId;
	
	
	private String programmeNumber;
	
	
		
	private Integer number;
	
	
	private String synopsis;
	
	
	
	private String materialId;
	
	
	private CertType certType;
	
	
	private String warningText;
	
	
	private String tags[];
	
	
	private AdSuport adsupport;
	
	
	private Date startDate;
	
	
	private Date endDate;
	
	
	
	
	private Series series;
	
	
	private String brightcoveId;
	
	
	private String ingestProfile;
	
	
	private String ingestSource;
	
	
	private String txChannel;
	
	
	
	ProgrammeContentType contentType;
	
	private EpisodeStatus episodeStatus;
	
	
	private List<ScheduleEvent> scheduleEvents=new ArrayList<ScheduleEvent>();
	
	
	private Date lastModifiedAt;
	
	
	
	private Date createdAt;
	
	
	private String numberOfAdsPerBreak;
	
	private Set<ComplianceInformation> ComplianceInformations;
	
	
	private List<CuePoint> cuePoints=new ArrayList<CuePoint>();
	private List<AvailabilityWindow> availabilities=new ArrayList<AvailabilityWindow>();
	
	private Double durationScheduled;
	private Double durationUploaded;;
	

	 private String showType;
	
	 private String prAuk;
	 
	 private String[] excludeddevices;
	 
	 private String geoAllowedCountries;
	 
	 private String imageURL;
	 
	 private String supplier; 
	 
	 private Long firstTXDate;
	 private Long recordedAt;
	 
	 private Integer episodeSequenceNumber;
	 
	 private String editorNotes;
	 
	 private  CurrentAvailabilityStatus currentAvailabilityStatus=CurrentAvailabilityStatus.AVAILABLE;
	 private  RequiredFieldsStatus requiredFieldsStatus;
	 private  String requiredFieldsMissing;
	 
	 
	 
	

	public String getRequiredFieldsMissing() {
		return requiredFieldsMissing;
	}


	public void setRequiredFieldsMissing(String requiredFieldsMissing) {
		this.requiredFieldsMissing = requiredFieldsMissing;
	}


	public RequiredFieldsStatus getRequiredFieldsStatus() {
		return requiredFieldsStatus;
	}


	public void setRequiredFieldsStatus(RequiredFieldsStatus requiredFieldsStatus) {
		this.requiredFieldsStatus = requiredFieldsStatus;
	}


	public CurrentAvailabilityStatus getCurrentAvailabilityStatus() {
		return currentAvailabilityStatus;
	}


	public void setCurrentAvailabilityStatus(CurrentAvailabilityStatus currentAvailabilityStatus) {
		this.currentAvailabilityStatus = currentAvailabilityStatus;
	}


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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAssetId() {
		return assetId;
	}


	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}


	

	public Integer getNumber() {
		return number;
	}


	public void setNumber(Integer number) {
		this.number = number;
	}


	public String getSynopsis() {
		return synopsis;
	}


	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}


	public String getMaterialId() {
		return materialId;
	}


	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}


	public CertType getCertType() {
		return certType;
	}


	public void setCertType(CertType certType) {
		this.certType = certType;
	}


	public String getWarningText() {
		return warningText;
	}


	public void setWarningText(String warningText) {
		this.warningText = warningText;
	}


	

	public AdSuport getAdsupport() {
		return adsupport;
	}


	public void setAdsupport(AdSuport adsupport) {
		this.adsupport = adsupport;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Series getSeries() {
		return series;
	}


	public void setSeries(Series series) {
		this.series = series;
	}


	

	public String getEditorNotes() {
		return editorNotes;
	}


	public void setEditorNotes(String editorNotes) {
		this.editorNotes = editorNotes;
	}


	public List<ScheduleEvent> getScheduleEvents() {
		return scheduleEvents;
	}


	public void setScheduleEvents(List<ScheduleEvent> scheduleEvents) {		
		this.scheduleEvents = scheduleEvents;
	}

   public String getBrightcoveId() {
		return brightcoveId;
	}


	public void setBrightcoveId(String brightcoveId) {
		this.brightcoveId = brightcoveId;
	}


public Long getRecordedAt() {
		return recordedAt;
	}


	public void setRecordedAt(Long recordedAt) {
		this.recordedAt = recordedAt;
	}


public Episode(){
	   
   }
	public Episode(uk.co.boxnetwork.model.Episode episode,List<uk.co.boxnetwork.model.ScheduleEvent> scheduleEvents) {
		super();
		this.id = episode.getId();
		this.title = episode.getTitle();
		this.name = episode.getName();
		this.assetId = episode.getAssetId();
		this.programmeNumber = episode.getCtrPrg();
		this.number = episode.getNumber();
		this.synopsis = episode.getSynopsis();
		this.materialId = episode.getMaterialId();
		this.certType = episode.getCertType();
		this.warningText = episode.getWarningText();
		this.contentType=episode.getContentType();
		this.durationScheduled=episode.getDurationScheduled();
		this.showType=episode.getShowType();
		this.prAuk=episode.getPrAuk();
		this.numberOfAdsPerBreak=episode.getNumberOfAdsPerBreak();
		
		this.tags=GenericUtilities.commandDelimitedToArray(episode.getTags());
		this.excludeddevices=GenericUtilities.commandDelimitedToArray(episode.getExcludeddevices());
		
		this.adsupport = episode.getAdsupport();
		this.startDate = episode.getStartDate();
		this.endDate = episode.getEndDate();
		this.brightcoveId=episode.getBrightcoveId();
		if(episode.getSeries()!=null){
			this.series = new Series(episode.getSeries());
		}
		
		this.lastModifiedAt=episode.getLastModifiedAt();
		this.createdAt=episode.getCreatedAt();
		
		this.ingestProfile=episode.getIngestProfile();
		this.ingestSource=episode.getIngestSource();
		this.txChannel=episode.getTxChannel();
		this.recordedAt=episode.getRecordedAt();
		this.episodeStatus=episode.getEpisodeStatus();
		
		this.imageURL=episode.getImageURL();
		this.supplier=episode.getSupplier();
		this.firstTXDate=episode.getFirstTXDate();
		this.episodeSequenceNumber=episode.getEpisodeSequenceNumber();
		this.durationUploaded=episode.getDurationUploaded();
		this.editorNotes=episode.getEditorNotes();
		
		this.setComplianceInformations(episode.getComplianceInformations());
		if(episode.getCuePoints()!=null){
			for(uk.co.boxnetwork.model.CuePoint cuep:episode.getCuePoints()){
				if(cuep.getTime()!=null){
					this.addCuePoint(new CuePoint(cuep));
				}
			}
		}
		if(episode.getAvailabilities()!=null){
			for(uk.co.boxnetwork.model.AvailabilityWindow availabilityWindow:episode.getAvailabilities()){
				this.addAvailability(new AvailabilityWindow(availabilityWindow));
			}
		}
		if(scheduleEvents!=null){
			for(uk.co.boxnetwork.model.ScheduleEvent evt:scheduleEvents){
				addScheduleEvent(new ScheduleEvent(evt));			
			}
		}
		this.geoAllowedCountries=episode.getGeoAllowedCountries();
		
		
		uk.co.boxnetwork.model.AvailabilityWindow currentEffectiveAvailability=GenericUtilities.calculateEffectiveAvailabilityWindow(episode, scheduleEvents);
		if(currentEffectiveAvailability!=null){
			Date now=new Date();
			long currentTime=now.getTime();
			if(currentEffectiveAvailability.getStart()!=null && currentTime<currentEffectiveAvailability.getStart()){
				this.currentAvailabilityStatus=CurrentAvailabilityStatus.NOT_AVAILABLE;
			}
			else if(currentEffectiveAvailability.getEnd()!=null && currentTime>currentEffectiveAvailability.getEnd()){
				this.currentAvailabilityStatus=CurrentAvailabilityStatus.NOT_AVAILABLE;
			}
			else{
				this.currentAvailabilityStatus=CurrentAvailabilityStatus.AVAILABLE;
			}
		}
	}
	public void update(uk.co.boxnetwork.model.Episode episode) {		
		episode.setTitle(this.title);		
		episode.setName(this.name);
		episode.setAssetId(this.assetId);
		episode.setCtrPrg(this.programmeNumber);
		episode.setNumber(this.number);
		episode.setSynopsis(this.synopsis);
		episode.setMaterialId(this.materialId);
		episode.setContentType(this.contentType);
		episode.setDurationScheduled(this.durationScheduled);
		episode.setShowType(this.showType);
		episode.setPrAuk(this.prAuk);
		episode.setImageURL(this.imageURL);
		if("".equals(this.certType)){
			this.certType=null;
		}
		episode.setCertType(this.certType);
		episode.setWarningText(this.warningText);
		episode.setNumberOfAdsPerBreak(numberOfAdsPerBreak);
		episode.setSupplier(supplier);
		episode.setTags(GenericUtilities.arrayToSeparatedString(this.tags,", "));
		episode.setExcludeddevices(GenericUtilities.arrayToSeparatedString(this.excludeddevices,", "));		
		if("".equals(this.adsupport)){
			this.adsupport=null;	
		}
		episode.setAdsupport(this.adsupport);	
		episode.setBrightcoveId(this.brightcoveId);
		
		episode.setIngestProfile(this.ingestProfile);
		episode.setIngestSource(this.ingestSource);	
		episode.setTxChannel(this.txChannel);	
		
		episode.setGeoAllowedCountries(this.geoAllowedCountries);
		episode.setFirstTXDate(this.firstTXDate);
		episode.setRecordedAt(this.recordedAt);
		episode.setEpisodeSequenceNumber(this.episodeSequenceNumber);
		episode.setDurationUploaded(this.durationUploaded);
		episode.setEditorNotes(this.editorNotes);
	}


	  public boolean updateWhenReceivedByMaterialId(uk.co.boxnetwork.model.Episode episode) {		
		boolean changed=false;
		if(!GenericUtilities.isNotValidTitle(this.title)){
			episode.setTitle(this.title);
			changed=true;
		}				
		if(this.number!=null){
			episode.setNumber(this.number);
			changed=true;
		}
		if(this.synopsis!=null){
			episode.setSynopsis(this.synopsis);
			changed=true;
		}
		if(this.certType!=null){
			episode.setCertType(this.certType);
			changed=true;
		}
		if(this.warningText!=null){
			episode.setWarningText(this.warningText);
			changed=true;
		}
		if(this.adsupport!=null){
			episode.setAdsupport(this.adsupport);
			changed=true;
		}
		if(this.durationScheduled!=null){
			episode.setDurationScheduled(this.durationScheduled);
			changed=true;
		}
			
		if(this.brightcoveId != null){
			episode.setBrightcoveId(this.brightcoveId);
			changed=true;
		}
		if(this.ingestProfile!=null){
			episode.setIngestProfile(this.ingestProfile);
			changed=true;
		}
		if(this.ingestSource!=null){
			episode.setIngestSource(this.ingestSource);
			changed=true;
		}
		if(this.txChannel!=null){
			episode.setTxChannel(this.txChannel);
			changed=true;
		}
		if(this.getContentType()!=null){
			episode.setContentType(this.contentType);
			changed=true;
		}

		if(this.getMaterialId()!=null){
			episode.setMaterialId(materialId);
			changed=true;
		}
		if(this.showType!=null){
			episode.setShowType(this.showType);
			changed=true;
		}
		if(this.prAuk!=null){
			episode.setPrAuk(this.prAuk);
			changed=true;
		}
		if(this.geoAllowedCountries!=null){
			episode.setGeoAllowedCountries(geoAllowedCountries);
			changed=true;
		}
		if(this.imageURL!=null){
			episode.setImageURL(this.imageURL);
			changed=true;
		}
		if(this.supplier!=null){
			episode.setSupplier(this.supplier);
			changed=true;
		}
		if(this.firstTXDate!=null){
			episode.setFirstTXDate(this.firstTXDate);
			changed=true;
		}
		if(this.episodeSequenceNumber!=null){
			episode.setEpisodeSequenceNumber(this.episodeSequenceNumber);
			changed=true;
		}
		if(this.recordedAt!=null){
			episode.setRecordedAt(this.recordedAt);
			changed=true;
		}
		if(this.editorNotes!=null){
			if(this.editorNotes.equals("null")){
				this.editorNotes=null;
			}
			episode.setEditorNotes(this.editorNotes);
			changed=true;
		}
		
		return changed;
	}
	
	

	public String[] getTags() {
		return tags;
	}


	public void setTags(String[] tags) {
		this.tags = tags;
	}


	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}


	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public Set<ComplianceInformation> getComplianceInformations() {
		return ComplianceInformations;
	}


	public void setComplianceInformations(Set<ComplianceInformation> complianceInformations) {
		ComplianceInformations = complianceInformations;
	}


	public String getIngestProfile() {
		return ingestProfile;
	}


	public void setIngestProfile(String ingestProfile) {
		this.ingestProfile = ingestProfile;
	}


	public String getIngestSource() {
		return ingestSource;
	}


	public void setIngestSource(String ingestSource) {
		this.ingestSource = ingestSource;
	}


	public String getTxChannel() {
		return txChannel;
	}


	public void setTxChannel(String txChannel) {
		this.txChannel = txChannel;
	}


	public List<CuePoint> getCuePoints() {
		return cuePoints;
	}


	public void setCuePoints(List<CuePoint> cuePoints) {
		this.cuePoints = cuePoints;
	}

  public void addCuePoint(CuePoint cuePoint){
	  this.cuePoints.add(cuePoint);
	  Collections.sort(this.cuePoints);
  }
 public void addScheduleEvent(ScheduleEvent evt){
	 this.scheduleEvents.add(evt);
 }


public String getProgrammeNumber() {
	return programmeNumber;
}


public void setProgrammeNumber(String programmeNumber) {
	this.programmeNumber = programmeNumber;
}


public ProgrammeContentType getContentType() {
	return contentType;
}


public void setContentType(ProgrammeContentType contentType) {
	this.contentType = contentType;
}


public Double getDurationScheduled() {
	return durationScheduled;
}


public void setDurationScheduled(Double durationScheduled) {
	this.durationScheduled = durationScheduled;
}


public String getShowType() {
	return showType;
}


public void setShowType(String showType) {
	this.showType = showType;
}


public String getPrAuk() {
	return prAuk;
}


public void setPrAuk(String prAuk) {
	this.prAuk = prAuk;
}


public EpisodeStatus getEpisodeStatus() {
	return episodeStatus;
}


public void setEpisodeStatus(EpisodeStatus episodeStatus) {
	this.episodeStatus = episodeStatus;
}





public String[] getExcludeddevices() {
	return excludeddevices;
}


public void setExcludeddevices(String[] excludeddevices) {
	this.excludeddevices = excludeddevices;
}


public String getGeoAllowedCountries() {
	return geoAllowedCountries;
}


public void setGeoAllowedCountries(String geoAllowedCountries) {
	this.geoAllowedCountries = geoAllowedCountries;
}




public List<AvailabilityWindow> getAvailabilities() {
	return availabilities;
}


public void setAvailabilities(List<AvailabilityWindow> availabilities) {
	this.availabilities = availabilities;
}


public String getNumberOfAdsPerBreak() {
	return numberOfAdsPerBreak;
}


public void setNumberOfAdsPerBreak(String numberOfAdsPerBreak) {
	this.numberOfAdsPerBreak = numberOfAdsPerBreak;
}

public void addAvailability(AvailabilityWindow availabilityWindow){
	this.availabilities.add(availabilityWindow);
}


public String getImageURL() {
	return imageURL;
}


public void setImageURL(String imageURL) {
	this.imageURL = imageURL;
}


public String getSupplier() {
	return supplier;
}


public void setSupplier(String supplier) {
	this.supplier = supplier;
}


public Long getFirstTXDate() {
	return firstTXDate;
}


public void setFirstTXDate(Long firstTXDate) {
	this.firstTXDate = firstTXDate;
}


public Integer getEpisodeSequenceNumber() {
	return episodeSequenceNumber;
}


public void setEpisodeSequenceNumber(Integer episodeSequenceNumber) {
	this.episodeSequenceNumber = episodeSequenceNumber;
}


public Double getDurationUploaded() {
	return durationUploaded;
}


public void setDurationUploaded(Double durationUploaded) {
	this.durationUploaded = durationUploaded;
}

private void requiredFieldsIsMissing(String fieldMissing){
	this.requiredFieldsStatus=RequiredFieldsStatus.NOT_COMPLETE;
	if(this.requiredFieldsMissing==null){
		this.requiredFieldsMissing="";
	}
	else{
		this.requiredFieldsMissing+=",";
	}
	this.requiredFieldsMissing+=fieldMissing;
	
}
private void checkRequestField(String cfield){
	if("episode.title".equals(cfield) && GenericUtilities.isNotValidName(this.title)){			
		requiredFieldsIsMissing(cfield);
	}
	else if("episode.synopsis".equals(cfield) && GenericUtilities.isNotValidName(this.synopsis)){
		requiredFieldsIsMissing(cfield);
	}
	else if("episode.programmeNumber".equals(cfield) && GenericUtilities.isNotValidName(this.programmeNumber)){
		requiredFieldsIsMissing(cfield);
	}
	else if("episode.materialId".equals(cfield) && GenericUtilities.isNotValidName(this.materialId)){
		requiredFieldsIsMissing(cfield);
	}
	
	else if("episode.tags".equals(cfield) && (this.tags==null || this.tags.length==0)){
		if(this.series!=null){
			if(this.series.getTags()!=null && this.series.getTags().length>0){
				return;
			}
			if(this.series.getSeriesGroup()!=null && this.series.getSeriesGroup().getTags()!=null && this.series.getSeriesGroup().getTags().length>0){
				return;
			}
		}
		requiredFieldsIsMissing(cfield);
	}
	else if("episode.excludeddevices".equals(cfield) && (this.excludeddevices==null || this.excludeddevices.length==0)){		
		requiredFieldsIsMissing(cfield);
	}
	else if("episode.certType".equals(cfield) && this.certType==null){
		requiredFieldsIsMissing(cfield);
	}
	else if("episode.contentType".equals(cfield) && this.contentType==null){
		requiredFieldsIsMissing(cfield);
		
	}
	else if("episode.txChannel".equals(cfield) && this.txChannel==null){
		requiredFieldsIsMissing(cfield);
	}		
}
public void calculateRequiredFields(AppConfig appConfig){
	this.requiredFieldsStatus=RequiredFieldsStatus.COMPLETE;
	this.requiredFieldsMissing=null;
	
	if(appConfig==null || appConfig.getRequiredFields()==null){		
		return;
	}
	String checkFields[]=GenericUtilities.commandDelimitedToArray(appConfig.getRequiredFields());
	if(checkFields==null|| checkFields.length==0){	
		return;
	}
	for(String cf:checkFields){
		checkRequestField(cf);
	}
}
	
	
}
