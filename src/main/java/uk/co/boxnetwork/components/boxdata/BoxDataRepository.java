package uk.co.boxnetwork.components.boxdata;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.BoxChannel;
import uk.co.boxnetwork.model.BoxEpisode;
import uk.co.boxnetwork.model.BoxScheduleEvent;
import uk.co.boxnetwork.model.ScheduleEvent;

@Repository
public class BoxDataRepository {
	private static final Logger logger=LoggerFactory.getLogger(BoxDataRepository.class);
	
	  @Autowired	
	  private EntityManager entityManager;
  
	  @Transactional
		public void createBoxChannel(BoxChannel boxChannel){
			if(boxChannel.getChannelId()==null||boxChannel.getChannelId().trim().length()==0){
				return;
			}
			BoxChannel efoundChannel=entityManager.find(BoxChannel.class, boxChannel.getChannelId());
			if(efoundChannel!=null){
				return;
			}
			entityManager.persist(boxChannel);
			
		}
	    
	  @Transactional
		public void updateOrCreate(BoxChannel boxChannel){
			if(boxChannel.getChannelId()==null||boxChannel.getChannelId().trim().length()==0){
				return;
			}
			if(boxChannel.getChannelName()==null||boxChannel.getChannelName().trim().length()==0){
				return;
			}
			BoxChannel channelInDB=findBoxChannelById(boxChannel.getChannelId());
			if(channelInDB==null){
				  channelInDB=findBoxChannelByChannelName(boxChannel.getChannelName());
				  if(channelInDB!=null){
					  logger.error("channelName is already used:"+boxChannel.getChannelName());				  
					  return;
				  }
				  else{
					  entityManager.persist(boxChannel);
				  }
			  }
			else{
				entityManager.merge(boxChannel);
			}			
		}
	  
	  public List<BoxChannel> findAllBoxChannel(){
			TypedQuery<BoxChannel> query=entityManager.createQuery("SELECT c FROM box_channel c", BoxChannel.class);
			return  query.getResultList();
      }
	  
	  public BoxChannel findBoxChannelById(String channelid){
			return  entityManager.find(BoxChannel.class, channelid);
			
	  }
	  public List<BoxScheduleEvent> findBoxScheduleEvent(BoxChannel boxChannel, Date scheduleTimestampFrom, Date scheduleTimestampTo){		   		   
		   String queryString="select e from box_schedule_event e where e.boxChannel=:boxChannel and e.scheduleTimestamp >= :scheduleTimestampFrom and e.scheduleTimestamp <= :scheduleTimestampTo ";		   
		   TypedQuery<BoxScheduleEvent> query=entityManager.createQuery(queryString, BoxScheduleEvent.class);
		   query.setParameter("boxChannel", boxChannel);
		   query.setParameter("scheduleTimestampFrom", scheduleTimestampFrom);
		   query.setParameter("scheduleTimestampTo", scheduleTimestampTo);
		   return query.getResultList();		   
	  }
	  
	  @Transactional
	  public BoxChannel removeChannelById(String channelId){
		  BoxChannel boxChannel=findBoxChannelById(channelId);
		  if(boxChannel==null){
			  return null;
		  }
		  entityManager.remove(boxChannel);
		  return boxChannel;
	  }
	  public BoxChannel findBoxChannelByChannelName(String channelName){			
		    TypedQuery<BoxChannel> query=entityManager.createQuery("SELECT c FROM box_channel c where c.channelName=:channelName", BoxChannel.class);
		    query.setParameter("channelName",channelName);	
		    
			List<BoxChannel> matched=query.getResultList();
			if(matched.size()>0){
				return matched.get(0);
			}
			else{
				return null;
			}
	  }
	  @Transactional
      public void updateBoxEpisodeWithScheduleEvent(ScheduleEvent evt, ImportScheduleRequest request){
   	   if(evt==null || evt.getEpisode()==null){
   		   return;
   	   }    	   
   	   String programmeNumber=evt.getEpisode().getCtrPrg();
   	       	  
   	   if(programmeNumber==null||evt.getScheduleTimestamp()==null){    		   
			   return;
		   }
		   programmeNumber=programmeNumber.trim();
		   if(programmeNumber.length()==0){
			   return;
		   }
		   
		   TypedQuery<BoxEpisode> query=entityManager.createQuery("SELECT b FROM box_episode b where b.programmeNumber=:programmeNumber", BoxEpisode.class);
		   List<BoxEpisode> matchedEpisodes=query.setParameter("programmeNumber",programmeNumber).getResultList();
		   if(matchedEpisodes.size()==0){
			   logger.error("failed to import box schedule, episode not found:programmeNumber="+programmeNumber);
			   return;
		   }
		   BoxEpisode matchedEpisode=matchedEpisodes.get(0);
		   BoxScheduleEvent boxScheduleEvent=null;
		   
		   
		   if(request.getChannelId()!=null){
			   BoxChannel foundChannel=entityManager.find(BoxChannel.class, request.getChannelId());
			   if(foundChannel!=null){
				   TypedQuery<BoxScheduleEvent> schedleQuery=entityManager.createQuery("SELECT e FROM box_schedule_event e where e.boxEpisode=:boxEpisode and e.scheduleTimestamp =:scheduleTimestamp and e.boxChannel=:boxChannel", BoxScheduleEvent.class);
				   schedleQuery.setParameter("boxEpisode",matchedEpisode);
				   schedleQuery.setParameter("scheduleTimestamp",evt.getScheduleTimestamp());
				   schedleQuery.setParameter("boxChannel",foundChannel);
				   List<BoxScheduleEvent> matchedSchdules=schedleQuery.getResultList();
				   if(matchedSchdules.size()==0){
					   boxScheduleEvent=new BoxScheduleEvent();
					   boxScheduleEvent.setBoxChannel(foundChannel);
					   boxScheduleEvent.setBoxEpisode(matchedEpisode);
					   boxScheduleEvent.setScheduleTimestamp(evt.getScheduleTimestamp());
					   entityManager.persist(boxScheduleEvent);
				   }
				   else{
					   boxScheduleEvent=matchedSchdules.get(0);					   
				   }
			   }
			   else{
				   logger.error("failed to import box schedule, channelid is not found in database:"+request.getChannelId());
				   return;
			   }
		   }
		   else{
			   logger.error("failed to import box schedule, channelid is mossing");
			   return;
		   }
		   
		   Date now=new Date();
		   
		   if(matchedEpisode.getBoxSchedule()==null){
			   matchedEpisode.setBoxSchedule(boxScheduleEvent);
			   matchedEpisode.setLastModifiedAt(new Date());
			   entityManager.merge(matchedEpisode);
		   }
		   else if(now.getTime()<matchedEpisode.getBoxSchedule().getScheduleTimestamp().getTime()){
			   matchedEpisode.setBoxSchedule(boxScheduleEvent);
			   matchedEpisode.setLastModifiedAt(new Date());
			   entityManager.merge(matchedEpisode);
		   }
		   
		   else if(matchedEpisode.getBoxSchedule().getScheduleTimestamp().getTime()>boxScheduleEvent.getScheduleTimestamp().getTime()){			   
				   matchedEpisode.setBoxSchedule(boxScheduleEvent);
				   matchedEpisode.setLastModifiedAt(new Date());
				   entityManager.merge(matchedEpisode);
		   }
      }
	
}
