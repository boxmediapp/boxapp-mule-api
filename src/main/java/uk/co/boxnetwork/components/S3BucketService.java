package uk.co.boxnetwork.components;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import sun.misc.BASE64Encoder;
import uk.co.boxnetwork.data.AWSConfig;
import uk.co.boxnetwork.data.s3.FileItem;

import uk.co.boxnetwork.data.s3.VideoFileItem;
import uk.co.boxnetwork.data.s3.VideoFileList;
import uk.co.boxnetwork.data.s3.MediaFilesLocation;
import uk.co.boxnetwork.data.s3.S3FileSignatureData;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class S3BucketService {
	static final protected Logger logger=LoggerFactory.getLogger(S3BucketService.class);
	
	@Autowired
	private AppConfig appConfig;	

	@Autowired
	private AWSConfig awsConfig;
	
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	
	public Regions computeAWSRegion(String region){
		if(region==null|| region.length()==0){
			return Regions.EU_WEST_1;
		}		
		return Regions.valueOf(region);				
	}
	public String computeS3BaseURL(String region, String bucket){		
		String baseURL=null;		
		if(region==null|| region.length()==0){
			baseURL="https://s3-eu-west-1.amazonaws.com/";			
		}
		else
			baseURL="https://s3-"+region.toLowerCase().replace("_", "-")+".amazonaws.com/";
		return baseURL+bucket;
	}

	
	private Regions getAWSRegion(){
		return computeAWSRegion(appConfig.getAwsRegion());						
	}
	
	private  AWSCredentials getAWSCredentials(){			   
	    return  new ProfileCredentialsProvider().getCredentials();
	    
	}
	public  AmazonS3 getAmazonS3(){
		return getAmazonS3(getAWSRegion());			    
	}
	public AmazonS3 getAmazonS3(Regions region){
		AWSCredentials credentials=getAWSCredentials();
		AmazonS3 s3 = new AmazonS3Client(credentials);
		if(region!=null){
			Region r = Region.getRegion(region);
			s3.setRegion(r);
		}
	    return s3;
	}
	
	public List<FileItem> listFiles(String bucketname, String prefix, int startIndex, int maximumRecords, String filenamecontains){
		return listFiles(bucketname, prefix, null, startIndex, maximumRecords, null, null,filenamecontains);
	}
	public List<FileItem> listFiles(String bucketname, String prefix, Regions region, int startIndex, int maximumRecords, Date fromDate, Date toDate, String filenamecontains){
		//prefix="4821467243001";
		if(startIndex<=0){
			startIndex=0;			
		}
		logger.info("===========list in:"+bucketname+":::"+prefix+":startIndex:"+startIndex+":"+maximumRecords);
		AmazonS3 s3=null;
		if(region==null){
			s3=getAmazonS3();
		}
		else{
			s3=getAmazonS3(region);
		}
		ListObjectsRequest request=new ListObjectsRequest().withBucketName(bucketname);
		if(prefix!=null){
			request.withPrefix(prefix);			
		}	
		
		
		ObjectListing objectListing = s3.listObjects(request);
		
		
		List<FileItem> ret=new ArrayList<FileItem>(); 
		int matchedRecordCounter=0;
		
		do{
		        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {		        	
		            String key=objectSummary.getKey();
		            if(key.endsWith("/")){
		            	continue;		            	
		            }  
		            if(filenamecontains!=null){
		            	if(key.indexOf(filenamecontains)==-1){
		            		continue;
		            	}
		            }
		            Date lastModifiedDate=objectSummary.getLastModified(); 
		            if(fromDate!=null && fromDate.after(lastModifiedDate)){
		            	continue;
		            }
		            if(toDate!=null && toDate.before(lastModifiedDate)){
		            	continue;
		            }
		            matchedRecordCounter++;		            
		            if(startIndex>=0){
		            	if(matchedRecordCounter<=startIndex){
		            		continue;
		            	}
		            }
		            if(maximumRecords>0 && ret.size()>=maximumRecords){
		            	return ret;
		            }
		            
		            FileItem itm=new FileItem();
		            itm.setFile(key);
		            itm.setLastModifiedDate(lastModifiedDate);
		            objectSummary.getLastModified();
		            ret.add(itm);
		        }
		        if(objectListing.isTruncated()){
		        	objectListing=s3.listNextBatchOfObjects(objectListing);	
		        }
		        else{
		        	objectListing=null;
		        }		        
		}while(objectListing!=null);
        return ret;
	}
	public void copyFile(String sourceBuckerName, String sourcefileName,String destBucketName, String destFileName){		
		AmazonS3 s3=getAmazonS3();		
		CopyObjectRequest copyObjRequest = new CopyObjectRequest(sourceBuckerName, 
				sourcefileName, destBucketName, destFileName);
		s3.copyObject(copyObjRequest);
	}
	public void copyFile(String sourceBuckerName, String sourcefileName,String destBucketName, String destFileName, Regions region){		
		AmazonS3 s3=getAmazonS3(region);		
		CopyObjectRequest copyObjRequest = new CopyObjectRequest(sourceBuckerName, 
				sourcefileName, destBucketName, destFileName);
		s3.copyObject(copyObjRequest);
	}
	
	public void moveFile(String sourceBuckerName, String sourcefileName,String destBucketName, String destFileName){
		AmazonS3 s3=getAmazonS3();
		CopyObjectRequest copyObjRequest = new CopyObjectRequest(sourceBuckerName, 
				sourcefileName, destBucketName, destFileName);
		s3.copyObject(copyObjRequest);
		s3.deleteObject(new DeleteObjectRequest(sourceBuckerName, sourcefileName));
	}
	public MediaFilesLocation listFilesInVideoBucket(String prefix, int beginIndex, int maximumNumberOfRecords, String filenamecontains){
		MediaFilesLocation videoFilesLocations=new MediaFilesLocation();
		videoFilesLocations.setBaseUrl(appConfig.getS3videoURL());
		videoFilesLocations.setFiles(listFiles(appConfig.getVideoBucket(),prefix,beginIndex,maximumNumberOfRecords,filenamecontains));
		logger.info("******number of s3 file for prefix=["+prefix+"]:"+videoFilesLocations.getFiles().size());
		
		return videoFilesLocations;
	}
	public void deleteImagesInImageBucket(String keyName){
		if(keyName==null|| keyName.trim().length()<=1){
			throw new RuntimeException("refused to do the delete operation on the s3 bucket:"+keyName);
		}
		logger.info("deleting the image in the ImageBucket:"+keyName);
		AmazonS3 s3=getAmazonS3();		
		s3.deleteObject(new DeleteObjectRequest(appConfig.getImageBucket(), keyName));
	}
	public void deletePublicImage(String keyName){
		 deleteImagesInImageBucket(appConfig.getImagePublicFolder()+"/"+keyName);
	}
	public void deleteMasterImage(String keyName){
		 deleteImagesInImageBucket(appConfig.getImageMasterFolder()+"/"+keyName);
	}
	public void deleteVideoFile(String keyName){
		if(keyName==null|| keyName.trim().length()<=1){
			throw new RuntimeException("refuxed to do the delete operation on the s3 bucket:"+keyName);
		}
		logger.info("deleting the video in the video bucket:"+keyName+"  in the bucket:"+appConfig.getVideoBucket());
		AmazonS3 s3=getAmazonS3();		
		s3.deleteObject(new DeleteObjectRequest(appConfig.getVideoBucket(), keyName));
	}
	public List<FileItem> listGenereratedImages(String prefix, int startIndex, int numberOfRecords){		
		String path=appConfig.getImagePublicFolder();
		if(prefix!=null){
			path=path+"/"+prefix;
		}		
		List<FileItem>  files=listFiles(appConfig.getImageBucket(),path,startIndex,numberOfRecords,null);
		return files;
	}
	
	public MediaFilesLocation listMasterImagesInImagesBucket(String prefix,int startIndex, int numberOfRecords){
		MediaFilesLocation videoFilesLocations=new MediaFilesLocation();
		videoFilesLocations.setBaseUrl(appConfig.getS3imagesURL());
		String path=appConfig.getImageMasterFolder();
		if(prefix!=null){
			path=path+"/"+prefix;
		}
		List<FileItem>  files=listFiles(appConfig.getImageBucket(),path,startIndex,numberOfRecords,null);
		videoFilesLocations.setFiles(files);
		if(files.size()>0){			
			for(FileItem item:files){
				if(item.getFile().length() > ( appConfig.getImageMasterFolder().length()+1) ){
					item.setFile(item.getFile().substring(appConfig.getImageMasterFolder().length()+1));
				}				
			}
			
		}
		logger.info("******number of s3 file for prefix=["+prefix+"]:"+videoFilesLocations.getFiles().size());		
		return videoFilesLocations;
	}
	
	public MediaFilesLocation uploadVideoFile(String filepath, String destFilename){
		MediaFilesLocation videoFileLocation=listFilesInVideoBucket(destFilename,0,-1,null);
		if(videoFileLocation.getFiles().size()>0){
			throw new RuntimeException("The file already exist on the video buccket:"+videoFileLocation.getFiles().get(0));
		}
		AmazonS3 s3Client=getAmazonS3();
		File file=new File(filepath);
		s3Client.putObject(new PutObjectRequest(appConfig.getVideoBucket(), destFilename, file));
		videoFileLocation.addFilename(destFilename);
		return videoFileLocation;		
	}
	public MediaFilesLocation uploadMasterImageFile(String filepath, String destFilename){
		MediaFilesLocation imageFileLocation=listMasterImagesInImagesBucket(destFilename,0,-1);
		if(imageFileLocation.getFiles().size()>0){
			throw new RuntimeException("The file already exist on the image buccket:"+imageFileLocation.getFiles().get(0));
		}
		AmazonS3 s3Client=getAmazonS3();
		File file=new File(filepath);
		
		s3Client.putObject(new PutObjectRequest(appConfig.getImageBucket(), appConfig.getImageMasterFolder()+"/"+destFilename, file));
		imageFileLocation.addFilename(destFilename);
		return imageFileLocation;		
	}
	public VideoFileList listVideoFileItem(String prefix, int startIndex, int numberOfRecords){
		MediaFilesLocation videoFileLocation=listFilesInVideoBucket(prefix,startIndex,numberOfRecords,null);
		List<VideoFileItem> videos=new ArrayList<VideoFileItem>();
		if(videoFileLocation.getFiles()!=null && videoFileLocation.getFiles().size()>0){
			for(FileItem fitem:videoFileLocation.getFiles()){
				VideoFileItem vitem=new VideoFileItem();
				vitem.setFile(fitem.getFile());	
				vitem.setLastModifidDate(fitem.getLastModifiedDate());
				String materialId=GenericUtilities.fileNameToMaterialID(fitem.getFile());
				List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(materialId+"%");
				if(matchedEpisodes.size()>0){
					vitem.setEpisodeTitle(matchedEpisodes.get(0).getTitle());
					vitem.setEpisodeId(matchedEpisodes.get(0).getId());
					vitem.setProgrammeNumber(matchedEpisodes.get(0).getCtrPrg());
					Double scheduledDuration=matchedEpisodes.get(0).getDurationScheduled();
					Double uploadedDuration=matchedEpisodes.get(0).getDurationUploaded();
					if(scheduledDuration!=null && uploadedDuration!=null){
						Double errorValue=(uploadedDuration-scheduledDuration);
						vitem.setDurationError(errorValue.longValue());
					}
				}
				videos.add(vitem);
			}
		}
		VideoFileList videoFileList=new VideoFileList();
		videoFileList.setBaseUrl(videoFileLocation.getBaseUrl());
		videoFileList.setFiles(videos);
		return videoFileList;
	}
	
	public VideoFileItem buildVideoFileItem(String filename){
		VideoFileItem vitem=new VideoFileItem();
		vitem.setFile(filename);	
		vitem.setLastModifidDate(new Date());
		String materialId=GenericUtilities.fileNameToMaterialID(filename);
		List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(materialId+"%");
		if(matchedEpisodes.size()>0){
			vitem.setEpisodeTitle(matchedEpisodes.get(0).getTitle());
			vitem.setEpisodeId(matchedEpisodes.get(0).getId());
			vitem.setProgrammeNumber(matchedEpisodes.get(0).getCtrPrg());
			Double scheduledDuration=matchedEpisodes.get(0).getDurationScheduled();
			Double uploadedDuration=matchedEpisodes.get(0).getDurationUploaded();
			if(scheduledDuration!=null && uploadedDuration!=null){
				Double errorValue=(uploadedDuration-scheduledDuration);
				vitem.setDurationError(errorValue.longValue());
			}
		}
		return vitem;
	}
	public MediaFilesLocation listMasterImageItem(String prefix, int startIndex, int numberOfRecords){		
		return listMasterImagesInImagesBucket(prefix,startIndex,numberOfRecords);		
	}
	
	public String getFullVideoURL(String fileName){
		return appConfig.getS3videoURL()+"/"+fileName;
	}
	public String getMasterImageFullURL(String fileName){
		return appConfig.getS3imagesURL()+"/"+appConfig.getImageMasterFolder()+"/"+fileName;		
	}
	
	
	public S3FileSignatureData createS3FileSignatureData(S3FileSignatureData data){		    					
	    try{
		    	String awsKey=awsConfig.getAwsAccessKeyId();
		    	String awsSecret=awsConfig.getAwsSecretAccessKey();
		    	String awsRegion=awsConfig.getAwsRegion();		    	
		    	data.calculateExpirationDate();
		    	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
		    	String datestamp=dateFormat.format(new Date());	    	
		    	data.setXamzDate(datestamp+"T000000Z");
		    	data.setAccessKey(awsKey);
		    	data.setSuccessActionStatus("201");
		    	data.setXamzCredential(awsKey+"/"+datestamp+"/"+awsRegion+"/s3/aws4_request");
		    	String policyDocument=GenericUtilities.getS3UploadPolicy(data).replace("\r","").replace("\n", "").replace("  "," ").trim();	    		    	
		    	String policy=GenericUtilities.toBase64(policyDocument);	    		    	
		        String signature = GenericUtilities.awsV4Sign(awsSecret,datestamp,awsRegion, "s3",policy);	         
		    	data.setPolicy(policy);
				data.setXamzSignature(signature);			
				data.setBaseURL("https://" + data.getBucket() + ".s3.amazonaws.com");
			}
			catch(Exception e){
				logger.error(e+" while singing the policy document",e);
			}
	    return data;
	}
	public String generatedPresignedURL(String url, int expiredInSeconds){
		return generatedPresignedURL(url,expiredInSeconds,HttpMethod.GET);
	}
	public String generatedPresignedURL(String url, int expiredInSeconds, HttpMethod method){
		if(url==null){
			return null;
		}
		url=url.trim();
		if(url.length()==0){
			return null;
		}
		String baseURL=appConfig.getS3videoURL();
		
		
		if(!url.startsWith(baseURL)){
			logger.error("*****Base URL does not match:"+baseURL+": with:"+url);
			throw new IllegalArgumentException("the url cannot be signed");
		}
		String filename=url.substring(baseURL.length());
		if(filename.startsWith("/")){
			filename=filename.substring(1);
		}
		logger.info("The video file to sign:"+filename);
		java.util.Date expiration = new java.util.Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += 1000 * expiredInSeconds; // Add 1 hour.
		expiration.setTime(milliSeconds);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(appConfig.getVideoBucket(), filename);
		generatePresignedUrlRequest.setMethod(method); 
		generatePresignedUrlRequest.setExpiration(expiration);
		AmazonS3 s3=getAmazonS3();
		URL signedURL= s3.generatePresignedUrl(generatePresignedUrlRequest);
		return signedURL.toString();
	}
	public String generatedPresignedURL(String bucketName, String keyName,int expiredInSeconds, HttpMethod method){
		logger.info("signing  bucketName=["+bucketName+"]keyBame=["+keyName+"]");
		java.util.Date expiration = new java.util.Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += 1000 * expiredInSeconds; // Add 1 hour.
		expiration.setTime(milliSeconds);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, keyName);
		generatePresignedUrlRequest.setMethod(method); 
		generatePresignedUrlRequest.setExpiration(expiration);
		AmazonS3 s3=getAmazonS3();
		URL signedURL= s3.generatePresignedUrl(generatePresignedUrlRequest);
		return signedURL.toString();
	}
   public String  invalidateCloudFrontCache(Collection<String> paths, String distributionId){
	   
	   AWSCredentials credentials=getAWSCredentials();
	   AmazonCloudFrontClient client = new AmazonCloudFrontClient(credentials);	   
	   Paths invalidation_paths = new Paths().withItems(paths).withQuantity(paths.size());
	   String requestId=String.valueOf(System.currentTimeMillis());	   	   
	   InvalidationBatch invalidation_batch = new InvalidationBatch(invalidation_paths, requestId);
	   CreateInvalidationRequest invalidation = new CreateInvalidationRequest(distributionId, invalidation_batch);
	   CreateInvalidationResult ret = client.createInvalidation(invalidation);
	   return requestId;
   }
   public String invalidateImageCDNCache(Collection<String> paths){
	   if(awsConfig.getImageCDNDistributionID()==null || awsConfig.getImageCDNDistributionID().length()==0){
		   return null;		   
	   }
	   return invalidateCloudFrontCache(paths,awsConfig.getImageCDNDistributionID());
   }
   public String  invalidateCDNClientImageCache(String imageFilename){
		List<String> files=new ArrayList<String>();
		if(appConfig.getImageClientFolder()==null|| appConfig.getImageClientFolder().trim().length()==0){
					return null;
		}
		String filepath="/"+appConfig.getImageClientFolder()+"/"+imageFilename;
		files.add(filepath);
		logger.info("Invalidating the invalidateCDNClientImageCache:"+filepath);
		return invalidateImageCDNCache(files);
		
	}
  
}
