package uk.co.boxnetwork.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.SMTPConfig;
import uk.co.boxnetwork.security.BoxUserService;
import uk.co.boxnetwork.util.GenericUtilities;

import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.persistence.EntityManager;

@Service
public class STMPEmailService {
	public static String DEFAULT_CONFIG_ID="default";
	
	public static boolean isValidConfigId(String id){
		return (id!=null && id.equals(DEFAULT_CONFIG_ID));
	}
	
	private static final Logger logger=LoggerFactory.getLogger(STMPEmailService.class);
	
			
	@Autowired	
	private EntityManager entityManager;
    
	
	
	
	@Autowired
	private BoxUserService boxUserService;
	

	private String encryptionKey="TiynaMII91mfU0FF";

	
	public SMTPConfig getSMTPConfig(){
		SMTPConfig config=entityManager.find(SMTPConfig.class, DEFAULT_CONFIG_ID);
		if(config==null){
			return null;
		}
		if(config.getPassword()!=null && config.getPassword().length()>0){
			config.setPassword(boxUserService.decryptContent(encryptionKey, config.getPassword()));
		}
		return config;
		
		
	}
	
	@Transactional
	public void updateSMTPConfig(SMTPConfig smtpConfig){
		if(smtpConfig.getPassword()!=null && smtpConfig.getPassword().length()>0){
			smtpConfig.setPassword(boxUserService.encryptContent(encryptionKey, smtpConfig.getPassword()));
		}
			if(smtpConfig.getId()==null){
				smtpConfig.setId(DEFAULT_CONFIG_ID);
				entityManager.persist(smtpConfig);
			}
			else{
				entityManager.merge(smtpConfig);
			}			
	}
	
	  public void sendApprovalNotification(BoxUser boxuser){
		  SMTPConfig smtpConfig=this.getSMTPConfig();
		  if(smtpConfig==null){
			  logger.error("No smtp comfiguration found");
			  return;
		  }
		  try{
			  	String emailSubject=GenericUtilities.getAccountCreatedEmailSubject(boxuser);
			  	String emailBody=GenericUtilities.getAccountCreatedEmailBody(boxuser);
			  	sendEmail(smtpConfig, smtpConfig.getAccountAdmins(), emailSubject, emailBody);
		  }
		  catch(Exception e){
			  logger.error("Error in sending the approval notification email:"+e,e);			  
		  }
		  
		  
		  
	  }
	
	  public void sendEmail(SMTPConfig smtpConfig,String toEmailAddress, String emailSubject, String emailBody) throws UnsupportedEncodingException, MessagingException{
		  
		  Transport transport=null;
		  // Create a Properties object to contain connection configuration information.
	    	Properties props = System.getProperties();
	    	props.put("mail.transport.protocol", "smtp");
	    	props.put("mail.smtp.port", Integer.parseInt(smtpConfig.getSmtpPort())); 
	    	props.put("mail.smtp.starttls.enable", "true");
	    	props.put("mail.smtp.auth", "true");

	        // Create a Session object to represent a mail session with the specified properties. 
	    	Session session = Session.getDefaultInstance(props);

	        // Create a message with the specified information. 
	        MimeMessage msg = new MimeMessage(session);
	        try{
	        msg.setFrom(new InternetAddress(smtpConfig.getFromEmailAddress(),smtpConfig.getFromName()));	        
	        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress));
	        msg.setSubject(emailSubject);
	        msg.setContent(emailBody,"text/html");
	        
	            
	        // Create a transport.
	        transport = session.getTransport();	  
	        transport.connect(smtpConfig.getSmtpHost(), smtpConfig.getUsername(), smtpConfig.getPassword());
	            transport.sendMessage(msg, msg.getAllRecipients());	            
	        }	        
	        finally
	        {
	            // Close and terminate the connection.	        	
	        		if(transport!=null)
	        			transport.close();
	        	
	        }
	        
  
		  
	  }
}
