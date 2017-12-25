package uk.co.boxnetwork.mule.transformers.metadata;


import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AdvertisementRule;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;


public class AdvertisementRuleTransformer extends BoxRestTransformer{

	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	AppConfig appConfig;
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator, String outputEncoding){		
		String ruleid=MuleRestUtil.getPathPath(message);
		if(ruleid==null || ruleid.length()==0){
			return getAllRules(message,outputEncoding);
		}
		else{
			return getAnAdvertisementRule(ruleid, message,outputEncoding);				
		}
		
	}	
	
	private  Object getAllRules(MuleMessage message, String outputEncoding){
		SearchParam searchParam=new SearchParam(message,appConfig, SearchParam.SearchParamType.ADVERTISEMENT_RULE);
	    return boxMetadataRepository.findAllAdvertisementRule(searchParam);		    							    
	}
	private Object getAnAdvertisementRule(String episodeid, MuleMessage message, String outputEncoding){
  		Long id=Long.valueOf(episodeid);
	    return boxMetadataRepository.findAdvertisementRuleById(id);									
   }
	
	@Override	
	protected Object processPOST(MuleMessage message, BoxOperator operator, String outputEncoding){
		String ruleInJson=null;
		try{	
			ruleInJson=(String)message.getPayloadAsString();
		}
		catch(Exception e){
			logger.error(e +" while getting the payload",e);
			returnError("failed to get the request payload", message);	
		}
			   logger.info("*****Posted a new advertisement rule:"+ruleInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   AdvertisementRule advertisementRule=null;
			   try {
				   advertisementRule = objectMapper.readValue(ruleInJson, AdvertisementRule.class);
			} catch (Exception e1) {
				logger.error("failed to parse into the AdvertisementRule:"+ruleInJson,e1);
				return returnError("failed to parse the advertiment rule payload, wrong format", message);	
			}
			if(advertisementRule.getNumberOfAdsPerBreak()==null){
				return returnError("nukberOf advertsPreBHreak is not specified",message);				
			}
			if(advertisementRule.getId()!=null){
				logger.info("advertisement rule id is set on post");
				return returnError("Do not support post when id is set for advertisement rule", message); 				   					   
			}
			else{
				boxMetadataRepository.persis(advertisementRule);
				return advertisementRule;
			}
    }
	@Override	
	protected Object processPUT(MuleMessage message, BoxOperator operator, String outputEncoding){
		String ruleid=MuleRestUtil.getPathPath(message);
		if(ruleid==null || ruleid.length()==0){
			returnError("does not support put", message);
		}
		
		String ruleInJson=null;
		try{	
			ruleInJson=(String)message.getPayloadAsString();
		}
		catch(Exception e){
			logger.error(e +" while getting the payload",e);
			returnError("failed to get the request payload", message);	
		}
			   logger.info("*****Posted a new advertisement rule:"+ruleInJson+"****");
			   com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
			   objectMapper.setSerializationInclusion(Include.NON_NULL);
			   AdvertisementRule advertisementRule=null;
			   try {
				   advertisementRule = objectMapper.readValue(ruleInJson, AdvertisementRule.class);
			} catch (Exception e1) {
				logger.error("failed to parse into the AdvertisementRule:"+ruleInJson,e1);
				return returnError("failed to parse the advertiment rule payload, wrong format", message);	
			}
			   if(advertisementRule.getNumberOfAdsPerBreak()==null){
					return returnError("nukberOf advertsPreBHreak is not specified",message);				
				}
			if(advertisementRule.getId()==null){
				logger.info("advertisement rule id is not set on put");
				return returnError("Do not support post when id is set for advertisement rule", message); 				   					   
			}
			else if(advertisementRule.getId()!=Long.valueOf(ruleid)){
				return returnError("advert rule id does not match ruleid=["+ruleid+"]", message);
			}
			else{
				boxMetadataRepository.merge(advertisementRule);				
				return advertisementRule;
			}
    }
	@Override
	protected Object processDELETE(MuleMessage message, BoxOperator operator, String outputEncoding){	
		String ruleid=MuleRestUtil.getPathPath(message);		 
		if(ruleid==null || ruleid.length()==0){
			return returnError("the ruleId is missing", message);
		}
		else{			  
				return boxMetadataRepository.removeAdvertisementRuleById(Long.valueOf(ruleid));				
		}			 
	}
	
}
