package uk.co.boxnetwork.components;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.Episode;

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

	
	
	
}
