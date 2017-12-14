package uk.co.boxnetwork.mule.model;

import java.nio.charset.Charset;
import java.util.Base64;

import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRequestInfo {
	static final protected Logger logger=LoggerFactory.getLogger(ClientRequestInfo.class);
	private String httpmethod;
	private String clientIPAdress;	
	private String realip;
	private String authorization;
	private String username;
	private String requesturi;
	private String referer;
	public ClientRequestInfo(MuleMessage message){
	  try{	
		  			this.referer=message.getProperty("referer", PropertyScope.INBOUND);		
		  			this.requesturi=message.getProperty("http.request.uri", PropertyScope.INBOUND);		
		  			this.httpmethod=message.getProperty("http.method", PropertyScope.INBOUND);
		            this.clientIPAdress=message.getProperty("http.remote.address", PropertyScope.INBOUND);
					this.realip=message.getProperty("X-Real-IP", PropertyScope.INBOUND);
					this.authorization=message.getProperty("authorization", PropertyScope.INBOUND);
					this.username=null;				
					if(authorization!=null){
						
						 String base64Credentials = authorization.substring("Basic".length()).trim();
					        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
					                Charset.forName("UTF-8"));
					        // credentials = username:password
					        final String[] values = credentials.split(":",2);
					        this.username=values[0];
					}						
	     }
	  catch(Exception e){
			logger.error(e+ "while decoding",e);  
	  }
	}
	@Override
	public String toString(){
		return "httpmethod=["+httpmethod+"]requesturi=["+requesturi+"] clientIPAdress=["+clientIPAdress+"]realip=["+realip+"]username=["+username+"]referer:"+referer+"]authorization=["+authorization+"]";
	}
	public String getHttpmethod() {
		return httpmethod;
	}
	public void setHttpmethod(String httpmethod) {
		this.httpmethod = httpmethod;
	}
	public String getClientIPAdress() {
		return clientIPAdress;
	}
	public void setClientIPAdress(String clientIPAdress) {
		this.clientIPAdress = clientIPAdress;
	}
	public String getRealip() {
		return realip;
	}
	public void setRealip(String realip) {
		this.realip = realip;
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRequesturi() {
		return requesturi;
	}
	public void setRequesturi(String requesturi) {
		this.requesturi = requesturi;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
}
