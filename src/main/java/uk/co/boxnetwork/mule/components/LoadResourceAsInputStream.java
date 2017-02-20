package uk.co.boxnetwork.mule.components;

import org.mule.api.MuleContext;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadResourceAsInputStream {
	private static final Logger logger=LoggerFactory.getLogger(LoadResourceAsInputStream.class);
	private String file;
	
	public void setFile(String file) {
		this.file = file;
	}

	public Object process(@Payload String payload, @Lookup MuleContext muleContext) throws Exception{
		  try{
		     if(file==null){
		       throw new RuntimeException("missing file parameter");	 
		     }
		     return LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream(file);
		  }
		  catch(Exception e){
			  logger.error(e+" while processing ther message payload:[["+payload+"]]");
		  }
		  return payload;
	  }
}
