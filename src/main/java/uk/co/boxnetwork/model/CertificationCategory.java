package uk.co.boxnetwork.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="certification_category")
public class CertificationCategory {
	@Id
	private String id;
	private String description;	
	private String name;
	public void update(CertificationCategory obj){
		this.description=obj.getDescription();
		this.name=obj.getName();
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "CertificationCategory [description=" + description + ", id=" + id + ", name=" + name + "]";
	}	
	public void adjustBeforeSave(Episode episode){
		
	}
}
