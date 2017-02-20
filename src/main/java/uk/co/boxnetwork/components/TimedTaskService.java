package uk.co.boxnetwork.components;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import uk.co.boxnetwork.data.ImportScheduleRequest;

import uk.co.boxnetwork.model.ImportScheduleTask;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.TaskType;
import uk.co.boxnetwork.model.TimedTask;

@Service
public class TimedTaskService {
	private static final Logger logger=LoggerFactory.getLogger(TimedTaskService.class);

	@Autowired 
	ImportC4ScheduleService importC4ScheduleService;
	
	@Autowired	
	private EntityManager entityManager;

	public List<TimedTask> findAllTimedTasks(){
		   TypedQuery<TimedTask> query=entityManager.createQuery("SELECT e FROM timed_task e", TimedTask.class);
		   return query.getResultList();
	   }
	
	@Transactional
	public void merge(TimedTask task){
		entityManager.merge(task);
	}
	@Transactional
	public void remove(TimedTask task){
		entityManager.remove(task);
	}
	@Transactional
	public void removeTaskById(long id){
		TimedTask task=entityManager.find(TimedTask.class, id);
		entityManager.remove(task);
	}
	@Transactional
	public void persist(TimedTask task){
		Calendar now=Calendar.getInstance();
		logger.info(":::::created:"+now.getTime());
		task.setLastTimeRun(now.getTime());
		entityManager.persist(task);
	}
	public TimedTask findTimedTaskById(Long id){
		   return entityManager.find(TimedTask.class, id);		   
    }
	
	@Transactional
	public void checkAndRunTasks(){
		List<TimedTask> tasks=findAllTimedTasks();
		for(TimedTask task:tasks){
			if(task.shouldRun()){
				if(task.getTaskType()==TaskType.ONETIME){
					remove(task);
					
				}				
				else if(task.getTaskType()==TaskType.REPEATED){
					merge(task);
				}
				else{
					throw new RuntimeException("Unknown task type for taimed task:"+task.getTaskType());
				}
				executeTask(task);
			}
			else{
				logger.info("task is not expired");
			}
		}		
	}
	
	
	
	public void executeTask(TimedTask task){		
		executeTask(task.getImportScheduleTask());		
	}
	public void executeTask(ImportScheduleTask importScheduleTask){
	
		if(importScheduleTask==null){
			logger.info("is not a importScheduleTask");
			return;
		}
		else{
			logger.info("executing the import schedule task");
		}
		ImportScheduleRequest importSechduleRequest=importScheduleTask.createImportScheduleRequest();
		importC4ScheduleService.importSchedule(importSechduleRequest);				
	}
	
		
}
