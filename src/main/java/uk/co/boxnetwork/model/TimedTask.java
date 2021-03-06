package uk.co.boxnetwork.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity(name="timed_task")
public class TimedTask {
	static final protected Logger logger=LoggerFactory.getLogger(TimedTask.class);
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="task_type")
	private TaskType taskType;
			
	@Column(name="last_time_run")
	private Date lastTimeRun;
	
	@Column(name="run_on_time")
	private String runOnTime;
	
	
	@OneToOne(optional=true, fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn( name = "import_schedule_task_id", nullable = true )
	private ImportScheduleTask importScheduleTask;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public Date getLastTimeRun() {
		return lastTimeRun;
	}

	public void setLastTimeRun(Date lastTimeRun) {
		this.lastTimeRun = lastTimeRun;
	}

	
	
	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public String getRunOnTime() {
		return runOnTime;
	}

	public void setRunOnTime(String runOnTime) {
		this.runOnTime = runOnTime;
	}
    public Date calculateExpectedTime(){
    	
    	Calendar calendar=Calendar.getInstance();    	    	
    	
    	if(getRunOnTime()==null){
    		return calendar.getTime();    		
    	}
    	String[] timeparts=getRunOnTime().trim().split(":");
    	if(timeparts.length>0){
    		int hour=Integer.parseInt(timeparts[0]);
    		calendar.set(Calendar.HOUR_OF_DAY, hour);
    	}
    	if(timeparts.length>1){
    		int minute=Integer.parseInt(timeparts[1]);
    		calendar.set(Calendar.MINUTE, minute);
    	}
    	if(timeparts.length>1){
    		int minute=Integer.parseInt(timeparts[1]);
    		calendar.set(Calendar.MINUTE, minute);
    	}
    	if(timeparts.length>2){
    		int seconds=Integer.parseInt(timeparts[2]);
    		calendar.set(Calendar.SECOND, seconds);
    	}
    	return calendar.getTime();
    }
	
	public ImportScheduleTask getImportScheduleTask() {
		return importScheduleTask;
	}

	public void setImportScheduleTask(ImportScheduleTask importScheduleTask) {
		this.importScheduleTask = importScheduleTask;
	}

	public boolean shouldRun(){
		Date expectedTime=calculateExpectedTime();
		Date now=new Date();
		logger.info("before:"+now.before(expectedTime)+":now:"+now+":expected:"+expectedTime);
		logger.info("after:"+expectedTime.after(getLastTimeRun())+":expected:"+expectedTime+":"+getLastTimeRun());
		
		if(now.getTime()>expectedTime.getTime() &&  expectedTime.getTime() > (getLastTimeRun().getTime()+2000)){				
			setLastTimeRun(new Date());
			return true;
		}

		return false;
	}
	
}
