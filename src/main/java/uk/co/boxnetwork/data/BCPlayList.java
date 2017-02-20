package uk.co.boxnetwork.data;

import uk.co.boxnetwork.data.bc.BCPlayListData;

public class BCPlayList {
	private String id;
	private BCPlayListData playListData;	
	private Integer videoCount;
	public BCPlayListData getPlayListData() {
		return playListData;
	}
	public void setPlayListData(BCPlayListData playListData) {
		this.playListData = playListData;
		if(this.playListData!=null){
			 if(this.playListData.getVideo_ids()!=null){
				 this.videoCount=this.playListData.getVideo_ids().length;
			 }
			 this.id=this.playListData.getId();
		}
	}	
	public Integer getVideoCount() {
		return videoCount;
	}
	public void setVideoCount(Integer videoCount) {
		this.videoCount = videoCount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
