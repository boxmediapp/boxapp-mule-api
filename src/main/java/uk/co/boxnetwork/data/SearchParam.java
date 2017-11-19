package uk.co.boxnetwork.data;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.mule.api.MuleMessage;
import org.mule.module.http.internal.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.ImageStatus;
import uk.co.boxnetwork.model.PublishedStatus;


public class SearchParam {
	
	static final protected Logger logger=LoggerFactory.getLogger(SearchParam.class);
	public static enum SearchParamType{
		GENERIC,
		BCREPORT,
		SCHEDULE,
		EPISODE,
		SERIES,
		SERIESGROUP,
		S3ITEM,
		BCPLAYLIST,
		BCITEMINPLAYLIST,
		ADVERTISEMENT_RULE
	}
	
	private String search=null;
	private Integer start=null;
	private Integer limit=null;
	private String contractNumber=null;
	private String title=null;
	private String sortBy=null;
	private String sortOrder=null;
	
	private String prefix=null;
	private String file=null;
	
	private String from=null;
	private String to=null;
	private String videoid=null;
	
    
	private SearchParamType searchType;
	
	private ImageStatus  imageStatus=null;
	private String programmeNumber=null;
	
	private Integer nunberOfImageSets=null;
	private Integer minNumberOfImageSets=null;
	
	
	
	
	
	public ImageStatus getImageStatus() {
		return imageStatus;
	}
	public void setImageStatus(ImageStatus imageStatus) {
		this.imageStatus = imageStatus;
	}
	public String getVideoid() {
		return videoid;
	}
	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	
	
	public SearchParam(String search, Integer start, Integer limit) {
		super();
		this.search = search;
		this.start = start;
		this.limit = limit;
		this.searchType=SearchParamType.GENERIC;
		
	}
	
	private void transformSortByParameter(){
		if(this.sortBy.indexOf(" ")>=0){
			   throw new RuntimeException("sortBy fields are illegal!!!");
		}
		if(searchType==SearchParamType.EPISODE){						
		   if(this.sortBy.equals("programmeNumber")){
			   this.sortBy="ctrPrg"; 
		   }
		}
		else if(searchType==SearchParamType.SCHEDULE){
			if(this.sortBy.equals("episdeoTitle")){
				   this.sortBy="episode.title"; 
			}
			else if(this.sortBy.equals("programmeNumber")){
				   this.sortBy="episode.ctrPrg"; 
			}			
		}

	}
	
	
	public SearchParam(MuleMessage message, AppConfig appConfig,SearchParamType searchType){
		this.searchType=searchType;
		this.limit=appConfig.getRecordLimit();
//		if(searchType==SearchParamType.BCPLAYLIST){
//			this.limit=30;
//		}
	
		ParameterMap queryparams=message.getInboundProperty("http.query.params");
		if(queryparams!=null){				
				this.search=queryparams.get("search");		
				if(this.search!=null){
						this.search=this.search.trim();
						if(this.search.length()==0){
							this.search=null;
						}
						else{
							if(this.searchType!=SearchParamType.S3ITEM && this.searchType!=SearchParamType.BCPLAYLIST && this.searchType!=SearchParamType.BCITEMINPLAYLIST){
								if(search.indexOf("%")==-1){
									search="%"+search+"%";				
								}
							}
							
						}
				}
				this.prefix=queryparams.get("prefix");		
				if(this.prefix!=null){
						this.prefix=this.prefix.trim();
						if(this.prefix.length()==0){
							this.prefix=null;
						}						
				}
				this.file=queryparams.get("file");		
				if(this.file!=null){
						this.file=this.file.trim();
						if(this.file.length()==0){
							this.file=null;
						}						
				}
				this.from=queryparams.get("from");		
				if(this.from!=null){
						this.from=this.from.trim();
						if(this.from.length()==0){
							this.from=null;
						}						
				}
				this.to=queryparams.get("to");		
				if(this.to!=null){
						this.to=this.to.trim();
						if(this.to.length()==0){
							this.to=null;
						}						
				}
				this.videoid=queryparams.get("videoid");		
				if(this.videoid!=null){
						this.videoid=this.to.trim();
						if(this.videoid.length()==0){
							this.videoid=null;
						}						
				}
								
				this.contractNumber=queryparams.get("contractNumber");
				if(this.contractNumber!=null){
					this.contractNumber=this.contractNumber.trim();
					if(this.contractNumber.length()==0){
						this.contractNumber=null;
					}						
			    }
				
				
				String startParam=queryparams.get("start");
				if(startParam!=null){
					startParam=startParam.trim();
					if(startParam.length()>0){
						try{
				    		this.start=Integer.valueOf(queryparams.get("start"));
				    	}
				    	catch(Exception e){
				    		logger.error(e+ " while convering the startParam:"+startParam,e);
				    	}
					}
				}
				this.title=queryparams.get("title");
				if(this.title!=null){
					this.title=this.title.trim();
					if(this.title.length()==0){
						this.title=null;
					}
				}
				this.sortBy=queryparams.get("sortBy");
				if(this.sortBy!=null){
					this.sortBy=this.sortBy.trim();
					if(this.sortBy.length()==0){
						this.sortBy=null;
					}
					else{
						   transformSortByParameter();
						
						}
				}
				this.sortOrder=queryparams.get("sortOrder");
				if(this.sortOrder!=null){
					this.sortOrder=this.sortOrder.trim();
					if(this.sortOrder.length()==0){
						this.sortOrder=null;
					}					
				}
				String lim=queryparams.get("limit");
				if(lim!=null){
					try{
						int lm=Integer.parseInt(lim);
						if(lm>0){
							this.limit=lm;
						}
					}
					catch(Exception e){
						logger.error("error parsing the limit="+lim,e);
					}
				}	
				String imageStatus=queryparams.get("imageStatus");
				if(imageStatus!=null){
					try{
							this.imageStatus=ImageStatus.valueOf(imageStatus);
					}
					catch(Exception e){
						logger.error("wrong enum imageStatus is passed:"+imageStatus);
					}
				}
				String programmeNumber=queryparams.get("programmeNumber");
				if(programmeNumber!=null){
							programmeNumber=programmeNumber.trim().replace("-","/");
							if(programmeNumber.length()>0){
								this.programmeNumber=programmeNumber;
							}
				}
				String imageSets=queryparams.get("nunberOfImageSets");
				if(imageSets!=null){
					try{
							this.nunberOfImageSets=Integer.valueOf(imageSets);
					}
					catch(Exception e){
						logger.error(e+" while converting the nunberOfImageSets to Integer",e);
					}
				}				
				String minNumberOfImageSets=queryparams.get("minNumberOfImageSets");
				if(minNumberOfImageSets!=null){
					try{
							this.minNumberOfImageSets=Integer.valueOf(minNumberOfImageSets);
					}
					catch(Exception e){
						logger.error(e+" while converting the minNumberOfImageSets to Integer",e);
					}
				}
				
				
		}
		
		
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}	

	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public String selectQuery(String allquery,String  filterQuery){				   
		   if(this.search==null){
			   return allquery;
		   }
		   else{			    
			    return filterQuery;
		   }
   }
public String getNewBoxEpisodeSelectQuery(){
	 
	 String query="SELECT e FROM box_episode e";
	 boolean addedWhere=false;
	 
	 if(this.nunberOfImageSets!=null){
		 query+=" where SIZE(e.imageSets) = :nunberOfImageSets ";
		 addedWhere=true;
	 }
	 else if(this.minNumberOfImageSets!=null){
		 query+=" where SIZE(e.imageSets) >= :minNumberOfImageSets ";
		 addedWhere=true;
	 }
	 if(this.search!=null){		
		 if(addedWhere){
			 query+=" and (e.title LIKE :search OR e.programmeNumber LIKE :search)";
		 }
		 else{
			 query+=" where (e.title LIKE :search OR e.programmeNumber LIKE :search)";
			 addedWhere=true;
		 }		 		 
	 }
	 if(this.programmeNumber!=null){		
		 if(addedWhere){
			 query+=" and (e.programmeNumber LIKE :programmeNumber)";
		 }
		 else{
			 query+=" where (e.programmeNumber LIKE :search)";			 
		 }		 		 
	 }
	 
	 
	return query;	
}
public String getImageSetSelectQuery(){
	String query="SELECT e FROM image_set e";	
    if(this.search!=null){		
			 query+=" where (e.title LIKE :search OR e.boxEpisode.programmeNumber LIKE :search)";		 
	}
    else if(this.programmeNumber!=null){
   	 	query+=" where (e.boxEpisode.programmeNumber LIKE :programmeNumber)";
    }
    return query;
}
public String getImageSelectQuery(){
	String query="SELECT e FROM image e";
	boolean hasWhere=false;
	
    if(this.search!=null){		
			 query+=" where (e.imageSet.title LIKE :search OR e.imageSet.boxEpisode.programmeNumber LIKE :search)";
			 hasWhere=true;
	}
    else if(programmeNumber!=null){
    	query+=" where (e.imageSet.boxEpisode.programmeNumber LIKE :programmeNumber)";
    	hasWhere=true;
    }
    if(imageStatus!=null){
    	if(hasWhere){
    		query+=" and e.imageStatus=:imageStatus";    		
    	}
    	else{
    		query+=" where (e.imageStatus=:imageStatus)";
    	}
    }
    return query;
    
}
	
	public String selectQuery(String allquery,String  filterQuery, String titleQuery){				   
		if(this.title!=null){
			return titleQuery;
		}
		else if(this.search==null){
			   return allquery;
		 }		   
		   else{			    
			    return filterQuery;
		   }
    }
	

	public String selectScheduleQuery(String allquery,String  fromQuery, String rangeQuery){				   
		if(this.from!=null && this.to!=null) {
			return rangeQuery;
		}		
		else if(this.from!=null){
			return fromQuery;
		}		
	   else{			    
		    return allquery;
	   }
}
	
	public String selectSeriesQuery(String allquery,String  filterQuery, String contractQuery){				   
			if(this.contractNumber!=null){
				return contractQuery;
			}
			else if(this.search==null){
			   return allquery;
		   }
		   else{			    
			    return filterQuery;
		   }
  }
	public void config(TypedQuery<?> typedQuery){
		if(this.title!=null){
			typedQuery.setParameter("title",this.title);
		}
		else if(this.contractNumber!=null){
			typedQuery.setParameter("contractNumber",this.contractNumber);
		}
		else if(this.search!=null){
			typedQuery.setParameter("search",this.search);
		   }
		if(this.getImageStatus()!=null){
			typedQuery.setParameter("imageStatus",this.imageStatus);
		}
		if(this.programmeNumber!=null){
			 typedQuery.setParameter("programmeNumber",programmeNumber);
		}
		if(this.minNumberOfImageSets!=null){
			 typedQuery.setParameter("minNumberOfImageSets",minNumberOfImageSets);
		}
		if(this.nunberOfImageSets!=null){
			typedQuery.setParameter("nunberOfImageSets",nunberOfImageSets);
		}
		if(this.from!=null){
			try{
				Long from=Long.valueOf(this.from);
				java.sql.Date d=new java.sql.Date(from);
				typedQuery.setParameter("from",d);
			}
			catch(Exception e){
				logger.error(e+" while parsing the from query parameter: from=["+from+"]",e);
			}
		}
		if(this.to!=null){
			try{
				Long to=Long.valueOf(this.to);
				java.sql.Date d=new java.sql.Date(to);
				typedQuery.setParameter("to",d);
			}
			catch(Exception e){
				logger.error(e+" while parsing the from query parameter: to=["+to+"]",e);
			}
		}
		if(this.start!=null && this.start>0){
			  typedQuery.setFirstResult(this.start);			   
		   }
		   if(this.limit!=null && this.limit>=0){
			   typedQuery.setMaxResults(this.limit);
		   }
		 
	}
   public boolean isEnd(int fetchSize){
	   return fetchSize<limit;
   }
   public void nextBatch(){
	   this.start+=this.limit;
   }
   public void nextBatch(int videoSize){
	   this.start+=videoSize;
   }
   public String addSortByToQuery(String query, String prefix){
	   if(this.sortBy==null){
		  return  query;
	   }	   	   
	   StringBuilder builder=new StringBuilder();
	   builder.append(query).append(" order by ").append(prefix).append(".").append(this.sortBy);
	   if(this.sortOrder!=null){
		builder.append(" ").append(this.sortOrder);   
	   }
	   return builder.toString();
   }

}
