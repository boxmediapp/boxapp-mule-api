package uk.co.boxnetwork.mule.transformers.bc;

import java.util.Calendar;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

import uk.co.boxnetwork.components.BCAnalyticService;
import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.soundmouse.SoundMouseData;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class BCAnalyticsDataTransformer extends  BoxRestTransformer{
	@Autowired
	BCAnalyticService analyticService;
	
	@Autowired
	AppConfig appConfig;
		
	@Autowired
    private BCConfiguration bcConfiguration;

	@Override
		protected Object processGET(MuleMessage message, BoxOperator operator, String outputEncoding){
			 
			 
		     SearchParam searchParam=new SearchParam(message, appConfig, SearchParam.SearchParamType.BCREPORT);
		     String fromDate=searchParam.getFrom();
		     String toDate=searchParam.getTo();
		     String videoid=searchParam.getVideoid();
		     String filter=null;
		     
		     if(videoid!=null){
						filter="video=="+videoid;
			 }
		     if(fromDate==null|| toDate==null){
					SoundMouseData soundMouseData=new SoundMouseData();
					fromDate=soundMouseData.getFrom();
					toDate=soundMouseData.getTo();						
			  }
				
			 try{	
				 
		     return analyticService.getReport(searchParam.getLimit(), searchParam.getStart(), bcConfiguration.getAnalyticsDimension().split(","),bcConfiguration.getAnalyticsFields().split(","), fromDate, toDate,filter);
		     
			 }
			 catch(Exception e){
				 logger.error(e+" while gettting bc report",e);				 
				 if(e instanceof HttpStatusCodeException){
					 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
					 logger.info("******ERROR in RestTemplate*****"+errorResponse);
					 return returnError(e.toString(),message);
				}				
				return returnError(e.toString(),message);
			 }
	}
	
    
	
	
      
   
}
