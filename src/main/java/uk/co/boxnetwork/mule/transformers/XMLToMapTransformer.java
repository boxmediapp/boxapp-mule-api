package uk.co.boxnetwork.mule.transformers;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.xml.sax.InputSource;

import uk.co.boxnetwork.mule.util.XmlToMapParser;






public class XMLToMapTransformer extends AbstractMessageTransformer{
	private XmlToMapParser xmlToMapParser;
	public void setXmlToMapParser(XmlToMapParser xmlToMapParser) {
		this.xmlToMapParser = xmlToMapParser;
	}
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {		
		try {
			SAXReader reader = new SAXReader();
			Object payload=message.getPayload();
			Document document=null;
			if(payload instanceof InputStream){
				document=reader.read((InputStream)payload);				
			}
			else if(payload instanceof InputSource){				 
				document=reader.read((InputSource)payload);
			}
			else if(payload instanceof Reader){
				document=reader.read((Reader)payload);
			}
			else if(payload instanceof byte[]){
				document=reader.read(new ByteArrayInputStream((byte[])payload));
			}
			else if(payload instanceof String){
				document=reader.read(new CharArrayReader(((String)payload).toCharArray()));
			}
			else
				throw new TransformerException(this,new Exception("payload is not the expected type:"+payload.getClass()));
			return xmlToMapParser.toMap(document);
	        
	    } catch (Exception e) {
	        System.out.println("Error: " + e);
	        e.printStackTrace();
	    }
	    return null;
	}

}
