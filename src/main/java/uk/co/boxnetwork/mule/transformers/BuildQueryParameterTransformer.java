package uk.co.boxnetwork.mule.transformers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.util.IOUtils;


public class BuildQueryParameterTransformer extends AbstractMessageTransformer{
		    
	
		@Override
		public Object transformMessage(MuleMessage message, String outputEncoding)
				throws TransformerException {
			Object payload=message.getPayload();
			StringBuilder builder=new StringBuilder();
			Map<String,String>queryparams=message.getInboundProperty("http.query.params");
			
			
				//payload['assettype']; assetid=payload['id']; if(assettype!=null && assettype.length()>0){ queryparameter= "assettype="+assettype; } if(assetid!=null && assetid.length()>0){ if(queryparameter.length()>0){ queryparameter=queryparameter+"&"; } queryparameter= queryparameter+"id="+assetid; } flowVars['queryparameter']=queryparameter;
			return payload;
		     
		}
	}
