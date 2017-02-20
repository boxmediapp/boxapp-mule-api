package uk.co.boxnetwork.components;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class TestMetadataMaintenanance {

	@Test
	public void testSoundFileNameFormat(){
		Date today=new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("ddMMyyHHmmss'CTINT'ddMMyyyy");
		System.out.println("::"+formatter.format(today));
		
	}
}
