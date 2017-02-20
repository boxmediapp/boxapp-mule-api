package uk.co.boxnetwork.data.bc;

public class BCAnalyticData {
	private String video;
	private Long video_view;
	private Long play_request;
	private Double play_rate;
	private Double video_duration;
	private Double video_engagement_1;
	private Double video_engagement_100;
	private Double video_engagement_25;
	private Double video_engagement_50;
	private Double video_engagement_75;
	private Double video_impression;
	private Double video_percent_viewed;
	private Double video_seconds_viewed;
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public Long getVideo_view() {
		return video_view;
	}
	public void setVideo_view(Long video_view) {
		this.video_view = video_view;
	}
	public Long getPlay_request() {
		return play_request;
	}
	public void setPlay_request(Long play_request) {
		this.play_request = play_request;
	}
	public Double getPlay_rate() {
		return play_rate;
	}
	public void setPlay_rate(Double play_rate) {
		this.play_rate = play_rate;
	}
	public Double getVideo_duration() {
		return video_duration;
	}
	public void setVideo_duration(Double video_duration) {
		this.video_duration = video_duration;
	}
	public Double getVideo_engagement_1() {
		return video_engagement_1;
	}
	public void setVideo_engagement_1(Double video_engagement_1) {
		this.video_engagement_1 = video_engagement_1;
	}
	public Double getVideo_engagement_100() {
		return video_engagement_100;
	}
	public void setVideo_engagement_100(Double video_engagement_100) {
		this.video_engagement_100 = video_engagement_100;
	}
	public Double getVideo_engagement_25() {
		return video_engagement_25;
	}
	public void setVideo_engagement_25(Double video_engagement_25) {
		this.video_engagement_25 = video_engagement_25;
	}
	public Double getVideo_engagement_50() {
		return video_engagement_50;
	}
	public void setVideo_engagement_50(Double video_engagement_50) {
		this.video_engagement_50 = video_engagement_50;
	}
	public Double getVideo_engagement_75() {
		return video_engagement_75;
	}
	public void setVideo_engagement_75(Double video_engagement_75) {
		this.video_engagement_75 = video_engagement_75;
	}
	public Double getVideo_impression() {
		return video_impression;
	}
	public void setVideo_impression(Double video_impression) {
		this.video_impression = video_impression;
	}
	public Double getVideo_percent_viewed() {
		return video_percent_viewed;
	}
	public void setVideo_percent_viewed(Double video_percent_viewed) {
		this.video_percent_viewed = video_percent_viewed;
	}
	public Double getVideo_seconds_viewed() {
		return video_seconds_viewed;
	}
	public void setVideo_seconds_viewed(Double video_seconds_viewed) {
		this.video_seconds_viewed = video_seconds_viewed;
	}
	public void copy(BCAnalyticData from){
		this.video=from.video;
		this.video_view=from.video_view;
		this.play_request=from.play_request;
		this.play_rate=from.play_rate;
		this.video_duration=from.video_duration;
		this.video_engagement_1=from.video_engagement_1;
		this.video_engagement_100=from.video_engagement_100;
		this.video_engagement_25=from.video_engagement_25;
		this.video_engagement_50=from.video_engagement_50;
		this.video_engagement_75=from.video_engagement_75;
		this.video_impression=from.video_impression;
		this.video_percent_viewed=from.video_percent_viewed;
		this.video_seconds_viewed=from.video_seconds_viewed;
	}
	public BCAnalyticData joinAnalytics(BCAnalyticData joiner){
		BCAnalyticData bcAnalyticsData=new BCAnalyticData();
		bcAnalyticsData.copy(joiner);
		bcAnalyticsData.video_view+=this.video_view;
		bcAnalyticsData.play_request=this.play_request;
		if(bcAnalyticsData.play_rate<this.play_rate){
			bcAnalyticsData.play_rate=this.play_rate;
		}
		bcAnalyticsData.video_engagement_1+=this.video_engagement_1;
		bcAnalyticsData.video_engagement_100+=this.video_engagement_100;
		bcAnalyticsData.video_engagement_25+=this.video_engagement_25;
		bcAnalyticsData.video_engagement_50+=this.video_engagement_50;
		bcAnalyticsData.video_engagement_75+=this.video_engagement_75;
		bcAnalyticsData.video_impression+=this.video_impression;
		bcAnalyticsData.video_percent_viewed+=this.video_percent_viewed;
		bcAnalyticsData.video_seconds_viewed+=this.video_seconds_viewed;
		return bcAnalyticsData;
	}
	
}
