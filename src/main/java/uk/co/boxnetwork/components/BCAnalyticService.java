package uk.co.boxnetwork.components;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.bc.BCAccessToken;
import uk.co.boxnetwork.data.bc.BCAnalyticsResponse;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class BCAnalyticService {
	private static final Logger logger=LoggerFactory.getLogger(BCAnalyticService.class);
	@Autowired
    private BCConfiguration bcConfiguration;
	
	
	@Autowired
	private AppConfig appConfig;
	
	
	@Autowired
	private BCAccessTokenService bcAccessToenService;
	
	public  BCAnalyticsResponse jsonToBCAnalyticsResponse(String responseInJson){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);	
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		BCAnalyticsResponse bcResponse;
		try {
			bcResponse = objectMapper.readValue(responseInJson, BCAnalyticsResponse.class);
			return bcResponse;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data",e);
			logger.error(responseInJson);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	public String getReport(Integer limit, Integer offset, String dimensions[],String fields[], String fromDate, String toDate, String filter){
		BCAccessToken accessToken=bcAccessToenService.getAccessToken();		
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		
		String analyticURL=bcConfiguration.analyticsURL(limit, offset, GenericUtilities.arrayToSeparatedString(dimensions, ","),GenericUtilities.arrayToSeparatedString(fields, ","),fromDate,toDate,filter);
		
		
		logger.info("analyticURL=["+analyticURL+"]");
		
	    ResponseEntity<String> responseEntity = rest.exchange(analyticURL, HttpMethod.GET, requestEntity, String.class);	    	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();		
	}
	
	public BCAnalyticsResponse getMediaItemReport(String fromDate, String toDate, String videoid){
		String filter=null;
		if(videoid!=null){
			filter="video=="+videoid;
		}
		String analyticResultString=getReport(null, null, bcConfiguration.getAnalyticsDimension().split(","),bcConfiguration.getAnalyticsFields().split(","), fromDate, toDate,filter);
		logger.info("received the anlytic response:"+analyticResultString);
		return jsonToBCAnalyticsResponse(analyticResultString);
	}
	
}
