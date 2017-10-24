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
	       
	
	public List<Episode> findAllEpisodes(SearchParam searchParam){		   		   
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
	public void persist(Long setid,Image image){
	   ImageSet imageSet=entityManager.find(ImageSet.class, setid);
	   if(imageSet==null){
		   throw new RuntimeException("Image Set does not exist");
	   }
 	   Date createdAt=new Date();
 	  image.setImageSet(imageSet);
 	  image.setCreatedAt(createdAt); 	   	    		  
 	  entityManager.persist(image);    	
	}
	
  public List<ImageSet> findImageSetByEpisodeId(Long episodeid){
	  TypedQuery<ImageSet> query=entityManager.createQuery("SELECT s FROM image_set s where s.episodeId=:episodeId", ImageSet.class);
	   return query.setParameter("episodeId",episodeid).getResultList();	
	}
  public ImageSet findImageSetById(Long id){
	   return entityManager.find(ImageSet.class, id);		   
  }
	
  public List<Image> findImagesByImageSet(ImageSet imageSet){
	  TypedQuery<Image> query=entityManager.createQuery("SELECT s FROM image s where s.imageSet=:imageSet", Image.class);
	   return query.setParameter("imageSet",imageSet).getResultList();	
	}
}
