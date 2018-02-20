package uk.co.boxnetwork.components;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.validator.jtype.Generic;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import uk.co.boxnetwork.data.DataReport;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AdvertisementRule;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.AvailabilityWindow;
import uk.co.boxnetwork.model.BCNotification;
import uk.co.boxnetwork.model.BoxChannel;
import uk.co.boxnetwork.model.BoxEpisode;
import uk.co.boxnetwork.model.BoxScheduleEvent;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.BoxUserRole;
import uk.co.boxnetwork.model.CertificationCategory;
import uk.co.boxnetwork.model.CertificationTime;
import uk.co.boxnetwork.model.CertificationType;
import uk.co.boxnetwork.model.ClientDevice;
import uk.co.boxnetwork.model.ComplianceInformation;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.EpisodeStatus;
import uk.co.boxnetwork.model.MediaApplicationID;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.model.MediaTag;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.ProgrammeCertification;
import uk.co.boxnetwork.model.ProgrammeContentType;
import uk.co.boxnetwork.model.PublishedStatus;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
import uk.co.boxnetwork.model.VideoStatus;
import uk.co.boxnetwork.util.GenericUtilities;


@Repository
public class BoxMedataRepository {
	private static final Logger logger=LoggerFactory.getLogger(BoxMedataRepository.class);
      @Autowired	
	  private EntityManager entityManager;
      
      @Autowired
      S3TableRepository s3TableRepository;
      
      
      public AppConfig findAppConfigByApplication(MediaApplicationID applicationId){
    	  TypedQuery<AppConfig> query=entityManager.createQuery("SELECT s FROM app_config s where s.applicationId=:applicationId", AppConfig.class);    	      	  
		  List<AppConfig> matchedAppConfigs=query.setParameter("applicationId",applicationId).getResultList();
		  if(matchedAppConfigs.size()>0){
			   return matchedAppConfigs.get(0);
		   }
		  else{
			  return null;
		  }
      }
      public AppConfig findDefaultAppConfig(){
    	  TypedQuery<AppConfig> query=entityManager.createQuery("SELECT s FROM app_config s", AppConfig.class);    	      	  
		  List<AppConfig> matchedAppConfigs=query.getResultList();
		  if(matchedAppConfigs.size()>0){
			   return matchedAppConfigs.get(0);
		   }
		  else{
			  return null;
		  }
      }
      
      @Transactional
      public void updateAppConfig(AppConfig config){
    	  if(config.getId()!=null){
    		  entityManager.merge(config);
    	  }
    	  else{
    		  entityManager.persist(config);    		  
    	  }    	  
      }

       @Transactional
	   public void importBoxEpisode(Episode episode){
		   String programmeNumber=episode.getCtrPrg();
		   if(programmeNumber==null){
			   return;
		   }
		   programmeNumber=programmeNumber.trim();
		   if(programmeNumber.length()==0){
			   return;
		   }
		   TypedQuery<BoxEpisode> query=entityManager.createQuery("SELECT b FROM box_episode b where b.programmeNumber=:programmeNumber", BoxEpisode.class);
		   List<BoxEpisode> matchedEpisode=query.setParameter("programmeNumber",programmeNumber).getResultList();
		   if(matchedEpisode.size()>0){
			   return;
		   }
		   BoxEpisode boxEpisode=new BoxEpisode();
		   boxEpisode.copyFrom(episode);
		   boxEpisode.setCreatedAt(new Date());
		   boxEpisode.setLastModifiedAt(new Date());
		   entityManager.persist(boxEpisode);		   
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
       public void persisEvent(ScheduleEvent newEvent){
    	   	Date lastModifiedAt=new Date();
			newEvent.setLastModifiedAt(lastModifiedAt);
			newEvent.setCreatedAt(lastModifiedAt);
			entityManager.persist(newEvent);
       }
       public void mergeEvent(ScheduleEvent event){
    	   Date lastModifiedAt=new Date();
    	   event.setLastModifiedAt(lastModifiedAt);			
    	   entityManager.merge(event);
       }
       public void removeScheduleEvent(ScheduleEvent evt){
    	   entityManager.remove(evt);
       }
       public void persisSeriesGroup(SeriesGroup seriesGroup){
    	    Date lastModifiedAt=new Date();
    	    seriesGroup.setLastModifiedAt(lastModifiedAt);
    	    seriesGroup.setCreatedAt(lastModifiedAt);
   			entityManager.persist(seriesGroup);
       }
       @Transactional
       public void persistCuePoint(CuePoint cuepoint, Episode episode){
    	  if(cuepoint.getId()!=null){
    		  entityManager.merge(cuepoint);
    		  return;
    	  }
    	  cuepoint.setEpisode(episode);
    	  episode.addCuePoint(cuepoint);
    	  entityManager.persist(cuepoint);
    	  persist(episode);
    	  markMetadataChanged(episode);
       }
       
       @Transactional
       public void persistAvailabilityWindow(AvailabilityWindow availabilityWindow, Episode episode){
    	  if(availabilityWindow.getId()!=null){
    		  entityManager.merge(availabilityWindow);
    		  return;
    	  }
    	  availabilityWindow.setEpisode(episode);
    	  episode.addAvailabilityWindow(availabilityWindow);
    	  entityManager.persist(availabilityWindow);
    	  persist(episode);
    	  markMetadataChanged(episode);
       }
       public void persist(AvailabilityWindow availabilityWindow){
    	   entityManager.persist(availabilityWindow);
       }
       @Transactional
       public void removeCuePoint(Long cueid){
    	   CuePoint cuepoint=findCuePoint(cueid);
    	   if(cuepoint==null){
    		   logger.info("could not find cue point to delete:cueid:"+cueid);
    		   return;
    	   }
    	   cuepoint.getEpisode().rempveCuePoint(cuepoint);
    	   entityManager.remove(cuepoint);    	   
    	   markMetadataChanged(cuepoint.getEpisode());
    	   
       }
       @Transactional
       public void removeAvailabilityWindow(Long avid){
    	   AvailabilityWindow avwindow=findAvailabilityWindowId(avid);
    	   markMetadataChanged(avwindow.getEpisode());
    	   entityManager.remove(avwindow);
    	   
       }
       
       
       public void persist(Episode episode){
    	   Date lastModifiedAt=new Date();
    	   episode.setLastModifiedAt(lastModifiedAt);
    	   if(episode.getId()==null){
    		   episode.setCreatedAt(lastModifiedAt);
    	   	   entityManager.persist(episode);
       		}
        	else{
        	   entityManager.merge(episode);
    	   
       		}
    	   importBoxEpisode(episode);
    	   s3TableRepository.linkEpisode(episode);
		}
       @Transactional
       public void updateEpisode(Episode episode){
    	   persist(episode); 
       }
       
       
       public void persist(CuePoint cuePoint){
    	   if(cuePoint.getId()!=null){
    		   entityManager.merge(cuePoint);
    	   }
    	   else{
    		   	entityManager.persist(cuePoint);
    	   }
       }
      public void persistEpisodeStatus(EpisodeStatus episodeStatus){
    	  if(episodeStatus.getId()==null){
    		  entityManager.persist(episodeStatus);
    	  }
    	  else{
    		  entityManager.merge(episodeStatus);
    	  }
      }
      
      
      @Transactional
      public void remove(EpisodeStatus episodeStatus){
    	  EpisodeStatus st=entityManager.find(EpisodeStatus.class, episodeStatus.getId());
    	  entityManager.remove(st);
      }
      @Transactional
      public void removeSeriesById(Long seriesid){
    	  Series series=entityManager.find(Series.class, seriesid);
    	  entityManager.remove(series);
      }
      @Transactional
      public void removeSeriesGroupById(Long seriesid){
    	  SeriesGroup seriesgroup=entityManager.find(SeriesGroup.class, seriesid);
    	  if(seriesgroup==null){
    		  logger.warn("the series group is already deleted:"+seriesid);
    		  return;
    	  }
    	  entityManager.remove(seriesgroup);
      }
      
       public void persisSeries(Series series){
		    	   Date lastModifiedAt=new Date();
		    	   series.setLastModifiedAt(lastModifiedAt);
		    	   series.setCreatedAt(lastModifiedAt);
		    	   if(series.getNextEpisodeNumber()==null){
		    		   series.setNextEpisodeNumber(1);   
		    	   }
				   entityManager.persist(series);

       }
       public void remove(CuePoint cuePoint){    	   
    		   entityManager.remove(cuePoint);    	   
       }
       
       public void mergeSeries(Series series){
    	   Date lastModifiedAt=new Date();
    	   series.setLastModifiedAt(lastModifiedAt);    
    	   if(series.getNextEpisodeNumber()==null){
    		   series.setNextEpisodeNumber(1);
    	   }
		   entityManager.merge(series);
       }
       @Transactional
       public void updateSeries(Series series){
    	   mergeSeries(series);
       }
       
       @Transactional
       public void mergeSeriesGroup(SeriesGroup seriesGroup){
    	   Date lastModifiedAt=new Date();
    	   seriesGroup.setLastModifiedAt(lastModifiedAt);    	   
		   entityManager.merge(seriesGroup);
       }
       public void removeSeriesGroup(SeriesGroup seriesGroup){
    	   entityManager.remove(seriesGroup);
       }
       @Transactional
       public void removeSeriesGroup(List<SeriesGroup> seriesGroup){
    	   if(seriesGroup==null||seriesGroup.size()==0){
    		   return;
    	   }
    	   for(SeriesGroup sg:seriesGroup){
    		   SeriesGroup seriesgroup=findSeriesGroupById(sg.getId());
    		   removeSeriesGroup(seriesgroup);
    	   }
    	   
       }
       
      
       
       @Transactional
       public void removeEpisode(Long episodeid){
    	   Episode episode=entityManager.find(Episode.class, episodeid);
    	   logger.info("The episode is going to be deleted:"+episodeid+":"+episode);
    	   List<ScheduleEvent> eventsToDelete=findScheduleEventByEpisode(episode);
    	   
    	   if(eventsToDelete!=null && eventsToDelete.size()>0){
    		   logger.info("removing the schedules:"+eventsToDelete.size());
    		   for(ScheduleEvent evt:eventsToDelete){
    			   ScheduleEvent event=entityManager.find(ScheduleEvent.class, evt.getId());
    			   entityManager.remove(event);    			   
    		   }
    	   }
    	   if(episode.getCuePoints()!=null && episode.getCuePoints().size()>0){
    		   logger.info("removing the cues:"+episode.getCuePoints().size());
	    	   for(CuePoint cp:episode.getCuePoints()){
	    		   entityManager.remove(cp);		    		   
	    	   }
	    	   episode.clearCuePoints();
    	   }    	   
    	   if(episode.getAvailabilities()!=null && episode.getAvailabilities().size()>0){
    		   logger.info("Removing the vailability:"+episode.getAvailabilities());
    		   for(AvailabilityWindow avail: episode.getAvailabilities()){
    			   entityManager.remove(avail);
    		   }
    		   episode.clearCuePoints();
    	   }
    	   entityManager.remove(episode);    	   
    	   if(episode.getEpisodeStatus()!=null){
    		   entityManager.remove(episode.getEpisodeStatus());
    	   }
    	   s3TableRepository.unlinkEpisode(episode);    	   
       }
       
       public void persisMediaTag(MediaTag mediaTag){
    	   Date lastModifiedAt=new Date();
    	   mediaTag.setCreatedAt(lastModifiedAt);    	   
		   entityManager.persist(mediaTag);
       }
       public void persisClientDevice(ClientDevice clientDevice){
    	   Date lastModifiedAt=new Date();
    	   clientDevice.setCreatedAt(lastModifiedAt);    	   
		   entityManager.persist(clientDevice);
       }
       
       public void persist(ComplianceInformation compliance){
    	   entityManager.persist(compliance);
       }
       
       public void merge(ComplianceInformation compliance){
    	   entityManager.merge(compliance);
       }
       public void persist(ProgrammeCertification programmeCertification){
    	   entityManager.persist(programmeCertification);
       }
       public void merge(ProgrammeCertification programmeCertification){
    	   entityManager.merge(programmeCertification);    	   
       }
       public void persis(CertificationType certificationType){
    	   entityManager.persist(certificationType);
       }
       public void merge(CertificationType certificationType){
    	   entityManager.merge(certificationType);
       }
       public void persist(CertificationCategory certificationcategory){
    	   entityManager.persist(certificationcategory);    	   
       }
       public void merge(CertificationCategory certificationcategory){
    	   entityManager.merge(certificationcategory);    	   
       }
       public void persist(CertificationTime certificationTime){
    	   entityManager.persist(certificationTime);    	   
       }
       public void merge(CertificationTime certificationTime){
    	   entityManager.merge(certificationTime);    	   
       }
       
       public void persist(BCNotification bcNotification){
    	   entityManager.persist(bcNotification);
       }
       
       public void update(Series series){
		   mergeSeries(series);		   
	   }
       @Transactional
	   public void createEvent(ScheduleEvent newEvent){		    		   
		    
			if(GenericUtilities.isNotAValidId(newEvent.getScheduleEventID())){
				newEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
				persisEvent(newEvent);				
			}
			else{
				logger.info("querying the sechedule event:");
				List<ScheduleEvent> existingEvents=findScheduleEventByScheduleEventId(newEvent.getScheduleEventID());
				if(existingEvents.size()==0){
					newEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
					persisEvent(newEvent);
					
				}
				else{
					if(existingEvents.size()>1){
						logger.warn("There are mot than matching events matching evenid="+newEvent.getScheduleEventID());
					}
					ScheduleEvent existingEvent=existingEvents.get(0);
					existingEvent.setEpisode(saveEpisode(newEvent.getEpisode()));
					existingEvent.merge(newEvent);
					mergeEvent(existingEvent);
				}
				
			}
					        		    				
	   }	
      
      
              
       
       
       
       public void statusUpdateFromC4Created(Episode episode){
    	   EpisodeStatus episodeStatus=new EpisodeStatus();
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
    		    persistEpisodeStatus(episodeStatus);
    	      }
    	     else{
    	    	 persistEpisodeStatus(episodeStatus);
    	     }
    	     episode.setEpisodeStatus(episodeStatus); 
    	
       }
       
       public void statusUpdateFromC4Updated(Episode episode){
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
    	     persistEpisodeStatus(episodeStatus);
       }
       
       
       
	   
	   
	   private Episode returnExistingEpisodeOrPersist(Episode episode,List<Episode> matchedEpisodes,String messageOnDuplication){
		   if(matchedEpisodes.size()==0){
			   statusUpdateFromC4Created(episode);
			   episode.setSeries(saveSeries(episode.getSeries()));		    
			    persist(episode);			   	
			   	return episode;
			   			   
		   }
		   else{
			   if(matchedEpisodes.size()>1){
				   logger.warn("Marching the episodes more than once:"+messageOnDuplication);
			   }
			   Episode existingEpisode=matchedEpisodes.get(0);
			   logger.info("Not updatd");
//			   existingEpisode.setSeries(saveSeries(episode.getSeries()));
//			   statusUpdateFromC4Updated(existingEpisode);
//			   update(existingEpisode);
			   return existingEpisode;
		   }		   
	   }
	   public CertificationCategory saveCertificationCategory(CertificationCategory certificationCategory){
		   if(certificationCategory==null){
			   return null;			   
		   }
		   List<CertificationCategory> matched=findCertificationCategoryById(certificationCategory.getId());
		   if(matched.size()==0){
			   persist(certificationCategory);
		   }
		   else{
			   matched.get(0).update(certificationCategory);			   
			   merge(certificationCategory);
		   }
			return certificationCategory;			
	   }
	   public CertificationTime saveCertificationTime(CertificationTime certificationTime){
		   if(certificationTime==null){
			   return certificationTime;
		   }
		   List<CertificationTime> matched=findCertificationTimeById(certificationTime.getId());
		   if(matched.size()==0){
			   persist(certificationTime);
		   }
		   else{
			   matched.get(0).update(certificationTime);			   
			   merge(certificationTime);
		   }
		   return certificationTime;
	   }
	   public CertificationType saveCertificationType(CertificationType certificationType){
		   if(certificationType==null){
			   return null;
		   }
		   certificationType.setCertificationCategory(saveCertificationCategory(certificationType.getCertificationCategory()));
		   certificationType.setCertificationTime(saveCertificationTime(certificationType.getCertificationTime()));
		   List<CertificationType> matched=findCertificationTypeById(certificationType.getId());
		   if(matched.size()==0){
			   persis(certificationType);
		   }
		   else{
			   matched.get(0).update(certificationType);			   
			   merge(certificationType);
		   }
		   return certificationType;
	   }
	   public ProgrammeCertification saveProgrammeCertification(ProgrammeCertification programmeCertification){
		   if(programmeCertification == null){
			   return null;
		   }
		   programmeCertification.setCertificationType(saveCertificationType(programmeCertification.getCertificationType()));
		   List<ProgrammeCertification> matchedProgramemeCertification=findProgrammeCertificationById(programmeCertification.getId());
		   if(matchedProgramemeCertification.size()==0){
			   persist(programmeCertification);
		   }
		   else{
			   matchedProgramemeCertification.get(0).update(programmeCertification);			   
			   merge( matchedProgramemeCertification.get(0));
		   }
		   
		   return programmeCertification;
	   }
	   public Set<ComplianceInformation> saveComplianceInformations(Set<ComplianceInformation> complianceInformations){		   
		   if(complianceInformations.size()==0){
			   return complianceInformations;
		   }
		   Set<ComplianceInformation> ret=new HashSet<ComplianceInformation>();
		   for(ComplianceInformation complianceInformation:complianceInformations){
			   ret.add(saveComplianceInfo(complianceInformation));			   
		   }
		   return ret;
	   }
	   public ComplianceInformation saveComplianceInfo(ComplianceInformation complianceInformation){
		   complianceInformation.setProgrammeCertification(saveProgrammeCertification(complianceInformation.getProgrammeCertification()));
		   List<ComplianceInformation> matchedCompliances=findComplianceInformationById(complianceInformation.getId());
		   if(matchedCompliances.size()==0){
			   persist(complianceInformation);			   
		   }
		   else{
			   matchedCompliances.get(0).update(complianceInformation);
			   merge(matchedCompliances.get(0));
		   }
		   return complianceInformation;
	   }
	   
	 
	   public Episode saveEpisode(Episode episode){
		   if(episode==null){
			   return null;			   
		   }
		   else{
			   episode.setComplianceInformations(saveComplianceInformations(episode.getComplianceInformations()));
			   episode.adjustBeforeSave();			   
				   if(GenericUtilities.isNotValidCrid(episode.getCtrPrg())){
					   if(GenericUtilities.isNotValidTitle(episode.getTitle())){
						   if(GenericUtilities.isNotValidTitle(episode.getName())){
							   episode.setSeries(saveSeries(episode.getSeries()));		    
							    persist(episode);		    							   	
							   	return episode;							   							      
						   }
						   else{
							   List<Episode> matchedEpisodes=findEpisodesByName(episode.getName());
							   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"title="+episode.getName());
						   }
						   
							
					   }
					   else{
						   List<Episode> matchedEpisodes=findEpisodesByTitle(episode.getTitle());
						   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"title="+episode.getTitle());
					   }
				   }
				   else{
					   List<Episode> matchedEpisodes=findEpisodesByCtrPrg(episode.getCtrPrg());
					   if(matchedEpisodes.size()==0 && (!GenericUtilities.isNotAValidId(episode.getPrimaryId()))){
						   matchedEpisodes=findEpisodesByPrimaryId(episode.getPrimaryId());
						   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"primaryid="+episode.getPrimaryId());
					   }
					   else{
						   return returnExistingEpisodeOrPersist(episode,matchedEpisodes,"ctrprg="+episode.getCtrPrg());
					   }
				   }
			  
			   
		   }
	   }
	   
	   
	   private Series returnExistingSeriesOrPersist(Series series,List<Series> matchedSeries,String messageOnDuplication){
		   if(matchedSeries.size()==0){	
			   if(series.getContractNumber()!=null){
				   Series foundSeries=findSingleSeriesByContractNumber(series.getContractNumber());
				   if(foundSeries!=null){
					   logger.warn("Found the series with the same contract number when try to create:"+foundSeries+"  for series=["+series);
					   foundSeries.mergeFieldValues(series);
					   mergeSeries(foundSeries);
					   return foundSeries;
				   }
			   }
				   persisSeries(series);
			   return series;
		   }
		   else{
			   if(matchedSeries.size()>1){
				   logger.warn("Marching the series more than once:"+messageOnDuplication);				   
			   }
			   Series existingSeries=matchedSeries.get(0);
			   existingSeries.merge(series);
			   mergeSeries(existingSeries);
			   return existingSeries;
		   }
	   }
	   public Series saveSeries(Series series){
		   if(series==null){
			   		return null;
		   }
		   else if(series.getId()!=null){			   
			   persisSeries(series);
			   return series;
		   }
		   else{
				   if(GenericUtilities.isNotAValidId(series.getPrimaryId())){
					   if(series.getContractNumber()!=null){
						   Series foundSeries=findSingleSeriesByContractNumber(series.getContractNumber());
						   if(foundSeries!=null){
							   logger.warn("Found the series with the same contract number when try to create:"+foundSeries+"  for series=["+series);
							   foundSeries.mergeFieldValues(series);
							   mergeSeries(foundSeries);
							   return foundSeries;
						   }
					   }
						   if(GenericUtilities.isNotAValidId(series.getContractNumber())){
							   if(GenericUtilities.isNotValidTitle(series.getName())){
								   
								   persisSeries(series);
								   
								   return series;
							   }
							   else{
								   List<Series> matchedSeries=findSeriesByName(series.getName());
								   return returnExistingSeriesOrPersist(series,matchedSeries,"name="+series.getName());
							   }
						   }  
						   else{
								   List<Series> matchedSeries=findSeriesByContractNumber(series.getContractNumber());
								   return returnExistingSeriesOrPersist(series,matchedSeries,"contractnumber="+series.getContractNumber());
						   }
				   }
				   else{			   
					   			List<Series> matchedSeries=findSeriesByPrimaryId(series.getPrimaryId());
					   			return returnExistingSeriesOrPersist(series,matchedSeries,"primaryId="+series.getPrimaryId());			   						   
				   }
		   }
	   }
	   public ScheduleEvent findScheduleEventById(Long id){
		   return entityManager.find(ScheduleEvent.class, id);		   
	   }
	   public List<ScheduleEvent> findAllScheduleEvent(){
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery("SELECT s FROM schedule_event s", ScheduleEvent.class);
		   return query.getResultList();
	   }
	   public List<ScheduleEvent> findScheduleEventsFrom(SearchParam searchParam){		   
		   String queryString=searchParam.selectScheduleQuery("SELECT s FROM schedule_event s", "SELECT s FROM schedule_event s where s.scheduleTimestamp >= :from","SELECT s FROM schedule_event s where s.scheduleTimestamp >= :from and s.scheduleTimestamp <= :to");
		   queryString=searchParam.addSortByToQuery(queryString, "s");
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery(queryString, ScheduleEvent.class);
		   searchParam.config(query);		   		   
		   return query.getResultList();
		   
		   
		   
	   }
	   public List<ScheduleEvent> findScheduleEventByEpisode(Episode episode){		
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery("SELECT s FROM schedule_event s where s.episode=:episode", ScheduleEvent.class);
		   
		   return query.setParameter("episode",episode).getResultList();
	   }

	   public List<ScheduleEvent> findScheduleEventByScheduleEventId(String scheduleEventID){
		   TypedQuery<ScheduleEvent> query=entityManager.createQuery("SELECT s FROM schedule_event s where s.scheduleEventID=:scheduleEventID", ScheduleEvent.class);
		   return query.setParameter("scheduleEventID",scheduleEventID).getResultList();
	   }
	   
	   
	   public Series findSeriesById(Long id){
		   return entityManager.find(Series.class, id);		   
	   }
	   public SeriesGroup findSeriesGroupById(Long id){
		   return entityManager.find(SeriesGroup.class, id);		   
	   }
	   public List<Series> findAllSeries(SearchParam searchParam){
		   String queryString=searchParam.selectSeriesQuery("SELECT s FROM series s", "SELECT s FROM series s where s.name LIKE :search OR s.contractNumber LIKE :search","SELECT s FROM series s where s.contractNumber = :contractNumber");
		   queryString=searchParam.addSortByToQuery(queryString, "s");
		   TypedQuery<Series> query=entityManager.createQuery(queryString, Series.class);
		   searchParam.config(query);		   		   
		   return query.getResultList();
	   }
	   
	   public List<SeriesGroup> findSeriesGroupByTitle(String title){
		   title=title.trim();		   
		   TypedQuery<SeriesGroup> query=entityManager.createQuery("SELECT p FROM series_group p where p.title=:title", SeriesGroup.class);
		   return query.setParameter("title",title).getResultList();
	   }
	   
	   public List<SeriesGroup> findSeriesGroupByMatchingTitle(String title){
		   title=title.trim();		   
		   TypedQuery<SeriesGroup> query=entityManager.createQuery("SELECT p FROM series_group p where p.title like :title", SeriesGroup.class);
		   return query.setParameter("title",title).getResultList();
	   }
	   public SeriesGroup retrieveDefaultSeriesGroup(){
		   
		   List<SeriesGroup> matchedSeriesGroups=findSeriesGroupByTitle(SeriesGroup.DEFAULT_SERIES_GROUP_TITLE);
		   if(matchedSeriesGroups.size()>0){
			   return matchedSeriesGroups.get(0);			   
		   }
		   SeriesGroup defaultSeriesGroup=new SeriesGroup();
		   defaultSeriesGroup.setTitle(SeriesGroup.DEFAULT_SERIES_GROUP_TITLE);
		   persisSeriesGroup(defaultSeriesGroup);
		   return defaultSeriesGroup;
		   
	   }
	   public Series retrieveDefaultSeries(){
			  List<Series> series= findSeriesByName(Series.DEFAULT_SERIES_TITLE);
			  if(series.size()==0){
				  SeriesGroup seriesgroup=retrieveDefaultSeriesGroup();
				  Series defaultSerie=new Series();
				  defaultSerie.setName(Series.DEFAULT_SERIES_TITLE);
				  persisSeries(defaultSerie);
				  return defaultSerie;
			  }
			  else
				  return series.get(0);
	  }
	   public List<SeriesGroup> findAllSeriesGroup(SearchParam searchParam){
		   String queryString=searchParam.selectQuery("SELECT p FROM series_group p", "SELECT p FROM series_group p where p.title LIKE :search", "SELECT p FROM series_group p where p.title = :title");
		   queryString=searchParam.addSortByToQuery(queryString, "p");
		   TypedQuery<SeriesGroup> query=entityManager.createQuery(queryString, SeriesGroup.class);
		   searchParam.config(query);		   		   
		   return query.getResultList();
	   }
	   public List<Episode> findEpisodesByName(String name){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.name=:name", Episode.class);
		   return query.setParameter("name",name).getResultList();
	   }
	   public List<Episode> findEpisodesByTitle(String title){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.title=:title", Episode.class);
		   return query.setParameter("title",title).getResultList();
	   }
	   public List<Episode> findEpisodesByBrightcoveId(String brightcoveId){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.brightcoveId=:brightcoveId", Episode.class);
		   return query.setParameter("brightcoveId",brightcoveId).getResultList();
	   }
	   public List<Episode> findEpisodesBySeries(Series series){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.series=:series", Episode.class);
		   return query.setParameter("series",series).getResultList();
	   }

	   public List<Series> findSeriesBySeriesGroup(SeriesGroup seriesGroup){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT e FROM series e where e.seriesGroup=:seriesGroup", Series.class);
		   return query.setParameter("seriesGroup",seriesGroup).getResultList();
	   }
	   

	   
	   
	   public Episode findEpisodeById(Long id){
		   return entityManager.find(Episode.class, id);		   
	   }
	   public CuePoint findCuePoint(Long id){
		   return entityManager.find(CuePoint.class, id);		   
	   }
	   public AvailabilityWindow findAvailabilityWindowId(Long id){
		   return entityManager.find(AvailabilityWindow.class, id);
	   }
	  
	   public List<MediaCommand> findAllMediaCommand(){
		   TypedQuery<MediaCommand> query=entityManager.createQuery("SELECT m FROM media_command m", MediaCommand.class);
		   return query.getResultList();
	   }
	   public List<MediaCommand> findMediaCommandByCommand(String command){
		   TypedQuery<MediaCommand> query=entityManager.createQuery("SELECT m FROM media_command m where m.command = :command", MediaCommand.class);
		   query.setParameter("command",command);		   
		   return query.getResultList();
	   }
	   public List<MediaCommand> findMediaCommandsNotEitherOfThem(String command1, String command2){
		   TypedQuery<MediaCommand> query=entityManager.createQuery("SELECT m FROM media_command m where m.command <> :command1  and m.command <> :command2  ", MediaCommand.class);
		   query.setParameter("command1",command1);
		   query.setParameter("command2",command2);
		   return query.getResultList();
	   }
	   @Transactional
	   public void removeMediaCommandById(Long id){
		   MediaCommand mediaCommand=entityManager.find(MediaCommand.class, id);		   
		   entityManager.remove(mediaCommand);		   
	   }
	   @Transactional
	   public void persistMediaCommand(MediaCommand mediaCommand){		   		   
		   entityManager.persist(mediaCommand);		   
	   }
	   public List<Episode> findAllEpisodes(SearchParam searchParam){
		   String queryString=searchParam.selectQuery("SELECT e FROM episode e", "SELECT e FROM episode e where e.title LIKE :search OR e.materialId LIKE :search");
		   queryString=searchParam.addSortByToQuery(queryString, "e");
		   TypedQuery<Episode> query=entityManager.createQuery(queryString, Episode.class);
		   
		   searchParam.config(query);		   		   
		   return query.getResultList();		   
	   }
	   public List<Episode> findEpisodeToReport(){		   
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.brightcoveId IS NOT NULL and LENGTH(e.brightcoveId) >2", Episode.class);
		   return query.getResultList();
	   }
	   public List<Episode> findEpisodesByMatId(String matid){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.materialId LIKE :matid", Episode.class);
		   return query.setParameter("matid",matid).getResultList();
	   }
	   
	   public List<Episode> findEpisodesByCtrPrg(String ctrPrg){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.ctrPrg=:ctrPrg", Episode.class);
		   return query.setParameter("ctrPrg",ctrPrg).getResultList();
	   }
	   public List<Episode> findEpisodesByTitleAndProgramId(String title,String ctrPrg){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.title like :title and e.ctrPrg=:ctrPrg", Episode.class);
		   query.setParameter("title",title);
		   query.setParameter("ctrPrg",ctrPrg);
		   return query.getResultList();
	   }
	   public List<Episode> findEpisodesByPrimaryId(String primaryId){
		   TypedQuery<Episode> query=entityManager.createQuery("SELECT e FROM episode e where e.primaryId=:primaryId", Episode.class);
		   return query.setParameter("primaryId",primaryId).getResultList();
	   }
	   public List<Series> findSeriesByPrimaryId(String primaryId){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s where s.primaryId=:primaryId", Series.class);
		   return query.setParameter("primaryId",primaryId).getResultList();
	   }
	   public List<Series> findSeriesByContractNumber(String contractNumber){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s where s.contractNumber=:contractNumber", Series.class);
		   return query.setParameter("contractNumber",contractNumber).getResultList();
	   }
	   public Series findSingleSeriesByContractNumber(String contractNumber){
		   List<Series> matchedSeries=findSeriesByContractNumber(contractNumber);
		   if(matchedSeries.size()==0){
			   return null;
		   }
		   if(matchedSeries.size()==1){
			   return matchedSeries.get(0);			   
		   }
		   Series seletedSeries=matchedSeries.get(0);
			List<Episode> selectedEpisodes=findEpisodesBySeries(seletedSeries);
			for(int i=1;i<matchedSeries.size();i++){
				Series series=matchedSeries.get(i);
				List<Episode> episodes=findEpisodesBySeries(series);
				if(episodes.size()>selectedEpisodes.size()){
					selectedEpisodes=episodes;
					seletedSeries=series;
				}
			}
			return seletedSeries; 
	   }
	   public List<Series> findSeriesByName(String name){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s where s.name=:name", Series.class);
		   return query.setParameter("name",name).getResultList();
	   }
	   public List<Series> findSeriesByNameAndContractNumber(String name, String contractNumber){
		   TypedQuery<Series> query=entityManager.createQuery("SELECT s FROM series s where s.name like :name and s.contractNumber=:contractNumber", Series.class);
		   query.setParameter("name",name);
		   query.setParameter("contractNumber",contractNumber);
		   return query.getResultList();
	   }
	   public List<ComplianceInformation> findComplianceInformationById(String id){
		   TypedQuery<ComplianceInformation> query=entityManager.createQuery("SELECT c FROM compliance_information c where c.id=:id", ComplianceInformation.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<ProgrammeCertification> findProgrammeCertificationById(String id){
		   TypedQuery<ProgrammeCertification> query=entityManager.createQuery("SELECT p FROM programme_certification p where p.id=:id", ProgrammeCertification.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<CertificationType> findCertificationTypeById(String id){
		   TypedQuery<CertificationType> query=entityManager.createQuery("SELECT c FROM certification_type c where c.id=:id", CertificationType.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<CertificationCategory> findCertificationCategoryById(String id){
		   TypedQuery<CertificationCategory> query=entityManager.createQuery("SELECT c FROM certification_category c where c.id=:id", CertificationCategory.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<CertificationTime> findCertificationTimeById(String id){
		   TypedQuery<CertificationTime> query=entityManager.createQuery("SELECT c FROM certification_time c where c.id=:id", CertificationTime.class);
		   return query.setParameter("id",id).getResultList();
	   }
	   public List<BCNotification> findAllBCNotification(){
		   TypedQuery<BCNotification> query=entityManager.createQuery("SELECT b FROM bc_notification b", BCNotification.class);
		   return query.getResultList();
	   }
	   public List<BCNotification> findBCNotificationByJobId(String jobid){
		   TypedQuery<BCNotification> query=entityManager.createQuery("SELECT b FROM bc_notification b where b.jobId=:jobId", BCNotification.class);
		   return query.setParameter("jobId",jobid).getResultList();
	   }
	   
	   
	   public void saveTags(String tags[]){
		   
		   if(tags==null||tags.length==0){
			   return;
		   }
		   for(int i=0;i<tags.length;i++){
			   saveTag(tags[i]);
		   }
		   
	   }
      
	    public void saveClientDevices(String devices[]){
		   
		   if(devices==null||devices.length==0){
			   return;
		   }
		   for(int i=0;i<devices.length;i++){
			   saveClientDevice(devices[i]);
		   }
		   
	   }
	   
	   
	   @Transactional
	   public void saveTag(String tag){
		   if(tag==null){
			   return;			   
		   }
		   tag=tag.trim();
		   if(tag.length()<2){
			   return;			   
		   }
		   tag=tag.toLowerCase();
		   TypedQuery<MediaTag> query=entityManager.createQuery("SELECT t FROM tag t where t.name=:name", MediaTag.class);
		   List<MediaTag> tags=query.setParameter("name",tag).getResultList();
		   if(tags.size()==0){
			   MediaTag mediaTag=new MediaTag();
			   mediaTag.setName(tag);
			   persisMediaTag(mediaTag);			   
		   }
	   }
	   @Transactional
	   public void saveClientDevice(String devicename){
		   if(devicename==null){
			   return;			   
		   }
		   devicename=devicename.trim();
		   if(devicename.length()<2){
			   return;			   
		   }
		   devicename=devicename.toLowerCase();
		   TypedQuery<ClientDevice> query=entityManager.createQuery("SELECT c FROM client_device c where c.name=:name", ClientDevice.class);
		   List<ClientDevice> devices=query.setParameter("name",devicename).getResultList();
		   if(devices.size()==0){
			   ClientDevice device=new ClientDevice();
			   device.setName(devicename);
			   persisClientDevice(device);			   
		   }
	   }
	   
	   public String[] getAllTags(){
		   TypedQuery<MediaTag> query=entityManager.createQuery("SELECT t FROM tag t order by t.name", MediaTag.class);
		   List<MediaTag> tags=query.getResultList();
		   if(tags.size()==0){
			   return null;
		   }
		   String[] ret=new String[tags.size()];
		   for(int i=0;i<ret.length;i++){
			   ret[i]=tags.get(i).getName();
		   }
		   return ret;		   
	   }
	   public List<ClientDevice> getAllClientDevices(){
		   TypedQuery<ClientDevice> query=entityManager.createQuery("SELECT c FROM client_device c order by c.name", ClientDevice.class);
		   List<ClientDevice> devices=query.getResultList();		   
		   return devices;		   
	   }
	   @Transactional
	   public MediaTag removeTag(String tag){
		   TypedQuery<MediaTag> query=entityManager.createQuery("SELECT t FROM tag t where t.name=:name", MediaTag.class);
		   query.setParameter("name",tag);
		   List<MediaTag> tags=query.getResultList();
		   MediaTag ret=null;
		   for(MediaTag t:tags){
			   ret=t;
			   entityManager.remove(t);
		   }
		   return ret;
	   }
	   @Transactional
	   public ClientDevice removeClientDevice(String devicename){
		   TypedQuery<ClientDevice> query=entityManager.createQuery("SELECT c FROM client_device c where c.name=:name", ClientDevice.class);
		   query.setParameter("name",devicename);
		   List<ClientDevice> devices=query.getResultList();
		   ClientDevice ret=null;
		   for(ClientDevice t:devices){
			   ret=t;
			   entityManager.remove(t);
		   }
		   return ret;
	   }
	   
	        
	   @Transactional
       public void updateUser(BoxUser user){
    	   entityManager.merge(user);
       }
	   @Transactional
       public void createUser(BoxUser user){
    	   entityManager.persist(user);
       }
	   @Transactional
	   public void deleteByUsername(String username){
		   BoxUser user= entityManager.find(BoxUser.class, username);
		   if(user!=null){
			   entityManager.remove(user);
		   }
	   }
       public List<BoxUser> findUserByUsername(String username){
    	   TypedQuery<BoxUser> query=entityManager.createQuery("SELECT u FROM user u where u.username=:username", BoxUser.class);    	   
		   return query.setParameter("username",username).getResultList();		   
	   }
       public BoxUser findAUserByUsername(String username){
    	   List<BoxUser> matchedusers=findUserByUsername(username);
           if(matchedusers.size()==0){
       			return null;
       	   }
           else{
        	   return matchedusers.get(0);
           }
       }
       
       public List<BoxUser> findUserByClientId(String clientId){
    	   TypedQuery<BoxUser> query=entityManager.createQuery("SELECT u FROM user u where u.clientId=:clientId", BoxUser.class);    	   
		   return query.setParameter("clientId",clientId).getResultList();
       }
	   public List<BoxUser> findAllUsers(){		   
    	   TypedQuery<BoxUser> query=entityManager.createQuery("SELECT u FROM user u", BoxUser.class);
		   List<BoxUser> users=query.getResultList();
		   return users;
       }
	   
	   @Transactional
		public void createBoxUserRole(BoxUserRole boxUserRole){
			if(boxUserRole==null||boxUserRole.getRolename().trim().length()==0){
				return;
			}
			BoxUserRole foundRole=entityManager.find(BoxUserRole.class, boxUserRole.getRolename());
			if(foundRole!=null){
				logger.info("role is already in db so will bot be created");
				return;
			}
			else{
				logger.info("is going to create a new role:"+boxUserRole.getRolename());
			}
			entityManager.persist(boxUserRole);		
		}
	   @Transactional
	   public void deleteAllBoxUserRole(){
		   List<BoxUserRole> userRoles=findAllUserRoles();
		   for(BoxUserRole userole:userRoles){
			   entityManager.remove(userole);
		   }
	   }
	   
	   @Transactional
	   public void updateUserRole(BoxUserRole userrole){
		   entityManager.merge(userrole);
	   }
	   

	   public List<BoxUserRole> findAllUserRoles(){		   
    	   TypedQuery<BoxUserRole> query=entityManager.createQuery("SELECT u FROM user_role u", BoxUserRole.class);
		   List<BoxUserRole> roles=query.getResultList();
		   return roles;
       }
	   
	   @Transactional
       public void markVideoTranscodeAsFailed(String bcVideoId){
		   	List<Episode> matchedEpisodes=findEpisodesByBrightcoveId(bcVideoId);
		   	for(Episode ep:matchedEpisodes){
		   		EpisodeStatus epstatus=ep.getEpisodeStatus();
		   		epstatus.setVideoStatus(VideoStatus.TRANSCODE_FAILED);
		   	}		   
       }
	   @Transactional
       public void markVideoTranscodeAsComplete(String bcVideoId){
		   	List<Episode> matchedEpisodes=findEpisodesByBrightcoveId(bcVideoId);
		   	for(Episode ep:matchedEpisodes){
		   		EpisodeStatus epstatus=ep.getEpisodeStatus();
		   		epstatus.setVideoStatus(VideoStatus.TRANSCODE_COMPLETE);
		   	}		   
       }
	   @Transactional
       public void markTranscodeAsCompleteByEpisodeId(Long episodeId){
		   	Episode episode=findEpisodeById(episodeId);
		   	episode.getEpisodeStatus().setVideoStatus(VideoStatus.TRANSCODE_COMPLETE);
		   	persistEpisodeStatus(episode.getEpisodeStatus());  	   
       }
	   
	   @Transactional
	   public void updateCue(uk.co.boxnetwork.data.CuePoint cuePoint){
		   CuePoint cue=findCuePoint(cuePoint.getId());
		   cuePoint.update(cue);
		   persist(cue);
		   markMetadataChanged(cue.getEpisode());
	   }
	   @Transactional
	   public void updateAvailabilityWindow(uk.co.boxnetwork.data.AvailabilityWindow availabilityWindow){
		   AvailabilityWindow avWindow=findAvailabilityWindowId(availabilityWindow.getId());
		   availabilityWindow.update(avWindow);
		   entityManager.merge(avWindow);		   
		   markMetadataChanged(avWindow.getEpisode());
	   }
	   
	   @Transactional
	   public void markMetadataChanged(Episode episode){
		   EpisodeStatus episodeStatus=episode.getEpisodeStatus();
		   if(GenericUtilities.isNotAValidId(episode.getBrightcoveId())){
			   if(episodeStatus.getMetadataStatus()!=MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER){
				   episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER);
				   persistEpisodeStatus(episodeStatus);
			   }			   
		   }
		   else if(episodeStatus.getMetadataStatus()==MetadataStatus.PUBLISHED){
			   episodeStatus.setMetadataStatus(MetadataStatus.NEEDS_TO_PUBLISH_CHANGES);
			   persistEpisodeStatus(episodeStatus);
		   }
	   }
	  
	   
	   
	   @Transactional
	   public void replaceAvailabilityWindow(Long episodeid, Date from, Date to){
		   Episode episode=findEpisodeById(episodeid);
		   if(episode.getAvailabilities()!=null && episode.getAvailabilities().size()>0){
				for(AvailabilityWindow availability:episode.getAvailabilities()){
					entityManager.remove(availability);
				}
				episode.clearAvailabilityWindows();
			}
			AvailabilityWindow availabilityWindow=new AvailabilityWindow();
			availabilityWindow.setStart(from.getTime());
			availabilityWindow.setEnd(to.getTime());
			availabilityWindow.setEpisode(episode);
			entityManager.persist(availabilityWindow);
			episode.addAvailabilityWindow(availabilityWindow);
			markMetadataChanged(episode);
			persist(episode);
	   }
	   
	   @Transactional
	   public void setEpisodeImage(Long id, String imageURL){
		   Episode episode=findEpisodeById(id);
		   episode.setImageURL(imageURL);
		   markMetadataChanged(episode);
		   persist(episode);
	   }
	   @Transactional
	   public void setSeriesImage(Long id, String imageURL){
		   Series series=findSeriesById(id);
		   series.setImageURL(imageURL);		   
		   persisSeries(series);
	   }
	   @Transactional
	   public void setSeriesGroupImage(Long id, String imageURL){
		   SeriesGroup seriesgroup=findSeriesGroupById(id);
		   seriesgroup.setImageURL(imageURL);		   
		   persisSeriesGroup(seriesgroup);
	   }
	   public void report(DataReport report){
		   TypedQuery<Long> queryLong=entityManager.createQuery("SELECT count(e) FROM episode e", Long.class);
	       report.setNumberOfEpisodes(queryLong.getSingleResult());
	       
	       TypedQuery<Double> queryDouble=entityManager.createQuery(" select SUM(e.durationUploaded)/3600 from episode e", Double.class);
	       report.setNumberOfHoursTotal(queryDouble.getSingleResult());
	       
	       queryDouble=entityManager.createQuery(" select SUM(e.durationUploaded)/3600 from episode e where e.episodeStatus.publishedStatus = uk.co.boxnetwork.model.PublishedStatus.ACTIVE", Double.class);
	       report.setNumberOfHoursActive(queryDouble.getSingleResult());
	       
	   }
	   public List<AdvertisementRule> findAllAdvertisementRule(SearchParam searchParam){
		   String queryString="SELECT a FROM advertisement_rule a";
		   TypedQuery<AdvertisementRule> query=entityManager.createQuery(queryString, AdvertisementRule.class);
		   return query.getResultList();		   
	   }
	   
	   public AdvertisementRule findAdvertisementRuleById(Long id){
		   return entityManager.find(AdvertisementRule.class, id);		   
	   }
	   @Transactional	   
	   public void persis(AdvertisementRule advertisementRule){
   	    Date lastModifiedAt=new Date();
   	    advertisementRule.setLastModifiedAt(lastModifiedAt.getTime());
   	    entityManager.persist(advertisementRule);
      }
	   @Transactional
	   public void merge(AdvertisementRule advertisementRule){
	   	    Date lastModifiedAt=new Date();
	   	    advertisementRule.setLastModifiedAt(lastModifiedAt.getTime());
	   	    entityManager.merge(advertisementRule);
	   }
	   @Transactional
	   public AdvertisementRule removeAdvertisementRuleById(Long ruleId){
		   AdvertisementRule rule=findAdvertisementRuleById(ruleId);
		   if(rule==null){
			   return null;   
		   }		   	   	    
		   logger.info("deleting the rule item:"+rule);
	   	   entityManager.remove(rule);
	   	   return rule;
	   }
	   
	   
	   
	   
}
