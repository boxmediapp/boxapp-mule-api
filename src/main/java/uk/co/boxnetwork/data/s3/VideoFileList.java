package uk.co.boxnetwork.data.s3;

import java.util.ArrayList;
import java.util.List;

public class VideoFileList {
	  private String baseUrl;
	  private List<VideoFileItem> files=new ArrayList<VideoFileItem>();
	  public String getBaseUrl() {
		return baseUrl;
	  }
	  public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	  }
	  public List<VideoFileItem> getFiles() {
		return files;
	  }
	  public void setFiles(List<VideoFileItem> files) {
		this.files = files;
	  }
	  public String highestVersion(){
			if(files.size()==0){
				return null;
			}		
			return files.get(0).getFile();			
			
		} 
}
