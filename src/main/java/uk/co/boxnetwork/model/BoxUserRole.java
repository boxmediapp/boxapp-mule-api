package uk.co.boxnetwork.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="user_role")
public class BoxUserRole {
	@Id 	
    private String rolename;

	@Column(name="api_access")
	private String apiAccess;
	
	@Column(name="operation_access")
	private String operationAccess;
	
	@Column(name="client_secret_duration")
	private Long secretDuration;
	
	private String application;
	
					
	public String getRolename() {
		return rolename;
	}

	
	
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}




	public String getApiAccess() {
		return apiAccess;
	}




	public void setApiAccess(String apiAccess) {
		this.apiAccess = apiAccess;
	}



	public Long getSecretDuration() {
		return secretDuration;
	}



	public void setSecretDuration(Long secretDuration) {
		this.secretDuration = secretDuration;
	}



	public String getOperationAccess() {
		return operationAccess;
	}



	public void setOperationAccess(String operationAccess) {
		this.operationAccess = operationAccess;
	}



	public String getApplication() {
		return application;
	}



	public void setApplication(String application) {
		this.application = application;
	}



	public BoxUserRole(String rolename, String apiAccess, String operationAccess,Long secretDuration, String app) {
		super();
		this.rolename = rolename;
		this.apiAccess = apiAccess;
		this.operationAccess=operationAccess;
		this.secretDuration = secretDuration;
		this.application=app;		
	}
	public BoxUserRole(){
		super();
	}
	
	
}
