package uk.co.boxnetwork.components;

import java.io.BufferedInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import uk.co.boxnetwork.data.BasicAuthenticatedURLConfiguration;
import uk.co.boxnetwork.data.C4Metadata;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.bc.BCAccessToken;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.data.s3.MediaFilesLocation;
import uk.co.boxnetwork.model.ComplianceInformation;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class ImportC4ScheduleService {
	private static final Logger logger=LoggerFactory.getLogger(ImportC4ScheduleService.class);
 @Autowired
 @Qualifier("c4ScheduleConfiguration")
 private BasicAuthenticatedURLConfiguration c4ScheduleConfiguration;
 
 @Autowired
 @Qualifier("c4CertificationConfiguration")
 private BasicAuthenticatedURLConfiguration c4CertificationConfiguration;

  @Autowired
 private BoxMedataRepository boxMetadataRepository;
	
  @Autowired
  C4ScheduleSoapParser c4SchedulerParser;
  
  @Autowired
  C4CertificationSoapParser c4CertificationSoapParser;
	
  @Autowired
  S3BucketService s3BucketService;
  
  @Autowired
  MetadataService metadataService;
  
 public String requestSchedulService(ImportScheduleRequest request){	
		RestTemplate rest=new RestTemplate();
		
		logger.info("c4ScheduleConfiguration="+c4ScheduleConfiguration+" c4CertificationConfiguration=["+c4CertificationConfiguration+"]request="+request);
		 String requestContent=GenericUtilities.readFileContent("data/schedule/request.xml");
		 requestContent=requestContent.replace("${channelId}",request.getChannelId());
		 requestContent=requestContent.replace("${fromDate}",request.getFromDate());
		 requestContent=requestContent.replace("${toDate}",request.getToDate());
		 requestContent=requestContent.replace("${type}",request.getType());
		 requestContent=requestContent.replace("${info}",request.getInfo());
		 
		 logger.info("*******requestContent**************:"+requestContent);
		 
		
		
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();
		headers.add("Content-Type", "text/xml;charset=UTF-8");	    
		headers.add("Authorization", "Basic " + c4ScheduleConfiguration.basicAuthentication());
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestContent, headers);		
	    ResponseEntity<String> responseEntity = rest.exchange(c4ScheduleConfiguration.getUrl(), HttpMethod.POST, requestEntity, String.class);
	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();	    	
	}
 
 
 public String requestCertification(String assetId){	
		RestTemplate rest=new RestTemplate();
		
		logger.info("Certification request assetId="+assetId);		
		 String requestContent=GenericUtilities.readFileContent("data/certification/request.xml");
		 requestContent=requestContent.replace("${assetId}",assetId);
		 
		 logger.info("***certification requestContent**************:"+requestContent);
		 				
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();
		headers.add("Content-Type", "text/xml;charset=UTF-8");	    
		headers.add("Authorization", "Basic " + c4CertificationConfiguration.basicAuthentication());
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestContent, headers);		
	    ResponseEntity<String> responseEntity = rest.exchange(c4CertificationConfiguration.getUrl(), HttpMethod.POST, requestEntity, String.class);
	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();	    	
	}
  
 public void requestS3(ScheduleEvent event) throws DocumentException{
	 if(event.getEpisode()!=null){
		 metadataService.requestS3(event.getEpisode()); 		 
	 }	 
 }
 
     
  public void requestCertification(ScheduleEvent event) throws DocumentException{
	 
	 if(event.getEpisode()!=null){
		 if(event.getEpisode().getAssetId()!=null && event.getEpisode().getAssetId().length()>0){
			 String certificationResponse=requestCertification(event.getEpisode().getAssetId());
			 Set<ComplianceInformation> complianceInformations=c4CertificationSoapParser.parse(certificationResponse);			 
			 event.getEpisode().setComplianceInformations(complianceInformations);			 
		 }
		
	 }
	 
 }

 
	public void parseAndImport(String scheduleDocument){		
		try {			
			C4Metadata c4metadata=c4SchedulerParser.parse(scheduleDocument);
			for(ScheduleEvent event: c4metadata.getScheduleEvents()){
				requestCertification(event);
				requestS3(event);
				boxMetadataRepository.createEvent(event);
				
			}
			
		} catch (DocumentException e) {
			logger.error("Error in Parsing schedule document",e);
		   throw new RuntimeException(e+ "in parsig the schedule document",e);
		}
		
	}

 
 public void importSchedule(ImportScheduleRequest request){	 	 
	 String schedule=requestSchedulService(request);
	 try{
		 parseAndImport(schedule);
	 }
	 catch(Exception ex){
		 logger.error("error is parsing the schedule", ex);
		 logger.error("response returned from the pirate:"+schedule);		 
	 }
 }
 
	
}
