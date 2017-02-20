package uk.co.boxnetwork.data.s3;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaFilesLocation {
	private static final Logger logger=LoggerFactory.getLogger(MediaFilesLocation.class);
  private String baseUrl;
  private List<FileItem> files=new ArrayList<FileItem>();
  public String getBaseUrl() {
	return baseUrl;
  }
  public void setBaseUrl(String baseUrl) {
	this.baseUrl = baseUrl;
  }
  public List<FileItem> getFiles() {
	return files;
  }
  public void setFiles(List<FileItem> files) {
	this.files = files;
  }
  public String highestVersion(){
		if(files.size()==0){
			return null;
		}
		else if(files.size()==1){
			  return files.get(0).getFile();			
		}
		else{
			FileItem highestVersion=files.get(0);
			for(int i=1;i<files.size();i++){
				FileItem current=files.get(i);
				if(current.getLastModifiedDate()!=null && highestVersion.getLastModifiedDate()!=null){
					if(current.getLastModifiedDate().after(highestVersion.getLastModifiedDate())){
						highestVersion=current;
					}
					else{
						if(highestVersion.getFile().compareTo(current.getFile())<0){							
							highestVersion=current;
						}
					}
				}
			}
			return highestVersion.getFile();			
		}
	} 
  
  public String retrieveMatchBasefilename(String match){
		if(files.size()==0){
			logger.info("no matching file");
			return null;
		}
		for(FileItem item:files){
			String fname=item.getFile();
			int ib=fname.indexOf(".");
			if(ib>0){
				fname=fname.substring(0,ib);
			}
			logger.info("comparing:fname=["+fname+"]match=["+match+"]");
			if(fname.equalsIgnoreCase(match)){
				return item.getFile(); 
			}
		}
		return null;
	} 
  
  
  public void addFilename(String filename){
	  FileItem fileItem=new FileItem();
	  fileItem.setFile(filename);
	  this.files.add(fileItem);
  }
}
