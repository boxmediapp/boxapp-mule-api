package uk.co.boxnetwork.components;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.regions.Regions;

import uk.co.boxnetwork.data.DataReport;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
import uk.co.boxnetwork.model.TimedTask;
import uk.co.boxnetwork.model.VideoStatus;

import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class MetadataMaintainanceService {	
	
	
	
	@Autowired
	private BoxMedataRepository repository;

	@Autowired
	private BCVideoService videoService;
	

	@Autowired
	private TimedTaskService timedTasks;
	
	
	@Autowired	
	private EntityManager entityManager;

	@Autowired
    private DataSource datasource;
	
	@Autowired
	MetadataService metataService;
	
	
	@Autowired
	private S3BucketService s3uckerService;

	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
    private BCConfiguration bcConfiguration;
	
	
	@Autowired
	private CommandServices commandService; 

	
	
	private static final Logger logger=LoggerFactory.getLogger(MetadataMaintainanceService.class);
	
	
	@Transactional
	public void fixTxChannel(){
		logger.info("fixing the channel......");
		
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
					if(fixTxChannel(episode)){													  
						repository.persist(episode);
					}			
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
	}
	@Transactional
	public void updateSeriesNextEpisodeNumber(){
		logger.info("updateSeriesNextEpisodeNumber......");
		
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
					Series series=episode.getSeries();
					if(series==null){
						continue;
					}
					if(series.updateNextEpisodeNumber(episode.getCtrPrg())){
						repository.mergeSeries(series);
					}
					
					
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
	}
	
	
	
	
	
	public void setAvailableWindowForAll(Date from, Date to){
logger.info("adding the default availability window......");
		
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
					setAvailableWindow(episode, from,to);													  
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
	}
	public void setAvailableWindow(Episode episode,Date from, Date to){
		repository.replaceAvailabilityWindow(episode.getId(), from, to);
		
		
	}
	public boolean fixTxChannel(Episode episode){
		if("Box Hits (SmashHits)".equals(episode.getTxChannel())){		  			  
			  episode.setTxChannel("Box Hits");
			  return true;
		}
		else if("Box Upfront (Heat)".equals(episode.getTxChannel())){			  
			  episode.setTxChannel("Box Upfront");
			  return true;
		 }		
		else
			return false;
	}
	
	public boolean fixTxChannel(uk.co.boxnetwork.data.Episode episode){
		if("Box Hits (SmashHits)".equals(episode.getTxChannel())){		  			  
			  episode.setTxChannel("Box Hits");
			  return true;
		}
		else if("Box Upfront (Heat)".equals(episode.getTxChannel())){			  
			  episode.setTxChannel("Box Upfront");
			  return true;
		 }		
		else
			return false;
	}
	@Transactional
    public void fixEpisodeStatusIfEmpty(){
    	logger.info("fixing the episode status......");
    	
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}		
							
					for(Episode episode:episodes){
							if(episode.getEpisodeStatus()==null){													  
								fixEpisodeStatusIfEmpty(episode);
								repository.persist(episode);
							}							
					}
					if(searchParam.isEnd(episodes.size())){
						break;
					}
					else{
						searchParam.nextBatch();
					}
		}
					logger.info("Completed the fixing the episode status");
    }
	@Transactional
    public void deleteAllTasls(){
    	logger.info("deleting all tasks......");
    	List<TimedTask> tasks=timedTasks.findAllTimedTasks();
    	for(TimedTask task: tasks){
    		timedTasks.remove(task);
    	}
    	logger.info("Completed the deleting the tasks");
    }
    public void fixEpisodeStatusIfEmpty(Episode episode){    	
    	if(episode.getEpisodeStatus()!=null){
    		return;
    	}
    	EpisodeStatus episodeStatus=new EpisodeStatus();
    	MetadataStatus metadataStatus=GenericUtilities.calculateMetadataStatus(episode);
    	if(metadataStatus!=null){
    		episodeStatus.setMetadataStatus(metadataStatus);
    	}
    	else{
    		episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_PUBLISH_CHANGES);
    	}
    	VideoStatus videoStatus=GenericUtilities.calculateVideoStatus(episode);
    	if(videoStatus!=null){
    		episodeStatus.setVideoStatus(videoStatus);
    	}
    	else{
    		metataService.importTranscodeStatusFromBrightcove(episode.getBrightcoveId(),episodeStatus);    		    
    	}
    	if(episodeStatus.getId()==null){
    		repository.persistEpisodeStatus(episodeStatus);
    	}
    	else{
    		repository.persistEpisodeStatus(episodeStatus);
    	}
    	episode.setEpisodeStatus(episodeStatus);    	
    }
    
   
    
    
    
    @Transactional     
    public void replaceIngestProfiles(String oldIngestProfile,String newIngestProfile){
    	logger.info("repacing the episode ingestProfile:oldIngestProfile="+oldIngestProfile+" newIngestProfile="+newIngestProfile);
    	
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}								
				for(Episode episode:episodes){
					replaceIngestProfiles(episode,oldIngestProfile,newIngestProfile);												  					
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
		logger.info("Completed the ingestprofile changes");
    }
    public void replaceIngestProfiles(Episode episode,String oldIngestProfile,String newIngestProfile){
    	if(episode.getIngestProfile()==null || (!episode.getIngestProfile().equals(oldIngestProfile))){
    		return;
    	}
    	episode.setIngestProfile(newIngestProfile);
    	EpisodeStatus episodeStatus=episode.getEpisodeStatus();
    	if(episodeStatus.getVideoStatus()==VideoStatus.TRANSCODE_COMPLETE ||episodeStatus.getVideoStatus()==VideoStatus.TRANSCODING){
    		episodeStatus.setVideoStatus(VideoStatus.NEEDS_RETRANSCODE);
    		repository.persistEpisodeStatus(episodeStatus);
    	}
    	repository.persist(episode);    	
    }
    
    
    public void removeOrphantSeriesGroup(){
    	SearchParam searchParam=new SearchParam(null, 0, appConfig.getRecordLimit());
    	while(true){
		    	List<SeriesGroup> seriesGroups=repository.findAllSeriesGroup(searchParam);
		    	if(seriesGroups.size()==0){
					break;
				}
		    	List<SeriesGroup> orphanned=new ArrayList<SeriesGroup>();
		    	
		    	for(SeriesGroup sg:seriesGroups){
		    		List<Series> series=repository.findSeriesBySeriesGroup(sg);
		    		if(series.size()==0){
		    			orphanned.add(sg);
		    		}
		    	}
		    	repository.removeSeriesGroup(orphanned);
		    	if(searchParam.isEnd(seriesGroups.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
    	}
    }
    
    
    
    
    
    
   
    
    public void dropProgrammeColumns(){
    //	JdbcTemplate jdbcTemlate=new JdbcTemplate(datasource);
    	//jdbcTemlate.execute("ALTER TABLE episode DROP COLUMN programme_id");
    	//jdbcTemlate.execute("ALTER TABLE schedule_event DROP COLUMN programme_id");
    	//jdbcTemlate.execute("ALTER TABLE series DROP COLUMN programme_id");
    	
//    	jdbcTemlate.execute("ALTER TABLE episode DROP FOREIGN KEY programme_id");
//    	jdbcTemlate.execute("ALTER TABLE schedule_event DROP FOREIGN KEY programme_id");
//    	jdbcTemlate.execute("ALTER TABLE series DROP FOREIGN KEY programme_id");
    	//jdbcTemlate.execute("ALTER TABLE programme DROP FOREIGN KEY programme_id");
    	    	
    }
    @Transactional
    public void dropProgrammeTable(){
    	JdbcTemplate jdbcTemlate=new JdbcTemplate(datasource);
    	
    	jdbcTemlate.execute("DROP TABLE programme");
    }

    public void updateAllPublishedStatys(){
    	
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}			
				for(Episode episode:episodes){
					updatePublishedStatus(episode);  
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
    }
    private void updatePublishedStatus(Episode episode){
    	metataService.updatePublishedStatus(episode);
    }
    
    
    
    
    public MediaCommand getSoundMouseMediaCommandForProcess(){    	
    	List<MediaCommand> mediaCommands=repository.findMediaCommandByCommand(MediaCommand.DELIVER_SOUND_MOUSE_HEADER_FILE);    	
    	if(mediaCommands.size()==0){
    		mediaCommands=repository.findMediaCommandByCommand(MediaCommand.DELIVER_SOUND_MOUSE_SMURF_FILE);
        	if(mediaCommands.size()==0){
        		return null;
        	}
    	}
    	MediaCommand mediaCommand=mediaCommands.get(0);
    	repository.removeMediaCommandById(mediaCommand.getId());
    	return mediaCommand;   	
    }
    
    
    
    public void scheduleToDeliverSoundmouseHeaderFile(Long episodeid){
  	  
    	MediaCommand mediaCommand=new MediaCommand();
    	mediaCommand.setCommand(MediaCommand.DELIVER_SOUND_MOUSE_HEADER_FILE);
    	mediaCommand.setEpisodeid(episodeid);
    	Episode episode=repository.findEpisodeById(episodeid);
    	
    	String matfilepart=GenericUtilities.materialIdToImageFileName(episode.getMaterialId());
    	String websafetitle=GenericUtilities.toWebsafeTitle(episode.getTitle());
    	//String filename=websafetitle+"_"+matfilepart+"_"+episode.getId()+".xml";
    	Date today=new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("ddMMyyHHmmss'CTINT'ddMMyyyy");
    	mediaCommand.setFilename(formatter.format(today)+".xml");
    	logger.info("Scheduled to deliver the soumdmouse header file:"+mediaCommand);
    	metataService.executeMediaCommandForLater(mediaCommand);
    }
    public void scheduleToDeliverSoundmouseSmurfFile(){
    	MediaCommand mediaCommand=new MediaCommand();
    	mediaCommand.setCommand(MediaCommand.DELIVER_SOUND_MOUSE_SMURF_FILE);    	
    	
    	metataService.executeMediaCommandForLater(mediaCommand);
    	logger.info("media command for delivery smurf is persisted");
    }
    
    
    
    
    
    
    @Transactional
    public void syncAppConfigWithDatabase(){
    	TypedQuery<AppConfig> query=entityManager.createQuery("SELECT s FROM app_config s", AppConfig.class);
		List<AppConfig> configs=query.getResultList();
		if(configs.size()==0){
			entityManager.persist(appConfig);
		}
		else{
			AppConfig configInDb=configs.get(0);
			if(configInDb.getVersion()==null || appConfig.getVersion()>configInDb.getVersion()){
				configInDb.importConfig(appConfig);
				entityManager.merge(configInDb);
			}
			else{
				configInDb.exportConfig(appConfig);
			}
		}     	
    }
    
    public void checkAllRecordsConsistency(){
    	

		
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
						EpisodeStatus episodeStatus=episode.getEpisodeStatus();
						/*
						if(episodeStatus.getMetadataStatus()!=MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER && episodeStatus.getPublishedStatus()==PublishedStatus.NOT_PUBLISHED){
							metataService.updatePublishedStatus(episode);
					    }
					    */		
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
    }
    
    @Transactional
    public void updateAppConfig(AppConfig config){
    	TypedQuery<AppConfig> query=entityManager.createQuery("SELECT s FROM app_config s", AppConfig.class);
		List<AppConfig> configs=query.getResultList();
		if(configs.size()>0){			
			AppConfig configInDb=configs.get(0);			
				configInDb.importConfig(config);
				entityManager.merge(configInDb);			
				configInDb.exportConfig(appConfig);
			}
    }
    
    
public void calculateUploadedDuration(){
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}
				for(Episode episode:episodes){
					  if(episode.getIngestSource()!=null && episode.getDurationUploaded()==null){
						  logger.info("*****calculating the duration for the episode:"+episode);
						   Double durationUploaded=metataService.checkVideoDuration(episode);
						   if(durationUploaded!=null){
							   episode.setDurationUploaded(durationUploaded);
							   repository.updateEpisode(episode);
						   }
					  }					  		
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
    }

  	
	public uk.co.boxnetwork.data.Series deleteSeriesImage(Long serisid, String imagefilename){	
  		uk.co.boxnetwork.data.Series series=metataService.getSeriesById(serisid);
  		if(series==null){
  			logger.error("not found series, not deleting the series image serisid=["+serisid+"]imagefilename=["+imagefilename+"]");
  			return null;
  		}
  		if(imagefilename==null){
  			logger.error("imagefile is null, not deleting the series image serisid=["+serisid+"]imagefilename=["+imagefilename+"]");
  			return series;
  		}
  		imagefilename=imagefilename.trim();
  		if(imagefilename.length()==0){
  			logger.error("imagefile is empty, not deleting the series image serisid=["+serisid+"]imagefilename=["+imagefilename+"]");  			
  		}  	
  		else if(imagefilename.equals(series.getImageURL())){
  			s3uckerService.deleteMasterImage(imagefilename);
  			metataService.setSeriesImage(serisid, null);
  			series.setImageURL(null);
  			
  		}
  		else{
  			logger.error("imagefile is different, not deleting the series image serisid=["+serisid+"]imagefilename=["+imagefilename+"]");
  		}
  		return series;
	}
	public uk.co.boxnetwork.data.SeriesGroup deleteSeriesGroupImage(Long seriousgroupid, String imagefilename){	
  		uk.co.boxnetwork.data.SeriesGroup seriesgroup=metataService.getSeriesGroupById(seriousgroupid);
  		if(seriesgroup==null){
  			logger.error("not found series group, not deleting the series group image seriousgroupid=["+seriousgroupid+"]imagefilename=["+imagefilename+"]");
  			return null;
  		}
  		if(imagefilename==null){
  			logger.error("imagefile is null, not deleting the series group image seriousgroupid=["+seriousgroupid+"]imagefilename=["+imagefilename+"]");
  			return seriesgroup;
  		}
  		imagefilename=imagefilename.trim();
  		if(imagefilename.length()==0){
  			logger.error("imagefile is empty, not deleting the series group image seriousgroupid=["+seriousgroupid+"]imagefilename=["+imagefilename+"]");  			
  		}  	
  		else if(imagefilename.equals(seriesgroup.getImageURL())){
  			s3uckerService.deleteMasterImage(imagefilename);
  			metataService.setSeriesGroupImage(seriousgroupid, null);
  			seriesgroup.setImageURL(null);
  			
  		}
  		else{
  			logger.error("imagefile is different, not deleting the series group image seriousgroupid=["+seriousgroupid+"]imagefilename=["+imagefilename+"]");
  		}
  		return seriesgroup;
	}
	public uk.co.boxnetwork.data.Episode deleteEpisodeImage(Long episodeid, String imagefilename){	
  		uk.co.boxnetwork.data.Episode episode=metataService.getEpisodeById(episodeid);
  		if(episode==null){
  			logger.error("episode not found, unable to delete the episode image episodeid=["+episodeid+"]imagefilename=["+imagefilename+"]");
  			return null;
  		}
  		if(imagefilename==null){
  			logger.error("imagefile is null, not deleting the episode image episodeid=["+episodeid+"]imagefilename=["+imagefilename+"]");
  			return episode;
  		}
  		imagefilename=imagefilename.trim();
  		if(imagefilename.length()==0){
  			logger.error("imagefile is empty, not deleting the episode image episodeid=["+episodeid+"]imagefilename=["+imagefilename+"]");  			
  		}  	
  		else if(imagefilename.equals(episode.getImageURL())){
  			s3uckerService.deleteMasterImage(imagefilename);
  			metataService.setEpisodeImage(episodeid, null);  			
  			episode.setImageURL(null);  			
  		}
  		else{
  			logger.error("imagefile is different, not deleting the series image serisid=["+episodeid+"]imagefilename=["+imagefilename+"]");
  		}
  		return episode;
	}
	
	public uk.co.boxnetwork.data.Episode deleteEpisodeVideoFile(Long episodeid, String videofilename){	
  		uk.co.boxnetwork.data.Episode episode=metataService.getEpisodeById(episodeid);
  		if(episode==null){
  			logger.error("episode not found, unable to delete the episode video episodeid=["+episodeid+"]videofilename=["+videofilename+"]");
  			return null;
  		}
  		if(videofilename==null){
  			logger.error("videofilename is null, not deleting the episode video episodeid=["+episodeid+"]videofilename=["+videofilename+"]");
  			return episode;
  		}
  		videofilename=videofilename.trim();
  		if(videofilename.length()==0){
  			logger.error("video file is empty, not deleting the episode video episodeid=["+episodeid+"]videofilename=["+videofilename+"]");  			
  		}  	
  		else if(episode.getIngestSource().endsWith(videofilename)){  			  			
  				s3uckerService.deleteVideoFile(videofilename);
  				metataService.setEpisodeIngestSource(episodeid, null);  			
  				episode.setIngestSource(null);
  		}
  		else{
  			logger.error("ingestSource is different, not deleting the episode video episodeid=["+episodeid+"]videofilename=["+videofilename+"]");
  		}
  		return episode;
	}
	
	public String createCSVBeBoxEpisodesFrom() throws UnsupportedEncodingException, FileNotFoundException{				
		SearchParam searchParam=new SearchParam(null, 0, appConfig.getRecordLimit());
		StringBuilder builder=new StringBuilder();
		builder.append("Birghtcove Video ID, Programme Id , Brightcove Title\n");
		int pageCounter=0;
    	while(true){
    		    pageCounter++;
    		    logger.info("getting video from bc:"+pageCounter);
    		    
    		    BCVideoData[] videos=videoService.getVideoList(searchParam.getLimit(), searchParam.getStart(), null, null);    		
		    	if(videos==null || videos.length==0){
		    		logger.info("completed retrieving all the bc video");
					break;
				}
		    	logger.info("returned records:"+videos.length+":"+videos[0].getId()+":"+videos[0].getName());			    	
		    	for(BCVideoData video:videos){
		    		String bcVideoId=video.getId();
	    			String bcTitle=video.getName();
		    		if( video.getReference_id()==null  || ((!video.getReference_id().startsWith("V_")) && (!video.getReference_id().startsWith("v_"))) ){		    					    			
		    			List<Episode> matchedEpisodes=repository.findEpisodesByBrightcoveId(bcVideoId);
		    			if(matchedEpisodes.size()==0){
		    				logger.info("needs to import:"+bcVideoId);		    	
		    				builder.append(bcVideoId+",,"+bcTitle+"\n");
		    			}		    			
		    			else{
		    				logger.info("already impported:"+bcVideoId);
		    			}
		    		}
		    		else{
			    		logger.info("is created from box media app:"+bcVideoId);
			    	}
		    	}
		    	
		    	searchParam.nextBatch(videos.length);	    		
    	  }	    
    	return  builder.toString();   	
	}
	
	
	
	
	
    
    
    
    public String importCSVFromBrightcove(String csvContent){
    	StringBuilder builder=new StringBuilder();    	
		String lines[]=csvContent.split("[\\r\\n]+");
		MediaCommand mediaCommand=new MediaCommand();
		for(String line:lines){
			String parts[]=line.split(",");
			if(parts.length<2){
				  logger.error("error in processing the line:"+line);
				  builder.append("error in processing the line:"+line);
				  continue;
			}
			String mediaItemId=parts[0];
			String programmeId=parts[1];
			parts=programmeId.split("/");
			if(parts.length!=2){
				logger.error("error in processing the line:"+line);
				builder.append("error in processing the line:"+line);
				continue;
			}			
			mediaCommand=new MediaCommand();
	    	mediaCommand.setBrightcoveId(mediaItemId);
	    	mediaCommand.setContractNumber(parts[0]);
	    	mediaCommand.setEpisodeNumber(parts[1]);
	    	mediaCommand.setCommand(MediaCommand.IMPORT_BRIGHTCOVE_EPISODE);
	    	metataService.importEpisodeFromBrightcove(mediaCommand,builder);
		}
		return builder.toString();		
	}    
    public String copyVideoFilesBucketToBucket(String csvContent){
    	StringBuilder builder=new StringBuilder();
    	String lines[]=csvContent.split("[\\r\\n]+");
    	int counter=0;
    	for(String line:lines){
    		counter++;
			String parts[]=line.split(",");
			String mediaId=parts[0];
			List<FileItem> files=s3uckerService.listFiles(appConfig.getVideoBucket(), bcConfiguration.getAccountId()+"/"+mediaId+".mp4",0,-1,null);
			if(files.size()>0){
				logger.info(counter+":::: already exists:"+mediaId);
				continue;
			}			
			logger.info(counter+"::::: downloading "+bcConfiguration.getAccountId()+"/"+mediaId+".mp4  from brightcove-import to:"+appConfig.getVideoBucket());
			
			files=s3uckerService.listFiles("brightcove-import", bcConfiguration.getAccountId()+"/"+mediaId+".mp4",Regions.US_WEST_2,0,-1,null,null,null);			
			if(files.size()==0){
				builder.append(mediaId+": does not exist\n");				
			}
			else{
				s3uckerService.copyFile("brightcove-import",bcConfiguration.getAccountId()+"/"+mediaId+".mp4" , appConfig.getVideoBucket(), bcConfiguration.getAccountId()+"/"+mediaId+".mp4", null);
			}
			
			logger.info(counter+":::::completed for:"+mediaId);			
    	}   
    	builder.append("completed!!!!");
    	return builder.toString();
    }
    
    
    public String verifyEpisodes(){
    	logger.info("verifying the episodes");
    	StringBuilder result=new StringBuilder();
    	
		int rcordLimit=appConfig.getRecordLimit();
		if(rcordLimit<1){
			rcordLimit=Integer.MAX_VALUE;
		}
		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
		while(true){
				List<Episode> episodes=repository.findAllEpisodes(searchParam);
				if(episodes.size()==0){
					break;
				}								
				for(Episode episode:episodes){
					verifyEpisode(episode,result);												  					
				}
				if(searchParam.isEnd(episodes.size())){
					break;
				}
				else{
					searchParam.nextBatch();
				}
		}
		logger.info("Completed the verification process");
		result.append("\n completed");
		return result.toString();
    }
    public void verifyEpisode(Episode episode, StringBuilder result){
    	if(episode.getImageURL()==null){
    		result.append(episode.getId()+":"+episode.getCtrPrg()+":image URL is empty");
    		return;
    	}
    	String imageURL=GenericUtilities.getImageWithSize(appConfig, episode.getImageURL(), 320, 180);
		try {
			URL imgURL=new URL(imageURL);
			HttpURLConnection conn = (HttpURLConnection)imgURL.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int code = conn.getResponseCode();
			if(code!=200){
				logger.info("will convert image for "+episode.getId()+":"+episode.getCtrPrg()+":returned:"+code+":"+imageURL);
				//if(episode.getImageURL().startsWith("65138_039_001")){
			    result.append("\n"+episode.getId()+":"+episode.getCtrPrg()+":returned:"+code+":"+imageURL+"\n");
				metataService.persistConvertImaggeMediaCommand(appConfig.getImageMasterFolder()+"/"+episode.getImageURL());
				//}
			}
		} catch (Exception e) {
			logger.error( e+" for :"+imageURL,e);			
			result.append(episode.getId()+":"+episode.getCtrPrg()+":error:"+e+":"+imageURL+"\n");
			
		
			
		}
		
    }
    
    
   
    
    public DataReport getDataReport(){
    	DataReport report=new DataReport();    	
    	repository.report(report);    	    	
    	return report;    	
    }
 }
