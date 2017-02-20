package uk.co.boxnetwork.mule.transformers;

import java.io.IOException;
import java.io.InputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageTransformer;


public class FileUnzipTransformer extends   AbstractMessageTransformer{

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		Object payload=message.getPayload();
		if(payload==null){
		   logger.warn(" Input payload is null");
			return payload;
		}			
		
		else if(payload instanceof String){
			logger.info("executed one single branch:the type is string:"+payload);
			return payload;
		}
		else if(payload instanceof InputStream){
			return createUnzipStream((InputStream)payload,message);			
		}
		
		return payload;
	}
	private InputStream createUnzipStream(InputStream in,MuleMessage message) throws TransformerException{
		ZipInputStream zis = new ZipInputStream(in);
		ZipEntry zentry;
		try {
			zentry = zis.getNextEntry();
		} catch (IOException e) {
			Message errorMEssage = MessageFactory.createStaticMessage("failed to get the zip entry");
	        throw new TransformerException(errorMEssage, this);
		}
		if(zentry!=null){
			message.setInvocationProperty("zippedFilename", "ddd"+zentry.getName());
					
		}
		else
			message.setInvocationProperty("zippedFilename", "unknown.txt");
		return zis;
		
	}
	

}