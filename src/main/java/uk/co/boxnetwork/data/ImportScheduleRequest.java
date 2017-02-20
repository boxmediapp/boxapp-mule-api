package uk.co.boxnetwork.data;

public class ImportScheduleRequest {
	private String channelId;
	private String fromDate;
	private String toDate;
	private String type;
	private String info;
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
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
	@Override
	public String toString() {
		return "ImportScheduleRequest [channelId=" + channelId + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", type=" + type + ", info=" + info + "]";
	}	
	
}
