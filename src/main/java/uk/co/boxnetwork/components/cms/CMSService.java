package uk.co.boxnetwork.components.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.boxdata.BoxDataRepository;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.cms.BoxChannelData;
import uk.co.boxnetwork.data.cms.CMSMenuData;
import uk.co.boxnetwork.data.cms.CMSPlaylistData;
import uk.co.boxnetwork.data.image.BoxScheduleEventData;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.BoxChannel;
import uk.co.boxnetwork.model.BoxScheduleEvent;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MediaApplicationID;
import uk.co.boxnetwork.model.cms.CMSEpisode;
import uk.co.boxnetwork.model.cms.CMSMenu;
import uk.co.boxnetwork.model.cms.CMSPlaylist;

@Service
public class CMSService {
	private static final Logger logger=LoggerFactory.getLogger(CMSService.class);
	
	@Autowired
	CMSRepository cmsRepository;
	
	@Autowired
	BoxDataRepository  boxdataRepository;
	
	@Autowired 
	BCVideoService bcVideoService;
	
	@Autowired
	AppConfig appConfig;
	
	@Autowired
	BoxMedataRepository boxMetadataRepository;
	
	
	@Transactional
	public List<CMSMenuData> findAllCMSMenu(MediaApplicationID applicationId){				
			List<CMSMenu> cmsmenus=cmsRepository.findAllCMSMenu(applicationId);
			List<CMSMenuData> ret=new ArrayList<CMSMenuData>();
			for(CMSMenu cmsmenu:cmsmenus){
				CMSMenuData cmsMenuData=new CMSMenuData();
				cmsMenuData.importData(cmsmenu);
				ret.add(cmsMenuData);
			}
			return ret;		
	}
	@Transactional
	public CMSMenuData getCMSMenuById(Long id){
	
		CMSMenu cmsMenu=cmsRepository.findCMSMenuById(id);
		CMSMenuData cmsMenuData=new CMSMenuData();
		cmsMenuData.importData(cmsMenu);
		return cmsMenuData;
		
	}
	
	public void updateCMSMenu(CMSMenuData cmsmenuData,MediaApplicationID applicationId){
		  if(!isValid(cmsmenuData)){
			  logger.error("Failed to create:Invalid cmsmenuData:"+cmsmenuData);
			  return;
		  }
		  CMSMenu cmsMenu=new CMSMenu();
		  cmsmenuData.exportAttributes(cmsMenu);
		  cmsMenu.setApplicationId(applicationId);
		  List<CMSPlaylist> playlist=new ArrayList<CMSPlaylist>();
		  for(CMSPlaylistData pllistdata:cmsmenuData.getPlaylist()){
			  		if(!isValid(pllistdata)){
			  			logger.error("Failed to create:Invalid pllistdata:"+pllistdata);
			  			return;
			  		}
			  		CMSPlaylist cmsPlaylist=new CMSPlaylist();
			  		pllistdata.exportAttributes(cmsPlaylist);
			  		List<CMSEpisode> episodes=loadCMSEpisodesFromPlayList(pllistdata.getId());
			  		episodes=cmsRepository.updateEpisodes(episodes);			  		
			  		cmsPlaylist.setEpisodes(episodes);
			  		cmsPlaylist=cmsRepository.updatePlayList(cmsPlaylist);
			  		playlist.add(cmsPlaylist);
		  }
		  cmsMenu.setPlaylist(playlist);
		  cmsRepository.updateCMSMenu(cmsMenu);		
	}
	@Transactional
	public CMSMenuData removeCMSMenuById(Long id, MediaApplicationID applicationId){
		CMSMenuData cmsMenuData=new CMSMenuData();
		cmsMenuData.setId(id);
		  CMSMenu cmsMenu=cmsRepository.removeCMSMenuById(id);
		  if(cmsMenu!=null){
			  logger.info("CMS Menu is deleted:"+cmsMenu);
			  cmsMenuData.importData(cmsMenu);
		  }
		  return cmsMenuData;
	}
	
	private boolean isValid(CMSMenuData cmsmenuData){
		  if(cmsmenuData.getTitle()==null){
			  return false;
		  }
		  if(cmsmenuData.getTitle().trim().length()==0){
			  return false;
		  }
		  if(cmsmenuData.getPlaylist()==null){
			  return false;
		  }
		  if(cmsmenuData.getPlaylist().size()==0){
			  return false;
		  }
		  
		  return true;
	  }
	  private boolean isValid(CMSPlaylistData pldata){
		  if(pldata.getId()==null){
			  return false;
		  }
		  if(pldata.getId().trim().length()==0){
			  return false;
		  }
		  if(pldata.getTitle()==null){
			  return false;
		  }
		  return true;
	  }
	  public void createCMSMenu(CMSMenuData cmsmenuData, MediaApplicationID applicationId){		  
		  if(!isValid(cmsmenuData)){
			  logger.error("Failed to create:Invalid cmsmenuData:"+cmsmenuData);
			  return;
		  }
		  CMSMenu cmsMenu=new CMSMenu();
		  cmsmenuData.exportAttributes(cmsMenu);
		  cmsMenu.setApplicationId(applicationId);
		  List<CMSPlaylist> playlist=new ArrayList<CMSPlaylist>();
		  for(CMSPlaylistData pllistdata:cmsmenuData.getPlaylist()){
			  		if(!isValid(pllistdata)){
			  			logger.error("Failed to create:Invalid pllistdata:"+pllistdata);
			  			return;
			  		}
			  		CMSPlaylist cmsPlaylist=new CMSPlaylist();
			  		pllistdata.exportAttributes(cmsPlaylist);
			  		List<CMSEpisode> episodes=loadCMSEpisodesFromPlayList(pllistdata.getId());
			  		episodes=cmsRepository.updateEpisodes(episodes);			  		
			  		cmsPlaylist.setEpisodes(episodes);
			  		cmsPlaylist=cmsRepository.updatePlayList(cmsPlaylist);
			  		playlist.add(cmsPlaylist);
		  }
		  cmsMenu.setPlaylist(playlist);
		  cmsRepository.updateCMSMenu(cmsMenu);		  
	  }
	  private List<CMSEpisode> loadCMSEpisodesFromPlayList(String id){
			BCVideoData[] bcData=bcVideoService.getVideoDataInPlayList(id, null, null,null);
			if(bcData==null || bcData.length==0){
				logger.error("No episodes in the playlist:"+id);
				return null;
			}
			List<CMSEpisode> episodes=new ArrayList<CMSEpisode>();
			for(BCVideoData bcVideo:bcData){
				CMSEpisode cmsVideo=new CMSEpisode();
				cmsVideo.importFrom(bcVideo);
				episodes.add(cmsVideo);
			}
			return episodes;
	  }
	  
	  public BoxChannelData findBoxChannelById(String channelid,Long scheduleTimestampFrom, Long scheduleTimestampTo){
		  BoxChannel channel=boxdataRepository.findBoxChannelById(channelid);
		  BoxChannelData channelData=new BoxChannelData();
		  channelData.importFrom(channel);
		  retrieveSchedule(channel,channelData,scheduleTimestampFrom,scheduleTimestampTo);
		  
		  return channelData;
	  }
	  void retrieveSchedule(BoxChannel channel,BoxChannelData channelData,Long scheduleTimestampFrom,Long scheduleTimestampTo){
		  if(scheduleTimestampFrom==null || scheduleTimestampTo==null){
			  logger.info("scheduleTimestampFrom/scheduleTimestampTo is empty");
			  return;
		  }
		  
		  List<BoxScheduleEvent> schdules=boxdataRepository.findBoxScheduleEvent(channel, new Date(scheduleTimestampFrom*1000), new Date(scheduleTimestampTo*1000));
		  if(schdules.size()==0){
			  logger.info("No schedules for the channel");
			  return;
		  }
		  List<BoxScheduleEventData> scheduleData=new ArrayList<BoxScheduleEventData>();
		  for(BoxScheduleEvent evt:schdules){
			  BoxScheduleEventData scheduledata=new BoxScheduleEventData(evt);
			  scheduleData.add(scheduledata);
		  }
		  channelData.setSchedule(scheduleData);		  
		  
	  }
	  public List<BoxChannelData> findAllBoxChannel(Long scheduleTimestampFrom, Long scheduleTimestampTo){
		  List<BoxChannel> channels=boxdataRepository.findAllBoxChannel();
		  List<BoxChannelData> channeldata=new ArrayList<BoxChannelData>();
		  for(BoxChannel ch:channels){
			  BoxChannelData cdata=new BoxChannelData();
			  cdata.importFrom(ch);
			  retrieveSchedule(ch,cdata,scheduleTimestampFrom,scheduleTimestampTo);
			  
			  channeldata.add(cdata);
		  }
		  return channeldata;
		  
	  }
	  public void updateChannel(BoxChannelData channelData){
		  BoxChannel boxChannel=new BoxChannel();
		  channelData.exportTo(boxChannel);
		  boxdataRepository.updateOrCreate(boxChannel);		  
	  }
	  public BoxChannelData deleteChannelById(String channelId){		  
		  BoxChannelData channelData=new BoxChannelData();
		  channelData.setChannelId(channelId);
		  BoxChannel boxChannel=boxdataRepository.removeChannelById(channelId);
		  if(boxChannel!=null){
			  logger.info("Box Channel is deleted:"+boxChannel);
			  channelData.importFrom(boxChannel);
		  }
		  return channelData;
	  }
	  @Transactional
	  public List<CMSPlaylistData> findCMSPlaylistByEpisodeId(Long id){
		  List<CMSPlaylistData> playlistdata= new ArrayList<CMSPlaylistData>();
		  Episode episode=boxMetadataRepository.findEpisodeById(id);
		  if(episode==null){
			  logger.error("not found episode:"+id);
			  return playlistdata;			  
		  }
		  if(episode.getBrightcoveId()==null||episode.getBrightcoveId().trim().length()==0){
			  logger.error("episod does not have provide id:"+id);
			  return playlistdata; 
		  }
		  List<CMSPlaylist> playlists=cmsRepository.findCMSPlaylistByCMSEpisodeId(episode.getBrightcoveId());
		  for(CMSPlaylist pl:playlists){
			  CMSPlaylistData pldata=new CMSPlaylistData();
			  pldata.importData(pl);
			  playlistdata.add(pldata);
		  }
		  return playlistdata;
	  }

}
