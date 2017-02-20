package uk.co.boxnetwork.data.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;
import uk.co.boxnetwork.util.GenericUtilities;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class S3FileSignatureData {
	private static final Logger logger=LoggerFactory.getLogger(S3FileSignatureData.class);
	  private  String file;
	  private String baseURL;
	  private String path;
	  private String accessKey;
	  private String accessSecret;
	  
	  private String bucket;
	  private String acl="private";
	  private String successActionStatus;
	  private String contentType;
	  private String xamzMetaUUID;
	  private String xamzServerSideeEcryption="AES256";
	  private String xamzAlgorithm="AWS4-HMAC-SHA256";
	  
	  private String xamzCredential;	  
	  private String xamzDate;	  
	  private String policy;
	  private String xamzSignature;
	  private String expirationDate;
	  
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;		
	}
	private void calculateFilePart(){
		int ie=this.path.lastIndexOf("/");
		if(ie!=-1){
			file=this.path.substring(ie+1);				
		}
		else{
			file=this.path;
		}
	}
	public void parseURL(String url){	
		int ib=url.indexOf("://");
		if(ib==-1){
			logger.error("it is not in the correct url format:"+url);
			return;
		}
		int ie=url.indexOf("/",ib+"://".length()+1);
		if(ie==-1){
			logger.error("the url is missing the file part:"+url);
			return;
		}
		baseURL=url.substring(0,ie);
		String fullpathpart=url.substring(ie+1);
		ib=fullpathpart.indexOf("?");
		if(ib==-1){			
			this.path=fullpathpart;
		}
		else{
			this.path=fullpathpart.substring(0,ib);
			String queryparam=fullpathpart.substring(ib+1);
			String queryparts[]= queryparam.split("&");
			for(String qp:queryparts){
				ib=qp.indexOf("=");
				if(ib==-1){
					logger.error("unknow query param part:"+qp);
					
					continue;
				}
				String qname=qp.substring(0,ib);
				if(qname.equals("AWSAccessKeyId")){
					this.accessKey=qp.substring(ib+1);				
				}
				else if(qname.equals("Signature")){
					this.accessSecret=qp.substring(ib+1);
				}
			}
		}
		
		calculateFilePart();
	}
	public String getBaseURL() {
		return baseURL;
	}
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getAccessSecret() {
		return accessSecret;
	}
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}
	public String getAcl() {
		return acl;
	}
	public void setAcl(String acl) {
		this.acl = acl;
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getXamzMetaUUID() {
		return xamzMetaUUID;
	}
	public void setXamzMetaUUID(String xamzMetaUUID) {
		this.xamzMetaUUID = xamzMetaUUID;
	}
	public String getXamzServerSideeEcryption() {
		return xamzServerSideeEcryption;
	}
	public void setXamzServerSideeEcryption(String xamzServerSideeEcryption) {
		this.xamzServerSideeEcryption = xamzServerSideeEcryption;
	}
	public String getXamzCredential() {
		return xamzCredential;
	}
	public void setXamzCredential(String xamzCredential) {
		this.xamzCredential = xamzCredential;
	}
	public String getXamzAlgorithm() {
		return xamzAlgorithm;
	}
	public void setXamzAlgorithm(String xamzAlgorithm) {
		this.xamzAlgorithm = xamzAlgorithm;
	}
	public String getXamzDate() {
		return xamzDate;
	}
	public void setXamzDate(String xamzDate) {
		this.xamzDate = xamzDate;
	}
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public String getXamzSignature() {
		return xamzSignature;
	}
	public void setXamzSignature(String xamzSignature) {
		this.xamzSignature = xamzSignature;
	}
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getSuccessActionStatus() {
		return successActionStatus;
	}
	public void setSuccessActionStatus(String successActionStatus) {
		this.successActionStatus = successActionStatus;
	}
	public void calculateExpirationDate(){
		Calendar expiration=Calendar.getInstance();
    	expiration.add(Calendar.HOUR, 2);	  
    	setExpirationDate(GenericUtilities.toFullUTCFormat(expiration.getTime()));
	}
	
		
	  
}
