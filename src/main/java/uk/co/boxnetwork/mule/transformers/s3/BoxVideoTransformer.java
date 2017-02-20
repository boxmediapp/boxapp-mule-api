package uk.co.boxnetwork.mule.transformers.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.s3.VideoFileItem;
import uk.co.boxnetwork.data.s3.VideoFileList;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxVideoTransformer extends BoxRestTransformer{
	
	@Autowired
	private S3BucketService s3uckerService;
	
	@Autowired
	MetadataMaintainanceService metadataMaintainanceService;
	
	@Autowired
	AppConfig appConfig;
	
	protected Object processGET(MuleMessage message, String outputEncoding){
		
		SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.S3ITEM);
		int start=0;
		int numberOfRecords=-1;
		if(searchParam.getStart()!=null){
			start=searchParam.getStart();
		}
		if(searchParam.getLimit()!=null){
			numberOfRecords=searchParam.getLimit();
		}		
		String searchText=searchParam.getPrefix();
		if(searchText!=null){
			searchText=searchText.trim();
		}
		if(searchText==null|| searchText.length()==0){
			return s3uckerService.listVideoFileItem(searchParam.getPrefix(),start,numberOfRecords);
		}
		else{
			
			    logger.info("Searching the s3 item with the search:"+searchText);
				VideoFileList videolist=s3uckerService.listVideoFileItem(null,0,Integer.MAX_VALUE);
				List<VideoFileItem> files=videolist.getFiles();
				if(files==null||files.size()==0){
					logger.info("the s3 bucket is empty");
					return videolist;
				}
				else{
					logger.info("number of records in the s3:"+files.size()+" and search="+searchText);
				}
				List<VideoFileItem> matchedFiles=new ArrayList<VideoFileItem>();
				for(int i=0;i<files.size();i++){
					VideoFileItem item=files.get(i);
					if(item.getFile().startsWith(searchText)){
						matchedFiles.add(item);
					}
					else if(item.getEpisodeTitle()!=null && item.getEpisodeTitle().toLowerCase().startsWith(searchText.toLowerCase())){
						matchedFiles.add(item);
					}
					else if(item.getProgrammeNumber()!=null && item.getProgrammeNumber().startsWith(searchText)){
						matchedFiles.add(item);
					}
				}
				logger.info("marched s3 files:"+matchedFiles.size());
				List<VideoFileItem> selectedFiles=new ArrayList<VideoFileItem>();
				int count=0;
				
				for(int i=start;count<numberOfRecords && i<matchedFiles.size();i++){
					selectedFiles.add(matchedFiles.get(i));
					count++;
				}
				videolist.setFiles(selectedFiles);
			    return videolist;
		}
				
	}
	protected Object processPOST(MuleMessage message, String outputEncoding){
		Set<String> attachementnames=message.getInboundAttachmentNames();
				
		for(String attachementname:attachementnames){
			logger.info("reciving::::"+attachementname);
			DataHandler dataHandler=message.getInboundAttachment(attachementname);
			String filepath="/data/"+attachementname;
			InputStream in;
			OutputStream out=null;
			try {
				in = dataHandler.getInputStream();
				out=new FileOutputStream(filepath);
				StreamUtils.copy(in, out);
				out.close();
				Object obj= s3uckerService.uploadVideoFile(filepath, attachementname);
				File fp=new File(filepath);
				fp.delete();
				return obj;
				
			} catch (IOException e) {
				logger.error(e+ " whilte recevimg the upload",e);
			}
			finally{
				try{
					if(out!=null){
						out.close();
					}					
				}
				catch(Exception e){
					logger.error(e+ " whilte close",e);
				}
				File file=new File(filepath);
				file.delete();
			}
		
			
			break;
		}
		logger.info("*******Completed***");
		return message.getPayload();		
	}
	
	
	protected Object processDELETE(MuleMessage message, String outputEncoding){			
		String path=MuleRestUtil.getPathPath(message);
		String pathcomonents[]=path.split("/");
		logger.info("path::::"+path);
		if(pathcomonents.length>2 && pathcomonents[0].equals("series")){
			return returnError("The Method DELETE for this path not supported",message);
		}
		else if(pathcomonents.length>2 && pathcomonents[0].equals("episode")){
			return deleteEpisodeVideo(pathcomonents[1], pathcomonents[2]);
		}
		else{
			return returnError("The Method DELETE for this path not supported",message);
		}
	}
	
	private Object deleteEpisodeVideo(String episodeid, String videofilename){
  		Long id=Long.valueOf(episodeid);
  		return metadataMaintainanceService.deleteEpisodeVideoFile(id, videofilename);  		
	}
	
	
}