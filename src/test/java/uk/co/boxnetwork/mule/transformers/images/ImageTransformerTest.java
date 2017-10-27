package uk.co.boxnetwork.mule.transformers.images;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.co.boxnetwork.data.image.Image;
import uk.co.boxnetwork.mule.components.LoadResourceAsInputStream;

public class ImageTransformerTest {
	static final protected Logger logger=LoggerFactory.getLogger(ImageTransformerTest.class);
	@Test
	public void JsonTransformShouldReturnImage() throws JsonParseException, JsonMappingException, IOException{
		InputStream ins=LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream("data/image/image.json");
		String jsonText=IOUtils.toString(ins);
		
		
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();								
		   objectMapper.setSerializationInclusion(Include.NON_NULL);
		   Image image=null;		   
			   image = objectMapper.readValue(jsonText, Image.class);
				
	}
}
