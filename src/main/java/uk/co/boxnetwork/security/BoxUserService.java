package uk.co.boxnetwork.security;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;


import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.RandomStringGenerator;
import uk.co.boxnetwork.data.app.LoginInfo;
import uk.co.boxnetwork.model.BoxUser;


public class BoxUserService implements UserDetailsService{
	
	private static final Logger logger=LoggerFactory.getLogger(BoxUserService.class);
	
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	private String encryptionKey;
	
	private String defaultRootPassword;
	
	@Autowired
	private RandomStringGenerator randomStringGenerator;
	
	
	
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}


	public void setDefaultRootPassword(String defaultRootPassword) {
		this.defaultRootPassword = defaultRootPassword;
	}


	private List<SimpleGrantedAuthority> getAuthorities(BoxUser user) {
        List<SimpleGrantedAuthority> authList = new ArrayList<SimpleGrantedAuthority>();
        String roles[]=user.getRoles().split(",");
        for(String role:roles){
        	role=role.trim();
        	String rolename="ROLE_"+role.toUpperCase();        	
        	authList.add(new SimpleGrantedAuthority(rolename));
        }
        return authList;        
    }
	
	 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		
		BoxUser boxuser=getUserByUserName(username);
		
		String password=null;
		if(boxuser==null){
			boxuser=getUserByClientId(username);
			if(boxuser==null){
				throw new UsernameNotFoundException("User details not found with this username: " + username);
			}
			Date now=new Date();
			if(now.getTime()>boxuser.getSecretExpiresAt()){
				logger.error("The client secret is expired:"+now.getTime()+">"+boxuser.getSecretExpiresAt());
			}
			else{
				password=boxuser.getClientSecret();
			}
			
						
			/*
			if("root".equals(username)){
				password=this.defaultRootPassword;
				boxuser=new BoxUser();
				boxuser.setUsername("root");
				boxuser.setPassword(password);
				boxuser.setRoles("admin");				
				createNewUser(boxuser);
			}
			else{
				throw new UsernameNotFoundException("User details not found with this username: " + username);
			}
			*/
			
		}
		else{			
			password=boxuser.getPassword();			
		}	
		
		List<SimpleGrantedAuthority> authList = getAuthorities(boxuser);
		String encodedPassword = passwordEncoder.encode(password);
		User user=new User(username, encodedPassword, authList);
		return user;
	}
    public BoxUser getUserByUserName(String username){
    	List<BoxUser> matchedusers=boxMetadataRepository.findUserByUsername(username);
    	if(matchedusers.size()==0){
    		return null;
    	}    	
    	BoxUser boxuser=matchedusers.get(0);
		boxuser.decrypt(encryptionKey);
		return boxuser;
    }
    public BoxUser getUserByClientId(String clientId){
    	List<BoxUser> matchedusers=boxMetadataRepository.findUserByClientId(clientId);    	
    	if(matchedusers.size()==0){
    		return null;
    	}    	
    	BoxUser boxuser=matchedusers.get(0);		
		return boxuser;
    }
	public List<BoxUser> listUsers(){
		List<BoxUser> users=boxMetadataRepository.findAllUsers();
		for(BoxUser user:users){
			user.setPassword("***********");
		}
		return users;
	}
	
	 
	
	public void createNewUser(BoxUser user){
		user.encrypt(encryptionKey);
		boxMetadataRepository.createUser(user);
	}
	public void deleteUser(String username){
		boxMetadataRepository.deleteByUsername(username);		
	}
	public void updateUser(BoxUser user){
		user.encrypt(encryptionKey);
		boxMetadataRepository.updateUser(user);		
	}
	public LoginInfo createClientIdAndSecret(BoxUser user){
		List<BoxUser> matchedusers=boxMetadataRepository.findUserByUsername(user.getUsername());
    	if(matchedusers.size()==0){
    		return null;
    	}
    	BoxUser boxuser=matchedusers.get(0);
    	if(boxuser.getClientId()==null||boxuser.getClientId().trim().length()==0){
    		boxuser.setClientId(randomStringGenerator.nextString(10));    		
    	}
    	boxuser.setClientSecret(randomStringGenerator.nextString(23));
    	Date now=new Date();
    	long nowInMilliseconds=now.getTime();    	
    	boxuser.setSecretExpiresAt(nowInMilliseconds+3600*10*1000);
    	boxMetadataRepository.updateUser(boxuser);
    	return new LoginInfo(boxuser);    	
	}
}
