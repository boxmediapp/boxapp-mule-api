package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;

import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;

public class RefreshLoginTransformer extends BoxRestTransformer{	
	
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
		operator.setUser(null);
		return operator;
	}
	
}
