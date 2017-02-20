package uk.co.boxnetwork.mule.components;


import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.mule.api.endpoint.EndpointException;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transport.Connector;
import org.mule.endpoint.EndpointURIEndpointBuilder;

public class MessageReader {

	 
	
	public Object process(@Payload String payload, @Lookup("modifiedAssetQueueInboundEndpoint") EndpointURIEndpointBuilder endpointBuilder) throws Exception{
		
		InboundEndpoint inboundEndpoint=endpointBuilder.buildInboundEndpoint();
         Object object=inboundEndpoint.request(500);
         return object;
		
		
	}
}
