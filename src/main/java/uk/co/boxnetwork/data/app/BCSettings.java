package uk.co.boxnetwork.data.app;

import uk.co.boxnetwork.data.bc.BCEnvironmentType;

public class BCSettings {
	private String accountId;
	private BCEnvironmentType environmentType;
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public BCEnvironmentType getEnvironmentType() {
		return environmentType;
	}
	public void setEnvironmentType(BCEnvironmentType environmentType) {
		this.environmentType = environmentType;
	}
	
	
	
}
