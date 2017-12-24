package uk.co.boxnetwork.mule.transformers.images;

import java.util.Date;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.boxnetwork.components.OperationalLogRepository;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class DeletedImageTransformer extends BoxRestTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(AllClientImageTransformer.class);
	@Autowired
	OperationalLogRepository operationalLogRepository;
	
	
	@Autowired
	AppConfig appConfig;

	
	private String[] clientIds={"virgin","freesat","ee","sky","freeview"};
	
	
	
	protected boolean checkGETAccess(BoxOperator operator){
	    return true;		    
	}
	
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){				
		String path=MuleRestUtil.getPathPath(message);
		
		if(path==null || path.length()==0){
			return findDeletedImages(message,outputEncoding);
			}
		else{
			   int ib=path.indexOf("/");
			   String clientIdPath=null;
			   String logid=null;
			   if(ib==0){
				   return returnError("wrong path", message);
			   }
			   else if(ib>0){
				   clientIdPath=path.substring(0,ib);	
				   if((ib+1)<path.length()){
					   logid=path.substring(ib+1);
				   }
			   }
			   else{
				   clientIdPath=path;
			   }
			   String clientId=null;
			   for(String id:clientIds){
				   if(id.equals(clientIdPath)){
					   clientId=id;
					   break;
				   }
			   }
			   if(clientId==null){
				   if(logid!=null){
					   return returnError("wrong client id:"+path, message);
				   }
				   else{					   
					   try{
						   						   
						   return operationalLogRepository.findDeletedImageById(Long.valueOf(clientIdPath));
						   
					   }
					   catch(Exception e){
						   return returnError("wrong client id"+path, message);
					   }
				   }
				   
			   }
			   else{
				   return findDeletedImages(clientId,logid,message,outputEncoding);
			   }
			   
			   
								
		}
	}
	private Object findDeletedImages(String clientid,String logrecordid,MuleMessage message, String outputEncoding){
		logger.info("Processing deleted client image request clientid=["+clientid+"]recorddid=["+logrecordid+"]");
		if(logrecordid==null){
			return findDeletedImages(message,outputEncoding);
		}
		else{
			try{				  				   
				   return operationalLogRepository.findDeletedImageById(Long.valueOf(logrecordid));						   
				   
			   }
			   catch(Exception e){
				   return returnError("wrong logrecord id"+logrecordid, message);
			   }
		}
	}
	
	private  Object findDeletedImages(MuleMessage message, String outputEncoding){
		ParameterMap queryparams=message.getInboundProperty("http.query.params");
		Date deletedOnFrom=null;
		if(queryparams!=null && queryparams.get("deletedOnFrom")!=null){				
				
					try{
						deletedOnFrom=new Date(Long.valueOf(queryparams.get("deletedOnFrom"))); 						
					}
					catch(Exception e){
						logger.error(e+" when converting to date:"+queryparams.get("deletedOnFrom"),e);
					}				
		}
	   return operationalLogRepository.findDeletedImages(deletedOnFrom);	  	   
	}
	
	 
}
