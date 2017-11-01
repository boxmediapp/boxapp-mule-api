package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import uk.co.boxnetwork.util.GenericUtilities;

@Entity(name="app_config")
public class AppConfig {
	@Id
	@GeneratedValue
    private Long id;
	

	@Column(name="version")	
	private Integer version;
	 
	
 @Column(name="record_limit")	
 private Integer recordLimit;
 
 @Column(name="image_template_url")
 private String imagetemplateurl;
 
 @Column(name="brightcove_status")
 private Boolean brightcoveStatus;
  
 @Column(name="convert_image")
 private Boolean convertImage;
 
 
 
 @Column(name="send_update_to_soundmouse")
 private Boolean sendUpdateToSoundMouse;
 
 
 
 @Column(name="visibility_category")
 private String visibilityCategory;
 
 
 
 @Column(name="s3_video_url")
 private String s3videoURL;
 
 @Column(name="video_bucket")
private String videoBucket;
 
 @Column(name="image_bucket")
private String imageBucket;
 
 @Column(name="image_master_folder")
 private String imageMasterFolder;
 
 @Column(name="image_public_folder")
 private String imagePublicFolder;
 
 @Column(name="s3_images_url")
 private String s3imagesURL;
 
 @Column(name="aws_region")
 private String awsRegion;
 
 @Column(name="image_url_aliases")
 private String imageUrlAliases;
 
 
 @Column(name="requred_fields")
 private String requiredFields;
 
 @Column(name="auto_year_availability")
 private Integer autoYearsAvailability;
 
 @Column(name="auto_set_geo_allowed_countries")
 private String autoSetGeoAllowedCountries;
 
 @Column(name="auto_set_content_type")
 private ProgrammeContentType autoSetContentType;
 
 @Column(name="auto_set_tx_channel")
 private String autoSetTxChannel;
 
 @Column(name="auto_set_published_status")
 private PublishedStatus autoSetPublishedStatus;
 
 
 @Column(name="auto_create_place_holder")
 private Boolean autoCreatePlaceHolder;
 
 @Column(name="transcode_source_bucket")
 private String transcodeSourceBucket;
 
 @Column(name="transcode_dest_filename_prefix")
 private String transcodeDestFileNamePrefix;
 
 @Column(name="transcode_dest_bucket")
 private String transcodeDestBucket;
 
 
 
 @Column(name="auto_start_transcode")
 private Boolean autoTranscode;
 
 
 
 @Column(name="publish_programme_info")
 private Boolean publishProgrammeInfo;
 
  

 @Column(name="image_client_folder")
 private String imageClientFolder;
 
 @Column(name="image_client_base_url")
 private String imageClientBaseURL;
 
 
 
public String getImageClientBaseURL() {
	return imageClientBaseURL;
}

public void setImageClientBaseURL(String imageClientBaseURL) {
	this.imageClientBaseURL = imageClientBaseURL;
}

public String getImageClientFolder() {
	return imageClientFolder;
}

public void setImageClientFolder(String imageClientFolder) {
	this.imageClientFolder = imageClientFolder;
}

public Boolean getPublishProgrammeInfo() {
	return publishProgrammeInfo;
}

public void setPublishProgrammeInfo(Boolean publishProgrammeInfo) {
	this.publishProgrammeInfo = publishProgrammeInfo;
}

public Boolean getAutoTranscode() {
	return autoTranscode;
}

public void setAutoTranscode(Boolean autoTranscode) {
	this.autoTranscode = autoTranscode;
}

public String getTranscodeDestBucket() {
	return transcodeDestBucket;
}

public void setTranscodeDestBucket(String transcodeDestBucket) {
	this.transcodeDestBucket = transcodeDestBucket;
}

public String getTranscodeSourceBucket() {
	return transcodeSourceBucket;
}

public void setTranscodeSourceBucket(String transcodeSourceBucket) {
	this.transcodeSourceBucket = transcodeSourceBucket;
}

public String getTranscodeDestFileNamePrefix() {
	return transcodeDestFileNamePrefix;
}

public void setTranscodeDestFileNamePrefix(String transcodeDestFileNamePrefix) {
	this.transcodeDestFileNamePrefix = transcodeDestFileNamePrefix;
}

public Boolean getAutoCreatePlaceHolder() {
	return autoCreatePlaceHolder;
}

public void setAutoCreatePlaceHolder(Boolean autoCreatePlaceHolder) {
	this.autoCreatePlaceHolder = autoCreatePlaceHolder;
}

public PublishedStatus getAutoSetPublishedStatus() {
	return autoSetPublishedStatus;
}

public void setAutoSetPublishedStatus(PublishedStatus autoSetPublishedStatus) {
	this.autoSetPublishedStatus = autoSetPublishedStatus;
}

public String getAutoSetTxChannel() {
	return autoSetTxChannel;
}

public void setAutoSetTxChannel(String autoSetTxChannel) {
	if(autoSetTxChannel!=null && autoSetTxChannel.trim().length()==0){
		autoSetTxChannel=null;
	}
	this.autoSetTxChannel = autoSetTxChannel;
}

public ProgrammeContentType getAutoSetContentType() {
	return autoSetContentType;
}

public void setAutoSetContentType(ProgrammeContentType autoSetContentType) {	
	this.autoSetContentType = autoSetContentType;
}

public String getAutoSetGeoAllowedCountries() {
	return autoSetGeoAllowedCountries;
}

public void setAutoSetGeoAllowedCountries(String autoSetGeoAllowedCountries) {
	if(autoSetGeoAllowedCountries!=null && autoSetGeoAllowedCountries.trim().length()==0){
		autoSetGeoAllowedCountries=null;
	}
	this.autoSetGeoAllowedCountries = autoSetGeoAllowedCountries;
}

public Integer getAutoYearsAvailability() {
	return autoYearsAvailability;
}

public void setAutoYearsAvailability(Integer autoYearsAvailability) {
	this.autoYearsAvailability = autoYearsAvailability;
}

public String getRequiredFields() {
	return requiredFields;
}

public void setRequiredFields(String requiredFields) {
	this.requiredFields = requiredFields;
}

public Boolean getBrightcoveStatus() {
	return brightcoveStatus;
}

public void setBrightcoveStatus(Boolean brightcoveStatus) {
	this.brightcoveStatus = brightcoveStatus;
}

public Integer getRecordLimit() {
	return recordLimit;
}

public void setRecordLimit(Integer recordLimit) {
	this.recordLimit = recordLimit;
}


public String getImagetemplateurl() {
	return imagetemplateurl;
}

public void setImagetemplateurl(String imagetemplateurl) {
	this.imagetemplateurl = imagetemplateurl;
}

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public Boolean getConvertImage() {
	return convertImage;
}

public void setConvertImage(Boolean convertImage) {
	this.convertImage = convertImage;
}


public String getS3videoURL() {
	return s3videoURL;
}

public void setS3videoURL(String s3videoURL) {
	this.s3videoURL = s3videoURL;
}

public String getVideoBucket() {
	return videoBucket;
}

public void setVideoBucket(String videoBucket) {
	this.videoBucket = videoBucket;
}

public String getImageBucket() {
	return imageBucket;
}

public void setImageBucket(String imageBucket) {
	this.imageBucket = imageBucket;
}

public String getImageMasterFolder() {
	return imageMasterFolder;
}

public void setImageMasterFolder(String imageMasterFolder) {
	this.imageMasterFolder = imageMasterFolder;
}

public String getImagePublicFolder() {
	return imagePublicFolder;
}

public void setImagePublicFolder(String imagePublicFolder) {
	this.imagePublicFolder = imagePublicFolder;
}

public String getS3imagesURL() {
	return s3imagesURL;
}

public void setS3imagesURL(String s3imagesURL) {
	this.s3imagesURL = s3imagesURL;
}

public void exportConfig(AppConfig config){
     config.setBrightcoveStatus(this.brightcoveStatus);
     config.setImagetemplateurl(this.imagetemplateurl);
     config.setRecordLimit(this.recordLimit);	
     config.setId(config.getId());
     config.setVersion(this.version);
     config.setConvertImage(this.convertImage);
     config.setSendUpdateToSoundMouse(this.sendUpdateToSoundMouse);
     config.setVisibilityCategory(this.visibilityCategory);
     
     config.setS3videoURL(this.s3videoURL);
     config.setVideoBucket(this.videoBucket);
     config.setAwsRegion(this.awsRegion);
     config.setImageBucket(this.imageBucket);
     config.setImageMasterFolder(this.imageMasterFolder);
     config.setImagePublicFolder(this.imagePublicFolder);
     config.setS3imagesURL(this.s3imagesURL);
     config.setImageUrlAliases(this.imageUrlAliases);
     config.setRequiredFields(this.requiredFields);
     config.setAutoYearsAvailability(this.autoYearsAvailability);
     config.setAutoSetGeoAllowedCountries(this.autoSetGeoAllowedCountries);
     config.setAutoSetContentType(this.autoSetContentType);
     config.setAutoSetTxChannel(this.autoSetTxChannel);
     config.setAutoSetPublishedStatus(this.autoSetPublishedStatus);
     config.setAutoCreatePlaceHolder(this.autoCreatePlaceHolder);
     config.setTranscodeSourceBucket(this.transcodeSourceBucket);
     config.setTranscodeDestFileNamePrefix(this.transcodeDestFileNamePrefix);
     config.setTranscodeDestBucket(this.transcodeDestBucket);
     config.setAutoTranscode(this.autoTranscode);
     config.setPublishProgrammeInfo(this.publishProgrammeInfo);
     config.setImageClientFolder(this.imageClientFolder);
     config.setImageClientBaseURL(imageClientBaseURL);
    
}

public void importConfig(AppConfig config){	
    this.brightcoveStatus=config.getBrightcoveStatus();
    this.imagetemplateurl=config.getImagetemplateurl();
    this.recordLimit=config.getRecordLimit();
    this.version=config.getVersion();
    this.convertImage=config.getConvertImage();
    this.sendUpdateToSoundMouse=config.getSendUpdateToSoundMouse();
    this.visibilityCategory=config.getVisibilityCategory();
    
    
    this.s3videoURL=config.getS3videoURL();
    this.videoBucket=config.getVideoBucket();
    this.imageBucket=config.getImageBucket();
    this.imageMasterFolder=config.getImageMasterFolder();
    this.imagePublicFolder=config.getImagePublicFolder();
    this.s3imagesURL=config.getS3imagesURL();
    this.awsRegion=config.getAwsRegion();
    this.imageUrlAliases=config.getImageUrlAliases();
    this.requiredFields=config.getRequiredFields();
    this.autoYearsAvailability=config.getAutoYearsAvailability();
    this.autoSetGeoAllowedCountries=config.getAutoSetGeoAllowedCountries();
    this.autoSetContentType=config.getAutoSetContentType();
    this.autoSetTxChannel=config.getAutoSetTxChannel();
    this.autoSetPublishedStatus=config.getAutoSetPublishedStatus();
    this.autoCreatePlaceHolder=config.getAutoCreatePlaceHolder();
    this.transcodeSourceBucket=config.getTranscodeSourceBucket();
    this.transcodeDestFileNamePrefix=config.getTranscodeDestFileNamePrefix();
    this.transcodeDestBucket=config.getTranscodeDestBucket();
    this.autoTranscode=config.getAutoTranscode();
    this.publishProgrammeInfo=config.getPublishProgrammeInfo();
    this.imageClientFolder=config.getImageClientFolder();
    this.imageClientBaseURL=config.getImageClientBaseURL();
}

public Integer getVersion() {
	return version;
}

public void setVersion(Integer version) {
	this.version = version;
}

public Boolean getSendUpdateToSoundMouse() {
	return sendUpdateToSoundMouse;
}

public void setSendUpdateToSoundMouse(Boolean sendUpdateToSoundMouse) {
	this.sendUpdateToSoundMouse = sendUpdateToSoundMouse;
}

public String getVisibilityCategory() {
	return visibilityCategory;
}

public void setVisibilityCategory(String visibilityCategory) {
	this.visibilityCategory = visibilityCategory;
}

 public String getAwsRegion() {
	return awsRegion;
}

public void setAwsRegion(String awsRegion) {
	this.awsRegion = awsRegion;
}



public String getImageUrlAliases() {
	return imageUrlAliases;
}

public void setImageUrlAliases(String imageUrlAliases) {
	this.imageUrlAliases = imageUrlAliases;
}

public boolean checkIsURLPartOfImageUrlAliases(String imageURL){
	if(this.imageUrlAliases==null||this.imageUrlAliases.length()==0){
		return false;
	}
	if(imageURL==null||imageURL.length()==0){
		return false;
	}
	imageURL=imageURL.toLowerCase();
	String aliases[]=this.imageUrlAliases.split(",");
	for(String alias:aliases){
		alias=alias.trim();
		if(imageURL.startsWith("http://"+alias)){
			return true;
		}
		else if(imageURL.startsWith("https://"+alias)){
			return true;
		} 
	}
	return false;
}
public boolean shouldSendSoundmouseHeaderFile(uk.co.boxnetwork.data.Episode episode){
	 if(sendUpdateToSoundMouse==null){
		 return false;
	 }
	 if(!sendUpdateToSoundMouse){
		 return false;
	 }	 
	 if(GenericUtilities.isNotAValidId(episode.getBrightcoveId())){
		 return false;
	 }	 
	 return true;
 }



}
