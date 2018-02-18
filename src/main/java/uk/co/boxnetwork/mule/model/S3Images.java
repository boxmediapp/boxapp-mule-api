package uk.co.boxnetwork.mule.model;

import java.util.ArrayList;
import java.util.List;

import uk.co.boxnetwork.components.S3BucketService;
import uk.co.boxnetwork.data.Episode;
import uk.co.boxnetwork.data.Series;
import uk.co.boxnetwork.data.SeriesGroup;
import uk.co.boxnetwork.data.image.Image;
import uk.co.boxnetwork.data.s3.FileItem;
import uk.co.boxnetwork.model.AppConfig;

import uk.co.boxnetwork.util.GenericUtilities;

public class S3Images {
	
	
	public static void parseDimention(String filename,Image image){
		int dotIndex=filename.lastIndexOf('.');
		if(dotIndex<=0){
			return;
		}
		int bIbdex=filename.lastIndexOf('_',dotIndex);
		if(bIbdex<=0){
			return;
		}				
		String dimensionPart=filename.substring(bIbdex+1,dotIndex);		
		
		int xIndex=dimensionPart.indexOf('x');
		if(xIndex<0 || (xIndex+1)>=dimensionPart.length()){
			 return;
		}					
		String widthPart=dimensionPart.substring(0,xIndex);
		String heightPart=dimensionPart.substring(xIndex+1);
		try{
					int width=Integer.parseInt(widthPart);
					int height=Integer.parseInt(heightPart);
					image.setWidth(width);
					image.setHeight(height);							
		}
		catch(Exception e){							
					System.out.println("expcetion when parsing the width x part in image file");
					e.printStackTrace();
		}
	}
	
	public static List<Image> convertToImages(List<FileItem> fileitems, String s3BaseURL){
		List<Image> images=new ArrayList<Image>();
		for(FileItem fitem:fileitems){
				Image image=new Image();
				image.setFilename(fitem.getFile());
				image.setLastModifiedAt(fitem.getLastModifiedDate());
				image.setS3BaseURL(s3BaseURL);			
				image.setUrl(s3BaseURL+"/"+fitem.getFile());
				parseDimention(fitem.getFile(),image);
				images.add(image);
		}
		return images;
	}
	
	
	public static S3Images createEpisodeS3Images(uk.co.boxnetwork.model.Episode episode,AppConfig appConfig, S3BucketService s3uckerService){
				
		String programeNumber=episode.getCtrPrg();			
		if(programeNumber==null ||programeNumber.length()<3){
			throw new RuntimeException("Episode does not have valid programmeNumber");							
		}
		S3Images s3Images=new S3Images();
		Episode ep=new Episode();
		ep.setId(episode.getId());
		ep.setTitle(episode.getTitle());
		ep.setProgrammeNumber(programeNumber);
		ep.setImageURL(episode.getImageURL());
		s3Images.setEpisode(ep);
		
		String imageBucket=appConfig.getImageBucket();
		String masterImageFolder=appConfig.getImageMasterFolder();
		String publicImageFolder=appConfig.getImagePublicFolder();
		
		String imagefilenameBase=GenericUtilities.materialIdToImageFileName(programeNumber);		
		List<FileItem> masterImages=s3uckerService.listFiles(imageBucket, masterImageFolder+"/"+imagefilenameBase, 0, 200, null);
		List<FileItem> publicImages=s3uckerService.listFiles(imageBucket, publicImageFolder+"/"+imagefilenameBase, 0, 200, null);
		String s3BaseURL=s3uckerService.computeS3BaseURL(null, imageBucket);		
		s3Images.setMasterImages(convertToImages(masterImages,s3BaseURL));
		s3Images.setPublicImages(convertToImages(publicImages,s3BaseURL));				
		return s3Images;
	}
	
	 
	public static S3Images createProgrammeS3Images(uk.co.boxnetwork.model.Series programme,AppConfig appConfig, S3BucketService s3uckerService){
		
		String contractNumber=programme.getContractNumber();			
		if(contractNumber==null ||contractNumber.length()<2){
			throw new RuntimeException("Contract number is not valid in series");							
		}
		S3Images s3Images=new S3Images();
		Series ser=new Series();
		ser.setName(programme.getName());
		ser.setId(programme.getId());
		ser.setContractNumber(contractNumber);
		ser.setImageURL(programme.getImageURL());
		
		s3Images.setProgramme(ser);
		
		String imageBucket=appConfig.getImageBucket();
		String masterImageFolder=appConfig.getImageMasterFolder();
		String publicImageFolder=appConfig.getImagePublicFolder();
		
		String imagefilenameBase=GenericUtilities.materialIdToImageFileName(contractNumber);		
		List<FileItem> masterImages=s3uckerService.listFiles(imageBucket, masterImageFolder+"/"+imagefilenameBase+".", 0, -1, null);
		List<FileItem> tImages=s3uckerService.listFiles(imageBucket, publicImageFolder+"/"+imagefilenameBase+"_", 0, -1, null);
		List<FileItem> publicImages=new ArrayList();
		for(FileItem fitem:tImages){
			 String fpth=fitem.getFile();
			 int dotIndex=fpth.lastIndexOf(".");
			 if(dotIndex<=0 || (dotIndex+1) >= fpth.length()){
				 continue;
			 }
			 String fn=fpth.substring(publicImageFolder.length()+1+imagefilenameBase.length()+1,dotIndex);
			 if(fn.indexOf("_")>=0 || fn.indexOf("/")>=0 || fn.indexOf(".")>=0){
				 continue;
			 }
			 int xIndex=fn.indexOf('x');
			 if(xIndex<=0){
				 continue;
			 }
			 if((xIndex+1)>=fn.length()){
				 continue;
			 }
			 publicImages.add(fitem);
		}				
		String s3BaseURL=s3uckerService.computeS3BaseURL(null, imageBucket);		
		s3Images.setMasterImages(convertToImages(masterImages,s3BaseURL));
		s3Images.setPublicImages(convertToImages(publicImages,s3BaseURL));				
		return s3Images;
	}
	
	public static S3Images  createProgrammeCollectionS3Images(uk.co.boxnetwork.model.SeriesGroup programmeCollection,AppConfig appConfig, S3BucketService s3uckerService){
		
		String collectionTitle=programmeCollection.getTitle();			
		if(collectionTitle==null ||collectionTitle.length()<4){
			throw new RuntimeException("Programme Collection Title is not valid");							
		}
		S3Images s3Images=new S3Images();
		SeriesGroup prgCollection=new SeriesGroup();
		prgCollection.setTitle(programmeCollection.getTitle());
		prgCollection.setId(programmeCollection.getId());		
		prgCollection.setImageURL(programmeCollection.getImageURL());
	
		s3Images.setProgrammeCollection(prgCollection);
		
		String imageBucket=appConfig.getImageBucket();
		String masterImageFolder=appConfig.getImageMasterFolder();
		String publicImageFolder=appConfig.getImagePublicFolder();
		
		String imagefilenameBase=GenericUtilities.toWebsafeTitle(collectionTitle);	
		List<FileItem> masterImages=s3uckerService.listFiles(imageBucket, masterImageFolder+"/"+imagefilenameBase+".", 0, -1, null);
		List<FileItem> tImages=s3uckerService.listFiles(imageBucket, publicImageFolder+"/"+imagefilenameBase+"_", 0, -1, null);
		List<FileItem> publicImages=new ArrayList();
		for(FileItem fitem:tImages){
			 String fpth=fitem.getFile();
			 int dotIndex=fpth.lastIndexOf(".");
			 if(dotIndex<=0 || (dotIndex+1) >= fpth.length()){
				 continue;
			 }
			 String fn=fpth.substring(publicImageFolder.length()+1+imagefilenameBase.length()+1,dotIndex);
			 if(fn.indexOf("_")>=0 || fn.indexOf("/")>=0 || fn.indexOf(".")>=0){
				 continue;
			 }
			 int xIndex=fn.indexOf('x');
			 if(xIndex<=0){
				 continue;
			 }
			 if((xIndex+1)>=fn.length()){
				 continue;
			 }
			 publicImages.add(fitem);
		}				
		String s3BaseURL=s3uckerService.computeS3BaseURL(null, imageBucket);		
		s3Images.setMasterImages(convertToImages(masterImages,s3BaseURL));
		s3Images.setPublicImages(convertToImages(publicImages,s3BaseURL));				
		return s3Images;
	}
	
	private Episode episode;
	private Series programme;
	private SeriesGroup programmeCollection;
	private List<Image> masterImages;
	private List<Image> publicImages;
	
	
	
	
	public Episode getEpisode() {
		return episode;
	}




	public void setEpisode(Episode episode) {
		this.episode = episode;
	}




	public S3Images(){
		
	}




	public List<Image> getMasterImages() {
		return masterImages;
	}


	public void setMasterImages(List<Image> masterImages) {
		this.masterImages = masterImages;
	}


	public List<Image> getPublicImages() {
		return publicImages;
	}


	public void setPublicImages(List<Image> publicImages) {
		this.publicImages = publicImages;
	}

	public Series getProgramme() {
		return programme;
	}

	public void setProgramme(Series programme) {
		this.programme = programme;
	}

	public SeriesGroup getProgrammeCollection() {
		return programmeCollection;
	}

	public void setProgrammeCollection(SeriesGroup programmeCollection) {
		this.programmeCollection = programmeCollection;
	}


	
	
	
	
}
