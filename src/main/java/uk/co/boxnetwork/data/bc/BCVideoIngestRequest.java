package uk.co.boxnetwork.data.bc;

import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.model.Episode;

public class BCVideoIngestRequest {
	
	private BCMasterVideo master;
	private String profile;
	private String callbacks[];
	public BCVideoIngestRequest(){
		
	}
	public BCVideoIngestRequest(Episode episode,BCConfiguration configuration){
		master=new BCMasterVideo();
	
		master.setUrl(episode.getIngestSource());
	    profile=episode.getIngestProfile();
		callbacks=new String[1];
		
		callbacks=configuration.retrieveIngestCallbackUrls();
	}
	public BCMasterVideo getMaster() {
		return master;
	}
	public void setMaster(BCMasterVideo master) {
		this.master = master;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String[] getCallbacks() {
		return callbacks;
	}
	public void setCallbacks(String[] callbacks) {
		this.callbacks = callbacks;
	}
	
		
}
