package uk.co.boxnetwork.mule.transformers.c4;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleMessage;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.boxnetwork.components.ImportC4ScheduleService;
import uk.co.boxnetwork.components.MetadataService;
import uk.co.boxnetwork.data.ErrorMessage;
import uk.co.boxnetwork.data.ImportScheduleRequest;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.mule.transformers.BoxRestTransformer;
import uk.co.boxnetwork.mule.util.MuleRestUtil;

public class C4ScheduleTransformer extends BoxRestTransformer{

	@Autowired
	ImportC4ScheduleService c4schduleService;
	
	
	@Override
	protected Object processGET(MuleMessage message, String outputEncoding){				
		ImportScheduleRequest request=new ImportScheduleRequest();
		request.setChannelId("1865244993");
		request.setFromDate("2016-07-08");
		request.setToDate("2016-07-10");
		request.setInfo("Episode");
		request.setType("Press");		
		return c4schduleService.requestSchedulService(request);		
	}
	
}
