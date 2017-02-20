package uk.co.boxnetwork.components;
import java.io.CharArrayReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import uk.co.boxnetwork.data.C4Metadata;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.util.GenericUtilities;


@Component
public class C4ScheduleSoapParser {
	private static final Logger logger=LoggerFactory.getLogger(C4ScheduleSoapParser.class);
	private String schedulepath[]={"GetSchedulesByParamsResponse","GetSchedulesByParamsResult","ScheduleEventGroups"};
	
	@Autowired
	private XMLDocumentParser xmlparser;
		
	public C4Metadata parse(String scheduleDocument) throws DocumentException{
		SAXReader reader = new SAXReader();		
			Document document=reader.read(new CharArrayReader((scheduleDocument.toCharArray())));
			return parse(document);
	}
	public C4Metadata parse(Document document){
		C4Metadata c4Medata=new C4Metadata();
		
		Element shceduleEvenGroups=xmlparser.getElementByPaths(document,schedulepath);
		for ( Iterator<Element> i = shceduleEvenGroups.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();				
			processScheduleEventGroup(elem,c4Medata);				    
	    }
		return c4Medata;
		
	}	
	public void processScheduleEventGroup(Element groupElement,C4Metadata c4Medata){
		
		String groupdName=xmlparser.getElementContentByPaths(groupElement, "Name");
		c4Medata.setGroupdName(groupdName);
		String groupdId=xmlparser.getElementContentByPaths(groupElement, "ScheduleEventGroupID");
		c4Medata.setGroupdId(groupdId);
		
		for ( Iterator<Element> i = groupElement.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("ScheduleEvents")){
				processScheduleEvents(elem, c4Medata);
			}
		}
			
		
	}
	private void processScheduleEvents(Element scheduleEvents,C4Metadata c4Medata){
		for ( Iterator<Element> i = scheduleEvents.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("BS.ScheduleEvent")){				
				processScheduleEvent(elem,c4Medata);
			}
		}
	}
	
	
	private void processScheduleEvent(Element scheduleEventElement,C4Metadata c4Medata){
		ScheduleEvent scheduleEvent=new ScheduleEvent();
		
		scheduleEvent.setGroupId(c4Medata.getGroupdId());
		
		
		scheduleEvent.setGroupName(c4Medata.getGroupdName());
		
		String scheduleEventId=xmlparser.getElementContentByPaths(scheduleEventElement, "ScheduleEventID");		
		scheduleEvent.setScheduleEventID(scheduleEventId);
		
		
		String asset_id=xmlparser.getElementContentByPaths(scheduleEventElement, "Asset/AssetID");										
		if(asset_id!=null && (!"0".equals(asset_id))){			
			scheduleEvent.setAssetId(asset_id);						
		}
		String asset_name=xmlparser.getElementContentByPaths(scheduleEventElement, "Asset/AssetName");		
		scheduleEvent.setAssetName(asset_name);
		
		String asset_type=xmlparser.getElementContentByPaths(scheduleEventElement, "Asset/AssetType");
		scheduleEvent.setAssetType(asset_type);
		
		
		String distributionId=xmlparser.getElementContentByPaths(scheduleEventElement, "Distribution/DistributionID");
		scheduleEvent.setDistributionId(distributionId);
		
		String distributionType=xmlparser.getElementContentByPaths(scheduleEventElement, "Distribution/DistributionType");
		scheduleEvent.setDistributionType(distributionType);
			
		String scheduleType=xmlparser.getElementContentByPaths(scheduleEventElement, "ScheduleEventType");
		scheduleEvent.setScheduleEventType(scheduleType);
		
		String scheduleDuration=xmlparser.getElementContentByPaths(scheduleEventElement, "Duration/Time");
		scheduleEvent.setDuration(scheduleDuration);
		
		parseScheduleDayTime(scheduleEventElement,c4Medata,scheduleEvent);
		    
		for ( Iterator<Element> i = scheduleEventElement.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("Episode")){				
				processEpisode(elem,c4Medata,scheduleEvent);
			}
		}
		
		
		c4Medata.addScheduleEvent(scheduleEvent);
		
			
	}
	private void processEpisode(Element episodeElement,C4Metadata c4Medata,ScheduleEvent scheduleEvent){
		Episode episode=new Episode();
		
		String assetId=xmlparser.getElementContentByPaths(episodeElement, "Id/LegacyId/Id");
		episode.setAssetId(assetId);
		
		String primaryId=xmlparser.getElementContentByPaths(episodeElement, "Id/PrimaryId");
		episode.setPrimaryId(primaryId);
        
		String name=xmlparser.getElementContentByPaths(episodeElement, "Name");
		episode.setName(name);
		
		String ctrPrg=xmlparser.getElementContentByPaths(episodeElement, "CtrPrg");
		episode.setCtrPrg(ctrPrg);
		
		String numberString=xmlparser.getElementContentByPaths(episodeElement, "Number");
		if(numberString!=null && numberString.trim().length()>0){
		  try{	
			  int number=Integer.parseInt(numberString);
			  episode.setNumber(number);
		  }
		  catch(Exception e){
			  logger.error(e+" while parser Number numberString=["+numberString+"]",e);
		  }
			
		}
		
		String title=xmlparser.getElementContentByPaths(episodeElement, "Title");
		episode.setTitle(title);
		
		for ( Iterator<Element> i = episodeElement.elementIterator(); i.hasNext(); ) {
			Element elem = (Element) i.next();
			if(elem.getName().equals("Series")){				
				processSeries(elem,c4Medata,scheduleEvent,episode);
			}
		}
		
		if(name==null && primaryId==null && GenericUtilities.isNotAValidId(primaryId) && title==null && ctrPrg==null){
			logger.warn("There is not episode information ignoring............... this episode");
		}
		else{			
				scheduleEvent.setEpisode(episode);
		}
		
	}
	private void processSeries(Element seriesElement,C4Metadata c4Medata,ScheduleEvent scheduleEvent,Episode episode){
		Series series=new Series();		
		
		String assetId=xmlparser.getElementContentByPaths(seriesElement, "Id/LegacyId/Id");
		series.setAssetId(assetId);
		
		String primaryId=xmlparser.getElementContentByPaths(seriesElement, "Id/PrimaryId");
		series.setPrimaryId(primaryId);
		
		String name=xmlparser.getElementContentByPaths(seriesElement, "Name");
		series.setName(name);

		String contractNumber=xmlparser.getElementContentByPaths(seriesElement, "ContractNumber");
		series.setContractNumber(contractNumber);
		
		episode.setSeries(series);		
	}
	private void parseScheduleDayTime(Element scheduleEventElement,C4Metadata c4Medata,ScheduleEvent scheduleEvent){
		
		String scheduleDayString=xmlparser.getElementContentByPaths(scheduleEventElement, "Start/ScheduleDay");
		if(scheduleDayString!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date scheduleDay = sdf.parse(scheduleDayString);
				scheduleEvent.setScheduleDay(scheduleDay);
			} catch (ParseException e) {
				logger.info("The start date is not in correct format:scheduleDayString=["+scheduleDayString+"]");				
			}
		}
		
		
		String scheduleTimeString=xmlparser.getElementContentByPaths(scheduleEventElement, "Start/Timestamp");
		if(scheduleTimeString!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			try {
				Date scheduleTimestamp = sdf.parse(scheduleTimeString);				
				 scheduleEvent.setScheduleTimestamp(scheduleTimestamp);
				 
				 
			} catch (ParseException e) {
				logger.info("The start date timestamp is not in correct format  scheduleTimeString=["+scheduleTimeString+"]");
				
			}
		}
		
		
	}
	
	
	
}
