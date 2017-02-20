package uk.co.boxnetwork.mule.transformers.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BoxMasterImageTransformer extends BoxRestTransformer{
	
	@Autowired
	private S3BucketService s3uckerService;
	
	@Autowired
	MetadataMaintainanceService metadataMaintainanceService;
	
	@Autowired
	AppConfig appConfig;

	
		
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){
		logger.info("boximage.get request is received");
		SearchParam searchParam=new SearchParam(message,appConfig,SearchParam.SearchParamType.S3ITEM);
		int start=0;
		int numberOfRecords=-1;
		if(searchParam.getStart()!=null){
			start=searchParam.getStart();
		}
		if(searchParam.getLimit()!=null){
			numberOfRecords=searchParam.getLimit();
		}
		return s3uckerService.listMasterImageItem(searchParam.getPrefix(),start,numberOfRecords);		
	}
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){
		Set<String> attachementnames=message.getInboundAttachmentNames();
				
		for(String attachementname:attachementnames){
			logger.info("receiving::::"+attachementname+" for images");
			DataHandler dataHandler=message.getInboundAttachment(attachementname);
			String filepath="/data/"+attachementname;
			InputStream in;
			OutputStream out=null;
			try {
				in = dataHandler.getInputStream();
				out=new FileOutputStream(filepath);
				StreamUtils.copy(in, out);
				out.close();
				Object obj=s3uckerService.uploadMasterImageFile(filepath, attachementname);
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
			return deleteSeriesImage(pathcomonents[1], pathcomonents[2]);
		}
		else if(pathcomonents.length>2 && pathcomonents[0].equals("episode")){
			return deleteEpisodeImage(pathcomonents[1], pathcomonents[2]);
		}
		else if(pathcomonents.length>2 && pathcomonents[0].equals("seriesgroup")){
			return deleteSeriesGroupImage(pathcomonents[1], pathcomonents[2]);
		}
		else{
			return returnError("The Method DELETE for this path not supported",message);
		}
	}
	private Object deleteSeriesImage(String seriesid, String imagefilename){
  		Long id=Long.valueOf(seriesid);
  		return metadataMaintainanceService.deleteSeriesImage(id, imagefilename);  		
	}
	private Object deleteSeriesGroupImage(String seriesgroupid, String imagefilename){
  		Long id=Long.valueOf(seriesgroupid);
  		return metadataMaintainanceService.deleteSeriesGroupImage(id, imagefilename);  		
	}
	private Object deleteEpisodeImage(String episodeid, String imagefilename){
  		Long id=Long.valueOf(episodeid);
  		return metadataMaintainanceService.deleteEpisodeImage(id, imagefilename);  		
	}
			
	
}