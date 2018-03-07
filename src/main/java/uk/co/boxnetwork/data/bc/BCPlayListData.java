package uk.co.boxnetwork.data.bc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BCPlayListData {
	private String id;
	private String account_id;
	private String created_at;
	private String description;
	private Boolean favorite;
	private String name;
	private String reference_id;
	private String type;
	private String updated_at;
	private String[] video_ids;
	private Integer limit;
	private String search;
	public void clearForPatch(){
		this.id=null;
		this.account_id=null;
		this.created_at=null;
		this.updated_at=null;
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccount_id() {
		return account_id;
	}
	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getFavorite() {
		return favorite;
	}
	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReference_id() {
		return reference_id;
	}
	public void setReference_id(String reference_id) {
		this.reference_id = reference_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String[] getVideo_ids() {
		return video_ids;
	}
	public void setVideo_ids(String[] video_ids) {
		this.video_ids = video_ids;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public boolean containsVideoId(String viodeId){
		if(this.video_ids==null || this.video_ids.length==0){
			return false;
		}		
		for(String vid:this.video_ids){
			   	if(vid.equals(viodeId)){
			   		return true;
			   	}
		 }
	     return false;
	}
	public boolean removeVideoId(String viodeId){
		if(!this.containsVideoId(viodeId)){
			return false;
		}		
		List<String> nvideo_ids=new ArrayList<String>();
		for(String vid:this.video_ids){
		   	if(!vid.equals(viodeId)){
		   		nvideo_ids.add(vid);
		   	}
	    }
		this.video_ids=new String[nvideo_ids.size()];
		this.video_ids=nvideo_ids.toArray(this.video_ids);
		return true;
	}	
	
}
