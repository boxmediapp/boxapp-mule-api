package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Image;
import uk.co.boxnetwork.model.ImageSet;
import uk.co.boxnetwork.model.OperationLogType;
import uk.co.boxnetwork.model.OperationLogs;

@Service
public class OperationalLogRepository {
private static final Logger logger=LoggerFactory.getLogger(OperationalLogRepository.class);
	
	
	@Autowired
	private AppConfig appConfig;	

	@Autowired	
	private EntityManager entityManager;

	
	 
	
	public uk.co.boxnetwork.data.image.Image findDeletedImageById(Long id){				
		List<OperationLogs>  matchedrecords=OperationLogs.findDeletedImageLogs(entityManager,id,null);		
		if(matchedrecords.size()==0){
			return null;
		}
		return matchedrecords.get(0).toImage();		
	}	
	public List<uk.co.boxnetwork.data.image.Image> findDeletedImages (Date deletedOnFrom){
		 List<uk.co.boxnetwork.data.image.Image> ret=new ArrayList<uk.co.boxnetwork.data.image.Image>();		 
		List<OperationLogs>  matchedrecords=OperationLogs.findDeletedImageLogs(entityManager,null,deletedOnFrom);
		for(OperationLogs logrecord:matchedrecords){
			ret.add(logrecord.toImage());
		}
		return ret;		
	}
}
