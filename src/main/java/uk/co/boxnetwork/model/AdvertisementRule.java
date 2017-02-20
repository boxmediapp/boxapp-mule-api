package uk.co.boxnetwork.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="advertisement_rule")
public class AdvertisementRule {
	@Id
	@GeneratedValue
    private Long id;
	
	
	
	@Column(name="content_type")
	private MatchContentType contentType;

	
	
	
	@Column(name="content_minimum_duration")
	private Long contentMinimumDuration;
	
	@Column(name="content_maximum_duration")
	private Long contentMaximumDuration;
	
	
	@Column(name="advert_break_type")
    private MatchAdvertBreakType advertBreakType;
	
	
	@Column(name="advert_length")
	private Integer advertLength;
	
	
	
	@Column(name="number_of_ads_per_break")
	private Integer numberOfAdsPerBreak;
	
	
	
	@Column(name="last_modified_at")
	private Long lastModifiedAt;
	
	
	
	


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Integer getNumberOfAdsPerBreak() {
		return numberOfAdsPerBreak;
	}


	public void setNumberOfAdsPerBreak(Integer numberOfAdsPerBreak) {
		this.numberOfAdsPerBreak = numberOfAdsPerBreak;
	}


	


	public MatchAdvertBreakType getAdvertBreakType() {
		return advertBreakType;
	}


	public void setAdvertBreakType(MatchAdvertBreakType advertBreakType) {
		this.advertBreakType = advertBreakType;
	}


	


	public MatchContentType getContentType() {
		return contentType;
	}


	public void setContentType(MatchContentType contentType) {
		this.contentType = contentType;
	}




	public Integer getAdvertLength() {
		return advertLength;
	}


	public void setAdvertLength(Integer advertLength) {
		this.advertLength = advertLength;
	}


	public Long getLastModifiedAt() {
		return lastModifiedAt;
	}


	public void setLastModifiedAt(Long lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}


	public Long getContentMinimumDuration() {
		return contentMinimumDuration;
	}


	public void setContentMinimumDuration(Long contentMinimumDuration) {
		this.contentMinimumDuration = contentMinimumDuration;
	}


	public Long getContentMaximumDuration() {
		return contentMaximumDuration;
	}


	public void setContentMaximumDuration(Long contentMaximumDuration) {
		this.contentMaximumDuration = contentMaximumDuration;
	}


	@Override
	public String toString() {
		return "AdvertisementRule [id=" + id + ", contentType=" + contentType + ", contentMinimumDuration="
				+ contentMinimumDuration + ", contentMaximumDuration=" + contentMaximumDuration + ", advertBreakType="
				+ advertBreakType + ", advertLength=" + advertLength + ", numberOfAdsPerBreak=" + numberOfAdsPerBreak
				+ ", lastModifiedAt=" + lastModifiedAt + "]";
	}

	
}
