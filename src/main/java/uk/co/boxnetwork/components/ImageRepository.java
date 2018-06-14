package uk.co.boxnetwork.components;

import java.util.Date;
import java.util.List;


import javax.persistence.EntityManager;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.image.ImageSummaries;
import uk.co.boxnetwork.model.BoxChannel;
import uk.co.boxnetwork.model.BoxEpisode;
import uk.co.boxnetwork.model.BoxScheduleEvent;
import uk.co.boxnetwork.model.BoxUserRole;
import uk.co.boxnetwork.model.Image;
import uk.co.boxnetwork.model.ImageSet;
import uk.co.boxnetwork.model.ImageStatus;
import uk.co.boxnetwork.model.OperationLogs;


@Repository
public class ImageRepository {
	private static final Logger logger=LoggerFactory.getLogger(ImageRepository.class);
	@Autowired	
	private EntityManager entityManager;
	
	
	@Transactional
	public void updateBoxEpisode(BoxEpisode boxepisode){
		entityManager.merge(boxepisode);
	}
	
	
	
	public List<BoxEpisode> findBoxEpisodes(SearchParam searchParam){		   		   
		   String queryString=searchParam.getNewBoxEpisodeSelectQuery();
		   queryString=searchParam.addSortByToQuery(queryString, "e");
		   TypedQuery<BoxEpisode> query=entityManager.createQuery(queryString, BoxEpisode.class);		   
		   searchParam.config(query);		   		   
		   return query.getResultList();		   
	}
	
	public List<BoxScheduleEvent> findBoxScheduleEvent(SearchParam searchParam){		   		   
		   String queryString=searchParam.getBoxScheduleSelectQuery();
		   queryString=searchParam.addSortByToQuery(queryString, "e");
		   logger.info("query******:"+queryString);
		   TypedQuery<BoxScheduleEvent> query=entityManager.createQuery(queryString, BoxScheduleEvent.class);		   
		   searchParam.config(query);		   		   
		   return query.getResultList();		   
	}
	public BoxEpisode findEpisodeByProgrammeNumber(String programmeNumber){
		String queryString="SELECT e FROM box_episode e where e.programmeNumber=:programmeNumber";
		TypedQuery<BoxEpisode> typedQuery=entityManager.createQuery(queryString, BoxEpisode.class);
		typedQuery.setParameter("programmeNumber",programmeNumber);
		List<BoxEpisode> result=typedQuery.getResultList();
		if(result.size()==0){
			return null;
		}
		else{
			if(result.size()>1){
				logger.warn("Duplicated box_episode records for programmeNumber:"+programmeNumber+" size="+result.size());
			}
			return result.get(0);
		}		
	}
	public BoxEpisode findEpisodeById(Long id){
		   return entityManager.find(BoxEpisode.class, id);		   
	 }

	@Transactional
	public void persist(ImageSet imageSet){
 	   Date lastModifiedAt=new Date();
 	  imageSet.setLastModifiedAt(lastModifiedAt);
 	   if(imageSet.getId()==null){
 		  imageSet.setCreatedAt(lastModifiedAt);
 	   	   			entityManager.persist(imageSet);
    		}
     	else{
     				entityManager.merge(imageSet); 	   
    		}
	}
	@Transactional
	public void persist(Image image){
		Date lastModifiedAt=new Date();
		image.setLastModifiedAt(lastModifiedAt);
		 if(image.getId()==null){
			 image.setCreatedAt(lastModifiedAt);
	 	   	   			entityManager.persist(image);
	     }
	     else{
	     				entityManager.merge(image); 	   
	    }
	}
	
  
  public ImageSet findImageSetById(Long id){
	   return entityManager.find(ImageSet.class, id);		   
  }
  public Image findImageById(Long id){
	   return entityManager.find(Image.class, id);		   
  }
  @Transactional
  public void deleteImageById(Long id, String deletedBy){
	  Image image=findImageById(id);
	  entityManager.remove(image);	  
      OperationLogs logrecord=OperationLogs.buildImageDeleted(deletedBy, id);
	  entityManager.persist(logrecord);
	   
	       
		   
	   
	  	  
  }
  
  public List<ImageSet> findImageSet(SearchParam searchParam){	   
	   String queryString=searchParam.getImageSetSelectQuery();
	   queryString=searchParam.addSortByToQuery(queryString, "e");
	   TypedQuery<ImageSet> query=entityManager.createQuery(queryString, ImageSet.class);		   
	   searchParam.config(query);		   		   
	   return query.getResultList();
	}
	
  public List<Image> findImagesByImageSet(ImageSet imageSet){
	   TypedQuery<Image> query=entityManager.createQuery("SELECT s FROM image s where s.imageSet=:imageSet", Image.class);
	   return query.setParameter("imageSet",imageSet).getResultList();	
  }
  @Transactional
  public void deleteImageSetIfEmpty(ImageSet imageSet){
	  List<Image> imagesInImageSet=findImagesByImageSet(imageSet);
	  if(imagesInImageSet.size()==0){		  
		  ImageSet dbImageSet=findImageSetById(imageSet.getId());
		  logger.info("deleting the imageset because it is empty:"+imagesInImageSet);
		  entityManager.remove(dbImageSet);
	  }
  }
  @Transactional
  public void deleteImageSetById(Long id){
	   ImageSet dbImageSet=findImageSetById(id);
	   if(dbImageSet!=null){
		   entityManager.remove(dbImageSet);
	   }	 
  }
  public List<Image> findImages(SearchParam searchParam){	   
	   String queryString=searchParam.getImageSelectQuery();
	   queryString=searchParam.addSortByToQuery(queryString, "e");
	   TypedQuery<Image> query=entityManager.createQuery(queryString, Image.class);		   
	   searchParam.config(query);		   		   
	   return query.getResultList();
	}
  public ImageSummaries buildImageSummaries(){
	  ImageSummaries ret=new ImageSummaries();
	  String sql = "SELECT COUNT(s.id) FROM  image s";
	  TypedQuery<Long> q = entityManager.createQuery(sql, Long.class);
	  Long nunberOfImages = (Long)q.getSingleResult();
	  ret.setNunberOfImages(nunberOfImages);		  
	  
	  
	  sql = "SELECT COUNT(s.id) FROM  image_set s";
	  q = entityManager.createQuery(sql, Long.class);
	  Long numberOfImageSets = (Long)q.getSingleResult();
	  ret.setNumberOfImageSets(numberOfImageSets);

	  sql="SELECT COUNT(e.id) FROM box_episode e where SIZE(e.imageSets)=0";
	  q = entityManager.createQuery(sql, Long.class);
	  Long numberOfEpisodesMissingImages = (Long)q.getSingleResult();
	  ret.setNumberOfEpisodesMissingImages(numberOfEpisodesMissingImages);
	  
	  sql = "SELECT COUNT(s.id) FROM  image s where s.imageStatus=:imageStatus";
	  q = entityManager.createQuery(sql, Long.class);
	  q.setParameter("imageStatus",ImageStatus.WAITING_APPROVE);
	  Long numberOfImageWaitingApproved = (Long)q.getSingleResult();
	  ret.setNumberOfImageWaitingApproved(numberOfImageWaitingApproved);	
	  
	  sql = "SELECT COUNT(s.id) FROM  image s where s.imageStatus=:imageStatus";
	  q = entityManager.createQuery(sql, Long.class);
	  q.setParameter("imageStatus",ImageStatus.APPROVED);
	  Long numberOfImageApproved = (Long)q.getSingleResult();
	  ret.setNumberOfImageApproved(numberOfImageApproved);	
	  
	  
	  return ret;
  }
}
