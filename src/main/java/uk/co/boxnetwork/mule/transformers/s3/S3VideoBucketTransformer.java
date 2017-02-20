package uk.co.boxnetwork.mule.transformers.s3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.data.s3.MediaFilesLocation;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class S3VideoBucketTransformer extends BoxRestTransformer{
	@Autowired
	private S3BucketService s3uckerService;
	
	@Autowired
	AppConfig appConfig;
	
	
	protected Object processGET(MuleMessage message, String outputEncoding){
		logger.info("s3 list request is received");
		SearchParam searchParam=new SearchParam(message,appConfig,SearchParam.SearchParamType.S3ITEM);
		int start=0;
		int numberOfRecords=-1;
		if(searchParam.getStart()!=null){
			start=searchParam.getStart();
		}
		if(searchParam.getLimit()!=null){
			numberOfRecords=searchParam.getLimit();
		}
		MediaFilesLocation files=s3uckerService.listFilesInVideoBucket(null,start,numberOfRecords,searchParam.getFile());		
		return 	files.getFiles();
	
	}
}
