package uk.co.boxnetwork.components.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.cms.CMSMenuData;
import uk.co.boxnetwork.data.cms.CMSPlaylistData;
import uk.co.boxnetwork.model.MediaApplicationID;
import uk.co.boxnetwork.model.cms.CMSEpisode;
import uk.co.boxnetwork.model.cms.CMSMenu;
import uk.co.boxnetwork.model.cms.CMSPlaylist;

@Repository
public class CMSRepository {
	private static final Logger logger=LoggerFactory.getLogger(CMSRepository.class);
	
	
	  @Autowired	
	  private EntityManager entityManager;
    
	  public List<CMSMenu> findAllCMSMenu(MediaApplicationID applicationId){
		   TypedQuery<CMSMenu> query=entityManager.createQuery("SELECT c FROM cms_menu c where c.applicationId=:applicationId", CMSMenu.class);
		   return query.setParameter("applicationId",applicationId).getResultList();
	  }
	  @Transactional
      public CMSMenu updateCMSMenu(CMSMenu cmsmenu){
		  cmsmenu.setLastModifiedAt(new Date());
		  if(cmsmenu.getId()!=null){			  			  		 
			  return  entityManager.merge(cmsmenu);
		  }
		  else{			  
			  entityManager.persist(cmsmenu);
			  return cmsmenu;
		  }   	  		  
      }
	  
	  public CMSMenu findCMSMenuById(Long id){
		  return entityManager.find(CMSMenu.class, id);
	  }
	  @Transactional
	  public CMSMenu removeCMSMenuById(Long id,MediaApplicationID applicationId){
		  CMSMenu menuInDb=entityManager.find(CMSMenu.class, id);
		  if(menuInDb==null){
			  logger.error("Unable to delete, because not such cmsmenu with id:"+id);
			  return null;
		  }
		  if(menuInDb.getApplicationId()!=applicationId){
			  logger.error("Unabke to delete, because the applicationId does not match:"+menuInDb.getApplicationId()+":"+applicationId);
			  return null;
		  }
		  entityManager.remove(menuInDb);
		  return menuInDb;
		  
	  }
	  
	  
	  public CMSEpisode update(CMSEpisode episode){
		  episode.setSyncedAt(new Date());
		  CMSEpisode episodeInDB=entityManager.find(CMSEpisode.class,episode.getId());		  
		  if(episodeInDB==null){			  
			  entityManager.persist(episode);
			  return episode;
		  }
		  else{
			  return entityManager.merge(episode);			  
		  }
	  }
	  @Transactional
	  public List<CMSEpisode> updateEpisodes(List<CMSEpisode> episodes){		  	
		  List<CMSEpisode> ret=new ArrayList<CMSEpisode>();
		  for(CMSEpisode ep:episodes){
			  ret.add(update(ep));
		  }
		  return ret;		  
	  }
	  @Transactional
	  public CMSPlaylist updatePlayList(CMSPlaylist cmsPlaylist){
		  cmsPlaylist.setSyncedAt(new Date());
		  CMSPlaylist playlistIndDB=entityManager.find(CMSPlaylist.class, cmsPlaylist.getId());
		  if(playlistIndDB!=null){
			  entityManager.persist(cmsPlaylist);
			  return cmsPlaylist;
		  }
		  else{
			  return entityManager.merge(cmsPlaylist);
		  }		  
	  }
	  
}
