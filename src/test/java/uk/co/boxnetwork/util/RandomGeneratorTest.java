package uk.co.boxnetwork.util;

import org.junit.Test;

import uk.co.boxnetwork.components.RandomStringGenerator;

public class RandomGeneratorTest {
			
			@Test
	        public void testGenerator(){
			
				
				RandomStringGenerator generator=new RandomStringGenerator();
				System.out.println(generator.nextString(10));
				
				
			}
}
