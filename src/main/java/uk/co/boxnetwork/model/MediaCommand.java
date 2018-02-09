package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="media_command")
public class MediaCommand {
	public static String DUMMY_COMMAND="dummy_command";
	public static String DELIVER_SOUND_MOUSE_HEADER_FILE="deliver_soundmouse_header_file";
	public static String DELIVER_SOUND_MOUSE_SMURF_FILE="deliver_soundmouse_smurf_file";
	public static String PUBLISH_ALL_CHANGES="publish-all-changes";
	public static String IMPORT_BRIGHCOVE_IMAGE="import-brightcove-image";
	public static String CHECK_TRANSCODE_IN_PRGRESS="check-transcode-inprogress";
	public static String IMPORT_BRIGHTCOVE_EPISODE="import-brightcove-episode";
	public static String CONVERT_FROM_MASTER_IMAGE="convert_from_master_image";
	public static String TRANSCODE_VIDEO_FILE="transcode_video_file";
	public static String INGEST_VIDEO_INTO_BC="ingest_video_into_brightcove";
	public static String CAPTURE_IMAGES_FROM_VIDEO="capture_image_from_video";
	public static String PUSH_CHANGES_ON_NEEDS_TO_PUBLISH="published_changes_on_needs_publish";
	public static String INSPECT_VIDEO_FILE="inspect_video_file";
	public static String INVALIDATE_CDN_CLIENT_IMAGE_CACHE="invalidate-client-image-cdn-cache";
	public static String COPY_IMAGE_TO_BOX_MEDIA_APP="copy-image-to-box-media-app";
	public static String COPY_IMAGE_SET="copy_image_set";
	
	
	
	@Id
	@GeneratedValue
    private Long id;

	
	  private String command;
	  
	  @Column(name="episode_id")
	  private Long episodeid;
	  
	  private Double secondsAt;
	  
	  
	  private String filename;
	  
	  
	  private String filepath;
	  
	  private String brightcoveId; 
	  
	  private String contractNumber;
	  
	  private String episodeNumber;
	  
	  private Long imageId;
	  
	  
	  @Column(name="image_set_id")
	  private Long imagesetId;
	  
	  
	  
	  public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}

	public Long getEpisodeid() {
		return episodeid;
	}

	public void setEpisodeid(Long episodeid) {
		this.episodeid = episodeid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getBrightcoveId() {
		return brightcoveId;
	}

	public void setBrightcoveId(String brightcoveId) {
		this.brightcoveId = brightcoveId;
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
	

	
	public Double getSecondsAt() {
		return secondsAt;
	}

	public void setSecondsAt(Double secondsAt) {
		this.secondsAt = secondsAt;
	}
	

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}
	
	

	public Long getImagesetId() {
		return imagesetId;
	}

	public void setImagesetId(Long imagesetId) {
		this.imagesetId = imagesetId;
	}

	@Override
	public String toString() {
		return "MediaCommand [id=" + id + ", command=" + command + ", episodeid=" + episodeid + ", filename=" + filename+". imageId="+imageId
				+ "]";
	}
	
  
}
