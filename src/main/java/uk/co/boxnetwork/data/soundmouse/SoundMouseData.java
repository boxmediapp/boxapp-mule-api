package uk.co.boxnetwork.data.soundmouse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.data.bc.BCAnalyticData;
import uk.co.boxnetwork.data.bc.BCAnalyticsResponse;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

public class SoundMouseData {
    private static final Logger logger=LoggerFactory.getLogger(SoundMouseData.class);
	private  Date fromDate;
	private Date toDate;
	private String from;
	private String to;
	private Date now;
	private String smurfFilename;
	private String smurfFilePath;
	private String createdAt;
	private String reportStartDate;
	private String reportEndDate;
	private Integer mediaCount=0;
	private Long usageCount=0l;
	

	private List<SoundMouseItem> soundMouseItems=new ArrayList<SoundMouseItem>();
	
	

	public SoundMouseData(){
		init();
	}
	private void init(){	
		Calendar fromCalendar=Calendar.getInstance();
		fromCalendar.set(Calendar.DAY_OF_MONTH, 1);
		fromCalendar.add(Calendar.MONTH,-1);
		this.fromDate=fromCalendar.getTime();	
	
		Calendar toCalendar=Calendar.getInstance();
	    toCalendar.set(Calendar.DAY_OF_MONTH, 1);
	    toCalendar.add(Calendar.DATE,-1);
	    this.toDate=toCalendar.getTime();	
	    this.now=new Date();
	    this.from=GenericUtilities.toStandardDateFormat(fromDate);
		this.to=GenericUtilities.toStandardDateFormat(toDate);
		this.smurfFilename="SMURF_BOX_CH4001_"+GenericUtilities.toSoundMouseSmurfFileFormat(now).toUpperCase()+"_version00001.xml";
		this.smurfFilePath="/data/"+smurfFilename;
		this.createdAt=GenericUtilities.toSoundmouseDateTimeFormat(this.now);
		this.reportStartDate=GenericUtilities.toSoundmouseDateTimeFormat(fromDate);
		this.reportEndDate=GenericUtilities.toSoundmouseDateTimeFormat(toDate);
		
	}
	
	
	
	public String toString(){
		return "report date range:["+from+","+to+"]";		
	}
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	public String getSmurfFilename() {
		return smurfFilename;
	}
	public void setSmurfFilename(String smurfFilename) {
		this.smurfFilename = smurfFilename;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public Date getNow() {
		return now;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public String getReportStartDate() {
		return reportStartDate;
	}
	public String getReportEndDate() {
		return reportEndDate;
	}
	public Integer getMediaCount() {
		return mediaCount;
	}
	
	public Long getUsageCount() {
		return usageCount;
	}
	public String getSmurfFilePath() {
		return smurfFilePath;
	}
	private SoundMouseItem findSoundMouseItemByMaterialItem(String materialId){
		if(soundMouseItems.size()==0){
			return null;
		}
		for(SoundMouseItem soundMouseItem:soundMouseItems){
			if(soundMouseItem.getMaterialId()==null){
				continue;
			}
			if(soundMouseItem.getMaterialId().equals(materialId)){
				return null;
			}
		}
		return null;
	}
	private void addSoundMouseItem(SoundMouseItem soundMouseItem){
		SoundMouseItem existingSoundMouseItem=findSoundMouseItemByMaterialItem(soundMouseItem.getMaterialId());
		if(existingSoundMouseItem==null){
			soundMouseItems.add(soundMouseItem);
			return;
		}
		existingSoundMouseItem.joinAnalytics(soundMouseItem);		
	}
	
	public void addSmurfItems(Episode episode,BCAnalyticsResponse bcAnalyticResponse) throws Exception{
		if(bcAnalyticResponse==null|| bcAnalyticResponse.getItems()==null|| bcAnalyticResponse.getItems().length==0){
			logger.info("empty analytic response returned for the range "+bcAnalyticResponse);
			return;
		}		
		BCAnalyticData analyticData=bcAnalyticResponse.getItems()[0];		
		episode.makeSoundMouseFriendy();
		this.usageCount+=analyticData.getVideo_view();
		boolean withCuePoints=false;
		
		if(GenericUtilities.shouldReportOnCuepoint(episode)){
			this.mediaCount+=episode.getCuePoints().size();
			withCuePoints=true;
		}
		else{
			this.mediaCount+=1;
			withCuePoints=false;
		}				
		if(withCuePoints){
			for(CuePoint cuePoint:episode.getCuePoints()){
				SoundMouseItem soundMouseItem=new SoundMouseItem();
				soundMouseItem.init(cuePoint);
				soundMouseItem.setAnalyticData(analyticData);
				addSoundMouseItem(soundMouseItem);
			}
		}
		else{
			SoundMouseItem soundMouseItem=new SoundMouseItem();
			soundMouseItem.init(episode);
			soundMouseItem.setAnalyticData(bcAnalyticResponse.getItems()[0]);
			addSoundMouseItem(soundMouseItem);
		}		
	}	
  public String buildXMLFromSoundMouseItems() throws Exception{
	  StringBuilder builder=new StringBuilder();
	  for(SoundMouseItem item:soundMouseItems){
		  builder.append(GenericUtilities.getSoundmouseSmurfForCuepoint(this, item));
	  }
	  return builder.toString();	  
  }
}
