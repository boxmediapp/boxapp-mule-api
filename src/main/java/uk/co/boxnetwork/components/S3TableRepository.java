package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.s3.MediaFilesLocation;
import uk.co.boxnetwork.data.s3.VideoFileItem;
import uk.co.boxnetwork.data.s3.VideoFileList;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.Image;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.model.S3VideoFileItem;

@Repository
public class S3TableRepository {
	private static final Logger logger=LoggerFactory.getLogger(S3TableRepository.class);
	
	
	@Autowired
	private AppConfig appConfig;	

	@Autowired	
	private EntityManager entityManager;
	
	
	
	
	@Transactional
    public void syncS3VideoFileItems(List<VideoFileItem> videofiles){
		if(videofiles.size()<=1){
			return;
		}
    	Query query=entityManager.createNativeQuery("DELETE FROM s3_video_file_item");
    	query.executeUpdate();
    	for(VideoFileItem videofile:videofiles){
    		S3VideoFileItem s3videoFileItem=new S3VideoFileItem();
    		s3videoFileItem.copyFrom(videofile);
    		 entityManager.persist(s3videoFileItem);
    	}    	
    }
	@Transactional
	public void createS3VideoFileItem(VideoFileItem videofile){
		S3VideoFileItem s3videoFileItem=new S3VideoFileItem();
		s3videoFileItem.copyFrom(videofile);
		 entityManager.persist(s3videoFileItem);
	}
	
	@Transactional
	public void onVideoFileDeleted(String videofile){
	    Query query=entityManager.createNativeQuery("DELETE FROM s3_video_file_item  where s3_video_file_item.file=:file");
		query.setParameter("file",videofile).executeUpdate();
	}
	@Transactional
	public void linkEpisode(Episode episode){
		String ingestURL=episode.getIngestSource();
		if(ingestURL==null){
			return;
		}
	    int ib=ingestURL.lastIndexOf("/");
	    String filename=ingestURL.substring(ib+1);	    
	    TypedQuery<S3VideoFileItem> query=entityManager.createQuery("SELECT m FROM s3_video_file_item m where m.file=:file", S3VideoFileItem.class);
		List<S3VideoFileItem> matchedItems=query.setParameter("file",filename).getResultList();
		if(matchedItems.size()==0){
			return;
		}
		for(S3VideoFileItem item:matchedItems){			
			item.setEpisodeId(episode.getId());
			item.setEpisodeTitle(episode.getTitle());
			item.setProgrammeNumber(episode.getCtrPrg());
			entityManager.merge(item);
		}		
	}
	@Transactional
	public void unlinkEpisode(Episode episode){
		if(episode.getId()==null){
			return;
		}
		TypedQuery<S3VideoFileItem> query=entityManager.createQuery("SELECT m FROM s3_video_file_item m where m.episodeId=:episodeId", S3VideoFileItem.class);
		List<S3VideoFileItem> matchedItems=query.setParameter("episodeId",episode.getId()).getResultList();
		if(matchedItems.size()==0){
			return;
		}
		for(S3VideoFileItem item:matchedItems){			
			item.setEpisodeId(null);
			item.setEpisodeTitle(null);
			item.setProgrammeNumber(null);
			entityManager.merge(item);
		}
	}
	public VideoFileList listVideoFileItem(String search, Integer startIndex, Integer numberOfRecords){
		VideoFileList videoFileList=new VideoFileList();
		videoFileList.setBaseUrl(appConfig.getS3videoURL());
		
		String queryString="SELECT m FROM s3_video_file_item m";
		
		if(search!=null){
			queryString+=" WHERE m.file LIKE :search OR m.episodeTitle LIKE :search OR m.programmeNumber LIKE :search";			 
		}
		TypedQuery<S3VideoFileItem> query=entityManager.createQuery(queryString, S3VideoFileItem.class);
		if(search!=null){
			query.setParameter("search",search+"%");
		}
		if(startIndex!=null && startIndex>0){
			query.setFirstResult(startIndex);			   
		 }
		 if(numberOfRecords!=null && numberOfRecords>=0){
			 query.setMaxResults(numberOfRecords);
		   }
		  List<S3VideoFileItem> matchedItems=query.getResultList();		  		  
		  List<VideoFileItem> videos=new ArrayList<VideoFileItem>();
		  for(S3VideoFileItem s3videofile:matchedItems){
			  VideoFileItem video=new VideoFileItem();
			  s3videofile.exportTo(video);
			  videos.add(video);
		  }
		  videoFileList.setFiles(videos);
		return videoFileList;
		
		
	}
}
