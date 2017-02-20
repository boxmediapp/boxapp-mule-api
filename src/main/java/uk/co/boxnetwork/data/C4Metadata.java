package uk.co.boxnetwork.data;

import java.util.ArrayList;
import java.util.List;


import uk.co.boxnetwork.model.ScheduleEvent;

public class C4Metadata {
  private String groupdName;
  private String groupdId;

  
  private List<ScheduleEvent> scheduleEvents=new ArrayList<ScheduleEvent>();
  public List<ScheduleEvent> getScheduleEvents() {
	return scheduleEvents;
  }
  public void addScheduleEvent(ScheduleEvent evt){
	  scheduleEvents.add(evt);	  
  }

  public String getGroupdName() {
	return groupdName;
  }
  public void setGroupdName(String groupdName) {
	this.groupdName = groupdName;
  }
  public String getGroupdId() {
	return groupdId;
  }
  public void setGroupdId(String groupdId) {
	this.groupdId = groupdId;
  }
   
   
}
