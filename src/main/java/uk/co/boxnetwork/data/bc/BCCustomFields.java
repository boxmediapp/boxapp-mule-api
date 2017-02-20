package uk.co.boxnetwork.data.bc;

import java.util.List;

import uk.co.boxnetwork.model.AdvertisementRule;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.CertType;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MatchAdvertBreakType;
import uk.co.boxnetwork.model.ProgrammeContentType;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
import uk.co.boxnetwork.util.GenericUtilities;

public class BCCustomFields {
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
  
  
  
  
  
  
  
  public BCCustomFields(){
	  
  }
  public BCCustomFields(Episode episode,List<AdvertisementRule> advertisementRules,AppConfig appConfig){
	  if(GenericUtilities.episodeHasSeries(episode)){		  
		  this.seriestitle= episode.getSeries().getName();	
		  if(episode.getSeries().getSeriesNumber()!=null){
			  seriesnumber=String.valueOf(episode.getSeries().getSeriesNumber());
			  if(seriesnumber.length()==1){
				  seriesnumber="0"+seriesnumber;
			  }
		  }
	  }
	  if(episode.getCertType()!=null){
		  certificationtype=episode.getCertType().getBcName();
	  }
	  if(appConfig.getPublishProgrammeInfo()!=null && appConfig.getPublishProgrammeInfo() && GenericUtilities.episodeHasSeries(episode)){
		  programmetitle= episode.getSeries().getName();
		  programmesynopsis=episode.getSeries().getSynopsis();
	  }
	
	  warningtext=episode.getWarningText();	  		 
	  if(episode.getNumber()!=null){
		  episodenumber=String.valueOf(episode.getNumber());
		  if(episodenumber.length()==1){
			  episodenumber="0"+episodenumber;
		  }
	  }
	  if(episode.getTxChannel()!=null){
		  txchannel=episode.getTxChannel();
	  }
	  if(episode.getContentType()!=null){
		  contenttype=episode.getContentType().getName();
	  }
	  if(episode.getExcludeddevices()!=null){
		  excludeddevices=episode.getExcludeddevices();				 
	  }
	  if(episode.getIngestProfile()!=null && episode.getIngestProfile().endsWith("-DRM-profile")){
		  drm="True";
	  }
	  else{
		  drm="False";		  
	  }
	  AdvertisementRule matchedRule=GenericUtilities.select(episode, advertisementRules, MatchAdvertBreakType.PREPROLL);
	  if(matchedRule!=null){		  
		  numberOfAdsInPreRoll=String.valueOf(matchedRule.getNumberOfAdsPerBreak());
	  }
  }
  public void export(Episode episode){
	  if((!GenericUtilities.isEmpty(this.seriesnumber)) || (!GenericUtilities.isEmpty(this.seriestitle))){
		  if(episode.getSeries()==null){
			  episode.setSeries(new Series());
		  }
		  Series series=episode.getSeries();
		  series.setName(this.seriestitle);		  
		  series.setSeriesNumber(GenericUtilities.toInteter(this.seriesnumber, "failed parsing the seriesnumber imported from bc"));
	  }
	  if(this.certificationtype!=null){		  
		   episode.setCertType(CertType.fromString(this.certificationtype));
	  }
	  if((!GenericUtilities.isEmpty(this.programmetitle)) || (!GenericUtilities.isEmpty(this.programmesynopsis))){
		  if(episode.getSeries()==null){
			  episode.setSeries(new Series());			  
		  }
		  Series series=episode.getSeries();
		  if(series.getSeriesGroup()==null){
			  series.setSeriesGroup(new SeriesGroup());
		  }
		  SeriesGroup seriesgroup=series.getSeriesGroup();	
		  series.setName(this.programmetitle);
		  series.setSynopsis(this.programmetitle);
		  seriesgroup.setTitle(this.programmetitle);
		  seriesgroup.setSynopsis(this.programmesynopsis);		 		  	  
	  }
	  if(!GenericUtilities.isEmpty(this.warningtext)){
		  episode.setWarningText(this.warningtext);			  
	  }
	  if(!GenericUtilities.isEmpty(this.episodenumber)){
		  episode.setNumber(GenericUtilities.toInteter(this.episodenumber, "failed parsing the episodenumber imported from bc"));			  
	  }
	  if(!GenericUtilities.isEmpty(this.txchannel)){
		  episode.setTxChannel(this.txchannel);			  
	  }
	  if(!GenericUtilities.isEmpty(this.contenttype)){
		  episode.setContentType(ProgrammeContentType.fromString(this.contenttype));			  
	  }
	  if(!GenericUtilities.isEmpty(this.excludeddevices)){
		  episode.setExcludeddevices(this.excludeddevices);			  
	  }
	  if(this.drm!=null && this.drm.equals("True")){
		  episode.setIngestProfile("box-plus-network-DRM-profile");
	  }
	  else{
		  episode.setIngestProfile("box-plus-network-1080p-profile");
	  }
	  
		  
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

 
}
