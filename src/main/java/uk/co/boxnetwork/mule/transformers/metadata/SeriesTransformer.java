package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.http.internal.ParameterMap;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.UpdatePraram;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MediaApplicationID;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;

public class SeriesTransformer  extends BoxRestTransformer{
	
	@Autowired
	MetadataService metadataService;
	
	@Autowired
	AppConfig appConfig;
	
	
	@Override
	protected  Object processGET(MuleMessage message,BoxOperator operator, String outputEncoding){
		MediaApplicationID applicationId=getApplicationId(operator);	
		String seriesid=MuleRestUtil.getPathPath(message);
		if(seriesid==null || seriesid.length()==0){
			return getAllSeries(message,outputEncoding,applicationId);
		}
		else{
			return getAnSeries(seriesid, message,outputEncoding);				
		}
	}
	private Object getAllSeries(MuleMessage message, String outputEncoding,MediaApplicationID applicationId){
		SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.SERIES);					    
		return metadataService.getAllSeries(searchParam,applicationId);					
						
	}
    private Object getAnSeries(String seriesid, MuleMessage message, String outputEncoding){
	  		Long id=Long.valueOf(seriesid);
		    return metadataService.getSeriesById(id);		    
	}
   
    @Override	
   protected Object processPUT(MuleMessage message,BoxOperator operator, String outputEncoding) throws Exception{
	   String seriesid=MuleRestUtil.getPathPath(message);
	   if(seriesid==null||seriesid.length()==0){
		   return new ErrorMessage("The episodeid is missing in DELETE");
	   }
	   Long id=Long.valueOf(seriesid);
	   uk.co.boxnetwork.data.Series  series=null;
	   
	   if(message.getPayload() instanceof uk.co.boxnetwork.data.Series){
		   series=(uk.co.boxnetwork.data.Series)message.getPayload();			   
	   }
	   else{			   
		   		String seriesInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****updating series"+seriesInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				
				
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				series = objectMapper.readValue(seriesInJson, uk.co.boxnetwork.data.Series.class);
	   }
	   UpdatePraram updatePraram=new UpdatePraram(message,appConfig);
	   if(updatePraram.isSwitchEpisodeSeriesGroup()){
		   metadataService.switchSeriesGroup(id,series);
		   
	   }
	   else{
		   metadataService.update(id,series);
	   }
	   
	   if(appConfig.getBrightcoveStatus()){
		   return metadataService.publishMetadatatoBCBySeriesId(id);
	   }
	   else{
		   return series;
	   }
	   
	   						 
	}
    @Override
	protected Object processDELETE(MuleMessage message, BoxOperator operator,String outputEncoding){	
		String seriesid=MuleRestUtil.getPathPath(message);
		if(seriesid==null || seriesid.length()==0){
			return returnError("Do not support delete all series",message);
		}
		else{
			Long id=Long.valueOf(seriesid);
			uk.co.boxnetwork.data.Series series=metadataService.getSeriesById(id);
			metadataService.deleteSeriesById(id);
			logger.info("Series is deleted successfully id="+id);
			return series;
		}			 
	} 
   
    
    @Override
    protected Object processPOST(MuleMessage message, BoxOperator operator,String outputEncoding){
		try{	
    		    String episodeInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****Posted a new series:"+episodeInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   uk.co.boxnetwork.data.Series series = objectMapper.readValue(episodeInJson, uk.co.boxnetwork.data.Series.class);
			   if(GenericUtilities.isNotValidTitle(series.getName())){
				   return returnError("Series title is not valid",message);				  
			   }				   
			   
			   uk.co.boxnetwork.data.Series sr=metadataService.createNewSeries(series);
			   return sr;
			   
		}
		catch(Exception e){
			throw new RuntimeException("proesing post:"+e,e);    			
		}
    }
    
    @Override
	protected Object processPATCH(MuleMessage message, BoxOperator operator,String outputEncoding) throws Exception{
		String programmeid=MuleRestUtil.getPathPath(message);
 	   
 	   if(programmeid==null||programmeid.length()==0){
 		   return new ErrorMessage("The programmeid is missing in PATCH");
 	   }
		   Long id=Long.valueOf(programmeid);
		   uk.co.boxnetwork.data.Series  programme=null;
		   
		   if(message.getPayload() instanceof uk.co.boxnetwork.data.Series){
			   programme=(uk.co.boxnetwork.data.Series)message.getPayload();			   
		   }
		   else{			   
			   		String seriesInJson=(String)message.getPayloadAsString();		   
				   logger.info("*****updating series"+seriesInJson+"****");
				   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
					
					
					objectMapper.setSerializationInclusion(Include.NON_NULL);
					programme = objectMapper.readValue(seriesInJson, uk.co.boxnetwork.data.Series.class);
		   } 		   
		   metadataService.patch(id,programme);
		   return programme;
		   
		
	}
    
}