package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;

public class BCImages {
	private BCImage thumbnail;
	private BCImage poster;
	public BCImages(){
		
	}
    
	public BCImage getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(BCImage thumbnail) {
		this.thumbnail = thumbnail;
	}
	public BCImage getPoster() {
		return poster;
	}
	public void setPoster(BCImage poster) {
		this.poster = poster;
	}
		
}
