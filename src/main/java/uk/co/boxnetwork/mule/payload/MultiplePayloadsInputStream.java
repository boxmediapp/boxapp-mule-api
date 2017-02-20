package uk.co.boxnetwork.mule.payload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplePayloadsInputStream extends InputStream{
	private static final Logger logger=LoggerFactory.getLogger(MultiplePayloadsInputStream.class);
	private List<InputStream>  streams;
    int currentIndex=0;
   
    
    public MultiplePayloadsInputStream(List<InputStream>  inputstreams)
    {       
    	 this.streams=inputstreams;    	
    }

    public int available() throws IOException
    {
    	int n=currentIndex;
    	
        while(n<streams.size()){    	
              int av=((InputStream)streams.get(n)).available();
              logger.info("available is called available=["+av+"]currentIndex=["+n+"]");
              if(av>0){
        	     return av;
              }
              n++;
        }
        return 0;
    }

    public synchronized void mark(int readlimit)
    {
       
    }

    public boolean markSupported()
    {
        return false;
    }

    public synchronized void reset() throws IOException
    {
           
    }

    public long skip(long n) throws IOException
    {
    	long skipped=0;    	
    	while(currentIndex< streams.size() && skipped<n){     		
    		long ns=((InputStream)streams.get(currentIndex)).skip(n);
    		logger.info("skipped::["+ns+"]currentIndex=["+currentIndex+"]");
    		if(ns<n){
    			currentIndex++;   		    			
    		}
    		skipped+=ns;
    	}	    	
        return skipped;
    }

    public int read() throws IOException
    {
    	   while(currentIndex< streams.size()){     	   
    	    	int ch=((InputStream)streams.get(currentIndex)).read();
    	    	
    	    	if(ch==-1){
    	    		currentIndex++;
    	    	}
    	    	else
    	    		return ch;
    	    	
    	   }
    	    
    	    return -1;
    	    
    }

    public void close() throws IOException
    {
       logger.info("closing the multiple inputstream");	
       for(InputStream in:streams){
    	  in.close();
      }
      logger.info("closed the multiple inputstream");	
    }


}
