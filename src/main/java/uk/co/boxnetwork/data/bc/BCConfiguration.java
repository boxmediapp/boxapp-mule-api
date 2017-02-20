package uk.co.boxnetwork.data.bc;

import org.apache.commons.codec.binary.Base64;

public class BCConfiguration {
	private String oauthurl;
	private String cmsurl;
	private String ingesturl;
	private String mediaapiurl;
	private String analyticsapiurl;
	
	private String accountId;
	private String clientId;
	private String clientSecret;
	private String apiToken;
	
	private String defaltIngestProfile;
	private String ingestCallback;
	
	private String analyticsDimension;
	private String analyticsFields;
	
	private String customFieldsUpdateType;
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getOauthurl() {
		return oauthurl;
	}
	public void setOauthurl(String oauthurl) {
		this.oauthurl = oauthurl;
	}
	public String getCmsurl() {
		return cmsurl;
	}
	public void setCmsurl(String cmsurl) {
		this.cmsurl = cmsurl;
	}
	public String getIngesturl() {
		return ingesturl;
	}
	public void setIngesturl(String ingesturl) {
		this.ingesturl = ingesturl;
	}
	public String requestAccessTokenURL(){
		return oauthurl+"/access_token";		
	}
	public String videoURL(Integer limit, Integer offset, String sort,String q){
		return queryURL(cmsurl+"/accounts/"+accountId+"/videos",limit,offset,sort,q);
	}
	
	public String analyticsURL(Integer limit, Integer offset, String dimensions,String fields,String from, String to, String filter){
		StringBuilder builder=new StringBuilder();
		builder.append(analyticsapiurl);
		builder.append("?");
		builder.append("accounts="+accountId);
		if(dimensions!= null){
			builder.append("&dimensions="+dimensions);
		}
		
		if(fields!=null){
			builder.append("&fields="+fields);
		}
		if(from!=null){
			builder.append("&from="+from);
		}
		if(to!=null){
			builder.append("&to="+to);
		}		
		if(limit!=null){
			builder.append("&limit="+limit);				
		}
		if(offset!=null){			
				builder.append("&offset="+offset);				
		}
		if(filter!=null){
			builder.append("&where="+filter);
		}
		return builder.toString();
	}
	public String queryURL(String baseURL, Integer limit, Integer offset, String sort,String q){
		StringBuilder builder=new StringBuilder();
		builder.append(baseURL);
		if(q!=null||limit!=null||offset!=null||sort!=null){
			builder.append("?");
			boolean appended=false;
			if(limit!=null){
				builder.append("limit="+limit);	
				appended=true;
			}
			if(offset!=null){
				if(appended){
					builder.append("&");
				}
				builder.append("offset="+offset);	
				appended=true;
			}
			if(sort!=null){
				if(appended){
					builder.append("&");
				}
				builder.append("sort="+sort);	
				appended=true;
			}
			if(q!=null){
				if(appended){
					builder.append("&");
				}
				builder.append("q="+q);	
				appended=true;
			}
			
		}
				
		return builder.toString();
	}
	
	public String ingestUrl(String videoid){
		return   ingesturl+"/accounts/"+ accountId+"/videos/"+videoid+"/ingest-requests";		
	}
	public String videoURL(String videoid){
		return cmsurl+"/accounts/"+accountId+"/videos/"+videoid;
	}
	public String assetURL(String videoid, String assetType, String assetId){
		if(assetId==null){
			return cmsurl+"/accounts/"+accountId+"/videos/"+videoid+"/assets/"+assetType;
		}
		else{
			return cmsurl+"/accounts/"+accountId+"/videos/"+videoid+"/assets/"+assetType+"/"+assetId;
		}
		
	}
	public String playlistURL(String playlistid,String q,String sort, Integer offset, Integer limit){
		StringBuilder ret=new StringBuilder();
		ret.append(cmsurl).append("/accounts/").append(accountId).append("/playlists/");
		if(playlistid!=null){
			ret.append(playlistid).append("/videos");			
		}
		if(offset==null || offset<0){
			offset=0;			
		}
		ret.append("?offset=").append(offset);
		if(limit!=null && limit>=0){			
			ret.append("&limit=").append(limit);			
		}
		if(sort!=null){
			ret.append("&sort=").append(sort);
		}
		if(q!=null){
			ret.append("&q=").append(q);
		}
		return ret.toString();		
	}
	public String playlistURL(String playlistid){
		StringBuilder ret=new StringBuilder();
		ret.append(cmsurl).append("/accounts/").append(accountId).append("/playlists/");
		if(playlistid!=null){
			ret.append(playlistid);			
		}		
		return ret.toString();		
	}
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	
	public String getDefaltIngestProfile() {
		return defaltIngestProfile;
	}
	public void setDefaltIngestProfile(String defaltIngestProfile) {
		this.defaltIngestProfile = defaltIngestProfile;
	}
	public String getIngestCallback() {
		return ingestCallback;
	}
	public void setIngestCallback(String ingestCallback) {
		this.ingestCallback = ingestCallback;
	}
	public String[] retrieveIngestCallbackUrls(){
		if(ingestCallback==null){
			return null;
		}
		return ingestCallback.split(",");
	}
	public String retrieveIngestCallbackUrls(int index){
		String urls[]=retrieveIngestCallbackUrls();
		if(urls==null){
			return null;
		}
		return urls[0];		
	} 
	
	public String basicAuthentication(){
		String plainCreds = clientId+":"+clientSecret;		
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		return base64Creds;
		
	}
	

	public String getMediaapiurl() {
		return mediaapiurl;
	}
	public void setMediaapiurl(String mediaapiurl) {
		this.mediaapiurl = mediaapiurl;
	}
	public String getAnalyticsapiurl() {
		return analyticsapiurl;
	}
	public void setAnalyticsapiurl(String analyticsapiurl) {
		this.analyticsapiurl = analyticsapiurl;
	}
	public String getAnalyticsDimension() {
		return analyticsDimension;
	}
	public void setAnalyticsDimension(String analyticsDimension) {
		this.analyticsDimension = analyticsDimension;
	}
	public String getAnalyticsFields() {
		return analyticsFields;
	}
	public void setAnalyticsFields(String analyticsFields) {
		this.analyticsFields = analyticsFields;
	}
	public String getCustomFieldsUpdateType() {
		return customFieldsUpdateType;
	}
	public void setCustomFieldsUpdateType(String customFieldsUpdateType) {
		this.customFieldsUpdateType = customFieldsUpdateType;
	}
	
  
}
