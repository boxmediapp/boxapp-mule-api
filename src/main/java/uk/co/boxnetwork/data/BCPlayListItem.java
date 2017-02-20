package uk.co.boxnetwork.data;

import uk.co.boxnetwork.data.bc.BCVideoData;

public class BCPlayListItem {
	private String id;
  private BCVideoData bcvideoData;

  
public String getId() {
	return id;
}


public void setId(String id) {
	this.id = id;
}


public BCVideoData getBcvideoData() {
	return bcvideoData;
}


public void setBcvideoData(BCVideoData bcvideoData) {
	this.bcvideoData = bcvideoData;
	if(this.bcvideoData!=null){
		 
		 this.id=this.bcvideoData.getId();
	}
}
  
}
