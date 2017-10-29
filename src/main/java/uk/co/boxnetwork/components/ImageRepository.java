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
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.Image;
import uk.co.boxnetwork.model.ImageSet;
import uk.co.boxnetwork.model.ScheduleEvent;

@Repository
public class ImageRepository {
	private static final Logger logger=LoggerFactory.getLogger(ImageRepository.class);
	@Autowired	
	private EntityManager entityManager;
	       
	
	public List<Episode> findEpisodesNotProcessed(SearchParam searchParam){		   		   
		   String queryString=searchParam.getEpisodeImageSelectQuery();
		   queryString=searchParam.addSortByToQuery(queryString, "e");
		   TypedQuery<Episode> query=entityManager.createQuery(queryString, Episode.class);		   
		   searchParam.config(query);		   		   
		   return query.getResultList();		   
	}
	public Episode findEpisodeById(Long id){
		   return entityManager.find(Episode.class, id);		   
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
	
  public List<ImageSet> findImageSetByEpisodeId(Long episodeid){
	  TypedQuery<ImageSet> query=entityManager.createQuery("SELECT s FROM image_set s where s.episodeId=:episodeId", ImageSet.class);
	   return query.setParameter("episodeId",episodeid).getResultList();	
  }
  public ImageSet findImageSetById(Long id){
	   return entityManager.find(ImageSet.class, id);		   
  }
  public Image findImageById(Long id){
	   return entityManager.find(Image.class, id);		   
  }
  @Transactional
  public void deleteImageById(Long id){
	  Image image=findImageById(id);
	  entityManager.remove(image);
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
  public List<Image> findImages(SearchParam searchParam){	   
	   String queryString=searchParam.getImageSelectQuery();
	   queryString=searchParam.addSortByToQuery(queryString, "e");
	   TypedQuery<Image> query=entityManager.createQuery(queryString, Image.class);		   
	   searchParam.config(query);		   		   
	   return query.getResultList();
	}
}
