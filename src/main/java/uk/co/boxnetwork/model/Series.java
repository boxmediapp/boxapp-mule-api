package uk.co.boxnetwork.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.co.boxnetwork.util.GenericUtilities;

@Entity(name="series")
public class Series {
	public static String DEFAULT_SERIES_TITLE="Default Series";
	
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="asset_id")
	private String assetId;
	
	@Column(name="primary_id")
	private String primaryId;
	
	private String name;

	@Column(name="contract_number")
	private String contractNumber;
	
	
	
	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn( name = "series_group_id", nullable = true )
	private SeriesGroup seriesGroup;
	
	
	
	
	@Column(name="last_modified_at")
	private Date lastModifiedAt;
	
	
	@Column(name="created_at")
	private Date createdAt;
	
	
	private String synopsis;
	
	@Column(name="series_number")
	private Integer seriesNumber;
	
	@Column(name="image_url")
	private String imageURL;
	
	
	@Column(name="next_episode_number")
	private Integer nextEpisodeNumber;
	
	
	
	private String tags;
	
	@Column(name="max_number_of_episodes")
	private Integer maxNumberOfEpisodes;
	
	@Column(name="application_id")
	private MediaApplicationID applicationId=MediaApplicationID.MEDIA_APP;
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getPrimaryId() {
		return primaryId;
	}

	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	
	
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	


	public void merge(Series series){
		if(GenericUtilities.isNotAValidId(this.assetId)){
			this.assetId=series.getAssetId();			
		}
		if(GenericUtilities.isNotAValidId(this.primaryId)){
			this.primaryId=series.getPrimaryId();			
		}
		if(GenericUtilities.isNotValidTitle(this.name)){
			this.name=series.getName();			
		}
		if(GenericUtilities.isNotValidContractNumber(this.contractNumber)){
			this.contractNumber=series.getContractNumber();			
		}
		if(this.seriesGroup==null){
			this.seriesGroup=series.getSeriesGroup();
		}
	}
	public void adjustBeforeSave(Episode episode){
		if(episode.getMaterialId()==null){
			episode.setMaterialId(contractNumber);
		}
	}

	public SeriesGroup getSeriesGroup() {
		return seriesGroup;
	}

	public void setSeriesGroup(SeriesGroup seriesGroup) {
		this.seriesGroup = seriesGroup;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public Integer getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(Integer seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Integer getNextEpisodeNumber() {
		return nextEpisodeNumber;
	}

	public void setNextEpisodeNumber(Integer nextEpisodeNumber) {
		this.nextEpisodeNumber = nextEpisodeNumber;
	}
	
    public Integer getMaxNumberOfEpisodes() {
		return maxNumberOfEpisodes;
	}

	public void setMaxNumberOfEpisodes(Integer maxNumberOfEpisodes) {
		this.maxNumberOfEpisodes = maxNumberOfEpisodes;
	}

	public boolean updateNextEpisodeNumber(String programmeId){
	   Integer episodeNumber=GenericUtilities.retrieEpisodeNumberFromProgrammeNumber(programmeId);
	   if(episodeNumber==null){
		   if(this.nextEpisodeNumber==null){
			   this.nextEpisodeNumber=1;
			   return true;
		   }
	   }
	   else{
		   if(this.nextEpisodeNumber==null || this.nextEpisodeNumber<=episodeNumber){
			   this.nextEpisodeNumber=episodeNumber+1;
			   return true;
		   }		   
	   }
	   return false;
    }
	public void mergeFieldValues(Series series){
		if(series.getAssetId()!=null){
			this.assetId=series.getAssetId();
		}
		if(series.getPrimaryId()!=null){
			this.primaryId=series.getPrimaryId();
		}
		
	}
	
	
	public MediaApplicationID getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(MediaApplicationID applicationId) {
		this.applicationId = applicationId;
	}

	@Override
	public String toString(){
		return "id="+id+" assetId=["+assetId+"]primaryId=["+primaryId+" contractNumber=["+contractNumber+"]lastModifiedAt="+lastModifiedAt+"]createdAt=["+createdAt+"]";
	}
}
