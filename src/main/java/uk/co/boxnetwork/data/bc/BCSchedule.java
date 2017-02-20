package uk.co.boxnetwork.data.bc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.model.AvailabilityWindow;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.util.GenericUtilities;

public class BCSchedule {
	static final protected Logger logger=LoggerFactory.getLogger(BCSchedule.class);
	private static DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	 private String starts_at;
	 private String ends_at;
	public BCSchedule(){
		
	}
	public BCSchedule(Date fromDate, Date toDate){
		
		starts_at=m_ISO8601Local.format(fromDate);
		ends_at=m_ISO8601Local.format(toDate);
	}
	public void export(Episode episode){
		if(GenericUtilities.isEmpty(this.starts_at) || GenericUtilities.isEmpty(this.ends_at)){
			return;			
		}
		try {			
			AvailabilityWindow availabilityWindow=new AvailabilityWindow();			
			availabilityWindow.setStart(m_ISO8601Local.parse(this.starts_at).getTime());
			availabilityWindow.setEnd(m_ISO8601Local.parse(this.ends_at).getTime());
			episode.addAvailabilityWindow(availabilityWindow);			
		} catch (ParseException e) {
				logger.error(e+" while importing bc schedule",e);
				
		}
		
		
	}
	public String getStarts_at() {
		return starts_at;
	}
	public void setStarts_at(String starts_at) {
		this.starts_at = starts_at;
	}
	public String getEnds_at() {
		return ends_at;
	}
	public void setEnds_at(String ends_at) {
		this.ends_at = ends_at;
	}
	
	 
}
