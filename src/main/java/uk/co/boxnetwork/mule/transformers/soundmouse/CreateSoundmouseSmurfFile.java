package uk.co.boxnetwork.mule.transformers.soundmouse;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.soundmouse.SoundMouseData;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MediaCommand;

public class CreateSoundmouseSmurfFile  extends AbstractMessageTransformer{
	static final protected Logger logger=LoggerFactory.getLogger(EpisodeIdToSoundMouseHeader.class);
	@Autowired
	MetadataService metadataService;
	
	@Autowired
	MetadataMaintainanceService metadataMaintainanceService;
	
	
	@Autowired
	AppConfig appConfig;

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		
		Object obj=message.getPayload();
		if(obj instanceof MediaCommand){
			return processMediaCommand((MediaCommand)obj,message);
		}		
		return obj;
	}
	private Object processMediaCommand(MediaCommand mediaCommand,MuleMessage message){
		if(MediaCommand.DELIVER_SOUND_MOUSE_SMURF_FILE.equals(mediaCommand.getCommand())){			    	
		     try {
				SoundMouseData soumdMouseData=metadataService.createSoundMouseSmurfFile();
				mediaCommand.setFilename(soumdMouseData.getSmurfFilename());
				mediaCommand.setFilepath(soumdMouseData.getSmurfFilePath());
				return mediaCommand;
			} catch (Exception e) {
				logger.error(e+ "while creating  the soundmouse smurf file",e);
				return e.toString();
			}
			
	    }
		else
			return "wrong mediacommand type:"+mediaCommand.getCommand();
	}
	

}
