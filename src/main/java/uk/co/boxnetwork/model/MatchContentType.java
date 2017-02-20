package uk.co.boxnetwork.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MatchContentType {
	ALL("ALL"),
	MUSIC_AND_CHARTS("Entertainment"),
	ENTERTAINMENTS("Music & Charts"),
	SHORTS("Shorts");
	
	private final String name;
	
	@JsonValue
    public String getName() {
        return name;
    }
	
	private  MatchContentType(String name){
		this.name=name;
	}
	public String toString() {
	     return this.name;
	}
	public static MatchContentType fromString(String textValue) {
	    if (textValue != null) {
	      for (MatchContentType p : MatchContentType.values()) {
	        if (textValue.equalsIgnoreCase(p.toString())) {
	          return p;
	        }
	      }
	    }
	    return null;
	  }
	
	public static MatchContentType match(ProgrammeContentType programContentType){
		if(programContentType==ProgrammeContentType.ENTERTAINMENTS){
			return ENTERTAINMENTS;
		}
		else if(programContentType==ProgrammeContentType.MUSIC_AND_CHARTS){
			return MUSIC_AND_CHARTS;
		}
		else if(programContentType==ProgrammeContentType.SHORTS){
			return SHORTS;
		}
		else{
			return ALL;
		}
	
		
	}
}
