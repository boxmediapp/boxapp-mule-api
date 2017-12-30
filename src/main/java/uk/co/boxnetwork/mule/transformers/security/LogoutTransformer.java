package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import uk.co.boxnetwork.data.app.LoginInfo;

import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class LogoutTransformer  extends BoxRestTransformer{	
	
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
	protected Object processPOST(MuleMessage message, BoxOperator operator,String outputEncoding){
		try{    				    											
			  LoginInfo loginInfo = operator.getLoginInfo();			
			  if(loginInfo!=null){
				  boxUserService.removeLoginInfoByClientId(loginInfo.getClientId());
			  }			  
			  return loginInfo;			
			}
		catch(Exception e){
			logger.error("error is processing creating user :"+message.getPayload().getClass().getName());
			throw new RuntimeException(e+" whule processing the payload",e);
		}
	}
	
}
