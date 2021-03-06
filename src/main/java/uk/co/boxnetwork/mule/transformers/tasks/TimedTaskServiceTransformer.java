package uk.co.boxnetwork.mule.transformers.tasks;

import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.components.TimedTaskService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.model.ImportScheduleType;
import uk.co.boxnetwork.model.TimedTask;
import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;


public class TimedTaskServiceTransformer extends BoxRestTransformer{
	
	@Autowired
	private TimedTaskService timedTaskService;
	
	
	@Override
	protected Object processPOST(MuleMessage message, BoxOperator operator,String outputEncoding){		
		String payload=null;
		try {
			payload = message.getPayloadAsString();
			logger.info("recived the timed Task Service:"+payload);
			
			com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			
			
			objectMapper.setSerializationInclusion(Include.NON_NULL);	    
			
			TimedTask timedTask = objectMapper.readValue(payload, TimedTask.class);
			
			timedTaskService.persist(timedTask);
			
			return timedTask;			
		} catch (Exception e) {			
			throw new RuntimeException("Error in TimedTaskServiceTransformer:"+e,e);
		}
		  		  	    			 
	}
	@Override
	 protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){
		    ParameterMap queryparams=message.getInboundProperty("http.query.params");
		    String channel=queryparams.get("channel");
		    String importScheduleType=queryparams.get("importScheduleType");
		    if(channel!=null && channel.trim().length()>0){
		    	logger.info("***findAllTimedTasksByChannelId:"+channel);
		    	List<TimedTask> tasks=timedTaskService.findAllTimedTasksByChannelId(channel.trim());		
				return tasks;
		    }
		    else if(importScheduleType!=null && importScheduleType.trim().length()>0){
		    	logger.info("***findAllTimedTasksBy:importScheduleType:"+importScheduleType);
		    	List<TimedTask> tasks=timedTaskService.findAllTimedTasksByImportScheduleType(ImportScheduleType.valueOf(importScheduleType.trim()));
		    	
		    	
				return tasks;
		    }
		    else{
		    	List<TimedTask> tasks=timedTaskService.findAllTimedTasks();			
				return tasks;
		    }
			
	 }
	@Override
	protected Object processDELETE(MuleMessage message, BoxOperator operator,String outputEncoding){	
		String taskid=MuleRestUtil.getPathPath(message);
		logger.info("deleting the task:"+taskid);
		if(taskid==null || taskid.length()==0){
			return new ErrorMessage("The Method DELETE not supported");
		}
		else{
			Long id=Long.valueOf(taskid);
			TimedTask task=timedTaskService.findTimedTaskById(id);
			timedTaskService.removeTaskById(id);
			return task;
			
		}
					 
	}
	  
   
}