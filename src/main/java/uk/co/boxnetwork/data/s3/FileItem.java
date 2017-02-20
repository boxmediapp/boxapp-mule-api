package uk.co.boxnetwork.data.s3;

import java.util.Date;

public class FileItem {
  private  String file;
  private  Date lastModifiedDate;
  
public String getFile() {
	return file;
}

public void setFile(String file) {
	this.file = file;
}

public Date getLastModifiedDate() {
	return lastModifiedDate;
}

public void setLastModifiedDate(Date lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
}

  
}
