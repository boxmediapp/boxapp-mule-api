package uk.co.boxnetwork.mule.transformers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.payload.MultipleJsonInputStream;



public class JoinAndDownloadMultipleJSonTransformer extends JoinMultipleJsonStreamTransformer{
	private static final Logger logger=LoggerFactory.getLogger(JoinAndDownloadMultipleJSonTransformer.class);
	private String dataDirectory;
	
	public String getDataDirectory() {
		return dataDirectory;
	}

	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	private String filenameprefix="img_int";
	
	@Override
	public Object createMultiplePayloadsInputStream(CopyOnWriteArrayList payload,String outputEncoding,MuleMessage message) throws TransformerException{
		MultipleJsonInputStream inputstream=new MultipleJsonInputStream(getInputStreamListFromCopyOnWriteArrayList(payload,outputEncoding));
		String uuid=message.getProperty(LogMessageTransformer.EVENT_NOTIFICATION_SESSION_ID, PropertyScope.SESSION);
		logger.info("multiple inputstream is combined:"+uuid);
		return createPayloadForInputStream(inputstream,outputEncoding,message);		
	}
	@Override
	public Object createPayloadForInputStream(InputStream payload,String outputEncoding,MuleMessage message) throws TransformerException{
		if(dataDirectory==null){
			Message errorMEssage = MessageFactory.createStaticMessage("data directory parameter is not set");
            throw new TransformerException(errorMEssage, this);            			
		}
		String uuid=message.getProperty(LogMessageTransformer.EVENT_NOTIFICATION_SESSION_ID, PropertyScope.SESSION);
		
		if(uuid==null){
			logger.warn("session id should be repsent, generating new id");
			uuid=UUID.randomUUID().toString();
		}
		
		String filepath=dataDirectory+File.separator+filenameprefix+uuid+".json";
		try{
			OutputStream fileout=new FileOutputStream(filepath);
			try{
				
				IOUtils.copyLarge(payload, fileout);
				fileout.close();
				payload.close();
				FileInputStream fileinpit=new FileInputStream(filepath);
				logger.info("Returning the input for file name filepath=["+filepath+"]");			
				return fileinpit;
			
			
			}
			finally{
				fileout.close();
			}
		}
		catch(Exception e){
			Message errorMEssage = MessageFactory.createStaticMessage("Error in creating the file input:"+e);
			logger.error("could not create the file input stream filepath=["+filepath+"]",e);
            throw new TransformerException(errorMEssage, this,e);  
		}		}
}
