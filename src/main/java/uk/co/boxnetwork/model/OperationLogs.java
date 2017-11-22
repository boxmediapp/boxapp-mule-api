package uk.co.boxnetwork.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

@Entity(name="operation_logs")
public class OperationLogs {
	
	public static OperationLogs createDeletedRecord(String deletedBy,String tableName, Long recordId){
		OperationLogs operationalLogs=new OperationLogs();
		operationalLogs.setCreatedAt(new Date());
		operationalLogs.setOperationType(OperationLogType.DELETED);
		operationalLogs.setUserName(deletedBy);
		operationalLogs.setTableName(tableName);
		operationalLogs.setRecordId(recordId);
		return operationalLogs;
	}
	
	public static OperationLogs buildImageDeleted(String username, Long id){
		OperationLogs logrecord=createDeletedRecord(username, "image", id);
		return   logrecord; 
	}

	public static List<OperationLogs> findDeletedImageLogs(EntityManager entityManager, Long recordId, Date deletedOnFrom){			
	   String querystring="SELECT s FROM operation_logs s where s.tableName=:tableName and s.operationType=:operationType";
	   if(recordId!=null){
		   querystring+=" and s.recordId=:recordId";		   
	   }
	   if(deletedOnFrom!=null){
		   querystring+=" and s.createdAt>=:createdAt";		   
	   }	   
	   TypedQuery<OperationLogs> query=entityManager.createQuery(querystring, OperationLogs.class);		
	   query.setParameter("tableName","image");
	   query.setParameter("operationType",OperationLogType.DELETED);
	   if(deletedOnFrom!=null){
		   query.setParameter("createdAt",deletedOnFrom);
	   }
	   if(recordId!=null){
		   query.setParameter("recordId",recordId);
	   }
	   return  query.getResultList();	  
	}
	
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name="table_name")
	private String tableName;
	
	@Column(name="operation_type")
	private OperationLogType operationType;
	
	@Column(name="user_name")
	private String userName;
	
	
	@Column(name="created_at")
	private Date createdAt;
	
	@Column(name="record_id")
	private Long recordId;
	
	
	
	
	@Column(name="operationDetails")
	private String  operationDetails;


	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public OperationLogType getOperationType() {
		return operationType;
	}


	public void setOperationType(OperationLogType operationType) {
		this.operationType = operationType;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public String getOperationDetails() {
		return operationDetails;
	}


	public void setOperationDetails(String operationDetails) {
		this.operationDetails = operationDetails;
	}


	public Long getRecordId() {
		return recordId;
	}


	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	
	public uk.co.boxnetwork.data.image.Image toImage(){
		uk.co.boxnetwork.data.image.Image ret=new uk.co.boxnetwork.data.image.Image();
		ret.setId(getRecordId());		
		return ret;
	}
	
}
