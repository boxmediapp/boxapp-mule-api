package uk.co.boxnetwork.components;

import java.util.Locale;

import java.security.SecureRandom;


import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class RandomStringGenerator {

	private static  final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static  final String lower = upper.toLowerCase(Locale.ROOT);
	private static  final String digits = "0123456789";
	private static  final String alphanum = upper + lower + digits;
	private static final char[] symbols=alphanum.toCharArray();
    
	private Random random= new SecureRandom();
        
    public String nextString(int length) {
    	char[] buf=new char[length];    	
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
    
    
}