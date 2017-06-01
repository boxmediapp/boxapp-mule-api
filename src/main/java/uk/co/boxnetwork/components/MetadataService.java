package uk.co.boxnetwork.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.co.boxnetwork.data.BCPlayList;
import uk.co.boxnetwork.data.BCPlayListItem;
import uk.co.boxnetwork.data.FileIngestRequest;
import uk.co.boxnetwork.data.SearchParam;

import uk.co.boxnetwork.data.bc.BCAnalyticsResponse;
import uk.co.boxnetwork.data.bc.BCConfiguration;
import uk.co.boxnetwork.data.bc.BCPlayListData;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.bc.BCVideoSource;
import uk.co.boxnetwork.data.bc.BcIngestResponse;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.data.s3.MediaFilesLocation;
import uk.co.boxnetwork.data.soundmouse.SoundMouseData;
import uk.co.boxnetwork.data.soundmouse.SoundMouseItem;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.AvailabilityWindow;
import uk.co.boxnetwork.model.BCNotification;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.PublishedStatus;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
import uk.co.boxnetwork.model.VideoStatus;

import uk.co.boxnetwork.util.GenericUtilities;

@Service
public class MetadataService {
	private static final Logger logger=LoggerFactory.getLogger(MetadataService.class);
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	S3BucketService s3BucketService;
	  	
	@Autowired
	BCVideoService videoService;
	
	@Autowired
	CommandServices commandService;

	@Autowired
	BCAnalyticService bcAnalyticService;
	
	@Autowired
	private AppConfig appConfig;

	@Autowired
    private BCConfiguration bcConfiguration;
	
	  ExecutorService pool = Executors.newFixedThreadPool(1);

	public List<uk.co.boxnetwork.data.SeriesGroup> getAllSeriesGroups(SearchParam searchParam){
		List<uk.co.boxnetwork.data.SeriesGroup> seriesgrps=new ArrayList<uk.co.boxnetwork.data.SeriesGroup>();
		List<SeriesGroup> seriesgroups=boxMetadataRepository.findAllSeriesGroup(searchParam);
		for(SeriesGroup seriesgroup:seriesgroups){
			seriesgrps.add(new uk.co.boxnetwork.data.SeriesGroup(seriesgroup));	
		}
		return seriesgrps;
	}
	public uk.co.boxnetwork.data.SeriesGroup getSeriesGroupById(Long id){
	SeriesGroup seriesGroup=boxMetadataRepository.findSeriesGroupById(id);
		if(seriesGroup==null){
			return null;
		}
		List<Series> serieses=boxMetadataRepository.findSeriesBySeriesGroup(seriesGroup);
		uk.co.boxnetwork.data.SeriesGroup ret=new uk.co.boxnetwork.data.SeriesGroup(seriesGroup);
		for(Series series:serieses){
			series.setSeriesGroup(null);
			uk.co.boxnetwork.data.Series series2=new uk.co.boxnetwork.data.Series(series);			
			ret.addSeries(series2);
		}				
		return ret;		
	}
	
	public List<uk.co.boxnetwork.data.Episode> getAllEpisodes(SearchParam searchParam){		
		return toDataEpisodes(boxMetadataRepository.findAllEpisodes(searchParam),appConfig);
	}
	
	/*
	public List<uk.co.boxnetwork.data.Episode> findEpisodes(String search){
		List<Episode> eposides=boxMetadataRepository.findEpisodes(search);
		logger.info("For search:"+search+" found matching:"+eposides.size());
		return toDataEpisodes(eposides);
	}
	*/
	private  List<uk.co.boxnetwork.data.Episode>  toDataEpisodes(List<Episode> eposides, AppConfig appConfig){
		List<uk.co.boxnetwork.data.Episode> ret=new ArrayList<uk.co.boxnetwork.data.Episode>();
		for(Episode episode:eposides){
			uk.co.boxnetwork.data.Episode dep=new uk.co.boxnetwork.data.Episode(episode,null);
			dep.setCuePoints(null);
			dep.setComplianceInformations(null);
			dep.setAvailabilities(null);
			dep.calculateRequiredFields(appConfig);
			ret.add(dep);			
		}
		
		return ret;
	}
	private  List<uk.co.boxnetwork.data.Series>  toDataSeries(List<Series> series){
		List<uk.co.boxnetwork.data.Series> ret=new ArrayList<uk.co.boxnetwork.data.Series>();
		for(Series s:series){
			uk.co.boxnetwork.data.Series dep=new uk.co.boxnetwork.data.Series(s);			
			ret.add(dep);
		}		
		return ret;
	}
	 
	
	
public List<uk.co.boxnetwork.data.Series> getAllSeries(SearchParam searchParam){		
		 return toDataSeries(boxMetadataRepository.findAllSeries(searchParam));
	}
public uk.co.boxnetwork.data.Series getSeriesById(Long id){
	Series series=boxMetadataRepository.findSeriesById(id);
	if(series==null){
		return null;
	}
	List<Episode> episodes=boxMetadataRepository.findEpisodesBySeries(series);
	List<uk.co.boxnetwork.data.Episode> eps=toDataEpisodes(episodes,appConfig);
	for(uk.co.boxnetwork.data.Episode ep:eps){
		ep.setScheduleEvents(null);
		ep.setSeries(null);
	}
	uk.co.boxnetwork.data.Series ret=new uk.co.boxnetwork.data.Series(series);
	ret.setEpisodes(eps);		
	return ret;		
}


	public uk.co.boxnetwork.data.Episode getEpisodeById(Long id){
		Episode episode=boxMetadataRepository.findEpisodeById(id);
		if(episode==null){
			logger.warn("not found episode:"+id);
			return null;
		}
		List<ScheduleEvent> scheduleEvents=boxMetadataRepository.findScheduleEventByEpisode(episode);
		
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(episode,scheduleEvents);
		ret.calculateRequiredFields(appConfig);
				
		
		return ret;		
	}
	
	
	
	public String getSoundMouseHeaderFile(Long id) throws Exception{
		Episode episode=boxMetadataRepository.findEpisodeById(id);		
		if(episode==null){
			logger.warn("not found episode:"+id);
			return null;
		}
	
		return GenericUtilities.getSoundmouseHeader(episode);
		
	}
	public String getSoundMouseSmurfFile(Long id) throws Exception{
		Episode episode=boxMetadataRepository.findEpisodeById(id);		
		if(episode==null){
			logger.warn("not found episode:"+id);
			throw new RuntimeException("episode not found");
		}
		
		if(GenericUtilities.isNotAValidId(episode.getBrightcoveId())){
			logger.warn("not found episode:"+id);
			throw new RuntimeException("episode is not published");
		}
		SoundMouseData soundMouseData=new SoundMouseData();	
		BCAnalyticsResponse bcAnalyticResponse=bcAnalyticService.getMediaItemReport(soundMouseData.getFrom(), soundMouseData.getTo(), episode.getBrightcoveId());
		soundMouseData.addSmurfItems(episode, bcAnalyticResponse);
		String itemContent=soundMouseData.buildXMLFromSoundMouseItems();
		String smurfContent=GenericUtilities.getSoundmouseSmurf(soundMouseData);
		return GenericUtilities.createFullSmurfContent(smurfContent,itemContent);
		
	}
	
	
	
	
	public SoundMouseData createSoundMouseSmurfFile() throws Exception{
		List<Episode> episodes=boxMetadataRepository.findEpisodeToReport();
		if(episodes.size()==0){
			logger.warn("there is no episode to report");
			return null;
		}		
		SoundMouseData soundMouseData=new SoundMouseData();				
		File smurfItemFile=new File("/tmp/box_"+System.currentTimeMillis()+".txt");
		
		Writer writer=null;
		try{
			for(Episode episode:episodes){
				BCAnalyticsResponse bcAnalyticResponse=bcAnalyticService.getMediaItemReport(soundMouseData.getFrom(), soundMouseData.getTo(), episode.getBrightcoveId());
				soundMouseData.addSmurfItems(episode, bcAnalyticResponse);				
			}
			
			writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(smurfItemFile), "utf-8"));
			String textContent=soundMouseData.buildXMLFromSoundMouseItems();
			if(textContent!=null && textContent.length()>0){
				writer.write(textContent);
			}
		}
		finally{
			if(writer!=null){
				writer.close();
			}
			
		}
		try{
				String smurfContent=GenericUtilities.getSoundmouseSmurf(soundMouseData);
				logger.info("smurfContent::::"+smurfContent);
				logger.info("creating the smurf file:"+soundMouseData.getSmurfFilePath());
				File smurffile=new File(soundMouseData.getSmurfFilePath());
				GenericUtilities.createFullSmurfContent(smurfContent, smurfItemFile, smurffile);
				
		}
		finally{
			smurfItemFile.delete();
		}
		return  soundMouseData;		
	}
	
	public uk.co.boxnetwork.data.CuePoint getCuePointById(Long id){
		CuePoint cuepoint=boxMetadataRepository.findCuePoint(id);
		if(cuepoint==null){
			logger.warn("not found cue:"+id);
			return null;			
		}
		return new uk.co.boxnetwork.data.CuePoint(cuepoint);
		
	}
	public uk.co.boxnetwork.data.AvailabilityWindow getAvailabilityWindowId(Long id){
		AvailabilityWindow availabilityWindow=boxMetadataRepository.findAvailabilityWindowId(id);
		if(availabilityWindow==null){
			logger.warn("not found availability:"+id);
			return null;			
		}
		return new uk.co.boxnetwork.data.AvailabilityWindow(availabilityWindow);
		
	}
	
	public void statusUpdateOnEpisodeUpdated(Episode existingEpisode, String oldIngestSource, String oldIngstProfile){
		EpisodeStatus episodeStatus=existingEpisode.getEpisodeStatus();
		MetadataStatus metadataStatus=GenericUtilities.calculateMetadataStatus(existingEpisode);
		if(metadataStatus!=null){
			episodeStatus.setMetadataStatus(metadataStatus);
		}
		else{
			if(GenericUtilities.isNotAValidId(existingEpisode.getBrightcoveId())){
				episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
			}
			else{
				episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_PUBLISH_CHANGES);
			}
			
		}
		VideoStatus videoStatus=GenericUtilities.calculateVideoStatus(existingEpisode,oldIngestSource,oldIngstProfile);
		if(videoStatus!=null){
			episodeStatus.setVideoStatus(videoStatus);
		}
		if(episodeStatus.getId()==null){
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
    	else{
    		boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
 	   
    }
	
	public void statusBoundSourceVideo(Episode existingEpisode){
		EpisodeStatus episodeStatus=existingEpisode.getEpisodeStatus();
 	   VideoStatus videoStatus=GenericUtilities.calculateVideoStatus(existingEpisode);
 	    if(videoStatus!=null){
 	    	episodeStatus.setVideoStatus(videoStatus); 	    	
 	    }
 	    else{
 	    	episodeStatus.setVideoStatus(VideoStatus.NEEDS_RETRANSCODE);
 	    	
 	    } 	   
 	    boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    }

	@Transactional
	public void update(long id, uk.co.boxnetwork.data.Episode episode){
		Episode existingEpisode=boxMetadataRepository.findEpisodeById(id);
		if(existingEpisode==null){
			logger.warn("not found episode:"+id);
			return;
		}
		String oldIngestSource=existingEpisode.getIngestSource();
		String oldIngestProfile=existingEpisode.getIngestProfile();
		logger.info("Before upating the episode:"+existingEpisode);
		episode.update(existingEpisode);
		statusUpdateOnEpisodeUpdated(existingEpisode,oldIngestSource,oldIngestProfile);
		
		boxMetadataRepository.saveTags(episode.getTags());
		boxMetadataRepository.saveClientDevices(episode.getExcludeddevices());
		logger.info("After upating the episode:"+existingEpisode);
		
		checkS3ToUpdateVidoStatus(existingEpisode);
		if(GenericUtilities.isNotValidCrid(existingEpisode.getImageURL())){
			String fname=retrieveEpisodeImageFromS3(existingEpisode);
			if(fname!=null){
				existingEpisode.setImageURL(fname);				
			}
		}		
		boxMetadataRepository.persist(existingEpisode);		
	}
	@Transactional
	public void switchEpsiodeSeries(long id, uk.co.boxnetwork.data.Episode episode){
		Episode existingEpisode=boxMetadataRepository.findEpisodeById(id);
		if(existingEpisode==null){
			logger.warn("not found episode:"+id);
			return;
		}
		Series series=boxMetadataRepository.findSeriesById(episode.getSeries().getId());
		if(series==null){
			logger.warn("not found series:"+episode.getSeries().getId());
			return;
		}
		existingEpisode.setSeries(series);
		boxMetadataRepository.persist(existingEpisode);		
	}
	
	public void update(long id, uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		SeriesGroup existingSeriesGroup=boxMetadataRepository.findSeriesGroupById(id);		
		if(existingSeriesGroup==null){
			return;
		}
		seriesGroup.update(existingSeriesGroup);
		if(GenericUtilities.isNotValidCrid(existingSeriesGroup.getImageURL())){
			String fname=retrieveSeriesGroupImageFromS3(existingSeriesGroup);
			if(fname!=null){
				existingSeriesGroup.setImageURL(fname);				
			}
		}
		

		boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
		
		List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroup.getTitle());
		mergeSeriesGroupTo(matchedSeriesGroups,existingSeriesGroup);		
	}
	private void mergeSeriesGroupTo(List<SeriesGroup> matchedSeriesGroups, SeriesGroup targetSeriesGroup){
		
		List<SeriesGroup> recordsToDelete=new ArrayList<SeriesGroup>();
		for(SeriesGroup sg:matchedSeriesGroups){
			if(sg.getId()!=null){
				if(!sg.getId().equals(targetSeriesGroup.getId())){
					recordsToDelete.add(sg);
				}
			}
		}
		if(recordsToDelete.size()==0){
			return;
		}
		for(SeriesGroup gr:recordsToDelete){
			List<Series> series=boxMetadataRepository.findSeriesBySeriesGroup(gr);
			for(Series sr:series){				
				sr.setSeriesGroup(targetSeriesGroup);
				boxMetadataRepository.updateSeries(sr);
			}	
			if(!GenericUtilities.isNotValidTitle(gr.getSynopsis())){
				if(GenericUtilities.isNotValidTitle(targetSeriesGroup.getSynopsis())){
					targetSeriesGroup.setSynopsis(gr.getSynopsis());
				}
				else{
					targetSeriesGroup.setSynopsis(targetSeriesGroup.getSynopsis()+" "+ gr.getSynopsis());
				}
			}
			
		}				
		boxMetadataRepository.removeSeriesGroup(recordsToDelete);		
	}
	
	
	private void statusUpde(Episode episode){
		EpisodeStatus episodeStatus=episode.getEpisodeStatus();
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
		
		if(episodeStatus.getId()==null){
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
    	else{
    		boxMetadataRepository.persistEpisodeStatus(episodeStatus);
    	}
		
	}
	
	private void whenVideoAvailable(EpisodeStatus episodeStatus, String ingestProfile){
		if(episodeStatus.getVideoStatus()==VideoStatus.MISSING_VIDEO){
			if(GenericUtilities.isNotValidName(ingestProfile)){
				episodeStatus.setVideoStatus(VideoStatus.MISSING_PROFILE);									
			}
			else{
				episodeStatus.setVideoStatus(VideoStatus.NEEDS_TRANSCODE);
									
			}
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
		}
	}
		
	public void checkS3ToUpdateVidoStatus(Episode episode){
		
		if(!GenericUtilities.isNotValidName(episode.getIngestSource())){
			whenVideoAvailable(episode.getEpisodeStatus(),episode.getIngestProfile());			
		}
		else{			
				requestS3(episode);
				if(!GenericUtilities.isNotValidName(episode.getIngestSource())){
					   Double durationUploaded=checkVideoDuration(episode);
					   if(durationUploaded!=null){
						   episode.setDurationUploaded(durationUploaded);
					   }					   
					whenVideoAvailable(episode.getEpisodeStatus(),episode.getIngestProfile());
				}
		}
	}
	
	public String retrieveSeriesGroupImageFromS3(SeriesGroup seriesGroup){
		if(seriesGroup==null){			
			return null;			
		}
		if(GenericUtilities.isNotValidTitle(seriesGroup.getTitle())){
			logger.info("Series group does not have valid title");
			return null;
		}
		String websafeTitle=GenericUtilities.toWebsafeTitle(seriesGroup.getTitle());
		MediaFilesLocation matchedImages=s3BucketService.listMasterImagesInImagesBucket(websafeTitle,0,-1);
		logger.info("matching the file name in the image s3 bucket:websafeTitle=["+websafeTitle+"]matchedImages:"+matchedImages.getFiles().size());
		return matchedImages.retrieveMatchBasefilename(websafeTitle);		
	}
	public String retrieveSeriesImageFromS3(Series series){
		if(series==null){
			return null;
		}		
		if(GenericUtilities.isNotValidContractNumber(series.getContractNumber())){
			return null;			
		}				
		MediaFilesLocation matchedImages=s3BucketService.listMasterImagesInImagesBucket(series.getContractNumber(),0,-1);
		String matchfilename=matchedImages.retrieveMatchBasefilename(series.getContractNumber());
		if(matchfilename!=null){
			return matchfilename;
		}
		if(GenericUtilities.isNotValidTitle(series.getName())){
			return null;
		}
		String websafetitle=GenericUtilities.toWebsafeTitle(series.getName());	
		matchedImages=s3BucketService.listMasterImagesInImagesBucket(websafetitle+"_"+series.getContractNumber(),0,-1);
		return matchedImages.retrieveMatchBasefilename(websafetitle+"_"+series.getContractNumber());
	}
	public String retrieveEpisodeImageFromS3(Episode episode){
		if(episode==null){
			return null;
		}	
		String matchedfile=null;
		if(!GenericUtilities.isNotValidCrid(episode.getMaterialId())){
			String matid=GenericUtilities.materialIdToImageFileName(episode.getMaterialId());			
			MediaFilesLocation matchedEpisodeImages=s3BucketService.listMasterImagesInImagesBucket(matid,0,-1);
			matchedfile=matchedEpisodeImages.retrieveMatchBasefilename(matid);
			if(matchedfile!=null){
				return matchedfile;
			}
		}
		if(GenericUtilities.isNotValidContractNumber(episode.getCtrPrg()) || GenericUtilities.isNotValidTitle(episode.getTitle()) ){				
			return null;
		}		
		String websafeTitle=GenericUtilities.toWebsafeTitle(episode.getTitle());
		String matid=GenericUtilities.materialIdToImageFileName(episode.getMaterialId());		
		MediaFilesLocation matchedEpisodeImages=s3BucketService.listMasterImagesInImagesBucket(websafeTitle+"_"+matid,0,-1);				
		return matchedEpisodeImages.retrieveMatchBasefilename(websafeTitle+"_"+matid);
	}
	
	
	@Transactional
	public uk.co.boxnetwork.data.Episode updateEpisodeById(uk.co.boxnetwork.data.Episode episode){
		Episode episodeInDB=boxMetadataRepository.findEpisodeById(episode.getId());
		if(episodeInDB==null){
			logger.info("not found episode");
			return null;
		}
		if(episode.updateWhenReceivedByMaterialId(episodeInDB)){
			boxMetadataRepository.persist(episodeInDB);
		}
		if(episode.getEpisodeStatus()!=null){
			changeEpisodeStatus(episode,episodeInDB);
		}
		return new uk.co.boxnetwork.data.Episode(episodeInDB,null);
	}
	public void  changeEpisodeStatus(uk.co.boxnetwork.data.Episode episode,Episode episodeInDB){
		
		EpisodeStatus episodeStatus=episode.getEpisodeStatus();
		EpisodeStatus episodeStatusInDB=episodeInDB.getEpisodeStatus();
		if(episodeStatus.getPublishedStatus()==PublishedStatus.ACTIVE){
			
			if(episodeInDB.getBrightcoveId()==null){
				   logger.error("brigtcoveid is null when trying to activate the epsideo");
					throw new RuntimeException("Needs to create the place holder first");
			}
			else if(episodeStatusInDB.canActivate()){				   
				BCVideoData videodata=videoService.changeVideoStatus(episodeInDB.getBrightcoveId(),"ACTIVE");			
				logger.info("*****"+videodata);
				episodeStatusInDB.setPublishedStatus(PublishedStatus.ACTIVE);
				boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
			}
			else{
				logger.error("trying to activate the epsideo not approved");
				throw new RuntimeException("Needs to approve the episode first");
			}
		}
		else if(episodeStatus.getPublishedStatus()==PublishedStatus.INACTIVE){
			if(episodeInDB.getBrightcoveId()==null){				
				if(episodeStatusInDB.getPublishedStatus()==PublishedStatus.ACTIVE){
					episodeStatusInDB.setPublishedStatus(PublishedStatus.INACTIVE);
					boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
					logger.info("INACTIVE because bcid is null");
					
				}
				else{
					logger.info("ignoring deactivation command because bcid is null");
					
				}
			}
			else{
					BCVideoData videodata=videoService.changeVideoStatus(episodeInDB.getBrightcoveId(),"INACTIVE");
					logger.info("INACTIVE is set");
					if(episodeStatusInDB.getPublishedStatus()==PublishedStatus.ACTIVE){
						episodeStatusInDB.setPublishedStatus(PublishedStatus.INACTIVE);
						boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
					}
			}			
		}
		else if(episodeStatus.getPublishedStatus()!=episodeStatusInDB.getPublishedStatus()){
			episodeStatusInDB.setPublishedStatus(episodeStatus.getPublishedStatus());
			boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
		}		
		else{
			logger.info("****publish status not changed:"+episodeStatus.getPublishedStatus());
		}
		
//		if(episodeStatus.update(episodeStatusInDB)){        	
//			boxMetadataRepository.persistEpisodeStatus(episodeStatusInDB);
//		}
    }
	@Transactional
	public uk.co.boxnetwork.data.Episode reicevedEpisodeByMaterialId(uk.co.boxnetwork.data.Episode episode){
		String materiaId=episode.getMaterialId();
		
		if(materiaId==null){
			throw new RuntimeException("MaterialId is required");
		}
		materiaId=materiaId.trim();
		if(materiaId.length()==0){
			throw new RuntimeException("MaterialId is empty");
		}
		
		String contractNumber=GenericUtilities.getContractNumber(episode);
		String programmeId=GenericUtilities.getProgrammeNumber(episode);
		logger.info("contractNumber:"+contractNumber+" programmeId=["+programmeId+"]");
		Series existingSeries=null;
		SeriesGroup existingSeriesGroup=null;	
		Episode existingEpisode=null;		
		
		List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(programmeId);		
		
		if(matchedEpisodes.size()==0){
			logger.info("matching episode number not found programmeId:"+programmeId);
		}
		else if(matchedEpisodes.size()>1){
			throw new RuntimeException("more than one episodes matched to the materia id:"+materiaId);
		}
		else{
				existingEpisode=matchedEpisodes.get(0);
				existingSeries=existingEpisode.getSeries();
				if(existingSeries!=null){
					existingSeriesGroup=existingSeries.getSeriesGroup();
					
				}
		}	
		if(existingSeries==null && episode.getSeries()!=null && episode.getSeries().getId()!=null){
			existingSeries=boxMetadataRepository.findSeriesById(episode.getSeries().getId());
			if(existingEpisode!=null){
				existingSeriesGroup=existingSeries.getSeriesGroup();
			}			
		}
	    if(existingSeries==null && (!GenericUtilities.isNotValidCrid(contractNumber))){
		     List<Series> matchSeries=boxMetadataRepository.findSeriesByContractNumber(contractNumber);
		     if(matchSeries.size()==0){
		    	 logger.info("matching series not found contractNumber:"+contractNumber);
		     }
		     else if(matchSeries.size()>1){
		    	 throw new RuntimeException("more than one series matching the contractNumber:"+contractNumber);
		     }
		     else{
		    	 	existingSeries=matchSeries.get(0);		    	 	
		    	 	existingSeriesGroup=existingSeries.getSeriesGroup();
		     }
	    }   
		if(existingSeriesGroup==null && episode.getSeries() !=null && episode.getSeries().getSeriesGroup() !=null && (!GenericUtilities.isNotValidTitle(episode.getSeries().getSeriesGroup().getTitle()))){
			List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(episode.getSeries().getSeriesGroup().getTitle());
			if(matchedSeriesGroups.size()>0){
				existingSeriesGroup=matchedSeriesGroups.get(0);
			}
			else{
					existingSeriesGroup=new SeriesGroup();
					episode.getSeries().getSeriesGroup().update(existingSeriesGroup);
					boxMetadataRepository.persisSeriesGroup(existingSeriesGroup);
					logger.info("created a nw series group:"+existingSeriesGroup);
			}
		}
		else if(existingSeriesGroup==null){
			existingSeriesGroup=boxMetadataRepository.retrieveDefaultSeriesGroup();
		}
		
		if(existingSeries==null && episode.getSeries()!=null && GenericUtilities.isNotValidTitle(episode.getSeries().getName())){
			existingSeries=new Series();					
			episode.getSeries().update(existingSeries);				
			if(GenericUtilities.isNotValidCrid(existingSeries.getContractNumber())){
				existingSeries.setContractNumber(contractNumber);
			}									
			existingSeries.setSeriesGroup(existingSeriesGroup);
			existingSeries.updateNextEpisodeNumber(episode.getProgrammeNumber());
			boxMetadataRepository.persisSeries(existingSeries);
		}
		if(GenericUtilities.isNotValidCrid(existingSeriesGroup.getImageURL())){
			String fname=retrieveSeriesGroupImageFromS3(existingSeriesGroup);
			if(fname!=null){
				if(GenericUtilities.isNotValidCrid(existingSeriesGroup.getImageURL())){
					existingSeriesGroup.setImageURL(fname);
					boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
				}
			}
		}
		if(existingSeries!=null){
			checks3ToSetSeriesImage(existingSeries);
			if(existingSeries.updateNextEpisodeNumber(episode.getProgrammeNumber())){
				boxMetadataRepository.mergeSeries(existingSeries);
			}
		}
		
		if(existingEpisode==null){
			logger.info("creating a new episode*******1222222******");
			existingEpisode=new Episode();			
			episode.update(existingEpisode);
			existingEpisode.setSeries(existingSeries);
			EpisodeStatus episodeStatus=new EpisodeStatus();
			episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
			episodeStatus.setVideoStatus(VideoStatus.NO_PLACEHOLDER);	
			if(appConfig.getAutoSetPublishedStatus()!=null){
				episodeStatus.setPublishedStatus(appConfig.getAutoSetPublishedStatus());				
			}
			boxMetadataRepository.persistEpisodeStatus(episodeStatus);
			existingEpisode.setEpisodeStatus(episodeStatus);
			boxMetadataRepository.saveTags(episode.getTags());
			boxMetadataRepository.saveClientDevices(episode.getExcludeddevices());
			if(GenericUtilities.isNotValidCrid(existingEpisode.getImageURL())){
				String fname=retrieveEpisodeImageFromS3(existingEpisode);
				if(fname!=null){
					existingEpisode.setImageURL(fname);				
				}
			}			
			boxMetadataRepository.persist(existingEpisode);
			if(appConfig.getAutoYearsAvailability()!=null){
				logger.info("**Auto creating availability");				
				boxMetadataRepository.replaceAvailabilityWindow(existingEpisode.getId(), Calendar.getInstance().getTime(),GenericUtilities.nextYearsDate(appConfig.getAutoYearsAvailability()));
			}
			else{
				logger.info("will not auto create availability");
			}
			
		}		
		else{	
			   logger.info("Upating the existing episode");
			    existingEpisode.setSeries(existingSeries);
				episode.updateWhenReceivedByMaterialId(existingEpisode);				
				boxMetadataRepository.saveTags(episode.getTags());
				statusUpde(existingEpisode);
				if(GenericUtilities.isNotValidCrid(existingEpisode.getImageURL())){
					String fname=retrieveEpisodeImageFromS3(existingEpisode);
					if(fname!=null){
						existingEpisode.setImageURL(s3BucketService.getMasterImageFullURL(fname));				
					}
				}
				boxMetadataRepository.persist(existingEpisode);
		}
		replaceCuePoints(existingEpisode,episode.getCuePoints());		
		checkS3ToUpdateVidoStatus(existingEpisode);		
		boxMetadataRepository.persist(existingEpisode);	
		uk.co.boxnetwork.data.Episode ret=new uk.co.boxnetwork.data.Episode(existingEpisode,null);
		ret.setComplianceInformations(existingEpisode.getComplianceInformations());
		return ret;
	}
	
	@Transactional
	public uk.co.boxnetwork.data.Series createNewSeries(uk.co.boxnetwork.data.Series series){
		Series ser=createOrUpdateSeries(series, series.getContractNumber());
		checks3ToSetSeriesImage(ser);
		return new uk.co.boxnetwork.data.Series(ser);
	}
	
	private void checks3ToSetSeriesImage(Series existingSeries){
		if(GenericUtilities.isNotValidCrid(existingSeries.getImageURL())){
			String fname=retrieveSeriesImageFromS3(existingSeries);
			if(fname!=null){
				existingSeries.setImageURL(fname);
				boxMetadataRepository.mergeSeries(existingSeries);
			}
		}
	}
	public Series createOrUpdateSeries(uk.co.boxnetwork.data.Series series, String contractNumber){
		if(series==null){
			return null;
		}		
		SeriesGroup sg=createOrUpdateSeriesGroup(series.getSeriesGroup());
		Series existingSeries=null;
		if(contractNumber!=null){
			List<Series> matchSeries=boxMetadataRepository.findSeriesByContractNumber(contractNumber);
			if(matchSeries.size()==0){
				if(GenericUtilities.isNotValidTitle(series.getName())){
					return null;					
				}
				else{
					existingSeries=new Series();					
					series.update(existingSeries);
					if(GenericUtilities.isNotValidCrid(series.getContractNumber())){
						series.setContractNumber(contractNumber);
					}
					existingSeries.setSeriesGroup(sg);
					
					boxMetadataRepository.persisSeries(existingSeries);
					return existingSeries;
				}
			}
			else{
				  existingSeries=matchSeries.get(0);
				  logger.info("Upating the existing with the contract number contractNumber=["+contractNumber+"]existingSeries=["+existingSeries+"]");
				  if(GenericUtilities.isNotValidTitle(series.getName()) && GenericUtilities.isNotValidTitle(series.getSynopsis()) ){					  
					  if(sg==null){
						  return existingSeries;
					  }
					  else{
						  existingSeries.setSeriesGroup(sg);
						  boxMetadataRepository.mergeSeries(existingSeries);
						  return existingSeries;
					  }
				  }
				  else if(GenericUtilities.isNotValidTitle(series.getName())){
					  if(GenericUtilities.isNotValidTitle(series.getSynopsis())){						  
						  if(sg==null){
							  return existingSeries;
						  }
						  else{
							  	existingSeries.setSeriesGroup(sg);
							  	boxMetadataRepository.mergeSeries(existingSeries);
							  	return existingSeries;
						  }
					  }
					  else{
						  if(GenericUtilities.isNotValidTitle(existingSeries.getSynopsis())){
							  existingSeries.setSynopsis(series.getSynopsis());
							  existingSeries.setSeriesGroup(sg);
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
						  else{
							  existingSeries.setSynopsis(existingSeries.getSynopsis()+" "+series.getSynopsis());
							  existingSeries.setSeriesGroup(sg);
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
					  }
				  }
				  else if(GenericUtilities.isNotValidTitle(series.getSynopsis())){
					  existingSeries.setSeriesGroup(sg);
					  existingSeries.setName(series.getName());					  
					  boxMetadataRepository.mergeSeries(existingSeries);
				      return existingSeries;					  	  
				  }
				  else{
					  	existingSeries.setSeriesGroup(sg);
					  	existingSeries.setName(series.getName());
					  	if(GenericUtilities.isNotValidTitle(existingSeries.getSynopsis())){
							  existingSeries.setSynopsis(series.getSynopsis());							  
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
						  else{
							  existingSeries.setSynopsis(existingSeries.getSynopsis()+" "+series.getSynopsis());							  
							  boxMetadataRepository.mergeSeries(existingSeries);
							  return existingSeries;
						  }
					  
				  }
				
			}
			
		}
		else{
			
			if(GenericUtilities.isNotValidTitle(series.getName())){
					return null;					
			}
			else{
				existingSeries=new Series();					
				series.update(existingSeries);
				existingSeries.setSeriesGroup(sg);
				boxMetadataRepository.persisSeries(existingSeries);
				return existingSeries;
			}
		
		
		}
		
		
		
		
	}
	
	
	@Transactional
	public uk.co.boxnetwork.data.SeriesGroup createANewSeriesGroup(uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		if(seriesGroup==null){
			throw new RuntimeException("nul value for seriesGroup");
		}				
		if(GenericUtilities.isNotValidTitle(seriesGroup.getTitle())){
			throw new RuntimeException("not valid title for seriesGroup");			
		}
		List<SeriesGroup> seriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroup.getTitle());
		if(seriesGroups.size()==0){
			SeriesGroup newSeriesGroup=new SeriesGroup();
			seriesGroup.update(newSeriesGroup);	
			if(GenericUtilities.isNotValidCrid(newSeriesGroup.getImageURL())){
				String fname=retrieveSeriesGroupImageFromS3(newSeriesGroup);
				if(fname!=null){
					newSeriesGroup.setImageURL(fname);				
				}
			}
			boxMetadataRepository.persisSeriesGroup(newSeriesGroup);
			return new uk.co.boxnetwork.data.SeriesGroup(newSeriesGroup);
		}
		else{
								
			   throw new RuntimeException("There is already a seriesgroup with the same title");	
		}
	}
	
	public SeriesGroup createOrUpdateSeriesGroup(uk.co.boxnetwork.data.SeriesGroup seriesGroup){
		if(seriesGroup==null){
			return boxMetadataRepository.retrieveDefaultSeriesGroup();
		}
		
		if(seriesGroup.getId()!=null){
			SeriesGroup seriesgroup=boxMetadataRepository.findSeriesGroupById(seriesGroup.getId());
			if(seriesgroup!=null){
				return seriesgroup;
			}
			else{
				logger.error("The series group with the id not found id:"+seriesGroup.getId());
				return boxMetadataRepository.retrieveDefaultSeriesGroup();
			}
		}
		if(GenericUtilities.isNotValidTitle(seriesGroup.getTitle())){
			logger.info("The series group title is not there, so ");
			return boxMetadataRepository.retrieveDefaultSeriesGroup();		
		}
		List<SeriesGroup> seriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroup.getTitle());
		if(seriesGroups.size()==0){
			SeriesGroup newSeriesGroup=new SeriesGroup();
			seriesGroup.update(newSeriesGroup);
			
			boxMetadataRepository.persisSeriesGroup(newSeriesGroup);
			return newSeriesGroup;
		}
		else{
				SeriesGroup existingSeriesGroup=seriesGroups.get(0);
				mergeSeriesGroupTo(seriesGroups, existingSeriesGroup);
				if(GenericUtilities.isNotValidTitle(seriesGroup.getSynopsis())){
					return existingSeriesGroup;
				}
				if(GenericUtilities.isNotValidTitle(existingSeriesGroup.getSynopsis())){
					existingSeriesGroup.setSynopsis(seriesGroup.getSynopsis());
					boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
					return existingSeriesGroup;
				}
				else {
					existingSeriesGroup.setSynopsis(seriesGroup.getSynopsis()+" "+seriesGroup.getSynopsis());
					boxMetadataRepository.mergeSeriesGroup(existingSeriesGroup);
					return existingSeriesGroup;
				}
					
				
		}
		
		
	}
	
	
	public void replaceCuePoints(Episode episodeToUpdate, List<uk.co.boxnetwork.data.CuePoint> cuePoints){
		if(cuePoints==null|| cuePoints.size()==0){
			logger.info("***number of cue points received is zero");
			return;
			
		}
		else{
			logger.info("***number of cue points received:"+cuePoints.size());
		}
		if(episodeToUpdate.getCuePoints()!=null){
		    for(CuePoint cp:episodeToUpdate.getCuePoints()){	    	
		    	boxMetadataRepository.remove(cp);	    	
		    }
		}
	    episodeToUpdate.clearCuePoints();
	    
	    
		for(uk.co.boxnetwork.data.CuePoint cuepoint:cuePoints){
			    logger.info("Received cue point:"+cuepoint);
			    if(cuepoint.getTime()==null){
			    	logger.info("Ignoring the cue points that time is null");
			    	continue;

			    }
			     CuePoint cue=new CuePoint();
			     cuepoint.update(cue);			     
			     logger.info("after update:"+cue);
			     episodeToUpdate.addCuePoint(cue);
			     cue.setEpisode(episodeToUpdate);
			     
			     
			     boxMetadataRepository.persist(cue);
			     
		}		
	}
	
	
	@Transactional
	public void update(long id, uk.co.boxnetwork.data.Series series){
		Series existingSeries=boxMetadataRepository.findSeriesById(id);
		if(existingSeries==null){
			return;
		}
		series.update(existingSeries);
		checks3ToSetSeriesImage(existingSeries);
		
		boxMetadataRepository.update(existingSeries);
		if(existingSeries.getSeriesGroup()==null){
			existingSeries.setSeriesGroup(boxMetadataRepository.retrieveDefaultSeriesGroup());
			boxMetadataRepository.update(existingSeries);
		}
		
	}
	@Transactional
	public void switchSeriesGroup(long id, uk.co.boxnetwork.data.Series series){
		String seriesGroupTitle=SeriesGroup.DEFAULT_SERIES_GROUP_TITLE;
		if(series.getSeriesGroup()!=null && series.getSeriesGroup().getTitle()!=null && series.getSeriesGroup().getTitle().trim().length()>0){
			seriesGroupTitle=series.getSeriesGroup().getTitle().trim();
		}
		Series existingSeries=boxMetadataRepository.findSeriesById(id);
		if(existingSeries==null){
			return;
		}		
		SeriesGroup seriesGroup=null;
		if(series.getSeriesGroup()!=null && series.getSeriesGroup().getId()!=null){
			seriesGroup=boxMetadataRepository.findSeriesGroupById(series.getSeriesGroup().getId());			
		}
		if(seriesGroup==null){
			List<SeriesGroup> matchedSeriesGroups=boxMetadataRepository.findSeriesGroupByTitle(seriesGroupTitle);
			if(matchedSeriesGroups.size()>0){
				seriesGroup=matchedSeriesGroups.get(0);
			}
		}
		if(seriesGroup==null){
			seriesGroup=new SeriesGroup();
			seriesGroup.setTitle(seriesGroupTitle);
			boxMetadataRepository.persisSeriesGroup(seriesGroup);
		}
		existingSeries.setSeriesGroup(seriesGroup);
		boxMetadataRepository.update(existingSeries);
	}
		
	public List<uk.co.boxnetwork.data.ScheduleEvent> getAllScheduleEventFrom(SearchParam  searchParam){
		List<ScheduleEvent> schedules= boxMetadataRepository.findScheduleEventsFrom(searchParam);
	
		List<uk.co.boxnetwork.data.ScheduleEvent> ret=new ArrayList<uk.co.boxnetwork.data.ScheduleEvent>();
		for(ScheduleEvent evt:schedules){
			ret.add(new uk.co.boxnetwork.data.ScheduleEvent(evt));			
		}
		return ret;
	}
	public ScheduleEvent getScheduleEventById(Long id){
		return boxMetadataRepository.findScheduleEventById(id);
	}
	
   @Transactional	 
   public void  bindVideoFile(String ingestFile){
	   String materialId=GenericUtilities.fileNameToMaterialID(ingestFile);
	   logger.info("should attach video file:"+ingestFile+" to the material id:"+materialId);
	   List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(materialId+"%");
	   if(matchedEpisodes.size()==0){
		   logger.info("None of the episode has the matching materialId for the video file:"+ingestFile);
		   return;
	   }
	   else if(matchedEpisodes.size()==1){
		   Episode episode=matchedEpisodes.get(0);
		   episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));		   
		   statusBoundSourceVideo(episode);
		   Double durationUploaded=checkVideoDuration(episode);
		   if(durationUploaded!=null){
			   episode.setDurationUploaded(durationUploaded);
		   }
		   boxMetadataRepository.persist(episode);
		   if(appConfig.getAutoTranscode()!=null && appConfig.getAutoTranscode() && episode.getBrightcoveId()!=null && episode.getIngestProfile()!=null){
			   persistIngestIntoBcCommand(episode.getId());
		   }
		   else{
			   logger.info("transcode should be initiated manually");
		   }
		   
	   }
	   else{
		   logger.warn("more than epsiode matching the materialId=["+materialId+"] while binding video file:"+ingestFile+"]");
		   for(Episode episode: matchedEpisodes){
			   if(episode.getIngestSource()!=null){
				   episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));
				   statusBoundSourceVideo(episode);
				   
				   Double durationUploaded=checkVideoDuration(episode);
				   if(durationUploaded!=null){
					   episode.setDurationUploaded(durationUploaded);
				   }
				   boxMetadataRepository.persist(episode);
				   if(episode.getIngestProfile()!=null && appConfig.getAutoTranscode()!=null && appConfig.getAutoTranscode()){
					   persistIngestIntoBcCommand(episode.getId());
				   }
			   }
		   }
	   }
	   
	   
   }
   
   public Double checkVideoDuration(Episode episode){
	   try{
		   if(GenericUtilities.isNotValidCrid(episode.getIngestSource())){
			   return null;
		   }
		   String url=s3BucketService.generatedPresignedURL(episode.getIngestSource(), 20);
		   String commandResult=commandService.getVideoDuration(url);
		   Double duration=Double.valueOf(commandResult);		   
		   
		   return duration;
	   }
	   catch(Exception e){
		   logger.error(e+" while getting the duration:"+e,e);
		   return null;
	   }
   }
   
   public void deleteEpisodeById(Long episodeid){	   
		 boxMetadataRepository.removeEpisode(episodeid);
   }
   
   public void deleteSeriesGroupById(Long seriesgroupid){
	   boxMetadataRepository.removeSeriesGroupById(seriesgroupid);	   
   }
   public void deleteSeriesById(Long seriesid){
	   boxMetadataRepository.removeSeriesById(seriesid);
	   
   }
   
   public  void requestS3(Episode episode){
		 String fileNameFilter=episode.calculateSourceVideoFilePrefix();
		 if(fileNameFilter==null){
			 return;
		 }
		 MediaFilesLocation matchedfiles=s3BucketService.listFilesInVideoBucket(fileNameFilter,0,-1,null);
		 String ingestFile=matchedfiles.highestVersion();
		 if(ingestFile!=null){
			 episode.setIngestSource(s3BucketService.getFullVideoURL(ingestFile));
		 }	 
		 	
	 }

	
  public void notifyTranscode(BCNotification notification){
	  if("FAILED".equals(notification.getStatus())){
		  boxMetadataRepository.markVideoTranscodeAsFailed(notification.getVideoId());		  
	  }
	  else if("SUCCESS".equals(notification.getStatus()) && "TITLE".equals(notification.getEntityType())){
		  boxMetadataRepository.markVideoTranscodeAsComplete(notification.getVideoId());		  
	  }
  }
  
  @Transactional
  public void updatePublishedStatus(Episode episode){
	  EpisodeStatus episodeStatus=episode.getEpisodeStatus();
	  if(episode.getBrightcoveId()==null){		  
		  if(episodeStatus.getMetadataStatus()!=MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER){			  			 
			  episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
			  episodeStatus.setVideoStatus(VideoStatus.NO_PLACEHOLDER);
			  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
		  }
		  else if(episodeStatus.getVideoStatus()!=VideoStatus.NO_PLACEHOLDER){
			  episodeStatus.setVideoStatus(VideoStatus.NO_PLACEHOLDER);
			  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
		  }
	  }
	  else{
			  BCVideoData videoData=videoService.getVideo(episode.getBrightcoveId());
			  if("INACTIVE".equals(videoData.getState())){
				  if(episodeStatus.getPublishedStatus()==PublishedStatus.ACTIVE){					  
					  episodeStatus.setPublishedStatus(PublishedStatus.INACTIVE);
					  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
				  }	  
			  }
			  if("ACTIVE".equals(videoData.getState())){
				  if(episodeStatus.getPublishedStatus()!=PublishedStatus.ACTIVE){
					  episodeStatus.setPublishedStatus(PublishedStatus.ACTIVE);
					  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
				  }	  
			  }
			  if(episodeStatus.getMetadataStatus()==MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER){
				  episodeStatus.setMetadataStatus(MetadataStatus.PUBLISHED);				  
				  boxMetadataRepository.persistEpisodeStatus(episodeStatus);
			  }
	  }
  }
  public  uk.co.boxnetwork.data.Episode publishMetadatatoBCByEpisodeId(Long id){
	  Episode uptoDateEpisode=boxMetadataRepository.findEpisodeById(id);	
	  if(uptoDateEpisode==null){
		  logger.warn("not found episode:"+id);
		  return null;
	  }
	  if(!GenericUtilities.isNotValidCrid(uptoDateEpisode.getBrightcoveId())){		  		  
			  BCVideoData videoData=videoService.publishEpisodeToBrightcove(id);
			  logger.info("The changes is pushed to the bc:"+videoData);
			  uptoDateEpisode=boxMetadataRepository.findEpisodeById(id);		  
	  }	
	  else{
		  BCVideoData videoData=videoService.publishEpisodeToBrightcove(id);
		  logger.info("place holder is create for the bc:"+videoData);
		  uptoDateEpisode=boxMetadataRepository.findEpisodeById(id);
	  }
	  return new uk.co.boxnetwork.data.Episode(uptoDateEpisode,null);
  }
  
  
  
  public void registerEpisodeForPublishMetadata(Long episodeid){
	   Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	   if(episode==null){
		   logger.info("episode seems deleted");
		   return;
	   }
	   boxMetadataRepository.markMetadataChanged(episode);
	   MediaCommand mediaCommand=new MediaCommand();
	   mediaCommand.setCommand(MediaCommand.PUSH_CHANGES_ON_NEEDS_TO_PUBLISH);
	   mediaCommand.setEpisodeid(episodeid);		  	 
	   executeMediaCommandForLater(mediaCommand);
	   logger.info("peristed the publish changes need to publish command:"+mediaCommand);
  }
  
  public  uk.co.boxnetwork.data.Series publishMetadatatoBCBySeriesId(Long id){
	  Series series=boxMetadataRepository.findSeriesById(id);
	  if(series==null){
		  logger.warn("not found series:"+id);
		  return null;
	  }
	  List<Episode> episodes=boxMetadataRepository.findEpisodesBySeries(series);
	  logger.info("Publishing all the episodes in this series");
	  for(Episode ep:episodes){		  
		  if(ep.getBrightcoveId()!=null){			  
			  registerEpisodeForPublishMetadata(ep.getId());			  
		  }		   
	  }	    
	  return new uk.co.boxnetwork.data.Series(series);
  }
  
  public  uk.co.boxnetwork.data.SeriesGroup publishMetadatatoBCBySeriesGroupId(Long id){
	  SeriesGroup seriesgroup=boxMetadataRepository.findSeriesGroupById(id);
	  List<Series> series=boxMetadataRepository.findSeriesBySeriesGroup(seriesgroup);
	  logger.info("Publishing all the episodes in this series group");
	  for(Series sr:series){
		  publishMetadatatoBCBySeriesId(sr.getId()); 
	  }	    
	  return new uk.co.boxnetwork.data.SeriesGroup(seriesgroup);
  }

  
  public void createNewCuepoint(Long episodeid, uk.co.boxnetwork.data.CuePoint cuePoint){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(episode==null){
		  logger.warn("not found episode:"+episodeid);
		  return;
	  }
	  CuePoint cue=new CuePoint();
	  cuePoint.update(cue);
	  boxMetadataRepository.persistCuePoint(cue,episode);
	  cuePoint.setId(cue.getId());
  }
  public void createNewAvailability(Long episodeid, uk.co.boxnetwork.data.AvailabilityWindow availabilityWindow){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(episode==null){
		  logger.warn("not found episode:"+episodeid);
		  return;
	  }
	  AvailabilityWindow avwindow=new AvailabilityWindow();
	  availabilityWindow.update(avwindow);
	  boxMetadataRepository.persistAvailabilityWindow(avwindow,episode);
	  availabilityWindow.setId(avwindow.getId());
  }
  
  public void updateCuepoint(Long episodeid,Long cueid, uk.co.boxnetwork.data.CuePoint cuePoint){
	  CuePoint cue=boxMetadataRepository.findCuePoint(cueid);
	  if(cue.getId()!=null){
		  if(!cue.getId().equals(cueid)){
			  throw new RuntimeException("Not permitted to update not matching cue cueid=["+cueid+"]cuePoint=["+cuePoint);  
		  }
	  }
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(episode.getId()!=null){
		  if(!episode.getId().equals(episodeid)){
			  throw new RuntimeException("Not permitted to update not matching cue episodeid=["+episodeid+"]");
		  }
	  }
	  boxMetadataRepository.updateCue(cuePoint);	  
	  
  }
  public void updateAvailabilityWindow(Long episodeid,Long availabilitywindowid, uk.co.boxnetwork.data.AvailabilityWindow availabilitywindow){
	  AvailabilityWindow avwindow=boxMetadataRepository.findAvailabilityWindowId(availabilitywindowid);
	  if(avwindow.getId()!=null){
		  if(!avwindow.getId().equals(availabilitywindowid)){
			  throw new RuntimeException("Not permitted to update not matching availability availabilitywindowid=["+availabilitywindowid+"]avwindow=["+avwindow);  
		  }
	  }
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  if(episode.getId()!=null){
		  if(!episode.getId().equals(episodeid)){
			  throw new RuntimeException("Not permitted to update not matching availabilitywindow episodeid=["+episodeid+"]");
		  }
	  }
	  boxMetadataRepository.updateAvailabilityWindow(availabilitywindow);	  
	  
  }
  public uk.co.boxnetwork.data.CuePoint deleteCuepoint(Long episodeid, Long cueid){	  	  
	  CuePoint cue=boxMetadataRepository.findCuePoint(cueid);
	  if(cue==null){
		  logger.error("could not find the cue point to delete cueid=["+cueid+"]");
		  return null;
	  }
	  if(cue.getEpisode().getId()!=null && cue.getEpisode().getId().equals(episodeid)){
		  boxMetadataRepository.removeCuePoint(cueid);		  
		  return new uk.co.boxnetwork.data.CuePoint(cue);
	  }
	  else{		  	
		  throw new RuntimeException("Not permitted to delete not matching cue cueid=["+cueid+"]episodeid=["+episodeid);		  
	  }
	  
  }
  public uk.co.boxnetwork.data.AvailabilityWindow deleteAvailabilityWindow(Long episodeid, Long avid){	  	  
	  AvailabilityWindow availabilityWindow=boxMetadataRepository.findAvailabilityWindowId(avid);
	  if(availabilityWindow==null){
		  return null;
	  }
	  if(availabilityWindow.getEpisode().getId()!=null && availabilityWindow.getEpisode().getId().equals(episodeid)){
		  boxMetadataRepository.removeAvailabilityWindow(avid);		  
		  return new uk.co.boxnetwork.data.AvailabilityWindow(availabilityWindow);
	  }
	  else{		  	
		  throw new RuntimeException("Not permitted to delete not matching availability avid=["+avid+"]episodeid=["+episodeid);		  
	  }
	  
  }
  
  
  private boolean matchImageToEpisodes(List<Episode> matchedEpisodes,String imagefile){
	  if(matchedEpisodes==null||matchedEpisodes.size()==0){
		  return false;
	  }	 
	   for(Episode episode:matchedEpisodes){					  					  
			  if(GenericUtilities.isNotValidCrid(episode.getImageURL())){
				  logger.info("setting imageURL for episode:"+episode.getId()+":"+imagefile+":"+imagefile);
				  boxMetadataRepository.setEpisodeImage(episode.getId(), imagefile);
//				  if(appConfig.getBrightcoveStatus()){
//					  persistPublishChangesNeedsPublish(episode.getId());
//				  }				  
			  }
			  else{
				  logger.info("imageURL is already set for episode:"+episode.getId()+":"+imagefile+":"+imagefile);						  
			  }
	   }				  
	  return true;
  }
  private boolean matchImageToSeries(List<Series> matchedSeries,String ImageName){
	  if(matchedSeries==null || matchedSeries.size()==0){
		  return false;
	  }
	  for(Series series:matchedSeries){					  					  
			  if(GenericUtilities.isNotValidCrid(series.getImageURL())){
				  logger.info("setting imageURL for series:"+series.getId()+":"+ImageName);
				  boxMetadataRepository.setSeriesImage(series.getId(), ImageName);
			  }
			  else{
				  logger.info("imageURL is already set for series:"+series.getId()+":"+ImageName);						  
			  }		  
	   }				  
		  return true;
  }
private boolean matchImageToSeriesGroup(List<SeriesGroup> matchedSeriesGroup,String imagefile){
	if(matchedSeriesGroup==null || matchedSeriesGroup.size()==0){
		  return false;
	  }	  
	 for(SeriesGroup seriesgroup:matchedSeriesGroup){
			  if(GenericUtilities.isNotValidCrid(seriesgroup.getImageURL())){
				  logger.info("setting imageURL for series group:"+seriesgroup.getId()+":"+imagefile);
				  boxMetadataRepository.setSeriesGroupImage(seriesgroup.getId(), imagefile);
			  }
			  else{
				  logger.info("imageURL is already for series group:"+seriesgroup.getId()+":"+imagefile);
			  }
		  }
	 return true;
	}

  public void notifyMasterImageDelete(String imagefile){
	  String basename=imagefile;
		
		int ib=imagefile.indexOf(".");
						
		if(ib!=-1){
			 basename=imagefile.substring(0,ib);
			 if(basename.length()<=1){
				 logger.error("filename too short, so will not be ldeted");
				 return;
			 }
		}
		
		List<FileItem> items=s3BucketService.listGenereratedImages(basename, 0, -1);
		for(FileItem item:items){
			logger.info("Deleting the file:"+item.getFile());
			s3BucketService.deleteImagesInImageBucket(item.getFile());
		}
		
	  
  }
  @Transactional
  public void setSeriesImage(Long seriesid, String imageURL){
	  Series series=boxMetadataRepository.findSeriesById(seriesid);
	  series.setImageURL(imageURL);
	  boxMetadataRepository.persisSeries(series);	  
	  logger.info("seting the image in the series to:"+imageURL);
	  
	  
  }
  @Transactional
  public void setSeriesGroupImage(Long seriegroupsid, String imageURL){
	  SeriesGroup seriesgrop=boxMetadataRepository.findSeriesGroupById(seriegroupsid);
	  seriesgrop.setImageURL(imageURL);
	  boxMetadataRepository.persisSeriesGroup(seriesgrop);	  
	  logger.info("setting the image in the series group to:"+imageURL);
	  
	  
  }
  
  
  @Transactional
  public void setEpisodeImage(Long episodeid, String imageURL){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  episode.setImageURL(imageURL);
	  boxMetadataRepository.persist(episode);	  
	  logger.info("setting the image in the episode imageURL=["+imageURL+"]");
  }
  @Transactional
  public void setEpisodeIngestSource(Long episodeid, String ingestSource){
	  Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
	  episode.setIngestSource(ingestSource);
	  boxMetadataRepository.persist(episode);	  
	  logger.info("setting the videosource in the episode ingestSource=["+ingestSource+"]");
  }
  public void notifyGeneratedImage(String imagefile){
	  try{
		  logger.info("processing  generated image notification:"+imagefile);
		  if(imagefile.length()==0){
			  logger.info("ingoring:"+imagefile);
			  return;
		  }	 
		  int ie=imagefile.indexOf(".");
		  if(ie<=0){
			  logger.info("ignoring the  file:"+imagefile);
			  return;
		  }
		  
		  String parts[]=imagefile.substring(0,ie).split("_");
		  
		  if(parts.length<2 || parts[0].length()==0){
			  logger.error("image file name not recognized:"+imagefile);
			  return;
		  }
		  String[] sizeparts=parts[parts.length-1].split("x");
		  if(sizeparts.length!=2){
			  logger.info("not recognized format for generated image:"+imagefile);
			  return;
		  }
		  int width=Integer.parseInt(sizeparts[0]);
		  int heigth=Integer.parseInt(sizeparts[1]);
		  String imageType=GenericUtilities.getImageType(width,heigth);
		  if(imageType==null){
			  logger.info("will not process this generated image:"+imagefile);
			  return;
		  }
		  List<Episode> matchedEpisodes=new ArrayList<Episode>();
		  
		  if(parts.length==2){		  			  
		      List<Series> matchedSeries=boxMetadataRepository.findSeriesByContractNumber(parts[0]);
			  if(matchedSeries.size()==0){
				      List<SeriesGroup> matchedSeriesGroup=boxMetadataRepository.findSeriesGroupByTitle(GenericUtilities.fromWebsafeTitle(parts[0]));				  					  
					  for(SeriesGroup sg:matchedSeriesGroup){
						  List<Series> foundSeries=boxMetadataRepository.findSeriesBySeriesGroup(sg);
						  if(foundSeries.size()>0){
							  for(Series sr:foundSeries){
								  matchedSeries.add(sr);
							  }
						  }						  
					  }				  				  
			  }
			  if(matchedSeries.size()>0){
				 for(Series sr:matchedSeries){					 
					 List<Episode> foundEpisode=boxMetadataRepository.findEpisodesBySeries(sr);
					 for(Episode ep:foundEpisode){
						 matchedEpisodes.add(ep);
					 }
				 }
			  }
	      }
		  else{
			  	String[] matparts=new String[parts.length-1];
			  	for(int i=0;i<(parts.length-1);i++){
				  matparts[i]=parts[i]; 
			  	}
			  	matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(GenericUtilities.partsToMatId(matparts,0));
			  	if(matchedEpisodes.size()==0){
			  		String matchTitle=GenericUtilities.fromWebsafeTitle(matparts[0]);
					String matid=GenericUtilities.partsToMatId(matparts, 1);	  
					List<Series> matchedSeries=boxMetadataRepository.findSeriesByNameAndContractNumber(matchTitle,matid);
					if(matchedSeries.size()>0){
						for(Series sr:matchedSeries){					 
							 List<Episode> foundEpisode=boxMetadataRepository.findEpisodesBySeries(sr);
							 for(Episode ep:foundEpisode){
								 matchedEpisodes.add(ep);
							 }
						 }
					}
					else{
						  matchedEpisodes=boxMetadataRepository.findEpisodesByTitleAndProgramId(matchTitle,matid);
						  if(matchedEpisodes.size()==0){
							  if(matparts.length>2){
						    	  matchedEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(matid);						    	  
						      }
						  }
					}
			  	}
				  
		  }
		  if(matchedEpisodes.size()==0){
			  logger.info("generated image not matched any episode:"+imagefile);
			  return;
		  }
		  for(Episode ep:matchedEpisodes){
			  videoService.publishImage(ep,imageType);
			  
		  }
		  
	  }
	  catch(Exception e){
		  logger.error(e+" while processing the generated image:"+imagefile,e);
	  }
  }
  
  public void notifyMasterImageUploaded(String imagefile){
	  logger.info("processing master image notification:"+imagefile);
	  if(imagefile.length()==0){
		  logger.info("ingoring:"+imagefile);
		  return;
	  }	 
	  int ie=imagefile.indexOf(".");
	  if(ie<=0){
		  logger.info("ignoring the  file:"+imagefile);
		  return;
	  }
	  
	  String parts[]=imagefile.substring(0,ie).split("_");
	  
	  if(parts.length==0 || parts[0].length()==0){
		  logger.error("the part of the image is _");
		  return;
	  }
	  
	  	        
	  if(parts.length==1){		  			  
		      List<Series> matchedSeries=boxMetadataRepository.findSeriesByContractNumber(parts[0]);
			  if(matchImageToSeries(matchedSeries,imagefile)){
				  logger.info("matched contractnumber for the series");				  
			  }	
			  List<SeriesGroup> matchedSeriesGroup=boxMetadataRepository.findSeriesGroupByTitle(GenericUtilities.fromWebsafeTitle(parts[0]));
			  if(matchImageToSeriesGroup(matchedSeriesGroup,imagefile)){
				  logger.info("matched series group with title");				
			  }	
			  return;
	  }
	  List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByMatId(GenericUtilities.partsToMatId(parts,0));
	  if(matchImageToEpisodes(matchedEpisodes, imagefile)){
		  logger.info("matched material id");
		  return;
	  }	
	  String matchTitle=GenericUtilities.fromWebsafeTitle(parts[0]);
	  String matid=GenericUtilities.partsToMatId(parts, 1);	  
      
	  List<Series> matchedSeries=boxMetadataRepository.findSeriesByNameAndContractNumber(matchTitle,matid);
	 
	   if(matchImageToSeries(matchedSeries, imagefile)){
		  logger.info("matched series by name and contract number");
		  return;	
	   }
	  
	  matchedEpisodes=boxMetadataRepository.findEpisodesByTitleAndProgramId(matchTitle,matid);
	  
      if(matchImageToEpisodes(matchedEpisodes, imagefile)){
			  logger.info("matched episodes for title and programmeid");
			  return;
	  }
      
      if(parts.length>2){
    	  matchedEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(matid);
    	  matchImageToEpisodes(matchedEpisodes, imagefile);
      }      
  }
  
  
  @Transactional
  public Episode importEpisode(Episode episode){
	  Series series=null;  
	  SeriesGroup seriesGroup=null;
	  
	  String contractNumber=null;
	  
	  String progrimeId=episode.getCtrPrg();
	  if(GenericUtilities.isNotValidCrid(progrimeId)){
		  throw new RuntimeException("Episode does not have a valid programmeId");
	  }
	  if(GenericUtilities.isNotValidTitle(episode.getTitle())){
		  throw new RuntimeException("Episode does not have a valid title");
	  }
	  List<Episode> matchedEpisodes=boxMetadataRepository.findEpisodesByCtrPrg(progrimeId);
	  if(matchedEpisodes.size()>0){
		  throw new RuntimeException("The episode with programmeId:["+progrimeId+"] already exists");
	  }
	  
	  if(episode.getSeries()!=null){
		  contractNumber=episode.getSeries().getContractNumber();
	  }
	  if(GenericUtilities.isEmpty(contractNumber)){
		  series=boxMetadataRepository.retrieveDefaultSeries();		  
	  }
	  else{
		    List<Series> matchedSeries=boxMetadataRepository.findSeriesByContractNumber(contractNumber);
		    if(matchedSeries.size()==0){
		    	series=episode.getSeries();		    	
		    	if(series.getSeriesGroup()!=null && (!GenericUtilities.isNotValidTitle(series.getSeriesGroup().getTitle()))){
		    		boxMetadataRepository.persisSeriesGroup(series.getSeriesGroup());
		    	}
		    	else{
		    		series.setSeriesGroup(boxMetadataRepository.retrieveDefaultSeriesGroup());
		    	}		    	
		    	boxMetadataRepository.persisSeries(series);
		    }
		    else{		    	
		    	series=matchedSeries.get(0);		    	
		    }
		    
	  }
	  episode.setSeries(series);
	  
	  if(!GenericUtilities.isEmpty(episode.getTags())){
		  String atgs[]=GenericUtilities.commandDelimitedToArray(episode.getTags());
		  boxMetadataRepository.saveTags(atgs);
	  }
	  episode.setIngestSource(null);
	  boxMetadataRepository.persist(episode);
	  if(episode.getCuePoints()!=null && episode.getCuePoints().size()>0){
		  for(CuePoint cuepoint:episode.getCuePoints()){
			  boxMetadataRepository.persist(cuepoint);
		  }
	  }
	  if(episode.getAvailabilities()!=null && episode.getAvailabilities().size()>0){
		  for(AvailabilityWindow availabilityWindow:episode.getAvailabilities()){
			  boxMetadataRepository.persist(availabilityWindow);
		  }
	  }
	  boxMetadataRepository.persistEpisodeStatus(episode.getEpisodeStatus());
	  
		checkS3ToUpdateVidoStatus(episode);
		if(GenericUtilities.isNotValidCrid(episode.getImageURL())){
			String fname=retrieveEpisodeImageFromS3(episode);
			if(fname!=null){
				episode.setImageURL(fname);
				boxMetadataRepository.persist(episode);
			}
		}	

		
	  
	  return episode;
  }
  public List<Episode> findEpisodesByCtrPrg(String programmeId){	  
	  return boxMetadataRepository.findEpisodesByCtrPrg(programmeId);
  }
  public void persistConvertImaggeMediaCommand(String masterFilename){	  
	  MediaCommand mediaCommand=new MediaCommand();
	  mediaCommand.setCommand(MediaCommand.CONVERT_FROM_MASTER_IMAGE);
	  mediaCommand.setFilename(masterFilename);
	  executeMediaCommandForLater(mediaCommand);
	  
	  
  }
 
  
  public void persistTranscodeCommand(String sourceVideoFileName){
	  MediaCommand mediaCommand=new MediaCommand();
	  mediaCommand.setCommand(MediaCommand.TRANSCODE_VIDEO_FILE);
	  mediaCommand.setFilename(sourceVideoFileName);
	  executeMediaCommandForLater(mediaCommand);
	  
	  
	  
  }
  public void persistIngestIntoBcCommand(Long episodeid){	  
		  MediaCommand mediaCommand=new MediaCommand();
		  mediaCommand.setCommand(MediaCommand.INGEST_VIDEO_INTO_BC);
		  mediaCommand.setEpisodeid(episodeid);		
		  executeMediaCommandForLater(mediaCommand);
		  	  
  }
  public void persistPublishChangesNeedsPublish(Long episodeid){
	  MediaCommand mediaCommand=new MediaCommand();
	  mediaCommand.setCommand(MediaCommand.PUSH_CHANGES_ON_NEEDS_TO_PUBLISH);
	  mediaCommand.setEpisodeid(episodeid);
	  executeMediaCommandForLater(mediaCommand);
	  
	  	  
  }
  
  public List<BCPlayList> findBCPlayListItems(SearchParam searchParam){
		List<BCPlayList> playlists=new ArrayList<BCPlayList>();
		BCPlayListData[] bcplaylistdata=videoService.listPlayListData(searchParam.getSearch(), searchParam.getSortBy(), searchParam.getStart(), searchParam.getLimit());
		if(bcplaylistdata==null||bcplaylistdata.length==0){
			return playlists;
		}
		for(BCPlayListData ps:bcplaylistdata){
			BCPlayList psitem=new BCPlayList();
			psitem.setPlayListData(ps);
			playlists.add(psitem);
		}
		return playlists;
	}
  public List<BCPlayListItem> getVideoDataInPlaylist(String playlistid,SearchParam searchParam){
	  List<BCPlayListItem> playlistitems=new ArrayList<BCPlayListItem>();
	  BCVideoData[] bcData=videoService.getVideoDataInPlayList(playlistid, searchParam.getSearch(), searchParam.getStart(),searchParam.getLimit());
	  if(bcData==null||bcData.length==0){
			return playlistitems;
	  }
	  for(BCVideoData bcd:bcData){
		  BCPlayListItem bcpi=new BCPlayListItem();
		  bcpi.setBcvideoData(bcd);
		  playlistitems.add(bcpi);
	  }
	  return playlistitems;
	  
  }
  public BCPlayList patchPlayList(String playlistid,BCPlayList playlist){
	  BCPlayListData plistdata= videoService.patchPlaylist(playlistid,playlist.getPlayListData());
	  BCPlayList psitem=new BCPlayList();
	  psitem.setPlayListData(plistdata);
	  return psitem;	  
  }
  public BCPlayList deletePlayList(String playlistid){
	  BCPlayListData plistdata=videoService.getAPlayListData(playlistid);
	  com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
	  String playListInJson;
		try {
			playListInJson = objectMapper.writeValueAsString(plistdata);
			logger.info("Following play list is going to be deleted:"+playListInJson);		
		} catch (JsonProcessingException e1) {
			logger.error("error while parsing the patched text to playlist data playlistdata="+plistdata,e1);						
		}		
	   logger.info("going to delete the playlist:"+playlistid);			  
	   videoService.deletePlaylist(playlistid);
	  BCPlayList psitem=new BCPlayList();
	  psitem.setPlayListData(plistdata);
	  return psitem;
  }
  
  
  public BCPlayList createPlayList(BCPlayList playlist){
	  BCPlayListData plistdata= videoService.createPlaylist(playlist.getPlayListData());
	  BCPlayList psitem=new BCPlayList();
	  psitem.setPlayListData(plistdata);
	  return psitem;	  
  }
  
  
  public BCPlayList getAPlaylist(String playlistid){
	  BCPlayListData plistdata=videoService.getAPlayListData(playlistid);	  
	  BCPlayList psitem=new BCPlayList();
	  psitem.setPlayListData(plistdata);
	  return psitem;
  }

  
  public void executeMediaCommandForLater(MediaCommand mediaCommand){
	  boxMetadataRepository.persistMediaCommand(mediaCommand);
	  logger.info("peristed the media Command:"+mediaCommand);	  
	  executeMediaCommands();
  }
  class MediaExecutionThreads   implements Callable<MediaCommand>{

		@Override
		public MediaCommand call() throws Exception {
			MediaCommand result=new MediaCommand();
			processOtherMediaCommands();
			return result;
		}
  	
  }

  
  public void executeMediaCommands(){    	
  	pool.submit(new MediaExecutionThreads());
  }
  private static MediaCommand mediaCommandProcessing=null; 
  private static Object lockMediaCommandProcess=new Object();   
   public void processOtherMediaCommands(){
   	try{
		    	synchronized(lockMediaCommandProcess){
		    		if(mediaCommandProcessing!=null){
		    			logger.info("Still processing the previous media Commands:"+mediaCommandProcessing);
		    			return;    			    			
		    		}
		    		else{
		    			mediaCommandProcessing=new MediaCommand();
		    			mediaCommandProcessing.setCommand(MediaCommand.DUMMY_COMMAND);
		    		}
		    	}
		    	for(int i=0;i<100;i++){
					    	mediaCommandProcessing=getMediaCommandsForProcess();
					    	if(mediaCommandProcessing==null){
					    		logger.info("no media command to process....");
					    		return;
					    	}
					    	else{
					    		logger.info("processing the media command:"+mediaCommandProcessing);
					    		 processCommand(mediaCommandProcessing);
					    	}
		    	}
   	}
   	catch(Exception e){
   		 logger.error(e+" while processing the media command:"+mediaCommandProcessing,e);
   		 
   	}
   	finally{
   		synchronized(lockMediaCommandProcess){
   			logger.info("completed the current command");
   			mediaCommandProcessing=null;
   		}
   	}
   }
   
   
   public MediaCommand getMediaCommandsForProcess(){    	
   	List<MediaCommand> mediaCommands=boxMetadataRepository.findMediaCommandsNotEitherOfThem(MediaCommand.DELIVER_SOUND_MOUSE_HEADER_FILE,MediaCommand.DELIVER_SOUND_MOUSE_SMURF_FILE);    	
   	if(mediaCommands.size()==0){
   			return null;
   	}
   	MediaCommand mediaCommand=mediaCommands.get(0);
   	boxMetadataRepository.removeMediaCommandById(mediaCommand.getId());
   	return mediaCommand;   	
   }

   public  Object processCommand(MediaCommand mediaCommand){
   	if(MediaCommand.PUBLISH_ALL_CHANGES.equals(mediaCommand.getCommand())){    		
   		pushAllChangesToBrightcove();    		
   	}
   	else if(MediaCommand.IMPORT_BRIGHCOVE_IMAGE.equals(mediaCommand.getCommand())){
   		importImageFromBrightcove(mediaCommand.getEpisodeid(),mediaCommand.getFilename());
   	}
   	else if(MediaCommand.CHECK_TRANSCODE_IN_PRGRESS.equals(mediaCommand.getCommand())){
   		return checkEpisoderanscodeStatus(mediaCommand.getEpisodeid());    		
   	}
   	else if(MediaCommand.PUSH_CHANGES_ON_NEEDS_TO_PUBLISH.equals(mediaCommand.getCommand())){
   		 publishChangesWhenNeedsPublish(mediaCommand);
   		 return mediaCommand;
   	}    	
   	else if(MediaCommand.IMPORT_BRIGHTCOVE_EPISODE.equals(mediaCommand.getCommand())){
   		    		
   		return importEpisodeFromBrightcove(mediaCommand,null); 
   	}
   	else if(MediaCommand.CONVERT_FROM_MASTER_IMAGE.equals(mediaCommand.getCommand())){    		    			
   		commandService.convertFromMasterImage(mediaCommand.getFilename());
   		return mediaCommand;
   	}    
   	else if(MediaCommand.TRANSCODE_VIDEO_FILE.equals(mediaCommand.getCommand())){    		    			
   		commandService.transcodeViodeFile(mediaCommand.getFilename());
   		return mediaCommand;
   	}
   	else if(MediaCommand.INGEST_VIDEO_INTO_BC.equals(mediaCommand.getCommand())){    		    			
   		logger.info("processing the auto ingest bc command"+mediaCommand);
   		FileIngestRequest fileIngestRequest=new FileIngestRequest();
   		fileIngestRequest.setEpisodeid(mediaCommand.getEpisodeid());    		
   		videoService.ingestVideoToBrightCove(fileIngestRequest);
   		return mediaCommand;
   	}
   	else if(MediaCommand.CAPTURE_IMAGES_FROM_VIDEO.equals(mediaCommand.getCommand())){
   		logger.info("processing the capture image command"+mediaCommand);
   		captureImageFromVideo(mediaCommand.getEpisodeid(), mediaCommand.getSecondsAt());
   	}
   	else if(MediaCommand.INSPECT_VIDEO_FILE.equals(mediaCommand.getCommand())){
   		logger.info("processing the list 1080 assets command"+mediaCommand);
   		try{
   				inspectVideoFile();
   		}
   		catch(Exception e){
   			logger.error(e+" while processing the inspect video command",e);
   		}
   		
   	}
   	else{
   		logger.info("ignore the command");
   	}
   	return mediaCommand;
    }
   public void pushAllChangesToBrightcove(){
   	
	   logger.info("pushing all changes to brightcove......");
	   		
	   		int rcordLimit=appConfig.getRecordLimit();
	   		if(rcordLimit<1){
	   			rcordLimit=Integer.MAX_VALUE;
	   		}
	   		SearchParam searchParam=new SearchParam(null, 0, rcordLimit);
	   		
	   		while(true){
	   				List<Episode> episodes=boxMetadataRepository.findAllEpisodes(searchParam);
	   				if(episodes.size()==0){
	   					break;
	   				}
	   				for(Episode episode:episodes){
	   					pushChangesToBrightcove(episode);												  
	   				}
	   				if(searchParam.isEnd(episodes.size())){
	   					break;
	   				}
	   				else{
	   					searchParam.nextBatch();
	   				}
	   		}
	       }
   
   void pushChangesToBrightcove(Episode episode){
   	if(episode.getEpisodeStatus().getMetadataStatus()==MetadataStatus.NEEDS_TO_PUBLISH_CHANGES){
   		publishMetadatatoBCByEpisodeId(episode.getId());
   	}
   	
   }
   
   public void importImageFromBrightcove(Long eposodeid, String mediaFileName){
   	Episode episode=boxMetadataRepository.findEpisodeById(eposodeid);
   	if(episode==null){
   		logger.error("Episode not found");
   		throw new RuntimeException("Episode not found");
   	}
   	if(episode.getBrightcoveId()==null){
   		logger.error("Episode does not have brightcoveid:");
   		
   		throw new RuntimeException("BrightcoveId is not in the episode");
   	}

   	BCVideoData video=videoService.getVideo(episode.getBrightcoveId());
   	if(video==null){
   		logger.error("failed to get bc video");
   		throw new RuntimeException("failed to get the media item from bc");
   	}
   	if(video.getImages()==null || video.getImages().getPoster()==null){
   		logger.error("Poster image not found in brightcove:"+episode.getBrightcoveId()+":eposodeid=["+eposodeid+"]");
   		throw new RuntimeException("Thre is no poster image in bc to import");
   	}
   	if(video.getImages().getPoster().getSrc()==null){
   		logger.error("Src is empty in poster");
   		throw new RuntimeException("src is empty in the poster in brightcove");
   	}
   	String filepath="/data/"+mediaFileName;
   	
   	try{		    	
		    	URL url=new URL(video.getImages().getPoster().getSrc());
		    	logger.info("downloading the bc image: "+url+"  to:"+mediaFileName);
		    	ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		    	FileOutputStream fos = new FileOutputStream(filepath);
		    	fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		    	fos.close();		    	
		    	s3BucketService.uploadMasterImageFile(filepath, mediaFileName);
   	}
   	catch(Exception e){
   		logger.error(e+ "while downloading the image from the brightcove:"+video.getImages().getPoster().getSrc()+":"+filepath,e);
   		 throw new RuntimeException(e+" while downloading image from brightcove");
   	}
   	finally{
   		File file=new File(filepath);
			file.delete();
   	}

   	
   }
   public EpisodeStatus checkEpisoderanscodeStatus(Long episodeid){    	
   	logger.info("checing the transcoding status for:"+episodeid);
   	Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
   	
   	if(episode==null){
   		throw new RuntimeException("epsode not found");    		
   	}
   	EpisodeStatus status=episode.getEpisodeStatus();
   	if(status==null){
   		throw new RuntimeException("epsode status is null");
   	}
   	
   	
        if(status.getVideoStatus()!=VideoStatus.TRANSCODING){
       	 logger.info("Video status is not TRANSCODING");
       	 return status;        	 
        }         
   	 BCVideoSource[] videos=videoService.getVideoSource(episode.getBrightcoveId());    			
   	  if(videos!=null && videos.length>=2){    		  
   		boxMetadataRepository.markTranscodeAsCompleteByEpisodeId(episode.getId());
   	  }    	
   	  return status;
   }
   
   public boolean waitForImageAvailability(MediaCommand mediaCommand, String imageURL){	   
  		logger.info("checking the vailability of the image url:"+imageURL);
  		int code=0;
  		for(int i=0;i<10;i++){   			
  			code=GenericUtilities.getURLHttpCode(imageURL);  			
  			if(code==403){
  				if(i<9){  					
		  				logger.info(" waiting....status:"+code+" for availability ("+i+"):"+imageURL);
		  				try {
		  					Thread.sleep(1000);
		  				} catch (InterruptedException e) {
		  					logger.error(e+" while waiting for the availability of :"+imageURL,e);
		  				}		  				
  				}  				
  			}
  			else if(code<200 || code>=300){
  	  			logger.error(code+": the Image URL having a problem, so will not automate publish the changed:"+imageURL);
  	  			return false;
  	  		}   			
  			else{
  				logger.info(" available:"+code+":"+imageURL);
  				return true;
  			}
  		}   		
  		if(code==403){
  			logger.info("Image is not created yet:"+imageURL+"saving back the mediaCommand:"+mediaCommand);
  			mediaCommand.setId(null);
  			executeMediaCommandForLater(mediaCommand);
  			return false;
  		}
  		else{
  			logger.info(" will not auto publish:"+imageURL);
  			return false;  			
  		}
   }
   
   
   public void publishChangesWhenNeedsPublish(MediaCommand mediaCommand){
	   logger.info(" executing publishedChangesWhenNeedsToPiublishImage for episodeid:"+mediaCommand.getEpisodeid());
	   	Episode episode=boxMetadataRepository.findEpisodeById(mediaCommand.getEpisodeid());
	   	if(episode==null){
	   		logger.info("episode not foind when publishedChangesWhenNeedsToPiublishImage episodeid="+mediaCommand.getEpisodeid());
	   		return;
	   	}
	   	if(episode.getEpisodeStatus()==null || episode.getEpisodeStatus().getMetadataStatus()!=MetadataStatus.NEEDS_TO_PUBLISH_CHANGES){    		
	      	 logger.info("episode status is not in NEEDS_TO_PUBLISH_CHANGES");
	      	 return;        	 
	       }
	   	if(episode.getBrightcoveId()==null){
	   		logger.info("episode brightcove is null");
	   		return;
	   	}
	   	publishMetadatatoBCByEpisodeId(mediaCommand.getEpisodeid());
	   	
			
   }

   
   public Episode importEpisodeFromBrightcove(MediaCommand mediaCommand, StringBuilder errorBuilder){
      	String brigthcoveid=mediaCommand.getBrightcoveId();
      	if(brigthcoveid==null){    		
      		logger.error("brightcoveid is not provided");
      		if(errorBuilder!=null){
      			errorBuilder.append("brightcoveid is not provided");
      		}
      		return null;
      	}
      	brigthcoveid=brigthcoveid.trim();
      	String episodenumber=mediaCommand.getEpisodeNumber();
      	if(episodenumber!=null)
      		episodenumber=episodenumber.trim();
      	if(brigthcoveid.length()==0){    		
      		logger.error("brightcoveid is not provided");
      		if(errorBuilder!=null){
      			errorBuilder.append("brightcoveid is not provided");
      		}
      		return null;
      	}
      	String contractNumber=mediaCommand.getContractNumber();
      	if(contractNumber!=null){
      		contractNumber=contractNumber.trim();
      	}
      	Episode episode=bcVideoToEpisode(brigthcoveid,errorBuilder);
      	String bcImageURL=episode.getImageURL();
      	episode.setImageURL(null);
      	if(!GenericUtilities.isEmpty(contractNumber)){
      		if(episode.getSeries()==null){
      			episode.setSeries(new Series());
      		}
      		episode.getSeries().setContractNumber(contractNumber);
          	if(!GenericUtilities.isEmpty(episodenumber)){
          		episode.setCtrPrg(GenericUtilities.composeCtrPrg(contractNumber, episodenumber));
          		
          		
          	}
          	else{
          		episode.setCtrPrg(GenericUtilities.composeCtrPrg(contractNumber, "1"));        		        		
          	}
          	episode.setMaterialId(episode.getCtrPrg()+"/001");
      	}
      	else{
      		throw new RuntimeException("The contract Number is required");
      	}
      	
      	List<Episode> matchedEpisodes=findEpisodesByCtrPrg(episode.getCtrPrg());
      	if(matchedEpisodes.size()>0){
      		errorBuilder.append("failed to import:"+brigthcoveid+" because the programmeid already exist:"+episode.getCtrPrg());
      		return matchedEpisodes.get(0);
      	}
      	
      	
      	String videoSourceFileName=getVideoFileName(episode.getBrightcoveId());
      	if(videoSourceFileName==null){
      		logger.warn(episode.getBrightcoveId()+": video file does not exist\n");
      		if(errorBuilder!=null){
          		errorBuilder.append(episode.getBrightcoveId()+": video file does not exist\n");
          	}
      	}
      	else{    		
      		String destinationFilename=GenericUtilities.materialIdToVideoFileName(episode.getMaterialId());
      		int ib=videoSourceFileName.indexOf(".");
      		String ext=videoSourceFileName.substring(ib);
      		destinationFilename+=ext;
      		s3BucketService.moveFile(appConfig.getVideoBucket(), videoSourceFileName, appConfig.getVideoBucket(), destinationFilename);    		
      		episode.setIngestSource(s3BucketService.getFullVideoURL(destinationFilename));    				
      		Double durationUploaded=checkVideoDuration(episode);
      		if(durationUploaded!=null){
      						   episode.setDurationUploaded(durationUploaded);
      		}					       					
      	}
      	Episode episodeInDB=importEpisode(episode);
      	
      	if(bcImageURL!=null && GenericUtilities.isEmpty(episodeInDB.calculateImageURL()) ){
      		if(!appConfig.checkIsURLPartOfImageUrlAliases(bcImageURL)){
      			
   	    		String imagefileName=GenericUtilities.materialIdToImageFileName(episode.getMaterialId());
   	    		String ext=GenericUtilities.getFileExtFromURL(bcImageURL);	    		
   	    		if(ext!=null){
   	    			imagefileName=imagefileName+"."+ext;
   	    			importImageFromBrightcove(episode.getId(),imagefileName);
   	    		}
   	    		else{
   	    			  logger.error("extention can not be retrieved from"+bcImageURL);
   	    		}
   	    		
      		}
      		else{
      			logger.warn("Will not download the bc image from:"+bcImageURL);
      		}
   	    	
      	}
      	
      	return episodeInDB;
      }
   
   public Episode bcVideoToEpisode(String brightcoveid, StringBuilder errorBuilder){
       if(GenericUtilities.isEmpty(brightcoveid)){
       	logger.error("brightcoveid is null, will not import");
       	if(errorBuilder!=null){
       		errorBuilder.append("brightcoveid is null, will not import\n");
       	}
       	return null;
       }
   	BCVideoData video=videoService.getVideo(brightcoveid);
   	Episode episode=new Episode();
   	video.export(episode);
   	if(episode.getEpisodeStatus()==null){
			episode.setEpisodeStatus(new EpisodeStatus());
		}
   	importTranscodeStatusFromBrightcove(brightcoveid,episode.getEpisodeStatus());
   	if(video.getImages()!=null && video.getImages().getPoster()!=null && video.getImages().getPoster().getSrc()!=null){
   		episode.setImageURL(video.getImages().getPoster().getSrc());
   	}
   	return episode;
   }
   
   public void importTranscodeStatusFromBrightcove(String brightcoveid,EpisodeStatus episodeStatus){
   	BCVideoSource[] videos=videoService.getVideoSource(brightcoveid);		
		if(videos!=null && videos.length>=2){
			episodeStatus.setVideoStatus(VideoStatus.TRANSCODE_COMPLETE);
			episodeStatus.setNumberOfTranscodedFiles(videos.length);
		}
		else{
			episodeStatus.setVideoStatus(VideoStatus.NEEDS_TRANSCODE);
			if(videos!=null){
				episodeStatus.setNumberOfTranscodedFiles(videos.length);
			}
		}
   }
   
   private String getVideoFileName(String brightCoveId){
   	List<FileItem> items=s3BucketService.listFiles(appConfig.getVideoBucket(), bcConfiguration.getAccountId()+"/"+brightCoveId,0,-1,null);
   	if(items.size()==0){
   		return null;
   	}
   	if(items.size()==1){
   		return items.get(0).getFile();
   	}
   	for(FileItem itm:items){
   		int ib=itm.getFile().indexOf(".");
   		if(ib==-1){
   			return itm.getFile();
   		}
   	}
   	return items.get(0).getFile();
   }
   
   public void inspectVideoFile() throws FileNotFoundException{
	    
	   
	    SearchParam searchParam=new SearchParam(null,0,null);
		List<Episode> episodes=boxMetadataRepository.findAllEpisodes(searchParam);
		if(episodes.size()==0){
			logger.info("no episode found");
			return;
		}
		File outputfile=new File("/data/videoFiles.csv");
		PrintWriter writer=new PrintWriter(outputfile);
		writer.print("Title,Programme Number,Width,Height,Codec Width,Codec Height,Aspect Ratio,Duration\n");
		
		for(Episode episode:episodes){
			writer.print(episode.getTitle()+", "+episode.getCtrPrg()+", ");
			if(episode.getIngestSource()==null){
				writer.print("\n");
				continue;
			}
			if(GenericUtilities.isNotValidCrid(episode.getIngestSource())){
				writer.print("\n");
				continue;
		    }			
			try{
				String url=s3BucketService.generatedPresignedURL(episode.getIngestSource(), 60);
				String result=commandService.inspectVideoFile(url);
				com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
				Map map= objectMapper.readValue(result, Map.class);
				List<Map> streams=(List<Map>)map.get("streams");
				
				for(Map stream:streams){
					String codecType=(String)stream.get("codec_type");
					if("video".equals(codecType)){
						writer.print(stream.get("width")+",");
						writer.print(stream.get("height")+",");
						writer.print(stream.get("coded_width")+",");
						writer.print(stream.get("coded_height")+",");
						writer.print(stream.get("display_aspect_ratio")+",");
						writer.print(stream.get("duration")+"\n");
						break;
					}
					
				}
				
				
				
			}		
			catch(Exception e){
				logger.error(e+" while processing episode:"+episode.getIngestSource(), e);
			}	
			writer.print("\n");
			
		}
		writer.close();
   	
   	
   
 }
   public void captureImageFromVideo(Long episodeid,Double secondsAt){
		Episode episode=boxMetadataRepository.findEpisodeById(episodeid);
   	if(episode==null){
   		logger.error("Episode not found");
   		throw new RuntimeException("Episode not found");
   	}
   	if(episode.getIngestSource()==null){
   		logger.error("Episode does not have ingestSource");    		
   		throw new RuntimeException("ingestSource is not in the episode");
   	}
   	if(GenericUtilities.isNotValidCrid(episode.getIngestSource())){
   		logger.error("Episode does not have ingestSource");    		
   		throw new RuntimeException("ingestSource is not in the episode");
		 }
   	String url=s3BucketService.generatedPresignedURL(episode.getIngestSource(), 900);
   	
   	String imagefileName=GenericUtilities.materialIdToImageFileName(episode.getMaterialId());
		String ext="png";	    		
		String mediaFileName=imagefileName+"."+ext;			
   	String filepath="/data/"+mediaFileName;
   	
   	try{		    	
   		commandService.captureImageFromVideo(url, secondsAt, filepath);
   		s3BucketService.uploadMasterImageFile(filepath, mediaFileName);
   	}
   	catch(Exception e){
   		logger.error(e+ "while capturing the image from the video:"+url+":"+filepath,e);
   		 throw new RuntimeException(e+" while downloading image from brightcove");
   	}
   	finally{
   		File file=new File(filepath);
			file.delete();
   	}
	}
   
}
