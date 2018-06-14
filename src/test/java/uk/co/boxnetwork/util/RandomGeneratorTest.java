package uk.co.boxnetwork.util;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.co.boxnetwork.components.RandomStringGenerator;

public class RandomGeneratorTest {
			
			@Test
	        public void testGenerator(){
			
				
				RandomStringGenerator generator=new RandomStringGenerator();
				System.out.println(generator.nextString(10));
				
				
			}
		
			@Test
			
			public void isLegalSortByValue(){
				String template="^.*[^a-zA-Z0-9.].*$";
				
				String t1="dilshat";
				String t2="dilshatHewzulla1234345340";
				String t3="dilshat.hewzulla";
				
				assertFalse(t1.matches(template));
				assertFalse(t2.matches(template));
				assertFalse(t3.matches(template));
				
				
				String i1="dilshat\\dddd";
				String i2="dilshat/dddd";
				String i3="dilshat;dddd";
				assertTrue(i1.matches(template));
				assertTrue(i2.matches(template));
				assertTrue(i3.matches(template));
				
				
			}
}
