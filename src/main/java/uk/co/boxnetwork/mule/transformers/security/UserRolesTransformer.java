package uk.co.boxnetwork.mule.transformers.security;

import org.mule.api.MuleMessage;



import uk.co.boxnetwork.mule.model.BoxOperator;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;


public class UserRolesTransformer extends BoxRestTransformer{	
		
	@Override
	protected Object processGET(MuleMessage message, BoxOperator operator,String outputEncoding){		
			return boxUserService.getAllUserRoles();							
	}		

	
}
