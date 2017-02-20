package uk.co.boxnetwork.mule.components;

import org.mule.api.MuleContext;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.endpoint.EndpointURIEndpointBuilder;

public class EndpointMessageReceiver {
	

    private InboundEndpoint muleEndpoint;
    private String endpoint;
    private int timeout=500;
    
    
    public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public synchronized InboundEndpoint getInBoundEndpoint(MuleContext muleContext) throws InitialisationException, EndpointException{
    	if(muleEndpoint!=null)
    		return muleEndpoint;
    	EndpointURIEndpointBuilder endpointBuilder=muleContext.getRegistry().get(endpoint);
    	muleEndpoint=endpointBuilder.buildInboundEndpoint();
    	return muleEndpoint;
    }
   
	public Object process(@Payload String payload, @Lookup MuleContext muleContext) throws Exception{
		
	   InboundEndpoint endpoint=getInBoundEndpoint(muleContext);
	   
         Object object=endpoint.request(timeout);
         
         return object;
		
		
	}

public int getTimeout() {
	return timeout;
}

public void setTimeout(int timeout) {
	this.timeout = timeout;
}

}
