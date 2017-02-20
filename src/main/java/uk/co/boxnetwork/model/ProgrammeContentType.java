package uk.co.boxnetwork.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProgrammeContentType {	
	MUSIC_AND_CHARTS("Entertainment"),
	ENTERTAINMENTS("Music & Charts"),
	SHORTS("Shorts");
	
	private final String name;
	
	@JsonValue
    public String getName() {
        return name;
    }
	
	private  ProgrammeContentType(String name){
		this.name=name;
	}
	public String toString() {
	     return this.name;
	}
	public static ProgrammeContentType fromString(String textValue) {
	    if (textValue != null) {
	      for (ProgrammeContentType p : ProgrammeContentType.values()) {
	        if (textValue.equalsIgnoreCase(p.toString())) {
	          return p;
	        }
	      }
	    }
	    return null;
	  }
}
