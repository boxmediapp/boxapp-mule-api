package uk.co.boxnetwork.security;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.python.modules.synchronize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import uk.co.boxnetwork.components.BoxMedataRepository;
import uk.co.boxnetwork.components.RandomStringGenerator;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.app.LoginInfo;
import uk.co.boxnetwork.model.BoxUser;
import uk.co.boxnetwork.model.BoxUserRole;
import uk.co.boxnetwork.util.GenericUtilities;


public class BoxUserService implements UserDetailsService{
	
	private  static List<BoxUserRole> userRoles=new ArrayList<BoxUserRole>();
	
	
	
	
	private static final Logger logger=LoggerFactory.getLogger(BoxUserService.class);
	
	@Autowired
	private BoxMedataRepository boxMetadataRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	private String encryptionKey;
	
	
	
	@Autowired
	private RandomStringGenerator randomStringGenerator;
	
	
	
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}


	
	
	
	private List<SimpleGrantedAuthority> getAuthorities(BoxUser user) {
        List<SimpleGrantedAuthority> authList = new ArrayList<SimpleGrantedAuthority>();
        List<BoxUserRole> boxuserRoles=findBoxUserRole(user);        	
        for(BoxUserRole role:boxuserRoles){
        	authList.add(new SimpleGrantedAuthority(role.getApiAccess()));
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
			
		}
		else{			
			password=boxuser.getPassword();
			password=GenericUtilities.decrypt(encryptionKey, password);
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
			user.setClientSecret("*****");
		}
		return users;
	}
	
	 
	public void setPassword(BoxUser user, String password){
		user.setPassword(GenericUtilities.encrypt(encryptionKey, password));
	}
	public void createNewUser(BoxUser user){			
		boxMetadataRepository.createUser(user);
	}
	public void deleteUser(String username){
		boxMetadataRepository.deleteByUsername(username);		
	}
	public void updateUser(BoxUser user){		
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
    	BoxUserRole userrole=findSingleBoxUserRole(boxuser);
    	if(userrole==null){
    		logger.error("Could not find the matching user role for the user:"+user);
    		return null;
    	}
    	Date now=new Date();
    	long nowInMilliseconds=now.getTime();
    	boxuser.setSecretExpiresAt(nowInMilliseconds+userrole.getSecretDuration()*1000);
    	boxMetadataRepository.updateUser(boxuser);
    	return new LoginInfo(boxuser,userrole);    	
	}
	
	public  List<BoxUserRole> getAllUserRoles(){
			synchronized(userRoles){
					if(userRoles.size()==0){
						userRoles=boxMetadataRepository.findAllUserRoles();						
					}
			}
			return userRoles;		
	}
	public List<BoxUserRole> findBoxUserRole(BoxUser user){
			List<BoxUserRole> ret=new ArrayList<BoxUserRole>();		
			List<BoxUserRole> availableRoles=getAllUserRoles();		
			logger.info("****:user:"+user);
			logger.info("****:roles:"+user.getRoles());
			String[] userroles=user.getRoles().trim().split(",");
			for(BoxUserRole r:availableRoles){
				for(String userrole:userroles){
					if(r.getRolename().equals(userrole)){
						ret.add(r);
						break;
					}
				}
				if(ret.size()>=userroles.length){
					break;
				}
		    }					
			return ret;
	}
		public BoxUserRole findSingleBoxUserRole (BoxUser user){
		 List<BoxUserRole> userRoles=findBoxUserRole(user);
		 if(userRoles.size()==0){
			 return null;
		 }
		 else{
			 return userRoles.get(0);
		 }
	}
	
	
}
