package uk.co.boxnetwork.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringEscapeUtils;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mysql.jdbc.PreparedStatement.ParseInfo;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import sun.misc.BASE64Encoder;
import uk.co.boxnetwork.data.bc.BCAnalyticData;
import uk.co.boxnetwork.data.bc.BCSchedule;
import uk.co.boxnetwork.data.s3.S3FileSignatureData;
import uk.co.boxnetwork.data.soundmouse.SoundMouseData;
import uk.co.boxnetwork.data.soundmouse.SoundMouseItem;
import uk.co.boxnetwork.model.AdvertisementRule;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.AvailabilityWindow;
import uk.co.boxnetwork.model.CuePoint;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.MatchAdvertBreakType;
import uk.co.boxnetwork.model.MatchContentType;
import uk.co.boxnetwork.model.MetadataStatus;
import uk.co.boxnetwork.model.ScheduleEvent;
import uk.co.boxnetwork.model.Series;
import uk.co.boxnetwork.model.SeriesGroup;
import uk.co.boxnetwork.model.VideoStatus;
import uk.co.boxnetwork.mule.components.LoadResourceAsInputStream;

public class GenericUtilities {
	private static final Logger logger=LoggerFactory.getLogger(GenericUtilities.class);
	
	
	
	static {
	    try {
	        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
	        field.setAccessible(true);
	        field.set(null, java.lang.Boolean.FALSE);
	    } catch (Exception ex) {
	    }
	} 
	
	public static StrongTextEncryptor getEncryptor(String encruptionKey){
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
 	    textEncryptor.setPassword(encruptionKey);
 	    return textEncryptor;
 	   
	}
	public static String encrypt(String encruptionKey,String content){
		StrongTextEncryptor textEncryptor =getEncryptor(encruptionKey); 
		return textEncryptor.encrypt(content);		 	   
	}
	public static String decrypt(String encruptionKey,String content){
		StrongTextEncryptor textEncryptor =getEncryptor(encruptionKey); 
		return textEncryptor.decrypt(content);		 	   
	}
	
  public static boolean equalString(String v1, String v2){
	  if(v1==null){
		   return v2==null;		   
	  }
	  else 
		  return v1.equals(v2);
  }
  public static boolean isNotValidCrid(String v){
	  if(isNotAValidId(v)){
		  return true;
	  }
	  v=v.trim();
	  if(v.length()<5){
		  return true;		  
	  }
	  return false;
  }
  public static boolean isNotValidContractNumber(String v){
	  if(isNotAValidId(v)){
		  return true;
	  }
	  v=v.trim();
	  if(v.length()<5){
		  return true;		  
	  }
	  return false;
  }
  public static boolean isNotValidTitle(String v){
	  if(isNotAValidId(v)){
		  return true;
	  }
	  v=v.trim();
	  if(v.length()<3){
		  return true;		  
	  }
	  return false;
  }
  public static boolean isNotAValidId(String v){
	  if(v==null){
		  return true;
	  }
	 if(v.length()==0)
		 return true;
	 v=v.trim();
	 if(v.length()==0)
		 return true;
	 if(v.equals("0")){
		 return true;
	 }
	 return false;	 
  }
  public static boolean isNotValidName(String v){
	  return isNotValidTitle(v);
  }
  public static String readStream(InputStream is) {
	    StringBuilder sb = new StringBuilder(512);
	    try {
	        Reader r = new InputStreamReader(is, "UTF-8");
	        int c = 0;
	        while ((c = r.read()) != -1) {
	            sb.append((char) c);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return sb.toString();
	}
  public static String readFileContent(String file){
	  InputStream in=LoadResourceAsInputStream.class.getClassLoader().getResourceAsStream(file);
	  return readStream(in);
	  
  }
  public static boolean isEmpty(String v){
	  if(v==null){
		  return true;
	  }
	  v=v.trim();
	  return v.length()==0;
  }
  public static Integer bcInteger(String v){
	  if(isEmpty(v)){
		  return null;
	  }
	  try{
		  return Integer.valueOf(v);
	  }
	  catch(Exception e){
		  logger.error(e+ " converting to integer:"+v,e);
		  return null;
	  }
	  
  }
  public static String bcString(String v){
	  if(isEmpty(v)){
		  return null;
	  }
	  return v;
	  
  }
  
  public static String getValueInMap(Map<String, Object> messageMap, String paths){
	  String[] path=paths.split("\\.");
	  return getValueInMap(messageMap,path);
	  
  }
  public static String getValueInMap(Map<String, Object> messageMap, String path[]){
	  return getValueInMap(messageMap,path,0);
  }
  private static Object getValueInMapByVar(Map<String, Object> messageMap, String variableName){
	  
    int ib=variableName.indexOf("[");
    int ie=variableName.indexOf("]");
    if(ib!=-1 && ie>ib){
    	String indexString=variableName.substring(ib+1,ie);
    	String varName=variableName.substring(0,ib);
    	int ind=Integer.valueOf(indexString);
    	Object obj=messageMap.get(varName);
    	if(obj instanceof ArrayList){
    		List<Object> objarray=(List<Object>)obj;
    		if(objarray.size()<=ind){
    			logger.warn(" array index out of boundary in getValueInMapByVar() varName=["+varName+"]ind=["+ind+"]but array size is:"+objarray.size());
    			return null;
    		}
    		else
    			return objarray.get(ind);
    	}
    	return obj;
    	
    }
    else{
    	return messageMap.get(variableName);
    }
	  
	  
	  
  }
  private  static String getValueInMap(Map<String, Object> messageMap, String path[], int index){
	  if(index>=path.length){
		  return null;
		  
	  }
	  
	  Object obj=getValueInMapByVar(messageMap,path[index]);
	  if(obj==null){
		  return null;
	  }
	  if((index+1)>=path.length){
		  if(obj instanceof String){			  
			  return (String)obj;
		  }
		  else{
			  logger.warn("Unpexected type on "+path+":"+index+":"+obj.getClass().getName());
			  return null;			  
		  }
	  }
	  else if(obj instanceof Map){
		  Map<String, Object> objMap=(Map<String, Object>)obj;
		  return getValueInMap(objMap, path,index+1);
	  }
	  else{
		  logger.warn("Unpexected type on "+path+":"+index+":"+obj.getClass().getName());
		  return null;
		  
	  }
	  
  }
  
  public static String materialIdToVideoFileName(String materialID){
	  String matParts[]=materialID.split("/");
		 StringBuilder filenameBuilder=new StringBuilder();
		 filenameBuilder.append("V");
		 int counter=0;
		 for(String mpart:matParts){
			 filenameBuilder.append("_");
			 filenameBuilder.append(mpart);
			 counter++;
			 if(counter>2){
				 break;
			 }
	     }
		 return filenameBuilder.toString();
  }
  public static String fileNameToMaterialID(String filename){
	  if(filename.startsWith("V_") || filename.startsWith("v_")){
		  String fpath=filename.substring(2);
		  int ie=fpath.indexOf(".");
		  if(ie!=-1){
			  fpath=fpath.substring(0,ie);
		  }
		  String matParts[]=fpath.split("_");
		  StringBuilder matidBuilder=new StringBuilder();			 
			 int counter=0;
			 for(String mpart:matParts){
				 if(counter>0){
					 matidBuilder.append("/");
				 }				 
				 matidBuilder.append(mpart);
				 counter++;
				 if(counter>1){
					 break;
				 }
		     }
			 return matidBuilder.toString();
	  }		
	  else
		  return null;
  }
  
  public static String getProgrammeNumber(uk.co.boxnetwork.data.Episode episode){
	  
	  if(isNotValidCrid(episode.getProgrammeNumber())){
		  String materialId=episode.getMaterialId();
		  String matParts[]=materialId.split("/");
		  if(matParts.length<=2){
			  return materialId;
		  }
		  else{
			  return matParts[0]+"/"+matParts[1];
		  }
	  }
	  else
		  return episode.getProgrammeNumber();
	  
	  
  }
  public static String getContractNumber(uk.co.boxnetwork.data.Episode episode){
	  String pid=episode.getProgrammeNumber();
	  if(isNotValidCrid(pid)){
		  pid=episode.getMaterialId();
	  }
	  if(isNotValidCrid(pid)){
		  return null;
	  }
	  String matParts[]=pid.split("/");
	  return matParts[0];
  }
  public static String composeCtrPrg(String contractNumber, String episodeNumber){
	  return contractNumber+"/"+episodeNumber;
  }
  public static Integer retrieEpisodeNumberFromProgrammeNumber(String programmeNumber){
	  try{
		  if(programmeNumber==null||programmeNumber.length()==0){
			  return null;
		  }
		  String parts[]=programmeNumber.split("/");
		  if(parts.length<=1){
			  logger.info("failed to retrieve the episode nimber from the programeNuber:"+programmeNumber);
			  return null;
		  }
		  return Integer.parseInt(parts[parts.length-1]);		  
	  }
	  catch(Exception e){
		  logger.error("error retrieving the episode number from the programmeNumber:"+programmeNumber);
		  return null;
	  }
  }
  public static VideoStatus calculateVideoStatus(Episode episode){
	   return  calculateVideoStatus(episode, episode.getIngestSource(), episode.getIngestProfile());  
  }
  public static boolean transcodeConditionNotStasfied(VideoStatus status){
  	return status==VideoStatus.MISSING_PROFILE || status==VideoStatus.MISSING_VIDEO || status == VideoStatus.NO_PLACEHOLDER;
  }
  public static VideoStatus calculateVideoStatus(Episode episode, String previousIngestSource, String previousIngestProfile){
	   if(isEmpty(episode.getIngestSource())){
			return VideoStatus.MISSING_VIDEO;		
	   }
	   else if(isEmpty(episode.getIngestProfile())){
			return VideoStatus.MISSING_PROFILE;
	   }
	   else if(isEmpty(episode.getBrightcoveId())){
			return VideoStatus.NO_PLACEHOLDER;
	   }
	   else if((!episode.getIngestSource().equals(previousIngestSource)) || (!episode.getIngestProfile().equals(previousIngestProfile))){
		    return VideoStatus.NEEDS_RETRANSCODE;
	   }
	   else if(episode.getEpisodeStatus()==null || episode.getEpisodeStatus().getVideoStatus()==null || transcodeConditionNotStasfied(episode.getEpisodeStatus().getVideoStatus())){		   
		   return VideoStatus.NEEDS_TRANSCODE;
	   }
	   else{
		   return null;
	   }
		   	   
 }
  public static MetadataStatus calculateMetadataStatus(Episode episode){
	  if(isEmpty(episode.getBrightcoveId())){
			return MetadataStatus.NEEDS_TO_CREATE_PLACEHOLDER;			
	   }
	  else
		    return null; 
  }
  public static String validateEpisode(uk.co.boxnetwork.data.Episode episode){
		if(isNotValidCrid(episode.getProgrammeNumber())){
			   return "programNumber is not valid";
		}
		if(isNotValidTitle(episode.getTitle())){
			   return "title is not valid";
		}
		if(isNotValidCrid(episode.getMaterialId())){
			episode.setMaterialId(episode.getProgrammeNumber());
		}
		return null;		
	}
  public static String toWebsafeTitle(String title){
	  if(title==null||title.length()==0){
		  return title;
	  }
	  return   title.toLowerCase().replaceAll("[&\\/\\\\#,\\ +()$~%.'\":*?<>{}]","-");	  	  
  }
  public static String fromWebsafeTitle(String websafeTitle){
	  if(websafeTitle==null||websafeTitle.length()==0){
		  return websafeTitle;
	  }	  
	  return websafeTitle.replace("-", " ");	  
  }
  public static String partsToMatId(String [] parts, int first){
	  if(first>=parts.length){
		  return null;
	  }
	  StringBuilder builder=new StringBuilder();
	  builder.append(parts[first]);	  
	  for(int i=first+1;i<parts.length;i++){
		  builder.append("/");
		  builder.append(parts[i]);
	  }
	  return builder.toString();
  }
  public static String materialIdToImageFileName(String materialId){
	  if(materialId==null){
		  return null;
	  }
	  else{
		  return materialId.replace("/", "_");		  
	  }
  }
  public static String fixChannel(String channelName){
	  if("Box Hits (SmashHits)".equals(channelName)){		  
		  return "Box Hits";
	  }
	  else if("Box Upfront (Heat)".equals(channelName)){
		  return "Box Upfront";
	  }
	 return channelName;
  }
  public static String[] commandDelimitedToArray(String tags){
	  if(tags==null){
			return null;			
	  }
	  tags=tags.trim();
	  if(tags.length()==0){
		  return null;
	  }
	  String[] tagArray=tags.split(",");
	  int emptyCounter=0;
	  
	  for(int i=0;i<tagArray.length;i++){
		  	tagArray[i]=tagArray[i].trim();
		  	if(tagArray[i].length()==0){
		  		emptyCounter++;
		  	}
	  }
	  if(emptyCounter==0){
		  return tagArray;
	  }
	  if(tagArray.length==emptyCounter){
		  return null;
	  }
	  String[] notEmptyValues=new String[tagArray.length-emptyCounter];
	  int counter=0;
	  for(String c:tagArray){
		  if(c.length()>0){
			  notEmptyValues[counter++]=c;
		  }
	  }
	  return notEmptyValues;
  }
  
  public static String arrayToSeparatedString(String tags[], String separator){
		if(tags==null ||tags.length==0){
			return null;	
		}
		else{				
				List<String> added=new ArrayList<String>();
				StringBuilder builder=new StringBuilder();
				for(int i=0;i<tags.length;i++){					 
					 tags[i]=tags[i].trim();
					 if(tags[i].length()==0){
						 continue;
					 }
					 if(added.contains(tags[i])){
						 	continue;					
					 }
					 if(added.size()>0){
						 builder.append(separator);
					 }
					 builder.append(tags[i]);
					 added.add(tags[i]);
				}
				String retvalue=builder.toString();
				if(retvalue.length()==0){
					return null;
				}
				else{
						return retvalue;
					}
		}
  
  }
	
  
  

  public static Date nextYearsDate(Integer years){
	    Calendar calendar=Calendar.getInstance();		
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)+years);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		return calendar.getTime();
  }
    public static String getPosterImageURL(AppConfig appConfig, String imagename){
    	return getImageWithSize(appConfig, imagename, 1920, 1080);
    }
    public static String getThumbnailImageURL(AppConfig appConfig, String imagename){
    	return getImageWithSize(appConfig, imagename, 320, 180);
    }
    public static String getImageType(int width, int height){
    	if(width==1920 && height==1080){
    		return "poster";
    	}
    	else if(width==320 && height==180){
    		return "thumbnail";
    	} 
    	else {
    		return null;
    	}
    		
    }
	public static String getImageWithSize(AppConfig config, String imagename, int width, int height){
		String basename=imagename;
		String ext="";
		int ib=imagename.indexOf(".");
						
		if(ib!=-1){
			 basename=imagename.substring(0,ib);
			 ext=imagename.substring(ib+1);
		}
		String template=config.getImagetemplateurl();
		String imgURL=template.replace("{image_name}",basename);
		imgURL=imgURL.replace("{width}", String.valueOf(width));
		imgURL=imgURL.replace("{height}", String.valueOf(height));
		imgURL=imgURL.replace("{ext}", ext);
		String urltimestamp=String.valueOf(new Date().getTime());
		imgURL=imgURL.replace("{urltimestamp}", urltimestamp);
		return imgURL;
	}
	public static String getS3ImageWithSize(AppConfig config, String imagename, int width, int height){
		String url=getImageWithSize(config,imagename,width,height);
		int ib=url.lastIndexOf("/");
		if(ib<=0){
			logger.error("could not create s3 url:"+url);			
			return url;
		}
		String imagefile=url.substring(ib+1);
		url=config.getS3imagesURL()+"/"+config.getImagePublicFolder()+"/"+imagefile;
		return url;		
	}
	public static int getURLHttpCode(String urlpath){
		try{
			URL url = new URL(urlpath);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			return connection.getResponseCode();			
		}
		catch(Exception e){
			logger.error("e+ while testing the url:"+urlpath,e);
			return -1;			
		}
	}
	
	
	private static freemarker.template.Configuration freemarker_config=null;
	public static void initFreeMarker() throws IOException{
		if(freemarker_config!=null){
			return;
		}
		freemarker_config = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
		
		//freemarker_config.setDirectoryForTemplateLoading(file);
		freemarker_config.setClassForTemplateLoading(GenericUtilities.class, "/data");
		freemarker_config.setDefaultEncoding("UTF-8");
		freemarker_config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}
	public static Template getTemplate(String templateName) throws IOException{		
		initFreeMarker();		
		Template template=freemarker_config.getTemplate(templateName);
		return template;
	}
	public static String getSoundmouseHeader(Episode episode) throws Exception{
		episode.makeSoundMouseFriendy();
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("episode", episode);
		return executeTemplateFile("/soundmouse/soundmouse-header.xml",root);		
	}
	public static String getS3UploadPolicy(S3FileSignatureData data) throws Exception{		
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("data", data);
		return executeTemplateFile("/s3/policy.json",root);		
	}
	public static String getSoundmouseSmurf(SoundMouseData soundMouseData)  throws Exception{
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("soundMouseData", soundMouseData);
		return executeTemplateFile("/soundmouse/soundmouse-smurf.xml",root);
	}
	public static String getSoundmouseSmurfForCuepoint(SoundMouseData soundMouseData, SoundMouseItem soundMouseItem)  throws Exception{
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("soundMouseData", soundMouseData);
		root.put("soundMouseItem", soundMouseItem);
		return executeTemplateFile("/soundmouse/soundmouse-smurf-item.xml",root);
	}
	public static String createFullSmurfContent(String smurfContent, String smurfItemsContent){
		String beginIdentifier="<mediaList>";
		String endIdentifier="</mediaList>";
		int ib=smurfContent.indexOf(beginIdentifier);
		if(ib==-1){
			throw new RuntimeException("wrong format in the smurffile template, could not find mediaList");
		}
		ib+=beginIdentifier.length();
		int ie=smurfContent.indexOf(endIdentifier,ib);
		if(ie==-1){
			throw new RuntimeException("wrong format in the smurffile template, could not find the ending mediaList tag");
		}
		return smurfContent.substring(0,ib)+smurfItemsContent+smurfContent.substring(ie);		
	}
	public static void createFullSmurfContent(String smurfContent,File smurfItemFile, File smurFile) throws Exception{
		BufferedReader reader=null;
		BufferedWriter writer=null;
		try{		
				reader=new BufferedReader(new FileReader(smurfItemFile));
				writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(smurFile), "utf-8"));
				String beginIdentifier="<mediaList>";
				String endIdentifier="</mediaList>";
				int ib=smurfContent.indexOf(beginIdentifier);
				if(ib==-1){
					throw new RuntimeException("wrong format in the smurffile template, could not find mediaList");
				}
				ib+=beginIdentifier.length();
				int ie=smurfContent.indexOf(endIdentifier,ib);
				if(ie==-1){
					throw new RuntimeException("wrong format in the smurffile template, could not find the ending mediaList tag");
				}
				
				writer.write(smurfContent.substring(0,ib));
				String line;
				
				while((line=reader.readLine())!=null){
					writer.write(line);
					writer.write("\n");
				}
				reader.close();
				writer.write(smurfContent.substring(ie));
				writer.close();
				logger.info("succesfully created:"+smurFile);
		}
		finally{
			if(reader!=null){
				reader.close();				
			}
			if(writer!=null){
				writer.close();				
			}
		}
	}
	public static String executeTemplateFile(String templateFileName,Map<String, Object> root) throws Exception{
		Template temp = getTemplate(templateFileName);		
		StringWriter writer=new StringWriter();
	    temp.process(root, writer);
	    writer.close();
	    return writer.toString();
	}
	
	public static String makeSoundMouseFriendy(String value){
		if(value==null|| value.length()==0){
			return null;
		}
		return StringEscapeUtils.escapeXml(value);
	}
	public static String makeDurationMouseFriendy(String value){
		if(value==null|| value.length()==0){
			return null;			
		}
		String parts[]=value.split(":");
		if(parts.length<4){
			return value;
		}
		return parts[0]+":"+parts[1]+":"+parts[2];
	}
	
	public static boolean episodeHasSeries(Episode episode){
		if(episode==null){
			return false;			
		}
		if(episode.getSeries()==null){
			return false;
		}
		if(Series.DEFAULT_SERIES_TITLE.equals(episode.getSeries().getName())){
			return false;
		}
		return true;
	}
	public static boolean episodeHasSeriesGroup(Episode episode){
		if(episode==null){
			return false;			
		}
		if(episode.getSeries()==null){
			return false;
		}
		if(Series.DEFAULT_SERIES_TITLE.equals(episode.getSeries().getName())){
			return false;
		}
		if(episode.getSeries().getSeriesGroup()==null){
			return false;
		}
		if(SeriesGroup.DEFAULT_SERIES_GROUP_TITLE.equals(episode.getSeries().getSeriesGroup().getTitle())){
			return false;
		}
		return true;
	}
	
	static SimpleDateFormat standardDateFormatter=new SimpleDateFormat("yyyy-MM-dd");	
	public static String toStandardDateFormat(Date datevalue){
		return standardDateFormatter.format(datevalue);
	}
	
	
	static SimpleDateFormat soundMouseSmurfFileDateFormatter=new SimpleDateFormat("yyyyMMdd-HHmmss_MMMyyyy");	
	public static String toSoundMouseSmurfFileFormat(Date datevalue){
		return soundMouseSmurfFileDateFormatter.format(datevalue);
	}
	
	static SimpleDateFormat sourceMouseformatter=new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");
	public static String toSoundmouseDateTimeFormat(Date datevalue){
		return sourceMouseformatter.format(datevalue);
	}
	
	static SimpleDateFormat utcFormatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static String toFullUTCFormat(Date datevalue){
		return utcFormatter.format(datevalue);
	}
	 
	
	public static boolean shouldReportOnCuepoint(Episode episode){
		if(episode.getCuePoints()==null){
			return false;
		}
		if(episode.getCuePoints().size()==0){
			return false;
		}
		boolean shouldReportOnCuepoint=false;
		for(CuePoint cuepoint:episode.getCuePoints()){
			if(!isNotValidCrid(cuepoint.getMateriaId())){
				shouldReportOnCuepoint=true;
			}
		}
		return shouldReportOnCuepoint;
	}
	public static Integer toInteter(String value, String errormesage){
		if(value==null){
			return null;			
		}		
		try{
			return Integer.valueOf(value.trim());
		}
		catch(Exception e){
			logger.error(e+" "+errormesage +"value=["+value+"]",e);
			return null;
		}
	}
	public static com.fasterxml.jackson.databind.ObjectMapper createObjectMapper(){
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_NULL);	
			objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
			objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
			objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
			objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
			objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper;
			
			
			
	}
	public static String getFileExtFromURL(String url){
		if(url==null){
			return null;			
		}
		int ib=url.lastIndexOf(".");
		if(ib<10){
			return null;
		}
		String ext=null;
	    int ie=url.indexOf("?", ib);	    
		if(ie!=-1){
			ext=url.substring(ib+1,ie);			
		}
		else
			ext=url.substring(ib+1);
		if(ext.length()<2){
			return null;
		}
		if(ext.matches("[A-Za-z]+")){
			return ext;
		}
		else{
			return null;
		}
			
	
	}
	
	
	public static AvailabilityWindow calculateEffectiveAvailabilityWindow(Episode episode,List<ScheduleEvent> schedules){
		Set<AvailabilityWindow> availabilityWindows=episode.getAvailabilities();			
		Date today=new Date();
		AvailabilityWindow 	availabilityWindow=null;
		if(availabilityWindows!=null && availabilityWindows.size()>0){
				if(availabilityWindows.size()==1){
					availabilityWindow=availabilityWindows.iterator().next();
					return availabilityWindow;
				}
				else{
					List<AvailabilityWindow> sortedAvailabilityWindows=new ArrayList<AvailabilityWindow>(availabilityWindows);
					Collections.sort(sortedAvailabilityWindows);
					
					for(int i=0;i<sortedAvailabilityWindows.size();i++){
						availabilityWindow=sortedAvailabilityWindows.get(i);
						if(today.before(new Date(availabilityWindow.getEnd()))){																
							return availabilityWindow;															
						}							
					}
					return availabilityWindow;
				}							
		}
	   if(schedules==null||schedules.size()==0){
			return null;				
		}
		Date fromDate=schedules.get(0).getScheduleTimestamp();
		Date toDate=fromDate;
		
		for(int i=1;i<schedules.size();i++){
			Date d=schedules.get(i).getScheduleTimestamp();
			if(d.before(fromDate)){
				fromDate=d;
			}
			if(d.after(toDate)){
				toDate=d;
			}				
		}
		AvailabilityWindow scheduleAvail=new AvailabilityWindow();
		scheduleAvail.setStart(fromDate.getTime());
		scheduleAvail.setEnd(toDate.getTime());
		return 	scheduleAvail;		
	}

	public static byte[] hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes("UTF-8"));
            return md.digest();
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute hash while signing request: " + e.getMessage(), e);
        }
    }
	public static byte[] HmacSHA256(String stringData, byte[] key) {
		String algorithm="HmacSHA256";					      
        try {
            byte[] data = stringData.getBytes("UTF-8");
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }
	 public static String awsV4Sign(String secret, String dateStamp, String regionName, String serviceName, String contentToSign) throws Exception  {
		 String SCHEME = "AWS4";
		 String TERMINATOR = "aws4_request";
	        byte[] kSecret = (SCHEME + secret).getBytes();
	        byte[] kDate    = HmacSHA256(dateStamp, kSecret);
	        byte[] kRegion  = HmacSHA256(regionName, kDate);
	        byte[] kService = HmacSHA256(serviceName, kRegion);
	        byte[] kSigning = HmacSHA256(TERMINATOR, kService);
	        byte[] contentInBytes = HmacSHA256(contentToSign, kSigning);
	        return toHex(contentInBytes);	        	
	 }
	 public static String toBase64(String content) throws UnsupportedEncodingException{
		 byte[]   bytesEncoded = Base64.getEncoder().encode(content.getBytes());
		return new String(bytesEncoded);
	 }
	 public static String fromBase64(String content) throws UnsupportedEncodingException{
		 byte[] valueDecoded= Base64.getDecoder().decode(content.getBytes());
		 return new String(valueDecoded);
	 }
	 public static String toHex(byte[] data) {
	        StringBuilder sb = new StringBuilder(data.length * 2);
	        for (int i = 0; i < data.length; i++) {
	            String hex = Integer.toHexString(data[i]);
	            if (hex.length() == 1) {
	                // Append leading zero.
	                sb.append("0");
	            } else if (hex.length() == 8) {
	                // Remove ff prefix from negative numbers.
	                hex = hex.substring(6);
	            }
	            sb.append(hex);
	        }
	        return sb.toString().toLowerCase(Locale.getDefault());
	    }
	    public static byte[] fromHex(String hexData) {
	        byte[] result = new byte[(hexData.length() + 1) / 2];
	        String hexNumber = null;
	        int stringOffset = 0;
	        int byteOffset = 0;
	        while (stringOffset < hexData.length()) {
	            hexNumber = hexData.substring(stringOffset, stringOffset + 2);
	            stringOffset += 2;
	            result[byteOffset++] = (byte) Integer.parseInt(hexNumber, 16);
	        }
	        return result;
	    }
	    public static List<AdvertisementRule> select(List<AdvertisementRule> advertisementRules,MatchAdvertBreakType breakType,MatchContentType matchContentType, Long duration){
	    	List<AdvertisementRule> ret=new ArrayList<AdvertisementRule>();	    	
	    	for(AdvertisementRule advertRule:advertisementRules){
	    		if(breakType==null|| breakType==MatchAdvertBreakType.ALL){
	    			if(advertRule.getAdvertBreakType()!=MatchAdvertBreakType.ALL){
	    				continue;
	    			}
	    		}
	    		else if(breakType!=advertRule.getAdvertBreakType()){
	    			continue;
	    		}
	    		if(matchContentType==null|| matchContentType==MatchContentType.ALL){
	    			if(advertRule.getContentType()!=MatchContentType.ALL){
	    				continue;
	    			}
	    		}
	    		else if(matchContentType!=advertRule.getContentType()){
	    			continue;
	    		}
	    		
	    		if(duration==null){
	    			if((advertRule.getContentMaximumDuration()!=null && advertRule.getContentMaximumDuration()>0) || (advertRule.getContentMinimumDuration()!=null && advertRule.getContentMinimumDuration()>0)){
	    				continue;
	    			}
	    		}
	    		else{
	    			if(advertRule.getContentMinimumDuration()!=null && duration< advertRule.getContentMinimumDuration()){
	    				continue;
	    			}
	    			if(advertRule.getContentMaximumDuration()!=null && duration > advertRule.getContentMaximumDuration()){
	    				continue;
	    			}
	    		}
	    		ret.add(advertRule);
	    	}
	    	return ret;
	    }
	    public static AdvertisementRule select(Episode episode,List<AdvertisementRule> advertisementRules,MatchAdvertBreakType breakType){	    	
	    	Long duration=null;
	    	if(episode.getDurationUploaded()!=null){
	    		duration=episode.getDurationUploaded().longValue();
	    	}
	    	if(duration==null && episode.getDurationScheduled()!=null){
	    		duration=episode.getDurationScheduled().longValue();
	    	}
	    	MatchContentType matchContentType=MatchContentType.match(episode.getContentType());
	    	List<AdvertisementRule> matchedReules=select(advertisementRules,breakType,matchContentType, duration);
	    	if(matchedReules.size()>0){
	    		return matchedReules.get(0);
	    	}
	    	matchedReules=select(advertisementRules,breakType,matchContentType, null);
	    	if(matchedReules.size()>0){
	    		return matchedReules.get(0);
	    	}
	    	if(matchContentType!=MatchContentType.ALL){	    		
		    	matchedReules=select(advertisementRules,breakType,MatchContentType.ALL, duration);
		    	if(matchedReules.size()>0){
		    		return matchedReules.get(0);
		    	}
		    	matchedReules=select(advertisementRules,breakType,MatchContentType.ALL, null);
		    	if(matchedReules.size()>0){
		    		return matchedReules.get(0);
		    	}
	    	}
	    	
	    	
	    	if(breakType!=MatchAdvertBreakType.ALL){
	    		matchedReules=select(advertisementRules,MatchAdvertBreakType.ALL,matchContentType, duration);
		    	if(matchedReules.size()>0){
		    		return matchedReules.get(0);
		    	}
		    	matchedReules=select(advertisementRules,MatchAdvertBreakType.ALL,matchContentType, null);
		    	if(matchedReules.size()>0){
		    		return matchedReules.get(0);
		    	}
		    	if(matchContentType!=MatchContentType.ALL){
		    		
			    	matchedReules=select(advertisementRules,MatchAdvertBreakType.ALL,MatchContentType.ALL, duration);
			    	if(matchedReules.size()>0){
			    		return matchedReules.get(0);
			    	}
			    	matchedReules=select(advertisementRules,MatchAdvertBreakType.ALL,MatchContentType.ALL, null);
			    	if(matchedReules.size()>0){
			    		return matchedReules.get(0);
			    	}
		    	}
		    	
	    		
	    	}
	    	
	    	return null;
	    }
	    
	    
}

