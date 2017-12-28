package uk.co.boxnetwork.security;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private  static Map<String, LoginInfo> temporaryLoginInfo=new HashMap<String, LoginInfo>();
	
	
	
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


	
	
	
	private List<SimpleGrantedAuthority> getAuthorities(List<BoxUserRole> boxuserRoles) {
        List<SimpleGrantedAuthority> authList = new ArrayList<SimpleGrantedAuthority>();                	
        for(BoxUserRole role:boxuserRoles){
        	authList.add(new SimpleGrantedAuthority(role.getApiAccess()));
        }
        return authList;        
    }
	
	 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		String password="Xh6OE0iiBi";				
		List<BoxUserRole> boxUserRoles=null;
		removeExpiredLoginInfo();
		
		LoginInfo loginInfo=findLoginInfoByClientId(username);			
		
		if(loginInfo!=null){ //logged in user by calling previously createLoginInfo()		
			loginInfo.refreshExpiresAt(); //refresh the expiry date of the temporary crendentials.
			boxUserRoles=loginInfo.getRoles(); //get the roles from the logininfo
			password=loginInfo.getClientSecret(); //passoword is the client secret.						
		}
		else{	
				BoxUser boxuser=getUserByClientId(username); //check whether user used the clientId & clientSecret?		
				if(boxuser==null){    //Try with the username and user password.
						boxuser=getUserByUserName(username);
						if(boxuser==null){ //All the crendtials are exausted.
							throw new UsernameNotFoundException("User details not found with this username: " + username);
						}
						password=GenericUtilities.decrypt(encryptionKey, boxuser.getPassword()); //Basic username & password credentials.
				}
				else{//clientId & clientSecret authentication.
					password=boxuser.getClientSecret();
				}																
				boxUserRoles=findBoxUserRole(boxuser);															
		}					
		List<SimpleGrantedAuthority> authList = getAuthorities(boxUserRoles);
		String encodedPassword = passwordEncoder.encode(password);
		return new User(username, encodedPassword, authList);		
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
	public String selectApplicationFromRoles(List<BoxUserRole> userroles){				
		if(userroles!=null&& userroles.size()>0){
			return userroles.get(0).getApplication();
		}
		else{
				return "subscribe";
		}
	}
	public LoginInfo createLoginInfo(String userName){
		List<BoxUser> matchedusers=boxMetadataRepository.findUserByUsername(userName);
    	if(matchedusers.size()==0){
    		return null;
    	}
    	BoxUser boxuser=matchedusers.get(0);
    	if(boxuser.getClientId()==null||boxuser.getClientId().trim().length()==0){
    		boxuser.setClientId(randomStringGenerator.nextString(10));
    		boxuser.setClientSecret(randomStringGenerator.nextString(23));
    		boxMetadataRepository.updateUser(boxuser);
    	}    	    	    	    
    	LoginInfo loginInfo= new LoginInfo(boxuser);    	
    	List<BoxUserRole> userroles=findBoxUserRole(boxuser);
    	loginInfo.setRoles(userroles);
    	loginInfo.setApplication(selectApplicationFromRoles(userroles));
    	loginInfo.setClientId(randomStringGenerator.nextString(10));
    	loginInfo.setClientSecret(randomStringGenerator.nextString(23));
    	loginInfo.refreshExpiresAt();    	    	
    	synchronized(temporaryLoginInfo){
    		temporaryLoginInfo.put(loginInfo.getClientId(), loginInfo);
    	}    	
    	return loginInfo;    	    	
	}
	
	public LoginInfo findLoginInfoByClientId(String clientId){
		synchronized(temporaryLoginInfo){
			return temporaryLoginInfo.get(clientId);
		}
	}
	public List<LoginInfo> findAllLoginInfoByUserName(String username){
		 List<LoginInfo> ret=new ArrayList<LoginInfo>();
		try{
			synchronized(temporaryLoginInfo){
				if(temporaryLoginInfo.size()==0){
					return ret;
				}
				for(Map.Entry<String, LoginInfo> linfo: temporaryLoginInfo.entrySet()){
					if(linfo.getValue().getUsername().equals(username)){
						ret.add(linfo.getValue());
					}
				}
				
			}
		}
		catch(Exception e){
					logger.error(e+" while getting the logininfo by username",e);					
		}
		return ret;
	}
	public void removeLoginInfoByClientId(String clientId){
		synchronized(temporaryLoginInfo){
				temporaryLoginInfo.remove(clientId);
				logger.info("**********logged out:"+clientId);
		}
	}
	public void removeExpiredLoginInfo(){
		try{
					synchronized(temporaryLoginInfo){
						if(temporaryLoginInfo.size()==0){
							return;
						}
						for(Map.Entry<String, LoginInfo> linfo: temporaryLoginInfo.entrySet()){
							if(linfo.getValue().expired()){
								temporaryLoginInfo.remove(linfo.getKey());
							}
						}
						
					}
		}
		catch(Exception e){
			logger.error(e+" while checking the expired logininfo",e);
		}
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
		
	
	
}
