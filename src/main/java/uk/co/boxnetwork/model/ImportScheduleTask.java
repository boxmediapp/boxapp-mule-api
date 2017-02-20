package uk.co.boxnetwork.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;



import uk.co.boxnetwork.data.ImportScheduleRequest;



@Entity(name="import_schedule_task")
public class ImportScheduleTask {
   
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="from_day_offset")
	private Integer fromDayOffset;
	
	@Column(name="from_date")
	private Date fromDate;
	
	
	@Column(name="to_day_offset")
	private Integer toDayOffset;
	
	@Column(name="to_date")
	private Date toDate;
	
	
	
	
	@Column(name="channel_id")
	private String channelId;
	
	
	private String type;
	
	
	private String info;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String dateToString(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
	public ImportScheduleRequest createImportScheduleRequest(){
		ImportScheduleRequest importRequest=new ImportScheduleRequest();
		importRequest.setChannelId(channelId);
		importRequest.setType(type);
		importRequest.setInfo(info);
		
		Calendar fromCalendar=Calendar.getInstance();
		if(fromDate!=null){
			fromCalendar.setTime(fromDate);
		}		
		if(fromDayOffset!=null){
			fromCalendar.add(Calendar.DATE,fromDayOffset);
		}
		importRequest.setFromDate(dateToString(fromCalendar.getTime()));
		
		Calendar toCalendar=Calendar.getInstance();
		if(toDate!=null){
			toCalendar.setTime(toDate);
		}		
		if(toDayOffset!=null){
			toCalendar.add(Calendar.DATE,toDayOffset);
		}
		importRequest.setToDate(dateToString(toCalendar.getTime()));
		
		return importRequest;		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getFromDayOffset() {
		return fromDayOffset;
	}

	public void setFromDayOffset(Integer fromDayOffset) {
		this.fromDayOffset = fromDayOffset;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getToDayOffset() {
		return toDayOffset;
	}

	public void setToDayOffset(Integer toDayOffset) {
		this.toDayOffset = toDayOffset;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	
}
