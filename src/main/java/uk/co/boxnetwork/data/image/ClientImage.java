package uk.co.boxnetwork.data.image;

import java.util.Date;

import uk.co.boxnetwork.model.AppConfig;

public class ClientImage {
	    private Long id;						
		private String url;	
		private int width;	
		private int height;		
		private String contractNumber;
		private String episodeNumber;
		private String tags;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
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
		
		public String getTags() {
			return tags;
		}
		public void setTags(String tags) {
			this.tags = tags;
		}
		public ClientImage(uk.co.boxnetwork.model.Image image, AppConfig appConfig){
			this.id=image.getId();						
			this.url=appConfig.getImageClientBaseURL()+"/"+image.getFilename();	
			this.width=image.getWidth();
			this.height=image.getHeight();
			this.tags=image.getTags();
			String programmeNumber=image.getImageSet().getProgrammeNumber();
			if(programmeNumber!=null&& programmeNumber.length()>0){
				String matParts[]=programmeNumber.split("/");
				this.contractNumber=matParts[0];
				if(matParts.length>1){
					this.episodeNumber=matParts[1];				
				}
			}	
		}
		
}
