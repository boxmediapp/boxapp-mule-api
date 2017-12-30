package uk.co.boxnetwork.mule.model;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.data.app.LoginInfo;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.BoxUserRole;

public class BoxOperator {
	static final protected Logger logger=LoggerFactory.getLogger(BoxOperator.class);
	private String httpmethod;
	private String clientIPAdress;	
	private String realip;	
	private String username;
	private String requesturi;
	private String referer;
	private List<BoxUserRole> roles;
	private BoxUser user;
	private LoginInfo loginInfo;
	
	private IdentityType identityType=IdentityType.NO_CREDENTIAL;
	
	public BoxOperator(MuleMessage message){
	  try{	
		  			this.referer=message.getProperty("referer", PropertyScope.INBOUND);		
		  			this.requesturi=message.getProperty("http.request.uri", PropertyScope.INBOUND);		
		  			this.httpmethod=message.getProperty("http.method", PropertyScope.INBOUND);
		            this.clientIPAdress=message.getProperty("http.remote.address", PropertyScope.INBOUND);
					this.realip=message.getProperty("X-Real-IP", PropertyScope.INBOUND);
				    String authorization=message.getProperty("authorization", PropertyScope.INBOUND);
					this.username=null;		
					
					if(authorization!=null && authorization.length() > 5){						
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
		return "httpmethod=["+httpmethod+"]requesturi=["+requesturi+"] clientIPAdress=["+clientIPAdress+"]realip=["+realip+"]username=["+username+"]referer:"+referer+"]";
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
	public List<BoxUserRole> getRoles() {
		return roles;
	}
	public void setRoles(List<BoxUserRole> roles) {
		this.roles = roles;
	}
	public boolean checkGETAccess(){
		if(roles==null){
			return false;
		}
		for(BoxUserRole role:roles){
			if(role.getOperationAccess().equals("full-access")){
				return true;
			}
			else if(role.getOperationAccess().equals("admin")){
				return true;
			}
			else if(role.getOperationAccess().equals("readonly-operator")){
				return true;
			}
		}
		return false;
		
	}
	public boolean checkPOSTAccess(){
		if(roles==null){
			return false;
		}
		for(BoxUserRole role:roles){
			if(role.getOperationAccess().equals("full-access")){
				return true;
			}
			else if(role.getOperationAccess().equals("admin")){
				return true;
			}
		}
		return false;		
	}
	public boolean checkAdminAccess(){
		if(roles==null){
			return false;
		}
		for(BoxUserRole role:roles){
			if(role.getOperationAccess().equals("admin")){
				return true;
			}			
		}
		return false;		
	}
	public boolean  checkPUTAccess(){	
		return checkPOSTAccess();
	}
	public boolean checkDELETEAccess(){
		return checkPOSTAccess();
	}
	public boolean checkPATCHAccess(){
		return checkPOSTAccess();
	}
	public IdentityType getIdentityType() {
		return identityType;
	}
	public void setIdentityType(IdentityType identityType) {
		this.identityType = identityType;
	}
	public BoxUser getUser() {
		return user;
	}
	public void setUser(BoxUser user) {
		this.user = user;
	}
	public LoginInfo getLoginInfo() {
		return loginInfo;
	}
	public void setLoginInfo(LoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}
	
	
}
