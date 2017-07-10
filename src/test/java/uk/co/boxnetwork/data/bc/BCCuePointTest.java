package uk.co.boxnetwork.data.bc;

import org.junit.Test;

import uk.co.boxnetwork.data.CuepointMetadata;

import static org.junit.Assert.*;

public class BCCuePointTest {
	
	
	@Test
	public void testPublishMetadataToBCShouldContainJson(){
		uk.co.boxnetwork.data.CuePoint cupointdata=new uk.co.boxnetwork.data.CuePoint();
		BCCuePoint cuepoint=new BCCuePoint(cupointdata,"2",null);		
		assertEquals("unexpected json value","{\"numberOfAds\":2}",cuepoint.getMetadata());
	}
	
	
	@Test
	public void testArtistAndTrackShouldBeinJson(){
		uk.co.boxnetwork.data.CuePoint cupointdata=new uk.co.boxnetwork.data.CuePoint();
		 CuepointMetadata metadata=new CuepointMetadata();
		 metadata.setArtist("dilshat");
		 metadata.setTrack("music");
		 cupointdata.setMetadata(metadata);
		BCCuePoint cuepoint=new BCCuePoint(cupointdata,"2",null);
		System.out.println(cuepoint.getMetadata());
		
	}
	
			
}
