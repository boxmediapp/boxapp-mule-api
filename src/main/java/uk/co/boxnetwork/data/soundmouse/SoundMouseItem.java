package uk.co.boxnetwork.data.soundmouse;

import uk.co.boxnetwork.data.bc.BCAnalyticData;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;

public class SoundMouseItem {
	private String title;
	private String materialId;
	private String duration;
	private BCAnalyticData analyticData;

	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMaterialId() {
		return materialId;
	}
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public BCAnalyticData getAnalyticData() {
		return analyticData;
	}
	public void setAnalyticData(BCAnalyticData analyticData) {
		this.analyticData = analyticData;
	}
	public void init(CuePoint cuePoint) {		
		this.title=cuePoint.getName();
		this.materialId=cuePoint.getMateriaId();
		this.duration=cuePoint.getDuration();
	}
	public void init(Episode episode){
		this.title=episode.getTitle();
		this.materialId=episode.getCtrPrg();
		int pduration=0;
		if(episode.getDurationScheduled()!=null){
			pduration=episode.getDurationScheduled().intValue();
		}
		if(pduration==0 && episode.getDurationUploaded()!=null){
			pduration=episode.getDurationUploaded().intValue();
		}
		
		String hh=String.valueOf(pduration/3600);
		if(hh.length()<2){
			hh='0'+hh;
		}
		pduration=pduration%3600;
		String mm=String.valueOf(pduration/60);
		if(mm.length()<2){
			mm='0'+mm;
		}
		String ss=String.valueOf(pduration%60);
		
		if(ss.length()<2){
			ss='0'+ss;
		}
		duration=hh+":"+mm+":"+ss;
	}
	
   public void joinAnalytics(SoundMouseItem joinerItem){
		this.title=joinerItem.title;		
		this.duration=joinerItem.duration;
		this.analyticData=this.analyticData.joinAnalytics(joinerItem.getAnalyticData());
   }
   

}
