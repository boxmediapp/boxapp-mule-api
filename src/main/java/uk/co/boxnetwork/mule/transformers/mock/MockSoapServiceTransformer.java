package uk.co.boxnetwork.mule.transformers.mock;

import org.mule.api.MuleMessage;



import uk.co.boxnetwork.data.ErrorMessage;

import uk.co.boxnetwork.mule.components.LoadResourceAsInputStream;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;


public class MockSoapServiceTransformer  extends BoxRestTransformer{
	
	private String file;
	
	public void setFile(String file) {
		this.file = file;
	}
	
	protected Object processGET(MuleMessage message, String outputEncoding){		
		return processPOST(message,outputEncoding);
	}
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){		
		  try{
			     if(file==null){
			       throw new RuntimeException("missing file parameter");	 
			     }
			     message.setOutboundProperty("Content-Type", "text/xml; charset=UTF-8");
			     message.setOutboundProperty("X-Content-Type-Options", "nosniff");
			     message.setOutboundProperty("Strict-Transport-Security", "max-age=15768000");
			     message.setOutboundProperty("X-Frame-Options", "SAMEORIGIN");
			     message.setOutboundProperty("X-XSS-Protection", "1; mode=block");
			     return LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream(file);
			     
			  }
			  catch(Exception e){
				  logger.error(e+" while processing ther message message:[["+message+"]]");
				  return new ErrorMessage(e+ "while processing GET"); 
			  }			  			  		  		  	    			 
	}		
	  
   
}