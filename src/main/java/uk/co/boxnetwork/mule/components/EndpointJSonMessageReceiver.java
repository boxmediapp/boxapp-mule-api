package uk.co.boxnetwork.mule.components;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointJSonMessageReceiver extends EndpointMessageReceiver{
	private static final Logger logger=LoggerFactory.getLogger(EndpointJSonMessageReceiver.class);
	private int arraySize=10;
	
	public int getArraySize() {
		return arraySize;
	}

	public void setArraySize(int arraySize) {
		this.arraySize = arraySize;
	}

	@Override
	public Object process(@Payload String payload, @Lookup MuleContext muleContext) throws Exception{
		  StringBuilder buffer=new StringBuilder();
		  List<String> messages=new ArrayList<String>();
		  for(int i=0;i<arraySize;i++){
			  Object obj=super.process(payload, muleContext);
			  if(obj==null)
				  break;
			  if(obj instanceof String){
				  messages.add((String)obj);
			  }
			  else if(obj instanceof MuleMessage){
				  Object payloadobj=((MuleMessage)obj).getPayload();
				  if(payloadobj instanceof String){
					  messages.add((String)payloadobj);
				  }
				  else
					  throw new RuntimeException("Unpexpected data type in the payload of the message received from the EndpointJSonMessageReceiver:"+payloadobj.getClass().getName()+" payloadobj=["+payloadobj+"]");
				  
			  }
			  else
				   throw new RuntimeException("Unpexpected data type is received from the EndpointJSonMessageReceiver:"+obj.getClass().getName()+" obj=["+obj+"]");
		  }
		  buffer.append("[");
	      boolean first=true;
	      for(String m:messages){
			    if(first){
			    		 first=false;			    		 
			    }
			    else{
			        buffer.append(", ");
			    
			     }
				buffer.append(m);
			    			  
		  }
  	      buffer.append("]");
		  return buffer.toString(); 
		  
	}
}
