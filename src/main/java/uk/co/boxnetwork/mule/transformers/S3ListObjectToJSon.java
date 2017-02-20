package uk.co.boxnetwork.mule.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.s3.FileItem;




public class S3ListObjectToJSon extends AbstractMessageTransformer{

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        
		
		Object payload=message.getPayload();
		  AWSCredentials credentials = null;
	        try {
	            credentials = new ProfileCredentialsProvider().getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (~/.aws/credentials), and is in valid format.",
	                    e);
	        }

	        AmazonS3 s3 = new AmazonS3Client(credentials);
	        Region r = Region.getRegion(Regions.EU_WEST_1);
	        s3.setRegion(r);
	        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                    .withBucketName("box-video"));
                    //.withPrefix("My"));
            List<FileItem> ret=new ArrayList<FileItem>();
            Map<String, String> queryprarams=message.getInboundProperty("http.query.params");
            String filepart=null;
            if(queryprarams!=null){
            	filepart=queryprarams.get("file");
            }
            
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                String key=objectSummary.getKey();
                if(key.endsWith("/")){
                	continue;
                	
                }
                if(filepart!=null&&filepart.length()>0&&(!key.contains(filepart))){
                	continue;                	
                }                
                FileItem itm=new FileItem();
                itm.setFile(key);
                ret.add(itm);
                
            	
            }
            com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
    		
    		
    		objectMapper.setSerializationInclusion(Include.NON_NULL);
            try{
            	message.setOutboundProperty("Content-Type", "application/json");
            	
            	return objectMapper.writeValueAsString(ret);
            }catch(Exception e){
            	return "{\"error\":\"error\"}";
            }
            
		
		//return payload;
	}

}
