package uk.co.boxnetwork.mule.transformers.metadata;

import java.util.List;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.SeriesGroup;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;
import uk.co.boxnetwork.util.GenericUtilities;

public class SeriesGroupTransformer extends BoxRestTransformer{
	@Autowired
	MetadataService metadataService;
	
	@Autowired
	AppConfig appConfig;
	
	
			
	@Override
	protected  Object processGET(MuleMessage message, String outputEncoding){
			
		String seriesgroupdid=MuleRestUtil.getPathPath(message);
		if(seriesgroupdid==null || seriesgroupdid.length()==0){
			return getAllSeriesGroups(message,outputEncoding);
		}
		else{
			return getAnSeriesGroup(seriesgroupdid, message,outputEncoding);				
		}
	}
	private Object getAllSeriesGroups(MuleMessage message, String outputEncoding){
			SearchParam searchParam=new SearchParam(message,appConfig,SearchParam.SearchParamType.SERIESGROUP);
			return metadataService.getAllSeriesGroups(searchParam);			
	}
    private Object getAnSeriesGroup(String seriesgroupdid, MuleMessage message, String outputEncoding){
	  		Long id=Long.valueOf(seriesgroupdid);
		    return metadataService.getSeriesGroupById(id);		    
	}
   
   
    @Override	
   protected Object processPUT(MuleMessage message, String outputEncoding) throws Exception{
	   String seriesgroupid=MuleRestUtil.getPathPath(message);
	   if(seriesgroupid==null||seriesgroupid.length()==0){
		   return returnError("seriesgroupid is required",message);
	   }
	   Long id=Long.valueOf(seriesgroupid);
	   uk.co.boxnetwork.data.SeriesGroup  seriesgroup=null;
	   
	   if(message.getPayload() instanceof uk.co.boxnetwork.data.SeriesGroup){
		   seriesgroup=(uk.co.boxnetwork.data.SeriesGroup)message.getPayload();			   
	   }
	   else{			   
		   		String seriesGroupInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****updating:"+seriesGroupInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
				
				
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				seriesgroup = objectMapper.readValue(seriesGroupInJson, uk.co.boxnetwork.data.SeriesGroup.class);
	   }
	   metadataService.update(id,seriesgroup);	 
	   if(appConfig.getBrightcoveStatus()){
		   return metadataService.publishMetadatatoBCBySeriesGroupId(id);   
	   }
	   else{
		   return seriesgroup;
	   }
	   						 
	}
    
    @Override
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
		String seriesgroupid=MuleRestUtil.getPathPath(message);
		if(seriesgroupid==null || seriesgroupid.length()==0){
			return returnError("Do not support delete all seriesgroup",message);
		}
		else{
			Long id=Long.valueOf(seriesgroupid);
			uk.co.boxnetwork.data.SeriesGroup series=metadataService.getSeriesGroupById(id);
			metadataService.deleteSeriesGroupById(id);
			
			logger.info("SeriesGroup is deleted successfully id="+id);
			return series;
		}			 
	}
    
    @Override
    protected Object processPOST(MuleMessage message, String outputEncoding){
		try{	
    		    String episodeInJson=(String)message.getPayloadAsString();		   
			   logger.info("*****Posted a new series group:"+episodeInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   uk.co.boxnetwork.data.SeriesGroup seriesgroup = objectMapper.readValue(episodeInJson, uk.co.boxnetwork.data.SeriesGroup.class);
			   if(GenericUtilities.isNotValidTitle(seriesgroup.getTitle())){
				   return returnError("Series group title is not valid",message);				  
			   }			   				   
			   uk.co.boxnetwork.data.SeriesGroup sr=metadataService.createANewSeriesGroup(seriesgroup);
			   return sr;
			   
		}
		catch(Exception e){
			throw new RuntimeException("proesing post:"+e,e);    			
		}
    }
	
}
