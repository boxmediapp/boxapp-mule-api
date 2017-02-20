package uk.co.boxnetwork.mule.transformers.bc;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class BCImportFromBCWithCSV extends BoxRestTransformer{
	
	@Autowired
	MetadataMaintainanceService metadataRepository;
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){
			String csvContent=null;
			
			try {
				    csvContent = (String)message.getPayloadAsString();				    
				    //return metadataRepository.copyVideoFilesBucketToBucket(csvContent);
				    return metadataRepository.importCSVFromBrightcove(csvContent);
				    //return metadataRepository.verifyEpisodes();
												
				} catch (Exception e) {
					logger.error("error is processing ingest:"+message.getPayload().getClass().getName()+" csv=["+csvContent+"]");
					throw new RuntimeException(e+" whule processing the payload",e);
				}		   
				   
					
		   }		  		  		  		  		  	    			 

	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){

		try {
			return metadataRepository.createCSVBeBoxEpisodesFrom();
		} catch (Exception e) {
			logger.error(e+" while trying to get the content that is created directly in bc",e);
			throw new RuntimeException(e+" while getting the bc created media entries");
		}
		
	}
	


}
	
	  
