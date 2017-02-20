package uk.co.boxnetwork.mule.transformers.bc;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.http.internal.ParameterMap;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.transformers.metadata.EpisodeTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BCVideoPublisher extends BoxRestTransformer{
	
	@Autowired
	BCVideoService bcVideoService;
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){		
		String episodeid=MuleRestUtil.getPathPath(message);
		if(episodeid==null||episodeid.length()==0){
			   return new ErrorMessage("The episodeid is missing in POST");
		  }
		  Long id=Long.valueOf(episodeid);
		  return bcVideoService.publishEpisodeToBrightcove(id);		  
		  		  	    			 
	}
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
		String episodeid=MuleRestUtil.getPathPath(message);
		if(episodeid==null||episodeid.length()==0){
			   return new ErrorMessage("The episodeid is missing in DELETE");
		  }
		  Long id=Long.valueOf(episodeid);
		  return bcVideoService.deleteEpisodeFromBrightcove(id);			 
	}
	 
   
}
