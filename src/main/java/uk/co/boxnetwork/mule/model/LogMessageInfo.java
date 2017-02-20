package uk.co.boxnetwork.mule.model;

import java.io.Serializable;

public class LogMessageInfo implements Serializable{
  
	private static final long serialVersionUID = 1L;
	private String logTitle;
	private String inboundEndpoint;
	private String outboundEndpoint;
	

	private String inboundInfo;
    private String outboundInfo;
    private String sessionInfo;
    private String invocationInfo;


	
	
	private String message;
	private String correlationId;
	private String payloadDataType;
	private String payloadAsString;
	private String exceptionInfo;
	private String rootExceptionStacktrace;
	private String exceptionStacktrace;
    private String payloadClassName;
    private String payload;
    private String sessionId;
    

	
    public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getInvocationInfo() {
		return invocationInfo;
	}

	public void setInvocationInfo(String invocationInfo) {
		this.invocationInfo = invocationInfo;
	}

    public String getInboundEndpoint() {
		return inboundEndpoint;
	}

	public void setInboundEndpoint(String inboundEndpoint) {
		this.inboundEndpoint = inboundEndpoint;
	}
	public String getOutboundEndpoint() {
		return outboundEndpoint;
	}

	public void setOutboundEndpoint(String outboundEndpoint) {
		this.outboundEndpoint = outboundEndpoint;
	}

    public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

    public String getLogTitle() {
		return logTitle;
	}

	public void setLogTitle(String logTitle) {
		this.logTitle = logTitle;
	}

    public String getInboundInfo() {
		return inboundInfo;
	}

	public void setInboundInfo(String inboundInfo) {
		this.inboundInfo = inboundInfo;
	}

	public String getOutboundInfo() {
		return outboundInfo;
	}

	public void setOutboundInfo(String outboundInfo) {
		this.outboundInfo = outboundInfo;
	}

	public String getSessionInfo() {
		return sessionInfo;
	}

	public void setSessionInfo(String sessionInfo) {
		this.sessionInfo = sessionInfo;
	}

	private int errorCode;
	
	
	
	public String getRootExceptionStacktrace() {
		return rootExceptionStacktrace;
	}

	public void setRootExceptionStacktrace(String rootExceptionStacktrace) {
		this.rootExceptionStacktrace = rootExceptionStacktrace;
	}

	public String getExceptionStacktrace() {
		return exceptionStacktrace;
	}

	public void setExceptionStacktrace(String exceptionStacktrace) {
		this.exceptionStacktrace = exceptionStacktrace;
	}

	public String getExceptionInfo() {
		return exceptionInfo;
	}

	public void setExceptionInfo(String exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}

	public String getPayloadAsString() {
		return payloadAsString;
	}

	public void setPayloadAsString(String payloadAsString) {
		this.payloadAsString = payloadAsString;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getPayloadDataType() {
		return payloadDataType;
	}

	public void setPayloadDataType(String payloadDataType) {
		this.payloadDataType = payloadDataType;
	}

	public String getPayloadClassName() {
		return payloadClassName;
	}

	public void setPayloadClassName(String payloadClassName) {
		this.payloadClassName = payloadClassName;
	}

	


	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String toString(){
		StringBuilder builder=new StringBuilder();
		builder.append("\n\n==========");
		if(logTitle!=null){
			builder.append(logTitle +"==");
		}
		if(outboundEndpoint!=null){
			builder.append("outbound:").append(outboundEndpoint);
		}
		else if(inboundEndpoint!=null){
			builder.append("inbound:").append(inboundEndpoint);
		}
		if(logTitle==null && outboundEndpoint==null && inboundEndpoint==null && message !=null){			
			builder.append(message);
	
		}
		if(sessionId!=null){
		  builder.append("::"+sessionId+"::");
		}
	 builder.append("============");
		if(message!=null){
			builder.append("\nmessage:"+message);
		}
		
		
		if(exceptionStacktrace!=null){
			builder.append("\nexceptionStacktrace:"+exceptionStacktrace);			
		}
		if(rootExceptionStacktrace!=null){
			builder.append("\nrootExceptionStacktrace:"+rootExceptionStacktrace);
		}
		if(exceptionInfo!=null){
			builder.append("\nexceptionInfo:"+exceptionInfo);
		}
		if(correlationId!=null){
			builder.append("\ncorrelationId:"+correlationId);
		}
		if(payloadDataType!=null){
			builder.append("\npayloadDataType:"+payloadDataType);
		}
		if(payloadClassName!=null){
			builder.append("\npayloadClassName:"+payloadClassName);
		}
		if(payloadAsString!=null){
			builder.append("\npayloadAsString:"+payloadAsString);
		}
		
		if(invocationInfo!=null){
			builder.append("\ninboundInfo:\n"+inboundInfo);
		}
	
		if(inboundInfo!=null){
			builder.append("\ninvocationInfo:\n"+invocationInfo);
		}
		if(outboundInfo!=null){
			builder.append("\noutboundInfo:\n"+outboundInfo);
		}
		
		if(sessionInfo!=null){
			builder.append("\nsessionInfo:"+sessionInfo);
		}
		builder.append("\n==============End================\n");
		return builder.toString();
	}
}
