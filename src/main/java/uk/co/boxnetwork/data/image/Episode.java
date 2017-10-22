package uk.co.boxnetwork.data.image;



import uk.co.boxnetwork.model.ImageStatus;


public class Episode {
	
		
	private Long id;
	private String  title;
	private String contractNumber;
	private String episodeNumber;
	private String programmeNumber;
	
	
	
	private ImageStatus imageStatus;
	
	private Integer imageSetNumber;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public String getEpisodeNumber() {
		return episodeNumber;
	}
	public void setEpisodeNumber(String episodeNumber) {
		this.episodeNumber = episodeNumber;
	}
	public ImageStatus getImageStatus() {
		return imageStatus;
	}
	public void setImageStatus(ImageStatus imageStatus) {
		this.imageStatus = imageStatus;
	}
	
	public String getProgrammeNumber() {
		return programmeNumber;
	}
	public void setProgrammeNumber(String programmeNumber) {
		this.programmeNumber = programmeNumber;
	}
	
	
	public Integer getImageSetNumber() {
		return imageSetNumber;
	}
	public void setImageSetNumber(Integer imageSetNumber) {
		this.imageSetNumber = imageSetNumber;
	}
	public Episode(uk.co.boxnetwork.model.Episode episode){
		this.id=episode.getId();
		this.title = episode.getTitle();	
		this.programmeNumber=episode.getCtrPrg();
		if(programmeNumber!=null&& programmeNumber.length()>0){
			String matParts[]=programmeNumber.split("/");
			this.contractNumber=matParts[0];
			if(matParts.length>1){
				this.episodeNumber=matParts[1];				
			}
		}		
		this.imageStatus=episode.getImageStatus();
		this.imageSetNumber=episode.getImageSetnumber();
	}
	
}
