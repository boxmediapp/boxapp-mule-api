package uk.co.boxnetwork.mule.util;


public class LinkReplacer {
	public static String replace(String content,String from,String to){
		if(content==null||content.length()<10){
			return content;
		}		
		StringBuilder builder=new StringBuilder();
		int ib=0;
		while((ib+3)<content.length()){
			int ie=content.indexOf(from,ib);			
			if(ie==-1)
				break;
			builder.append(content.substring(ib,ie));
			builder.append(to);			
			ib=ie+from.length();
		}
		if(ib==0){
			return content;
		}		
		if(ib<content.length()){
			builder.append(content.substring(ib));			
		}
		return builder.toString();
	}
}
