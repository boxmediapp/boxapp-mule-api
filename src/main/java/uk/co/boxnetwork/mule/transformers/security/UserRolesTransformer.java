package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;



import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;


public class UserRolesTransformer extends BoxRestTransformer{	
	
	protected boolean checkGETAccess(BoxOperator operator){
	    return true;		    
    }
	protected boolean checkPOSTAccess(BoxOperator operator){
	   return true;				 
	}	
	protected boolean checkPUTAccess(BoxOperator operator){	
	   return true;	  
	}
	protected boolean checkDELETEAccess(BoxOperator operator){			
	   return true;	   
	}	
	protected boolean checkPATCHAccess(BoxOperator operator){	   					
	   return operator.checkPATCHAccess();		   
	}
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){
		if(operator.checkAdminAccess()){
			return boxUserService.getAllUserRoles();
		}
		else{
				return deniedAccessMessage(message,outputEncoding);				
		}					
	}
	
	

	
}
