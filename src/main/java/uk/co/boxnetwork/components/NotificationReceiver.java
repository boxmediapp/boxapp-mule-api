package uk.co.boxnetwork.components;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;


import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class NotificationReceiver {	
	private static final Logger logger=LoggerFactory.getLogger(NotificationReceiver.class);
	
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private MetadataService metadataService; 
	
	
	
	@Autowired
	S3BucketService s3Service;
	
	
	
	private String getBucketName(Map<String, Object> messageMap){
		return GenericUtilities.getValueInMap(messageMap,"Records[0].s3.bucket.name");
	}
	private String getObjectKey(Map<String, Object> messageMap){
		return GenericUtilities.getValueInMap(messageMap,"Records[0].s3.object.key");
	}
	private String getEventName(Map<String, Object> messageMap){
		return GenericUtilities.getValueInMap(messageMap,"Records[0].eventName");
	}
	
	
	public void notify(Map<String, Object> messageMap){		
		if(!"Notification".equals(messageMap.get("Type") ) ){
			logger.warn(" Type is not notification:"+messageMap.get("Type"));
			return;
		}
		String messageInJson=(String)messageMap.get("Message");
		if(messageInJson==null){
			logger.warn("Message in the notification is null");
			return;			
		}
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);				
		try {
			Map<String, Object> msg = (Map<String, Object>)objectMapper.readValue(messageInJson, Map.class);
			notifyMeessage(msg);
		} catch (IOException e) {
			logger.error("error in json when parsing the Message in the notification:"+messageInJson, e);
		}
	}
	
	private void notifyMeessage(Map<String, Object> messageMap){		
		String key=getObjectKey(messageMap);
		if(key==null){
			logger.warn("Key is null in the notification message");
			return;			
		}
		if(key.endsWith("/")){
			logger.warn("Folder notification is ignored");
			return;
		}
		String eventName=getEventName(messageMap);
		if(eventName==null){
			logger.warn("EventName is null in the notification message");
			return;
		}
		String bucket=getBucketName(messageMap);
		if(bucket ==null){
			logger.warn("bucket is null in the notification message");
			return;
		}
		
		if(eventName.contains("ObjectCreated")){
			onFileUpload(bucket, key);
		}
		else if(eventName.contains("ObjectRemoved")){
			onFileDeleted(bucket, key);
		}
		else{
			logger.info("Ignoring event:"+eventName);
		}
		
		
	}
	private void onFileUpload(String bucketName, String file){
		logger.info("****file to compare:"+file);
		logger.info("image path:"+appConfig.getImageMasterFolder());
		if(appConfig.getImageBucket().equals(bucketName)){
			onImageBucketUpload(file);
		}
		else if(appConfig.getVideoBucket().equals(bucketName)){
			onVideoBucketUpload(file);
		}
		onTranscodeBucketUpload(bucketName,file);
	}
	
	private void onFileDeleted(String bucketName, String file){
		if(appConfig.getImageBucket().equals(bucketName)){
			onImageBucketFileDeleted(file);
		}
		else if(appConfig.getVideoBucket().equals(bucketName)){
			onVideoBucketFileDeleted(file);
		}
	}
	
	
	private void onImageBucketUpload(String file){
		logger.info("Uploaded to the image bucket"+ file);
		if(file.startsWith(appConfig.getImageMasterFolder())){
			try{
				String imageFile=file.substring(appConfig.getImageMasterFolder().length()+1);
				metadataService.notifyMasterImageUploaded(imageFile);
				
			}
			catch(Throwable e){
				logger.error(e+" while setting the image in the metadata on notification"+file,e );
			}
			if(appConfig.getConvertImage()==null || (!appConfig.getConvertImage())){
				logger.info("**** will not convert images because of the config");			
			}
			else{		
				  metadataService.persistConvertImaggeMediaCommand(file);				  				  
			}
			
			
		}
		else if(file.startsWith(appConfig.getImagePublicFolder())){
			String imageFile=file.substring(appConfig.getImagePublicFolder().length()+1);
			metadataService.notifyGeneratedImage(imageFile);
		}
	}
   private void onVideoBucketUpload(String file){
	   logger.info("Uploaded to the video bucket:"+file);
	   metadataService.bindVideoFile(file);
	   
	}
   
   private void onTranscodeBucketUpload(String bucketName,String file){		
		if(GenericUtilities.isEmpty(appConfig.getTranscodeSourceBucket())){
			logger.info("transcode source bucket is not set");
			return;
		}
		if(GenericUtilities.isEmpty(appConfig.getTranscodeDestBucket())){
			logger.info("transcode dest bucket is not set");
			return;
		}
		if(appConfig.getTranscodeSourceBucket().equals(bucketName)){			
			 logger.info("Uploaded to the transcode bucket"+ file+" transcodeBucket:"+appConfig.getTranscodeSourceBucket());		
			 metadataService.persistTranscodeCommand(file);
		}
		else{
			logger.info("not transcode source bucket");
		}
		
	}
	
	private void onImageBucketFileDeleted(String file){
		logger.info("image file is deleted:"+file);
		if(appConfig.getConvertImage()==null || (!appConfig.getConvertImage())){
			logger.info("**** ignoring delete because of the config");
			return;
		}
		
		if(file.startsWith(appConfig.getImageMasterFolder())){			
			try{
				String imageFile=file.substring(appConfig.getImageMasterFolder().length()+1);
				metadataService.notifyMasterImageDelete(imageFile);
			}
			catch(Throwable e){
				logger.error(e+" while setting the image in the metadata on notification"+file,e );
			}
		}
				
	}
	private void onVideoBucketFileDeleted(String file){
		logger.info("video file is deleted:"+file);		
	}
}
