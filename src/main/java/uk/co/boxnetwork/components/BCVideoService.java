package uk.co.boxnetwork.components;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.data.SearchParam;

import uk.co.boxnetwork.data.bc.BCAccessToken;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCEnvironmentType;
import uk.co.boxnetwork.data.bc.BCErrorMessage;
import uk.co.boxnetwork.data.bc.BCPlayListData;
import uk.co.boxnetwork.data.bc.BCRemoteAsset;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.bc.BCVideoIngestRequest;
import uk.co.boxnetwork.data.bc.BCVideoSource;
import uk.co.boxnetwork.data.bc.BcIngestResponse;
import uk.co.boxnetwork.data.generic.RestResponseMessage;
import uk.co.boxnetwork.model.AdvertisementRule;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.BCNotification;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.VideoStatus;
import uk.co.boxnetwork.util.GenericUtilities;


@Service
public class BCVideoService {
	private static final Logger logger=LoggerFactory.getLogger(BCVideoService.class);
	@Autowired
    private BCConfiguration configuration;
	
	
	@Autowired
	private AppConfig appConfig;
	
	
	@Autowired
	private BCAccessTokenService bcAccessToenService;
	
	@Autowired
	BoxMedataRepository metadataRepository;
	
	
	public void statusPublishedToBrightCove(Episode episode, BCVideoData video){
		   EpisodeStatus episodeStatus=episode.getEpisodeStatus();
		   episodeStatus.setMetadataStatus(MetadataStatus.PUBLISHED);
		   VideoStatus videoStatus=GenericUtilities.calculateVideoStatus(episode);
	    	if(videoStatus!=null){
	    		episodeStatus.setVideoStatus(videoStatus);
	    	}
	    	else{
	    		videoStatus=episodeStatus.getVideoStatus();
	    	}
	    	if(videoStatus==VideoStatus.TRANSCODED || videoStatus == VideoStatus.TRANSCODE_COMPLETE){
	    		if(video!=null && video.getComplete()!=null && (!video.getComplete()) ){
	    			episodeStatus.setVideoStatus(VideoStatus.NOT_COMPLETE_STATE);
	    		}	    		 
	    	}	    	
	    	else if(episodeStatus.getVideoStatus()==VideoStatus.NOT_COMPLETE_STATE){
	    		if(video!=null && video.getComplete()!=null && (video.getComplete()) ){
	    			episodeStatus.setVideoStatus(VideoStatus.TRANSCODE_COMPLETE);
	    		}			
	    	}
	    	
	    		
	    	metadataRepository.persistEpisodeStatus(episodeStatus);
	    }
	public void statusUnpublishedFromBrightCove(Episode episode){
	   episode.getEpisodeStatus().setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
	   episode.getEpisodeStatus().setVideoStatus(VideoStatus.NO_PLACEHOLDER);
	   metadataRepository.persistEpisodeStatus(episode.getEpisodeStatus());	
    }
	public void statusIngestVideoToBrightCove(Episode episode, String jobId){    	   
	   	 episode.getEpisodeStatus().setVideoStatus(VideoStatus.TRANSCODING);
	   	episode.getEpisodeStatus().setTranscodeJobId(jobId);
	   	metadataRepository.persistEpisodeStatus(episode.getEpisodeStatus());
  }
	
	public String  listVideo(String limit, String offset, String sort,String q){
		return listVideo(GenericUtilities.bcInteger(limit),GenericUtilities.bcInteger(offset),GenericUtilities.bcString(sort),GenericUtilities.bcString(q));		
	}
	public  BCVideoData[] getVideoList(String limit, String offset, String sort,String q){
		return getVideoList(GenericUtilities.bcInteger(limit),GenericUtilities.bcInteger(offset),GenericUtilities.bcString(sort),GenericUtilities.bcString(q));		
	}
	public String  listVideo(Integer limit, Integer offset, String sort,String q){			
		    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
			RestTemplate rest=new RestTemplate();
			//rest.setErrorHandler(new RestResponseHandler());
			HttpHeaders headers=new HttpHeaders();			
		    headers.add("Accept", "*/*");
			headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
			HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);	
			String videoURL=configuration.videoURL(limit, offset, sort,q);
			logger.info("videoURL=["+videoURL+"]");
		    ResponseEntity<String> responseEntity = rest.exchange(videoURL, HttpMethod.GET, requestEntity, String.class);
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    return responseEntity.getBody();
	}
	public BCVideoData[] getVideoList(Integer limit, Integer offset, String sort,String q){
		String videoInJson=listVideo(limit,offset,sort,q);		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
	    BCVideoData video;
		try {
			BCVideoData[] videos = objectMapper.readValue(videoInJson, BCVideoData[].class);
			return videos;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data",e);
			logger.error(videoInJson);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
public String  getViodeInJson(String videoid){	
		
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);		
		String url=configuration.videoURL(videoid);
		logger.info("getting video details from bc url:"+url);
	    ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
	    responseEntity.getStatusCode();	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info("::::::getViodeInJson:::statuscode:"+statusCode);
	    return responseEntity.getBody();
	    
   }
	public String  getViodeSourcesInJson(String videoid){			
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);		
	    ResponseEntity<String> responseEntity = rest.exchange(configuration.videoURL(videoid)+"/sources", HttpMethod.GET, requestEntity, String.class);
	    responseEntity.getStatusCode();	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info("::::::getViodeSourcesInJson:::statuscode:"+statusCode);
	    return responseEntity.getBody();
	    
   }
	
	
	
	
	private String updateVideo(BCVideoData videodata, String brightcoveid){
		   BCAccessToken accessToken=bcAccessToenService.getAccessToken();			
		   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();				
		   objectMapper.setSerializationInclusion(Include.NON_NULL);
			try {
				String videoInJson = objectMapper.writeValueAsString(videodata);				
				logger.info("About to PATCH to brightcove, brightcoveid=["+brightcoveid+"] content="+videoInJson);
				String videoURL=configuration.videoURL(brightcoveid);
					//httpMethod=HttpMethod.PATCH;
					//					int ib=videoURL.indexOf("/v1/");
					//					
					//					videoURL="http://192.168.0.16:8888/v1/"+videoURL.substring(ib+4);
					PostMethod m = new PostMethod(videoURL) {					
						        @Override public String getName() { return "PATCH"; }					
				   };
				   m.setRequestHeader("Authorization", "Bearer " + accessToken.getAccess_token());
				   m.setRequestEntity(new StringRequestEntity(videoInJson, "application/json", "UTF-8"));
				   HttpClient c = new HttpClient();				
				   int sc = c.executeMethod(m);
				   logger.info(":::::::::statuscode:"+sc);
				   return m.getResponseBodyAsString();				
			} catch (Exception e) {
				logger.error("error while parsing the brightcove video data",e);
				if(e instanceof HttpStatusCodeException){
					 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
					 logger.info("******ERROR in RestTemplate*****"+errorResponse);
					 throw new RuntimeException(errorResponse,e);
				}
				else{
					 throw new RuntimeException(e+" while updatting the video in brightcove",e);
				}
				
				
			}
	}
	private String  createVideo(BCVideoData videodata){			
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		
				
		
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		
		try {
			String videoInJson = objectMapper.writeValueAsString(videodata);
			
			logger.info("About to POST to brightcove, content="+videoInJson);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(videoInJson, headers);
			String videoURL=configuration.videoURL(null,null,null,null);
			
		    ResponseEntity<String> responseEntity = rest.exchange(videoURL,HttpMethod.POST , requestEntity, String.class);
		    
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    return responseEntity.getBody();
		
		} catch (Exception e) {
			logger.error("error while parsing the brightcove video data",e);
			if(e instanceof HttpStatusCodeException){
				 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
				 logger.error("******ERROR in RestTemplate*****"+errorResponse);
				 throw new RuntimeException(errorResponse,e);
			}
			else{
				 throw new RuntimeException(e+" while creating video in brightcove",e);
			}
			
			
		}
		
		
	    
   }
	
	private  String deleteVideo(String brightcoveID){
		brightcoveID=brightcoveID.trim();
		if(brightcoveID.length()<5){
			throw new RuntimeException("brightcoveID is too short for delete:"+brightcoveID);
		}
		 BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		 RestTemplate rest=new RestTemplate();			
		 HttpHeaders headers=new HttpHeaders();			
		 headers.add("Accept", "*/*");
		 headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
			try {
			
				
				logger.info("About to DELETE  from brightcoveID="+brightcoveID);
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
				String videoURL=configuration.videoURL(brightcoveID);				
			    ResponseEntity<String> responseEntity = rest.exchange(videoURL,HttpMethod.DELETE , requestEntity, String.class);			    
			    responseEntity.getStatusCode();	    
			    HttpStatus statusCode=responseEntity.getStatusCode();
			    
			    logger.info(":::::::::statuscode:"+statusCode);
			    if(!statusCode.is2xxSuccessful()){
			    	throw new RuntimeException("Returned not successfull response from the server:statusCode="+statusCode+":"+statusCode.getReasonPhrase());
			    }
			    return responseEntity.getBody();
			
			} catch (Exception e) {
				logger.error("error while parsing the brightcove video data",e);
				if(e instanceof HttpStatusCodeException){
					 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
					 logger.error("******ERROR in RestTemplate*****"+errorResponse);
					 throw new RuntimeException(errorResponse,e);
				}
				else{
					 throw new RuntimeException(e+" while creating video in brightcove",e);
				}
				
				
			}
	}
	private String sendMediaRequest(String requestInJson){
		RestTemplate rest=new RestTemplate();							
			logger.info("sending media api request to bc:"+requestInJson);
			MultiValueMap<String, Object> parts = 
			          new LinkedMultiValueMap<String, Object>();			
			parts.add("JSONRPC",requestInJson);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			HttpEntity<MultiValueMap<String, Object>> requestEntity =
			          new HttpEntity<MultiValueMap<String, Object>>(parts, headers);
			ResponseEntity<String> responseEntity =
					rest.exchange(configuration.getMediaapiurl(), 
			                  HttpMethod.POST, requestEntity, String.class);
			HttpStatus statusCode=responseEntity.getStatusCode();	    
		    
		    logger.info(":::::::::statuscode:"+statusCode);
		    String responseBody= responseEntity.getBody();
		    logger.info("add media api response received:"+responseBody);
		    return responseBody;
	}
	
	
	 
	 private void publishThumnnailImage(Episode episode,BCVideoData videoData){
		    String imagename=episode.calculateImageURL();
			if(imagename==null || imagename.trim().length()==0){
				return;
			}
		    String thmnailmageURL=GenericUtilities.getThumbnailImageURL(appConfig, imagename);
		    int code=GenericUtilities.getURLHttpCode(thmnailmageURL);
		    if(code<=200 && code<300){
		    	logger.info("publishing the thumbnail:"+thmnailmageURL);
			    String resultJson=replaceThumbnailImage(videoData,thmnailmageURL);		    
			    logger.info("result of publihsing the thumbnail image:"+resultJson);
		    }
		    else{
		    	logger.warn("thumbnail image is not published to brightcove, because the image is not available:"+thmnailmageURL);
		    }
		    		 
	 }
	 private void publishPosterImage(Episode episode,BCVideoData videoData){
		    String imagename=episode.calculateImageURL();
			if(imagename==null || imagename.trim().length()==0){
				return;
			}			
			String posterImageURL=GenericUtilities.getPosterImageURL(appConfig, imagename);
			int code=GenericUtilities.getURLHttpCode(posterImageURL);
			if(code<=200 && code<300){				
				logger.info("publishing the poster image:"+posterImageURL+":"+episode.getId());
				String resultJson=replacePosterImage(videoData, posterImageURL);
				logger.info("result of replacing the post image:"+resultJson);
			}
			else{
				logger.warn("the poster image is not published to brightcove, because the image is not available:"+posterImageURL);
			}
	 }
	
	private String replaceThumbnailImage(BCVideoData videoData, String imageurl){					
			if(videoData.getImages()!=null && videoData.getImages().getThumbnail()!=null && videoData.getImages().getThumbnail().getAsset_id()!=null && videoData.getImages().getThumbnail().getAsset_id().trim().length()>1){
				String deleteResult=deleteAsset(videoData.getId(), "thumbnail",videoData.getImages().getThumbnail().getAsset_id());
				logger.info("delete thumbnail result:"+deleteResult);			
			}
			return addAsset(videoData.getId(),"thumbnail",imageurl);		
	}
	private String replacePosterImage(BCVideoData videoData, String imageurl){
		if(videoData.getImages()!=null && videoData.getImages().getPoster()!=null && videoData.getImages().getPoster().getAsset_id()!=null && videoData.getImages().getPoster().getAsset_id().trim().length()>1){
			String deleteResult=deleteAsset(videoData.getId(), "poster",videoData.getImages().getPoster().getAsset_id());
			logger.info("delete poster result:"+deleteResult);			
		}
		return addAsset(videoData.getId(),"poster",imageurl);		
	}
	
	
	private String  addAsset(String brightcoveid, String assetType,String imageurl){			
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		
		BCRemoteAsset asset=new BCRemoteAsset();
		asset.setRemote_url(imageurl);
		
		
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		
		try {
			String assetInJson = objectMapper.writeValueAsString(asset);
			String assetURL=configuration.assetURL(brightcoveid, assetType, null);
			
			logger.info("About to POST to brightcove:"+assetURL+", content="+assetInJson);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(assetInJson, headers);
			
			
			
		    ResponseEntity<String> responseEntity = rest.exchange(assetURL,HttpMethod.POST , requestEntity, String.class);
		    
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    return responseEntity.getBody();
		
		} catch (Exception e) {
			logger.error("error while parsing the brightcove video data",e);
			if(e instanceof HttpStatusCodeException){
				 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
				 logger.error("******ERROR in RestTemplate*****"+errorResponse);
				 throw new RuntimeException(errorResponse,e);
			}
			else{
				 throw new RuntimeException(e+" while creating video in brightcove",e);
			}
			
			
		}
		
		
	    
   }
	
	private String deleteAsset(String brightcoveID, String assetType,String assetId){
		brightcoveID=brightcoveID.trim();
		if(brightcoveID.length()<5 || assetId.length()<2){
			throw new RuntimeException("brightcoveID or assetID is too short for delete:"+brightcoveID+" assetId="+assetId);
		}
		 BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		 RestTemplate rest=new RestTemplate();			
		 HttpHeaders headers=new HttpHeaders();			
		 headers.add("Accept", "*/*");
		 headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
			try {
			
				
				String assetURL=configuration.assetURL(brightcoveID, assetType, assetId);
				
				logger.info("About to DELETE:"+assetURL);
				
				
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
								
			    ResponseEntity<String> responseEntity = rest.exchange(assetURL,HttpMethod.DELETE , requestEntity, String.class);			    
			    responseEntity.getStatusCode();	    
			    HttpStatus statusCode=responseEntity.getStatusCode();
			    
			    logger.info(":::::::::statuscode:"+statusCode);
			    if(!statusCode.is2xxSuccessful()){
			    	throw new RuntimeException("Returned not successfull response from the server:statusCode="+statusCode+":"+statusCode.getReasonPhrase());
			    }
			    return responseEntity.getBody();
			
			} catch (Exception e) {
				logger.error("error while parsing the brightcove video data",e);
				if(e instanceof HttpStatusCodeException){
					 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
					 logger.error("******ERROR in RestTemplate*****"+errorResponse);
					 throw new RuntimeException(errorResponse,e);
				}
				else{
					 throw new RuntimeException(e+" while creating video in brightcove",e);
				}
				
				
			}
	}
	
	
	private BcIngestResponse ingestVideo(BCVideoIngestRequest ingestRequest, String videoid){
		BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		
				
		
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		
		try {
			String requestInJson = objectMapper.writeValueAsString(ingestRequest);
			
			logger.info("About to posting to brightcove, requestInJson="+requestInJson);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<String>(requestInJson, headers);
			
			String ingestURL=configuration.ingestUrl(videoid);
		     ResponseEntity<String> responseEntity = rest.exchange(ingestURL,HttpMethod.POST , requestEntity, String.class);
			 
		    
		    responseEntity.getStatusCode();	    
		    HttpStatus statusCode=responseEntity.getStatusCode();
		    logger.info(":::::::::statuscode:"+statusCode);
		    String responseBody= responseEntity.getBody();
		    logger.info("The Ingest response:"+responseBody);
		    BcIngestResponse response= jsonToBcIngestResponse(responseBody);
		    String callbackurl=configuration.retrieveIngestCallbackUrls(0);		    
		    response.setCallback(callbackurl+"/"+response.getId());
		    return response;
		
		} catch (Exception e) {
			logger.error("error while parsing the brightcove video data",e);
			if(e instanceof HttpStatusCodeException){
				 String errorResponse=((HttpStatusCodeException)e).getResponseBodyAsString();
				 logger.info("******ERROR in RestTemplate*****"+errorResponse);
				 throw new RuntimeException(errorResponse,e);
			}
			else{
				 throw new RuntimeException(e+" while creating video in brightcove",e);
			}
		}
		
		
	}
	 
	public  BCVideoData jsonToBCVideoData(String videoInJson){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
	    BCVideoData video;
		try {
			video = objectMapper.readValue(videoInJson, BCVideoData.class);
			return video;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data",e);
			logger.error(e+" while parsing the bc response",e);						
			throw new RuntimeException(videoInJson,e);
		}
	}
	public  BCVideoSource[] jsonToBCVideoSource(String videoInJson){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();							
		try {
			return  objectMapper.readValue(videoInJson, BCVideoSource[].class);
						
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video source data",e);
			logger.error(videoInJson);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	private BcIngestResponse jsonToBcIngestResponse(String responseInJson){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();	
		
		BcIngestResponse response;
		try {
			response = objectMapper.readValue(responseInJson, BcIngestResponse.class);
			return response;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data",e);
			logger.error(responseInJson);
			throw new RuntimeException("Error parsing the ingest response"+e,e);
		}
	}
	public BCVideoData  getVideo(String videoid){			
	    String videoInJson=getViodeInJson(videoid);
	    return jsonToBCVideoData(videoInJson);		
	    
   }
	public BCVideoSource[]  getVideoSource(String videoid){			
	    String videoInJson=getViodeSourcesInJson(videoid);
	    return jsonToBCVideoSource(videoInJson);			    
   }
	
    public BCVideoData changeVideoStatus(String brightcoveid, String status){
    	if(configuration.getEnvironmentType()==BCEnvironmentType.READONLY){
    		logger.error("********changeVideoStatus is skipped:The BC Configuration is SET TO READONLY");
    		return new BCVideoData();    		
    	}
    	BCVideoData updateVideo=new BCVideoData();
		updateVideo.setState(status);			   				   
	    String reponse=updateVideo(updateVideo,brightcoveid);
	    logger.info("activate  video respomse:"+reponse);
	    BCVideoData updatedVideo=jsonToBCVideoData(reponse);	   			  
	    return updatedVideo;
    }
	public void publishImage(Episode episode, String imageType){
		if(configuration.getEnvironmentType()==BCEnvironmentType.READONLY){
    		logger.error("********publishImage is skipped:The BC Configuration is SET TO READONLY");
    		return;    		
    	}
		
		if(episode.getBrightcoveId()==null){
			logger.info("The episode is not yet published:"+episode.getId());
			return;
		}
		BCVideoData existingbcVideoData=getVideo(episode.getBrightcoveId());
		if("poster".equals(imageType)){
			publishPosterImage(episode, existingbcVideoData);
		}
		else if("thumbnail".equals(imageType)){
			 publishThumnnailImage(episode, existingbcVideoData);
		}
		else{
			logger.warn("the image is not posrter or thumbnail imageType="+imageType);
		}		 
	}
	@Transactional
	public BCVideoData publishEpisodeToBrightcove(long episodeid){
		if(configuration.getEnvironmentType()==BCEnvironmentType.READONLY){
    		logger.error("********publishEpisodeToBrightcove is skipped:The BC Configuration is SET TO READONLY");
    		return new BCVideoData();    		
    	}
		
		  Episode  episode=metadataRepository.findEpisodeById(episodeid);		  
		  if(episode==null){
			  throw new RuntimeException("The episodeid is not found in the database"); 
		  }
		  
		  List<ScheduleEvent> schedules=metadataRepository.findScheduleEventByEpisode(episode);
		  List<AdvertisementRule> advertisementRules=metadataRepository.findAllAdvertisementRule(null);
		  
		  BCVideoData newbcVideoData=new BCVideoData(episode,schedules,appConfig,configuration,advertisementRules);
		  
		  
		  if(episode.getBrightcoveId()==null){			 
			  newbcVideoData.setName(newbcVideoData.getName());			  
			  String reponse=createVideo(newbcVideoData);
			  logger.info("create video resposse:"+reponse);
			  BCVideoData createdVideo=jsonToBCVideoData(reponse);
			  logger.info("created video:"+createdVideo);
			  episode.setBrightcoveId(createdVideo.getId());
			  statusPublishedToBrightCove(episode,createdVideo);
			  metadataRepository.persist(episode);
			  
			  publishPosterImage(episode, createdVideo);
			  publishThumnnailImage(episode, createdVideo);			  
			  return createdVideo;
		  }
		  else{
			  
			  BCVideoData existingbcVideoData=getVideo(episode.getBrightcoveId());
			  DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			 
			  /*
			  try {
				  
				  Date brightmodifiedAt=m_ISO8601Local.parse(existingbcVideoData.getUpdated_at());
				  Calendar cal = Calendar.getInstance();
				  cal.setTime(brightmodifiedAt);
				  cal.add(Calendar.HOUR, -2);
				  brightmodifiedAt = cal.getTime();
				  
				  if(episode.getLastModifiedAt().before(brightmodifiedAt)){
					  logger.error("Brightcove date is latest that the boxmedia db");
					  throw new RuntimeException("could not update brightcove, for it contains the latest date:episode=["+episode+"]");					  
				  }
				  
			  	} catch (ParseException e) {
			  		logger.error(e+" parsing the updated_at in brightcove",e);		
			  		throw new RuntimeException("wrong format in updated_at in brightcove date:episode=["+episode+"]:"+existingbcVideoData.getUpdated_at());
			  	}
			  	*/
			  
			   BCVideoData updateVideo=new BCVideoData();
			   updateVideo.setState(null);			   
			   updateVideo.copyFrom(newbcVideoData);
			   if(existingbcVideoData.getId().equals(episode.getBrightcoveId())){
				   updateVideo.setReference_id(null);
				  String reponse=updateVideo(updateVideo,episode.getBrightcoveId());
				  logger.info("update video respomse:"+reponse);
				  BCVideoData updatedVideo=jsonToBCVideoData(reponse);
				  logger.info("updatedVideo video:"+updatedVideo);
				  statusPublishedToBrightCove(episode,updatedVideo);
				  publishPosterImage(episode, updatedVideo);
				  publishThumnnailImage(episode, updatedVideo);
				  return updatedVideo;
			  }
			  else{
				  logger.error("Different Brightcoveid is returned from the videocloud:"+existingbcVideoData.getId()+":"+episode.getBrightcoveId());		
			  		throw new RuntimeException("wrong format in updated_at in brightcove date:episode=["+episode+"]:"+existingbcVideoData.getUpdated_at()); 
			  }
			  
		  }
		  
		  
	}
	
	
	@Transactional
	public Object deleteEpisodeFromBrightcove(long episodeid){
		if(configuration.getEnvironmentType()==BCEnvironmentType.READONLY){
    		logger.error("********deleteEpisodeFromBrightcove is skipped:The BC Configuration is SET TO READONLY");
    		return new BCVideoData();    		
    	}
		
		  Episode  episode=metadataRepository.findEpisodeById(episodeid);		  
		  if(episode==null){			  
			  return new RestResponseMessage("EpisodeNotFound", "The episodeid is not found in the database for removing from the brightcove");
		  }
		  
		  if(episode.getBrightcoveId()==null){
			  return new RestResponseMessage("EpisodeNotPublished", "The episode is not published");			  
		  }
		  else{
			  BCVideoData existingbcVideoData=null;
			 try{ 
			     existingbcVideoData=getVideo(episode.getBrightcoveId());
		      }
			 catch(HttpClientErrorException e){
				if(e.getStatusCode()==HttpStatus.NOT_FOUND){
					logger.error("not found in the bc for delete");
					return new RestResponseMessage("EntryDoesNotExists", "The video is not found in the brightcove");
				}
				else{
					logger.error("Error retrieving the media entry from the brightcove",e);
					throw new RuntimeException("Failed to retrieved the media entry from the brightcove:"+e);
				}
			 }			  
			  DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			  if(existingbcVideoData.getId().equals(episode.getBrightcoveId())){				  
				  String reponse=deleteVideo(episode.getBrightcoveId());
				  logger.info("successfully deleted:"+episode.getBrightcoveId()+" for episode=["+episode+"]reponse=["+reponse+"]");
				  episode.setBrightcoveId(null);
				  statusUnpublishedFromBrightCove(episode);
				  metadataRepository.persist(episode);
				  return reponse;
			  }
			  else{
				  logger.error("Different Brightcoveid is returned from the videocloud:"+existingbcVideoData.getId()+":"+episode.getBrightcoveId());		
			  	   throw new RuntimeException("Different Brightcoveid is returned from the videocloud:"+existingbcVideoData.getId()+":"+episode.getBrightcoveId()); 
			  }
			  
		  }
		  
		  
	}
	
	
	@Transactional
	public BcIngestResponse ingestVideoToBrightCove(FileIngestRequest ingestRequest){		
		if(configuration.getEnvironmentType()==BCEnvironmentType.READONLY){
    		logger.error("********ingestVideoToBrightCove is skipped:The BC Configuration is SET TO READONLY");
    		return new  BcIngestResponse();    		
    	}
		
		  Episode  episode=metadataRepository.findEpisodeById(ingestRequest.getEpisodeid());		  
		  if(episode==null){
			  throw new RuntimeException("The episodeid is not found in the database"); 
		  }		  
		  if(episode.getBrightcoveId()==null){
			  throw new RuntimeException("The episode is not published to brightcove:"+episode);			  
		  }
		  else if(episode.getIngestSource()==null){
			  throw new RuntimeException("The ingestSource  is not set in episode: episode="+episode);			  
		  }
		  else if(episode.getIngestProfile()==null){
			  throw new RuntimeException("The ingestSource  is not specified"+episode);			  
		  }	
		  else{
			  	  BCVideoIngestRequest bcVideoIngestRequest=new BCVideoIngestRequest(episode,configuration);			  	  
			  	  BcIngestResponse response=ingestVideo(bcVideoIngestRequest,episode.getBrightcoveId());
			  	  String jobid=null;
			  	  if(response!=null){
			  		jobid=response.getId();
			  	  }
			  	  statusIngestVideoToBrightCove(episode,jobid);
			  	  
			  	  return response;
		  }
	}
	@Transactional
	public void persist(BCNotification bcNotification){
		metadataRepository.persist(bcNotification);		
	}
	
	
	public List<BCNotification> findAllBCNotification(){
		return metadataRepository.findAllBCNotification();		
	}
	public List<BCNotification> findBCNotificationByJobId(String jobid){
		return metadataRepository.findBCNotificationByJobId(jobid);		
	}
	public String  listPlaylistOrVideoInPlayList(String playlistid,String q,String sort,Integer offset, Integer limit){			
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);	
		String playlistURL=configuration.playlistURL(playlistid,q,sort,offset, limit);
		logger.info("playlistURL=["+playlistURL+"]");
	    ResponseEntity<String> responseEntity = rest.exchange(playlistURL, HttpMethod.GET, requestEntity, String.class);
	    responseEntity.getStatusCode();	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();
   }
	public String  getAPlaylist(String playlistid){			
	    BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);	
		String playlistURL=configuration.playlistURL(playlistid);
		logger.info("playlistURL=["+playlistURL+"]");
	    ResponseEntity<String> responseEntity = rest.exchange(playlistURL, HttpMethod.GET, requestEntity, String.class);
	    responseEntity.getStatusCode();	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();
}
	
	public BCPlayListData[] listPlayListData(String q,String sort,Integer offset, Integer limit){
		String playlistinInJson=listPlaylistOrVideoInPlayList(null,q, sort,offset,limit);
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();		
		try {
			BCPlayListData[] playlist = objectMapper.readValue(playlistinInJson, BCPlayListData[].class);
			return playlist;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove playlist data",e);
			logger.error(playlistinInJson);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	public BCPlayListData getAPlayListData(String playlistid){
		String playlistinInJson=getAPlaylist(playlistid);
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();		
		try {
			BCPlayListData playlist = objectMapper.readValue(playlistinInJson, BCPlayListData.class);
			return playlist;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove playlist data",e);
			logger.error(playlistinInJson);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	public BCVideoData[] getVideoDataInPlayList(String playlistId, String sort,Integer offset, Integer limit){
		String videoInJson=listPlaylistOrVideoInPlayList(playlistId,null, sort,offset,limit);
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();		
		try {
			BCVideoData[] videos = objectMapper.readValue(videoInJson, BCVideoData[].class);
			return videos;			
		} catch (IOException e) {
			logger.error("error while parsing the brightcove video data from the playlist",e);
			logger.error(videoInJson);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	public BCPlayListData patchPlaylist(String playlistId, BCPlayListData playlistdata){		
		playlistdata.clearForPatch();
		BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		headers.setContentType(MediaType.APPLICATION_JSON);	
		
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
		String patchInJson;
		try {
			patchInJson = objectMapper.writeValueAsString(playlistdata);
		} catch (JsonProcessingException e1) {
			logger.error("error while parsing the patched text to playlist data playlistdata="+playlistdata,e1);			
			throw new RuntimeException(e1.getMessage(),e1);
		}
		logger.info("About to patching playlist, requestInJson="+patchInJson);
	
		HttpEntity<String> requestEntity = new HttpEntity<String>(patchInJson, headers);
		
		
		String playlistURL=configuration.playlistURL(playlistId);
	    ResponseEntity<String> responseEntity = rest.exchange(playlistURL,HttpMethod.PATCH , requestEntity, String.class);
	    responseEntity.getStatusCode();	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    String responseBody= responseEntity.getBody();
	    logger.info("The playlist patch response:"+responseBody);
	    BCPlayListData playlist;
		try {
			playlist = objectMapper.readValue(responseBody, BCPlayListData.class);
		} catch (Exception e) {			
			logger.error("error while parsing the brightcove playlist data when patching",e);
			logger.error(responseBody);
			throw new RuntimeException(e.getMessage(),e);
		}
			
	    return playlist;
	    
	}
	public void deletePlaylist(String playlistId){		
		BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		headers.setContentType(MediaType.APPLICATION_JSON);	
		
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
		
		String playlistURL=configuration.playlistURL(playlistId);
		 HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
		 ResponseEntity<String> responseEntity = rest.exchange(playlistURL,HttpMethod.DELETE , requestEntity, String.class);			    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    String responseBody= responseEntity.getBody();
	    logger.info("The playlist delete response:"+responseBody);
	    			
	    
	}
	
	
	public BCPlayListData createPlaylist(BCPlayListData playlistdata){		
		
		BCAccessToken accessToken=bcAccessToenService.getAccessToken();
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();			
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Bearer " + accessToken.getAccess_token());
		headers.setContentType(MediaType.APPLICATION_JSON);	
		
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
		String playlistInJson;
		try {
			playlistInJson = objectMapper.writeValueAsString(playlistdata);
		} catch (JsonProcessingException e1) {
			logger.error("error while parsing the patched text to playlist data playlistdata="+playlistdata,e1);			
			throw new RuntimeException(e1.getMessage(),e1);
		}
		logger.info("About to post a playlist, requestInJson="+playlistInJson);
	
		HttpEntity<String> requestEntity = new HttpEntity<String>(playlistInJson, headers);
		
		
		String playlistURL=configuration.playlistURL(null);
	    ResponseEntity<String> responseEntity = rest.exchange(playlistURL,HttpMethod.POST , requestEntity, String.class);
	    responseEntity.getStatusCode();	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    String responseBody= responseEntity.getBody();
	    logger.info("The playlist post response:"+responseBody);
	    BCPlayListData playlist;
		try {
			playlist = objectMapper.readValue(responseBody, BCPlayListData.class);
		} catch (Exception e) {			
			logger.error("error while parsing the brightcove playlist data when patching",e);
			logger.error(responseBody);
			throw new RuntimeException(e.getMessage(),e);
		}
			
	    return playlist;
	    
	}
	
}
