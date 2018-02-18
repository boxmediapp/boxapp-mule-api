package uk.co.boxnetwork.mule.transformers.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.model.S3Images;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

import uk.co.boxnetwork.util.GenericUtilities;
public class S3ImageTransformer  extends BoxRestTransformer{
	
	@Autowired
	private S3BucketService s3uckerService;
	
	
	@Autowired
	AppConfig appConfig;

	@Autowired
	BoxMedataRepository boxMetaRepository;
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){		
		ParameterMap queryparams=message.getInboundProperty("http.query.params");
		if(queryparams==null){			
			returnError("Parameter missing",message, 400);
		}		
		String episodeidInString=queryparams.get("episodeid");
		String programmeInString=queryparams.get("programmeid");
		String collectionidInString=queryparams.get("collectionid");
		
		Long episodeid=null;
		Long programmeid=null;
		Long collectionid=null;
		if(episodeidInString!=null){
			episodeid=Long.valueOf(episodeidInString);			
		}
		if(programmeInString!=null){
			programmeid=Long.valueOf(programmeInString);			
		}
		if(collectionidInString!=null){
			collectionid=Long.valueOf(collectionidInString);
		}
		
		if(episodeid!=null){
			Episode episode=boxMetaRepository.findEpisodeById(episodeid);
			if(episode==null){
				return returnError("Episode not found",message, 404);
			}
			return S3Images.createEpisodeS3Images(episode, appConfig, s3uckerService);						
		}
		else if(programmeid!=null){
			Series programme=boxMetaRepository.findSeriesById(programmeid);
			if(programme==null){
				return returnError("Programme not found",message, 404);
			}
			return S3Images.createProgrammeS3Images(programme, appConfig, s3uckerService);
			
		}
		else if(collectionid!=null){
			SeriesGroup programmeCollection=boxMetaRepository.findSeriesGroupById(collectionid);
			if(programmeCollection==null){
				return returnError("programmeCollection not found",message, 404);
			}
			return S3Images.createProgrammeCollectionS3Images(programmeCollection, appConfig, s3uckerService);
			
		}
		else{
			return returnError("collectionid/programmeid/collectionid is required",message, 400);
		}
			
		
	}
				
	
}