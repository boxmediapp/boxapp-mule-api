package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="box_channel")
public class BoxChannel {
	
	

	@Id
	@Column(name="channel_id")
    private String channelId;
  
	
	@Column(name="channel_name")
	private String channelName;
	
	private String title;
	
	@Column(name="channel_path")
	private String channel_path;
	
	private String stream;
	
	private String mobile_stream;
	
	private String tv_stream;
	
	private String web_stream;
	
	private  String ios_stream;
	
	private String android_stream;
	
	private String roku_stream;
	
	private String nowtv_stream;
	
	private String eetv_stream;
	
	private String firetv_stream;
	
	private String xbox_stream;
	
	private String playstation_stream;
	
	private String lgtv_stream;
	
	private String samsung_stream;
	private String carousel;
	private String player_focus;
	private String player_blur;
	private String schedule_focus;
	private String schedule_blur;

   public BoxChannel() {
		
	}
	public BoxChannel(String channelId, String channelName) {
		super();
		this.channelId = channelId;
		this.channelName = channelName;
	}


	public String getChannelId() {
		return channelId;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getChannelName() {
		return channelName;
	}


	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getChannel_path() {
		return channel_path;
	}
	public void setChannel_path(String channel_path) {
		this.channel_path = channel_path;
	}
	public String getStream() {
		return stream;
	}
	public void setStream(String stream) {
		this.stream = stream;
	}
	public String getMobile_stream() {
		return mobile_stream;
	}
	public void setMobile_stream(String mobile_stream) {
		this.mobile_stream = mobile_stream;
	}
	public String getTv_stream() {
		return tv_stream;
	}
	public void setTv_stream(String tv_stream) {
		this.tv_stream = tv_stream;
	}
	public String getWeb_stream() {
		return web_stream;
	}
	public void setWeb_stream(String web_stream) {
		this.web_stream = web_stream;
	}
	public String getIos_stream() {
		return ios_stream;
	}
	public void setIos_stream(String ios_stream) {
		this.ios_stream = ios_stream;
	}
	public String getAndroid_stream() {
		return android_stream;
	}
	public void setAndroid_stream(String android_stream) {
		this.android_stream = android_stream;
	}
	public String getRoku_stream() {
		return roku_stream;
	}
	public void setRoku_stream(String roku_stream) {
		this.roku_stream = roku_stream;
	}
	public String getNowtv_stream() {
		return nowtv_stream;
	}
	public void setNowtv_stream(String nowtv_stream) {
		this.nowtv_stream = nowtv_stream;
	}
	public String getEetv_stream() {
		return eetv_stream;
	}
	public void setEetv_stream(String eetv_stream) {
		this.eetv_stream = eetv_stream;
	}
	public String getFiretv_stream() {
		return firetv_stream;
	}
	public void setFiretv_stream(String firetv_stream) {
		this.firetv_stream = firetv_stream;
	}
	public String getXbox_stream() {
		return xbox_stream;
	}
	public void setXbox_stream(String xbox_stream) {
		this.xbox_stream = xbox_stream;
	}
	public String getPlaystation_stream() {
		return playstation_stream;
	}
	public void setPlaystation_stream(String playstation_stream) {
		this.playstation_stream = playstation_stream;
	}
	public String getLgtv_stream() {
		return lgtv_stream;
	}
	public void setLgtv_stream(String lgtv_stream) {
		this.lgtv_stream = lgtv_stream;
	}
	public String getSamsung_stream() {
		return samsung_stream;
	}
	public void setSamsung_stream(String samsung_stream) {
		this.samsung_stream = samsung_stream;
	}
	public String getCarousel() {
		return carousel;
	}
	public void setCarousel(String carousel) {
		this.carousel = carousel;
	}
	public String getPlayer_focus() {
		return player_focus;
	}
	public void setPlayer_focus(String player_focus) {
		this.player_focus = player_focus;
	}
	public String getPlayer_blur() {
		return player_blur;
	}
	public void setPlayer_blur(String player_blur) {
		this.player_blur = player_blur;
	}
	public String getSchedule_focus() {
		return schedule_focus;
	}
	public void setSchedule_focus(String schedule_focus) {
		this.schedule_focus = schedule_focus;
	}
	public String getSchedule_blur() {
		return schedule_blur;
	}
	public void setSchedule_blur(String schedule_blur) {
		this.schedule_blur = schedule_blur;
	}
	
	@Override
	public String toString(){
		return 	"channelId="+channelId+" channelName="+channelName;		 
	}
}
