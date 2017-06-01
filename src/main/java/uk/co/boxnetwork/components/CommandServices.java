package uk.co.boxnetwork.components;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.util.IOUtils;


import uk.co.boxnetwork.model.AppConfig;

@Service
public class CommandServices {
	private static final Logger logger=LoggerFactory.getLogger(CommandServices.class);
     
	@Autowired
	private AppConfig appConfig;
	
	@Autowired 
	private S3BucketService s3BucketService;
	
	public String inspectVideoFile(String sourceURL) throws IOException, InterruptedException{
		logger.info("inspecting the video sourceURL=["+sourceURL+"]");
    	ProcessBuilder pb = new ProcessBuilder("ffprobe", "-v", "quiet", "-print_format", "json","-show_format","-show_streams", sourceURL);
    	Map<String, String> env = System.getenv();
    	String pathvalue=env.get("PATH");
    	Map<String, String> penv = pb.environment();
    	penv.put("PATH",pathvalue+":/usr/local/bin");
   	 logger.info("****: executing the commands.....");
 	Process process = pb.start();    	 
 	int errCode = process.waitFor();    	    	
 	 String outputResult=IOUtils.toString(process.getInputStream());
 	 String errorOutput=IOUtils.toString(process.getErrorStream());
 	 logger.info("**************outputResult=["+outputResult+"]");
 	 logger.info("*************errorOutput=["+errorOutput+"]");
 	 if(errCode!=0){    		 
 		 return "errorCode="+errCode +" with error message:"+errorOutput;
 	 }
 	 else{
 		 return outputResult;
 	 }

    	
	}
	public void captureImageFromVideo(String sourceURL, Double secondsAt, String outFilename) throws IOException, InterruptedException{
		logger.info("executing the capture imahe sourceURL=["+sourceURL+"]secondsAt=["+secondsAt+"]outFilename=["+outFilename+"]");		
    	executeCommand("capture_image_from_video",sourceURL,String.valueOf(secondsAt),outFilename);
    	logger.info("completed capture");    	
	}
	public void convertFromMasterImage(String masterImage){
    	 logger.info("converting the master image:"+masterImage);   
    	 try {
    		 convertS3ImageFile(masterImage);
		} catch (Throwable e) {
			logger.error("Failed to convert s3 images:masterImage="+masterImage,e);			
		}
    }
    public void convertS3ImageFile(String masterImage) throws IOException, InterruptedException{
    	String destfilename=masterImage;
    	int ib=destfilename.lastIndexOf("/");
    	if(ib!=1){
    		destfilename=destfilename.substring(ib+1);
    		if(destfilename.length()==0){
    			throw new RuntimeException("filename should not end with slash");
    		}    		
    	}
    	executeCommand("convert_s3_image",appConfig.getImageBucket(),masterImage,destfilename, appConfig.getImagePublicFolder());
    }
    public void transcodeViodeFile(String sourcevideoFileName){
    	 logger.info("trabscoding video:"+sourcevideoFileName);   
    	 try {
    		 convertMovToMp4(sourcevideoFileName);
		} catch (Throwable e) {
			logger.error("Failed to transcode:"+sourcevideoFileName,e);			
		}
    	
    }
    public void convertMovToMp4(String sourcevideoFileName) throws IOException, InterruptedException{
    	if(appConfig.getTranscodeDestBucket().equals(appConfig.getTranscodeSourceBucket())){
        	logger.error("souce and dest bucket cannot be the same for transcoode");
        	return;
        }    	
    	
    	String filename=sourcevideoFileName;
    	int ib=filename.lastIndexOf("/");
    	if(ib!=1){
    		filename=filename.substring(ib+1);
    		if(filename.length()==0){
    			throw new RuntimeException("filename should not end with slash");
    		}    		
    	}
    	String videoURL=s3BucketService.generatedPresignedURL(appConfig.getTranscodeSourceBucket(), sourcevideoFileName, 3600, HttpMethod.GET);    	
    	videoURL=videoURL.replace("https","http");
    	
    	StringBuilder destpathBuilder=new StringBuilder();
    	destpathBuilder.append("s3://");
    	destpathBuilder.append(appConfig.getTranscodeDestBucket());
    	destpathBuilder.append("/");
    	if(appConfig.getTranscodeDestFileNamePrefix()!=null){    	
    		destpathBuilder.append(appConfig.getTranscodeDestFileNamePrefix());
    	}
    	String destfilename=filename;
    	ib=destfilename.indexOf(".");
    	if(ib>0){
    		destfilename=destfilename.substring(0,ib);
    	}
    	destfilename+=".mp4";    	
    	destpathBuilder.append(destfilename);
    	String fulldestpath=destpathBuilder.toString();    	
        logger.info("executing the mov_to_mp4_to_s3 command:videoURL=["+videoURL+"]filename=["+filename+"]fulldestpath=["+fulldestpath+"]");
    	executeCommand("mov_to_mp4_to_s3",videoURL,destfilename);
    	logger.info("completed transcoding:"+videoURL);
    	logger.info("now uploading the video:"+destfilename);
    	executeCommand("upload_to_s3",destfilename,fulldestpath);
    	logger.info("completed the upload to s3:"+fulldestpath);
    }
    public String getVideoDuration(String videoURL) throws IOException, InterruptedException{
        videoURL=videoURL.replace("https","http");
    	String outputResult=executeCommand("getvideo_duration",videoURL);
    	if(outputResult!=null){
    		outputResult=outputResult.trim();    		
    	}
    	
    	return outputResult;
    	
    }
    
    public String executeCommand(String scriptname, String... arguments) throws IOException, InterruptedException{
    	List<String> commands=new ArrayList<String>();
    	commands.add("/bin/bash");
    	String userhomedir=System.getProperty("user.home");
    	
    	commands.add(userhomedir+"/bcs3uploader/"+scriptname+".sh");
    	for(String arg:arguments){
    		logger.info("******argument:"+arg);
    		commands.add(arg);
    	}
    	Map<String, String> env = System.getenv();
    	String pathvalue=env.get("PATH");    	    	
    	
    	ProcessBuilder pb = new ProcessBuilder(commands);
    	Map<String, String> penv = pb.environment();
    	penv.put("PATH",pathvalue+":/usr/local/bin");
    	 logger.info("****: executing the commands.....");
    	Process process = pb.start();    	 
    	int errCode = process.waitFor();    	    	
    	 String outputResult=IOUtils.toString(process.getInputStream());
    	 String errorOutput=IOUtils.toString(process.getErrorStream());
    	 logger.info("**************outputResult=["+outputResult+"]");
    	 logger.info("*************errorOutput=["+errorOutput+"]");
    	 if(errCode!=0){    		 
    		 return "errorCode="+errCode +" with error message:"+errorOutput;
    	 }
    	 else{
    		 return outputResult;
    	 }
    }
    
    

    
    
    
	
}
