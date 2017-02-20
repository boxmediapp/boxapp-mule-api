package uk.co.boxnetwork.mule.payload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleJsonInputStream extends InputStream{
	private static final Logger logger=LoggerFactory.getLogger(MultiplePayloadsInputStream.class);
	private List<InputStream>  streams;
    int currentIndex=0;
    private static enum StreramPosition{
    	BEGINNING,
    	MIDDLE
    }
    private  ReadStatus readStatus=ReadStatus.NOT_STARTED;
    private StreramPosition streamposition=StreramPosition.BEGINNING;
    private byte readedData[]=null;
    private int readedDataIndex=0;
    private ByteArrayOutputStream  bufferredContent=null;
    
    public MultipleJsonInputStream(List<InputStream>  inputstreams)
    {       
    	 this.streams=inputstreams;    	
    }

    public int available() throws IOException
    {
    	if(readedData!=null&& readedData.length>0)
    		return readedData.length;
    	int n=currentIndex;
    	
        while(n<streams.size()){    	
              int av=((InputStream)streams.get(n)).available();
              logger.info("available is called available=["+av+"]currentIndex=["+n+"]");
              if(av>0){
        	     return av;
              }
              n++;
        }
        if(readStatus==ReadStatus.RECEIVED_END_OF_ARRAY){
        	return 1;        	
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
    	if(readedData!=null&& readedData.length>0){
    		if(readedData.length>n){    			
    			byte newReadedData[]=new byte[readedData.length-(int)n];
                for(int i=0;i<newReadedData.length;i++){
                	newReadedData[i]=readedData[(int)n+i];                	
                }
                readedData=newReadedData;
                return n;    			
    		}
    		else{
    			skipped=readedData.length;
    			readedData=null;
    		}
    		
    	}
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
    private static enum ReadStatus{
    	NOT_STARTED,
    	FIRST_ARRAY_BEGINS,
    	DATA_BODY,
    	RECEIVED_END_OF_ARRAY,
    	SECOND_ARRAY_BEGINS,
    	TURN_OF_JOIN,    	
    	
    }
    
    private void clearBuffer(){
    	bufferredContent=null;
    }
    private void writeToBuffer(int ch){
    	bufferredContent.write(ch);
    }
    private int pushBufferedContentToStream(){
    	readedData=bufferredContent.toByteArray();
    	bufferredContent=null;
		readedDataIndex=0;
		return readedData[readedDataIndex++];  
    }
    private void receivedEndOfArray(){
    	bufferredContent=new ByteArrayOutputStream();
    	bufferredContent.write(']');
    	readStatus=ReadStatus.RECEIVED_END_OF_ARRAY;
    }
    private int joinArray(int ch){
    	bufferredContent=new ByteArrayOutputStream();
    	writeToBuffer(',');
    	writeToBuffer(' ');
    	writeToBuffer(ch);    	
    	readStatus=ReadStatus.DATA_BODY;
    	return pushBufferedContentToStream(); 
    }
    public int read() throws IOException
    {
    	  if(readedData!=null){
    		 if(readedDataIndex<readedData.length)
    			 return readedData[readedDataIndex++];
    		 else{
    			 readedDataIndex=0;
    			 readedData=null;    			 
    		 }
    			 
    	  }
    	   while(currentIndex< streams.size()){     	   
    	    	int ch=((InputStream)streams.get(currentIndex)).read();
    	    	if(ch==-1){
    	    		currentIndex++;
    	    		streamposition=StreramPosition.BEGINNING;
    	    		continue;
    	    	}
    	    	switch(readStatus){
    	    	         
    	    	         case NOT_STARTED:
    	    	        	       ch=processNotStarted(ch);
    	    	        	       break;
    	    	         case FIRST_ARRAY_BEGINS:
    	    	        	       ch=processFirstArrayBegins(ch);
    	    	                   break; 
    	    	         case DATA_BODY:
    	    	        	       ch=processDataBody(ch);
    	    	        	       break;
    	    	         case RECEIVED_END_OF_ARRAY:
    	    	        	       ch=processReceivedEndOfArray(ch);
    	    	        	       break;
    	    	         case SECOND_ARRAY_BEGINS:
    	    	        	      ch=processSecondArrayBegins(ch);
    	    	        	      
    	    	         
    	    	}
    	    	streamposition=StreramPosition.MIDDLE;
    	    	if(ch!=-1){    	    		
    	    		return ch;
    	    	}
    	   }
    	   if(readStatus==ReadStatus.RECEIVED_END_OF_ARRAY){
    		   readStatus=ReadStatus.TURN_OF_JOIN;
    		   return ']';    		   
    	   }
    	   return -1;
    	    
    }
    private int processSecondArrayBegins(int ch){
    	if(streamposition==StreramPosition.BEGINNING){
    		logger.info("stream ended in the second stream before receive the data");
        	writeToBuffer(ch);            	 
        	readStatus=ReadStatus.TURN_OF_JOIN;
        	return  pushBufferedContentToStream();
    	}
    	else if(ch<=' '){
    		logger.info("skipping the white space after the second array identifier");            	
        	writeToBuffer(ch);
        	return -1;
    	}
    	else if(ch=='{'){
    		logger.info("joining the array");
    		clearBuffer();
    		return joinArray(ch);    		
    	}
    	else{
    		logger.info("unexpected begining of the array, turnning it offf the joing ch=["+(char)ch+"]");
    		readStatus=ReadStatus.TURN_OF_JOIN;
        	return  pushBufferedContentToStream();
    	}
    }
    private int processReceivedEndOfArray(int ch){
    	if(streamposition==StreramPosition.BEGINNING){
        	logger.info("End of array is at the end of the stream"); 
            if(ch<=' '){
            	logger.info("skipping the white space at the beginning of the stream");            	
            	writeToBuffer(ch);
            	return -1;            	
            }
            else if(ch=='['){
            	logger.info("Received the second array begin identifier");
            	readStatus=ReadStatus.SECOND_ARRAY_BEGINS;
            	writeToBuffer(ch);
            	return -1;            	
            }
            else {
            	logger.info("Expected array begins at the second stream but received:["+ch+"]");
            	writeToBuffer(ch);            	 
            	readStatus=ReadStatus.TURN_OF_JOIN;
            	return  pushBufferedContentToStream();
            }
        
        }
    	else if(ch<=' '){
    		logger.info("skipping the white space at the beginning of the stream");            	
        	writeToBuffer(ch);
        	return -1;
    	}
    	else if(ch==']'){
    		logger.info("end of array identified encountered again");
    		int a=pushBufferedContentToStream();
    		receivedEndOfArray();
    		return a;    		
    	}
    	else{
    		logger.info("more content after end array identifier, so continue");
    		readStatus=ReadStatus.DATA_BODY;
    		writeToBuffer(ch);
    		return  pushBufferedContentToStream();
    	}
    	
    }
    private int processDataBody(int ch){
    	if(streamposition==StreramPosition.BEGINNING){
        	logger.info("endof character identifier is not received before end of the stream");        	
        	readStatus=ReadStatus.TURN_OF_JOIN;
        	return ch;
        }
    	if(ch==']'){        	
    		receivedEndOfArray();
    		return -1;
        }
    	else
    		return ch;
    }
    private int processFirstArrayBegins(int ch){
    	if(ch<=' '){
			logger.info("reading the white characters after the array begins");
		     return ch;
		}
		else if(ch=='{'){
			readStatus=ReadStatus.DATA_BODY;
			logger.info("Begin to receive data");
			return ch;
		}
		else if(ch==']'){
			logger.info("Received emppty array, waiting for the next set of array");
			receivedEndOfArray(); 
	    	return -1;    				
		}
			
		else{
		    logger.warn("Received the unexpected character expected '{' but received =["+(char)ch+']');
		    readStatus=ReadStatus.TURN_OF_JOIN;
		    return ch;
		}
   	
    }
    
    private int processNotStarted(int ch){
		if(ch<=' '){
			logger.info("reading the white characters at the beginning");
		     return ch;
		}
		else if(ch=='['){
			readStatus=ReadStatus.FIRST_ARRAY_BEGINS;			
			logger.info("Received the array beginning character");
			return ch;
		}
		else{
		    logger.warn("Received the unexpected character expected '[' but received =["+(char)ch+']');
		    readStatus=ReadStatus.TURN_OF_JOIN;
		    return ch;
		}

 }    
    
    public void close() throws IOException
    {
       logger.info("closing the multiple inputstream");	
       for(InputStream in:streams){
    	  in.close();
      }
      logger.info("the multiple inputstream closed");	
    }


}
