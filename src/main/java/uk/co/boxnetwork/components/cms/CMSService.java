package uk.co.boxnetwork.components.cms;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.components.BCVideoService;
import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.cms.CMSMenuData;
import uk.co.boxnetwork.data.cms.CMSPlaylistData;
import uk.co.boxnetwork.model.AppConfig;
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
	BCVideoService bcVideoService;
	
	@Autowired
	AppConfig appConfig;
	
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
	public CMSMenuData getCMSMenuById(Long id){
	
		CMSMenu cmsMenu=cmsRepository.findCMSMenuById(id);
		CMSMenuData cmsMenuData=new CMSMenuData();
		cmsMenuData.importData(cmsMenu);
		return cmsMenuData;
		
	}
	
	public void updateCMSMenu(CMSMenuData cmsmenu,MediaApplicationID applicationId){
		
	}
    
	public CMSMenuData removeCMSMenuById(Long id, MediaApplicationID applicationId){
		return null;
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
	
}
