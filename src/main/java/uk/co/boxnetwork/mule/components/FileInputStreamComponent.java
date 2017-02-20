package uk.co.boxnetwork.mule.components;

import java.io.File;
import java.io.FileInputStream;

import org.mule.api.MuleContext;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileInputStreamComponent{
	private static final Logger logger=LoggerFactory.getLogger(FileInputStreamComponent.class);
  private String directory;  
  public String getDirectory() {
	return directory;
  }
  public void setDirectory(String directory) {
	this.directory = directory;
  }
  public Object process(@Payload String payload, @Lookup MuleContext muleContext) throws Exception{
	  logger.info("input for reading the file:["+payload+"]");
	  payload=payload.replaceAll("\\/\\.", "/emptystring.");
	  logger.info("going to read from the file:["+payload+"]");
	  
	  if(payload.indexOf("..")!=-1)
		  throw new IllegalArgumentException("does not allow to access the parent directory");	    
	  File file=new File(directory+File.separator+payload);
	  if(!file.exists()){
		  return null;		  
	  }
      if(!file.isFile()){
    	  return null;    	  
      }
	  return new FileInputStream(file);	  		
	}
	
}
