package uk.co.boxnetwork.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import static org.junit.Assert.*;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.bc.BCVideoData;
import uk.co.boxnetwork.data.bc.BCVideoSource;
import uk.co.boxnetwork.mule.components.LoadResourceAsInputStream;
import uk.co.boxnetwork.util.GenericUtilities;


public class TestBCVideoService {

	@Test
	public void jsonToBCVideoDataShouldReturnBCVideoData() throws IOException{
		InputStream ins=LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream("data/bc/bc-video.json");
		String jsonText=IOUtils.toString(ins);
		//System.out.println(jsonText);
		BCVideoService videoService=new BCVideoService();
		BCVideoData videoData=videoService.jsonToBCVideoData(jsonText);
		assertEquals("drm ", "True", videoData.getCustom_fields().getDrm());
	}
	@Test
	public void jsonToBCVideoSourceDataShouldReturnBCVideoDataSource() throws IOException{
		InputStream ins=LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream("data/bc/bc-video-source.json");
		String videoInJson=IOUtils.toString(ins);
		//System.out.println(jsonText);
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GenericUtilities.createObjectMapper();
							
		
		BCVideoSource[] viodesources=objectMapper.readValue(videoInJson, BCVideoSource[].class);
		
	}
	@Test
	public void testConvertInvalidJson(){
		String message="{id=5074307031001, entity=ASSET, status=SUCCESS, error=null, action=CREATE, referenceId=}";
		message=message.replaceAll("\\{", "{\"");
		message=message.replaceAll("\\}", "\"}");
		message=message.replaceAll("\\=", "\":\"");
		message=message.replaceAll("\\, ", "\", \"");
		System.out.println("result:"+message);
	}
	
	@Test 
	public void testEpgTitle(){
		
		Pattern p = Pattern.compile("S\\d\\d E\\d\\d .*");
		Matcher m = p.matcher("S01 E06 This ishjkhfdsjaklfdsjk");
	    assertTrue(m.matches());
	    m = p.matcher("This ishjkhfdsjaklfdsjk");
	    assertFalse(m.matches());
	    
	    
	    
	    

		
		
		
	}
}
