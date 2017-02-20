package uk.co.boxnetwork.mule.transformers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.mule.model.LogMessageInfo;



public class LogMessageTransformer  extends AbstractMessageTransformer{
	public static final String EVENT_NOTIFICATION_SESSION_ID="EventNotificationSessionId";
	public static String getEventNotificationSessionId() {
		return EVENT_NOTIFICATION_SESSION_ID;
	}
	public static String LOG_INVOCATION_VARIABLE_NAME="logmessage";
	public static String ROOT_MESSAGE_PAYLOAD="RootMessagePayload";

		public static enum LogOperationType{
			INFO,
			RETURN_AS_PAYLOAD,
			RETURN_AS_INVOCATION
		}
		private static final Logger logger=LoggerFactory.getLogger(LogMessageTransformer.class);
		private LogOperationType type=LogOperationType.INFO;
		private boolean generateEventNotificationSesseionId=false;
		private boolean detailed=true;
		
		
	    public boolean isDetailed() {
			return detailed;
		}

		public void setDetailed(boolean detailed) {
			this.detailed = detailed;
		}

		public boolean isGenerateEventNotificationSesseionId() {
			return generateEventNotificationSesseionId;
		}

		public void setGenerateEventNotificationSesseionId(
				boolean generateEventNotificationSesseionId) {
			this.generateEventNotificationSesseionId = generateEventNotificationSesseionId;
		}

		public LogOperationType getType() {
			return type;
		}

		public void setType(LogOperationType type) {
			this.type = type;
		}
		private String logtitle;
		public String getLogtitle() {
			return logtitle;
		}

		public void setLogtitle(String logtitle) {
			this.logtitle = logtitle;
		}
	    
		@Override
		public Object transformMessage(MuleMessage message, String outputEncoding)
				throws TransformerException {
		    
			String eventNotificationSessionId=message.getProperty(EVENT_NOTIFICATION_SESSION_ID, PropertyScope.SESSION);
		    if(generateEventNotificationSesseionId || eventNotificationSessionId==null){
		    	eventNotificationSessionId=UUID.randomUUID().toString();
		    	message.setProperty(EVENT_NOTIFICATION_SESSION_ID, eventNotificationSessionId,PropertyScope.SESSION);
		    }	    
			
		    if(!detailed){		    	
		    	StringBuilder builder=new StringBuilder();
				builder.append("\n\n==========");
				if(logtitle!=null){
					builder.append(logtitle +"==");
				}								
				builder.append("::"+eventNotificationSessionId+"::");
				builder.append("==============End================\n");
				logger.info(builder.toString());
				return message.getPayload();		    	
		    }
		    
		    if(message.getProperty(ROOT_MESSAGE_PAYLOAD, PropertyScope.INVOCATION)==null){
				message.setProperty(ROOT_MESSAGE_PAYLOAD, message.getPayload(),PropertyScope.SESSION);
			}
			
		
			LogMessageInfo logmesage=new LogMessageInfo();
			logmesage.setLogTitle(logtitle);
			try{
			
			if(message.getPayload()!=null){
		    	logmesage.setPayloadClassName(message.getPayload().getClass().getName());
		    	logmesage.setPayloadDataType(String.valueOf(message.getDataType()));
		    	logmesage.setPayloadAsString(message.getPayloadAsString());
		    	logmesage.setPayload(String.valueOf(message.getPayload()));
		    }
			if(message.getExceptionPayload()!=null){	
		    	logmesage.setErrorCode(message.getExceptionPayload().getCode());
		    	logmesage.setMessage(message.getExceptionPayload().getMessage());
		    	logmesage.setExceptionInfo(getExceptionInfo(message));
		    	logmesage.setRootExceptionStacktrace(getRootExceptionStackTrace(message));
		    	logmesage.setExceptionStacktrace(getExceptionStacktrace(message));
		    }		
		      logmesage.setInboundInfo(getMuleMessageInfo(message,PropertyScope.INBOUND));
			  logmesage.setOutboundInfo(getMuleMessageInfo(message,PropertyScope.OUTBOUND));
			  logmesage.setInvocationInfo(getMuleMessageInfo(message,PropertyScope.INVOCATION));
			  logmesage.setSessionInfo(getMuleMessageInfo(message,PropertyScope.SESSION));
			
			  logmesage.setInboundEndpoint((String)message.getInboundProperty("MULE_ENDPOINT"));
			  
			  if(logmesage.getInboundEndpoint()==null){
			     logmesage.setInboundEndpoint((String)message.getInboundProperty("http.context.uri"));
			  }
			  if(logmesage.getInboundEndpoint()!=null){
				  logmesage.setInboundEndpoint((String)message.getInboundProperty("MULE_ORIGINATING_ENDPOINT"));
			  }
			  
		      
			  logmesage.setOutboundEndpoint((String)message.getOutboundProperty("MULE_ENDPOINT"));
			  logmesage.setSessionId(eventNotificationSessionId);
		
		 
			  
			}
		
			  catch(Exception ex){
				  logger.error("Error while getting the exception",ex);
				  
			 }
			  
			 if(logmesage.getExceptionStacktrace()!=null){
				 logger.error(logmesage.toString());
			 }
			 else
				 logger.info(logmesage.toString());
			 if(type==LogOperationType.RETURN_AS_PAYLOAD){
			      return  logmesage;
			 }
			 else if(type==LogOperationType.RETURN_AS_INVOCATION){
				 message.setInvocationProperty(LOG_INVOCATION_VARIABLE_NAME, logmesage);			 
			 }		      
			 return message.getPayload();
		}
	   
	   private String getExceptionInfo(MuleMessage message)throws TransformerException{	
		     
			 if(message.getExceptionPayload()!=null && message.getExceptionPayload().getInfo()!=null && message.getExceptionPayload().getInfo().size()>0){				
				    StringWriter sw=new StringWriter();
			        PrintWriter pw=new PrintWriter(sw);
				    Set entriySet=message.getExceptionPayload().getInfo().entrySet();
					Iterator it=entriySet.iterator();
				    while(it.hasNext()){
								Map.Entry mapEntry=(Map.Entry)it.next();
								pw.println(mapEntry.getKey()+"="+mapEntry.getValue()+" ");				
					}
					pw.close();
				    return sw.toString();					
			}
			 else
				 return null;
	 }

	   private String getRootExceptionStackTrace(MuleMessage message)throws TransformerException{			
			if(message.getExceptionPayload()!=null && message.getExceptionPayload().getRootException()!=null){
				    StringWriter sw=new StringWriter();
		            PrintWriter pw=new PrintWriter(sw);	    		
		    		message.getExceptionPayload().getRootException().printStackTrace(pw);
		    		pw.close();
				    return sw.toString();
		     }
			else
				return null;
		}

	   
	   private String getExceptionStacktrace(MuleMessage message) throws TransformerException{	
			
			if(message.getExceptionPayload()!=null && message.getExceptionPayload().getException()!=null){
				    StringWriter sw=new StringWriter();
	                PrintWriter pw=new PrintWriter(sw);		    	
			    	message.getExceptionPayload().getException().printStackTrace(pw);
			    	pw.close();
				    return sw.toString();
			}
			else
				return null;
		}
	 private String getMuleMessageInfo(MuleMessage message,PropertyScope scope){	 
		Set<String> propertyNames=message.getPropertyNames(scope);
		if(propertyNames==null){
			return null;
		}
		StringWriter sw=new StringWriter();
	    PrintWriter pw=new PrintWriter(sw);	
		for(String pname:propertyNames){
			   pw.println(pname+"="+message.getProperty(pname,scope));
		 }
	 	pw.close();
	    return sw.toString();
		
	}

	
}
