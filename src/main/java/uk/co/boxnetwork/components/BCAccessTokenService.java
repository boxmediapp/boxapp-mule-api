package uk.co.boxnetwork.components;

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

import uk.co.boxnetwork.data.bc.BCAccessToken;
import uk.co.boxnetwork.data.bc.BCConfiguration;


@Service
public class BCAccessTokenService {
	private static final Logger logger=LoggerFactory.getLogger(BCAccessTokenService.class);
	
	@Autowired
    private BCConfiguration configuration;
	
	
	static private long requestedAt=0;
	static private BCAccessToken currentToken=null;
	public BCAccessToken requestNewAccssToken(){	
		RestTemplate rest=new RestTemplate();
		//rest.setErrorHandler(new RestResponseHandler());
		HttpHeaders headers=new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
	    headers.add("Accept", "*/*");
		headers.add("Authorization", "Basic " + configuration.basicAuthentication());
		HttpEntity<String> requestEntity = new HttpEntity<String>("grant_type=client_credentials", headers);		
	    ResponseEntity<BCAccessToken> responseEntity = rest.exchange(configuration.requestAccessTokenURL(), HttpMethod.POST, requestEntity, BCAccessToken.class);
	    
	    HttpStatus statusCode=responseEntity.getStatusCode();
	    logger.info(":::::::::statuscode:"+statusCode);
	    return responseEntity.getBody();
	}
	public synchronized  BCAccessToken getAccessToken(){
		if(currentToken==null){
			requestedAt=System.currentTimeMillis()/1000;			
			currentToken=requestNewAccssToken();
			return currentToken;
		}
		else if((requestedAt+currentToken.getExpires_in()) >= (System.currentTimeMillis()/1000)){
			return currentToken;
		}
		else{
			requestedAt=System.currentTimeMillis()/1000;			
			currentToken=requestNewAccssToken();
			return currentToken;
		}
			
	}
}
