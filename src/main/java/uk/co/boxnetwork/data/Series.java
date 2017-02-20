package uk.co.boxnetwork.data;

import java.util.Date;
import java.util.List;

import uk.co.boxnetwork.util.GenericUtilities;



public class Series {
    private Long id;
	private String assetId;
	private String primaryId;
	private String name;
	private String contractNumber;		
	private SeriesGroup seriesGroup;
	private Date lastModifiedAt;
	private Date createdAt;
	private List<uk.co.boxnetwork.data.Episode> episodes;
	private String synopsis;	
	private String imageURL;
	private Integer seriesNumber;
	private Integer nextEpisodeNumber;
	
	private String[] tags;
	private Integer maxNumberOfEpisodes;
	
	
	
	
	public Series(uk.co.boxnetwork.model.Series series) {
		super();
		this.id = series.getId();
		this.assetId = series.getAssetId();
		this.primaryId = series.getPrimaryId();
		this.name = series.getName();
		this.contractNumber = series.getContractNumber();
		if(series.getSeriesGroup()!=null){
			this.seriesGroup = new SeriesGroup(series.getSeriesGroup());
		}
		this.lastModifiedAt=series.getLastModifiedAt();
		this.createdAt=series.getCreatedAt();
		this.synopsis=series.getSynopsis();		
		this.imageURL=series.getImageURL();
		this.seriesNumber=series.getSeriesNumber();
		this.tags=GenericUtilities.commandDelimitedToArray(series.getTags());
		this.nextEpisodeNumber=series.getNextEpisodeNumber();
		this.maxNumberOfEpisodes=series.getMaxNumberOfEpisodes();
	}
	public void update(uk.co.boxnetwork.model.Series series){		
		series.setAssetId(this.assetId);
		series.setPrimaryId(this.primaryId);
		series.setName(this.name);
		series.setContractNumber(this.contractNumber);	
		series.setSynopsis(this.synopsis);
		series.setImageURL(this.imageURL);
		series.setSeriesNumber(this.seriesNumber);
		series.setTags(GenericUtilities.arrayToSeparatedString(this.tags,", "));
		series.setMaxNumberOfEpisodes(this.maxNumberOfEpisodes);
		
	}
	public Series(){
		
	}

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
	public List<uk.co.boxnetwork.data.Episode> getEpisodes() {
		return episodes;
	}
	public void setEpisodes(List<uk.co.boxnetwork.data.Episode> episodes) {
		this.episodes = episodes;		
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public Integer getSeriesNumber() {
		return seriesNumber;
	}
	public void setSeriesNumber(Integer seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
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
	
	
	
  
}
