package uk.co.boxnetwork.mule.transformers.soundmouse;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.mail.handlers.message_rfc822;

import uk.co.boxnetwork.components.MetadataMaintainanceService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.util.GenericUtilities;

public class EpisodeIdToSoundMouseHeader   extends AbstractMessageTransformer{
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
		if(MediaCommand.DELIVER_SOUND_MOUSE_HEADER_FILE.equals(mediaCommand.getCommand())){
				String filepath="/data/"+mediaCommand.getFilename();
				mediaCommand.setFilepath(filepath);
			try{
					String soudnmouseContent=metadataService.getSoundMouseHeaderFile(mediaCommand.getEpisodeid());								
					Writer writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
					writer.write(soudnmouseContent);
					writer.close();					
					return mediaCommand;				
			}
			catch(Exception e){
				logger.error(e+ "while getting the soundmouse header file",e);
				return e.toString();
			}
		}
		else
			return "wrong type";
	}
	

}
