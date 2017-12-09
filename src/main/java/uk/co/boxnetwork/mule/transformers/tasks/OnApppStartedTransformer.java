package uk.co.boxnetwork.mule.transformers.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.springframework.beans.factory.annotation.Autowired;



import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.util.GenericUtilities;

public class OnApppStartedTransformer extends AbstractMessageTransformer{
	ExecutorService pool = Executors.newFixedThreadPool(1);
	
	@Autowired
	private MetadataMaintainanceService metadataMaintainanceService;


	@Autowired
	MetadataService metataService;

	Future<MediaCommand> commandExecution;
    boolean running=true;
    class MediaExecutionThreads implements Callable<MediaCommand>{

    	@Override
    	public MediaCommand call() throws Exception {
    		MediaCommand result=new MediaCommand();
    		while(running){
    			Thread.sleep(60000);
    			
    			metataService.processOtherMediaCommands();
    		}    		
    		return result;
    	}    	
    }
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		logger.info("Application initialization");
		metadataMaintainanceService.syncAppConfigWithDatabase();
		metadataMaintainanceService.syncS3VideoItems();
		metadataMaintainanceService.createBoxChannels();
		
		
		//metadataMaintainanceService.setLastModoifiedOfBoxEpisodes();
		//metadataMaintainanceService.importBoxEpisodesFromEpsiodes();
		
		//metadataMaintainanceService.updateSeriesNextEpisodeNumber();
		//metadataMaintainanceService.calculateUploadedDuration();
		//metadataMaintainanceService.checkAllRecordsConsistency();
		//metadataMaintainanceService.fixTxChannel();
		//metadataMaintainanceService.deleteAllTasls();
		//metadataMaintainanceService.fixEpisodeStatusIfEmpty();
		//metadataMaintainanceService.replaceIngestProfiles("high-resolution","box-plus-network-1080p-profile");			
		//metadataMaintainanceService.removeOrphantSeriesGroup();
		//metadataMaintainanceService.updateAllPublishedStatys();
		
		//metadataMaintainanceService.setAvailableWindowForAll(Calendar.getInstance().getTime(),GenericUtilities.nextYearDate());
		//metadataMaintainanceService.autoPublishChangesToBrightcove();
		commandExecution=pool.submit(new MediaExecutionThreads());
		return message.getPayload();
	}
	
	
}

