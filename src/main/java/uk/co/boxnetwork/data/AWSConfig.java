package uk.co.boxnetwork.data;

import com.amazonaws.regions.Regions;

public class AWSConfig {
private String awsAccessKeyId;
private String awsSecretAccessKey;
private String awsRegion;
public String getAwsAccessKeyId() {
	return awsAccessKeyId;
}
public void setAwsAccessKeyId(String awsAccessKeyId) {
	this.awsAccessKeyId = awsAccessKeyId;
}
public String getAwsSecretAccessKey() {
	return awsSecretAccessKey;
}
public void setAwsSecretAccessKey(String awsSecretAccessKey) {
	this.awsSecretAccessKey = awsSecretAccessKey;
}
public String getAwsRegion() {
	return awsRegion;
}
public void setAwsRegion(String awsRegion) {
	this.awsRegion = awsRegion;
}


}
