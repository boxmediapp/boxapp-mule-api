package uk.co.boxnetwork.mule.transformers.tasks;

import java.util.List;

import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.components.TimedTaskService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.model.TimedTask;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;


public class TimedTaskServiceTransformer extends BoxRestTransformer{
	
	@Autowired
	private TimedTaskService timedTaskService;
	
	
	@Override
	protected Object processPOST(MuleMessage message, String outputEncoding){		
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
	 protected Object processGET(MuleMessage message, String outputEncoding){
			List<TimedTask> tasks=timedTaskService.findAllTimedTasks();			
			return tasks;
	 }
	protected Object processDELETE(MuleMessage message, String outputEncoding){	
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