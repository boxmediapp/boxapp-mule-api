package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.data.image.ImageSummaries;
import uk.co.boxnetwork.model.AppConfig;


import uk.co.boxnetwork.model.Image;
import uk.co.boxnetwork.model.ImageBoxMediaStatus;
import uk.co.boxnetwork.model.ImageSet;
import uk.co.boxnetwork.model.ImageStatus;
import uk.co.boxnetwork.model.MediaCommand;
import uk.co.boxnetwork.util.GenericUtilities;




@Service
public class ImageService {
	static final protected Logger logger=LoggerFactory.getLogger(ImageService.class);
	@Autowired
	ImageRepository imageRepository;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	S3BucketService s3BucketService;
	
    @Autowired
    OperationalLogRepository operationalLogRepository;
	
	public List<uk.co.boxnetwork.data.image.BoxEpisodeData> findBoxEpisodes(SearchParam searchParam){
		return toDataEpisodes(imageRepository.findBoxEpisodes(searchParam),appConfig);
	}
	private  List<uk.co.boxnetwork.data.image.BoxEpisodeData>  toDataEpisodes(List<uk.co.boxnetwork.model.BoxEpisode> eposides, AppConfig appConfig){
		List<uk.co.boxnetwork.data.image.BoxEpisodeData> ret=new ArrayList<uk.co.boxnetwork.data.image.BoxEpisodeData>();		
		for(uk.co.boxnetwork.model.BoxEpisode episode:eposides){
			ret.add(toData(episode));			
		}
		return ret;
	}
	private uk.co.boxnetwork.data.image.BoxEpisodeData toData(uk.co.boxnetwork.model.BoxEpisode episode){
		uk.co.boxnetwork.data.image.BoxEpisodeData dep=new uk.co.boxnetwork.data.image.BoxEpisodeData(episode);
		
		if(episode.getImageSets().size()>0){
			List<uk.co.boxnetwork.data.image.ImageSet> imageSets =new ArrayList<uk.co.boxnetwork.data.image.ImageSet>();
			for(uk.co.boxnetwork.model.ImageSet imgset:episode.getImageSets()){
				imageSets.add(toData(imgset));				
			}
			dep.setImageSets(imageSets);
		}
		return dep;
		
	}
	
	public  uk.co.boxnetwork.data.image.BoxEpisodeData findEpisodeById(Long id){
		uk.co.boxnetwork.model.BoxEpisode episode=imageRepository.findEpisodeById(id);		
		if(episode!=null){
			return toData(episode);			
		}
		else{
			return null;
		}
				
	}
	private uk.co.boxnetwork.data.image.ImageSet toData(uk.co.boxnetwork.model.ImageSet imgset){
		uk.co.boxnetwork.data.image.ImageSet ret=new uk.co.boxnetwork.data.image.ImageSet(imgset);
		List<Image> dbimages=imageRepository.findImagesByImageSet(imgset);
		List<uk.co.boxnetwork.data.image.Image> images=new ArrayList<uk.co.boxnetwork.data.image.Image>();
		for(Image img:dbimages){
			images.add(new uk.co.boxnetwork.data.image.Image(img,appConfig));
		}
		ret.setImages(images);
		return ret;
	}
	
	public  uk.co.boxnetwork.data.image.ImageSet createImageSet(uk.co.boxnetwork.data.image.ImageSet imageSet){
		uk.co.boxnetwork.model.ImageSet dbImageSet=new uk.co.boxnetwork.model.ImageSet();
		uk.co.boxnetwork.model.BoxEpisode episode=imageRepository.findEpisodeByProgrammeNumber(imageSet.getProgrammeNumber());
		if(episode==null){
			throw new RuntimeException("failed to create Image Asset for attached episode not found imageSet=["+imageSet+"]");
		}		
		imageSet.update(dbImageSet);
		dbImageSet.setBoxEpisode(episode);
		imageRepository.persist(dbImageSet);
		return new uk.co.boxnetwork.data.image.ImageSet(dbImageSet);
		
	}
	
	public  uk.co.boxnetwork.data.image.Image createImage(uk.co.boxnetwork.data.image.Image image){		
		uk.co.boxnetwork.model.Image dbImage=new uk.co.boxnetwork.model.Image();
		image.update(dbImage);
		Long  imgSetId=image.getImageSet().getId();
		ImageSet imageSet=imageRepository.findImageSetById(imgSetId);
		dbImage.setImageSet(imageSet);		
		GenericUtilities.processImageBoxMediaStatus(dbImage);		
		imageRepository.persist(dbImage);
		return toData(dbImage);
	}
	public void updateImage(Long id,uk.co.boxnetwork.data.image.Image image){
       Image dbImage=imageRepository.findImageById(id);		
       image.update(dbImage);
		imageRepository.persist(dbImage);
	}
	public uk.co.boxnetwork.data.image.ImageSet deleteImageSetById(Long id, String deletedBy){
		ImageSet dbImageSet=imageRepository.findImageSetById(id);
		uk.co.boxnetwork.data.image.ImageSet imageSet=toData(dbImageSet);
		List<Image> dbImages=imageRepository.findImagesByImageSet(dbImageSet);
		for(Image img:dbImages){
			deleteImageById(img.getId(),deletedBy);
		}
		imageRepository.deleteImageSetById(id);	
		return imageSet;
	}
	
	public uk.co.boxnetwork.data.image.Image deleteImageById(Long id, String deletedBy){
		Image dbimage=imageRepository.findImageById(id);
		uk.co.boxnetwork.data.image.Image image=toData(dbimage);
		imageRepository.deleteImageById(id,deletedBy);
		
		logger.info("The image is deleted:"+image+" by:"+deletedBy);
		String path=image.getFilename().trim();
		if(path.length()==0){
			logger.warn("the image to be deleted does not have filename:"+image);
			return image;
		}
		int ib=path.lastIndexOf(".");
		if(ib<=0 ||(ib+1)>=path.length()){
			logger.error("path is not valid path, please delete the image manually:"+path);
			return image;
		}
		int ie=path.lastIndexOf("/", ib-1);
		if(ie<=0||(ie+2)>=ib){
			logger.error("path is not valid path, seems it is in the root folder, please delete the image manually:"+path);
			return image;	
		}
		logger.info("The image file is deleted from the s3:"+path);
		
		s3BucketService.deleteImagesInImageBucket(path);
		ImageSet  imageSet=dbimage.getImageSet();
		imageRepository.deleteImageSetIfEmpty(imageSet);
		
		return image; 
	}
	public void updateImageSet(Long id, uk.co.boxnetwork.data.image.ImageSet imageSet){
		ImageSet dbImageSet=imageRepository.findImageSetById(id);
		
		imageSet.update(dbImageSet);
		imageRepository.persist(dbImageSet);
	}
	public List<uk.co.boxnetwork.data.image.ImageSet> findImageSets(SearchParam searchParam){
		List<ImageSet> dbImageSets=imageRepository.findImageSet(searchParam);
		return toData(dbImageSets,appConfig);
	}
	private  List<uk.co.boxnetwork.data.image.ImageSet>  toData(List<ImageSet> imagesets, AppConfig appConfig){
		List<uk.co.boxnetwork.data.image.ImageSet> ret=new ArrayList<uk.co.boxnetwork.data.image.ImageSet>();
		for(ImageSet imgset:imagesets){						
			ret.add(toData(imgset));			
		}		
		return ret;
	}

	
	public uk.co.boxnetwork.data.image.ImageSet findImageSetById(Long setid){
		ImageSet dbImageSet=imageRepository.findImageSetById(setid);
		return toData(dbImageSet);		
	}
	uk.co.boxnetwork.data.image.Image toData(Image dbImage){
		uk.co.boxnetwork.data.image.Image ret=new uk.co.boxnetwork.data.image.Image(dbImage,appConfig);
		uk.co.boxnetwork.data.image.ImageSet imgSet=new uk.co.boxnetwork.data.image.ImageSet(dbImage.getImageSet());
		ret.setImageSet(imgSet);
		return ret;
	}
	public uk.co.boxnetwork.data.image.Image findImageById(Long imgid){
		Image dbImage=imageRepository.findImageById(imgid);
		return toData(dbImage);
	}
	public List<uk.co.boxnetwork.data.image.Image>  findImages(SearchParam searchParam){
		List<Image> dbImages=imageRepository.findImages(searchParam);
		List<uk.co.boxnetwork.data.image.Image> ret=new ArrayList<uk.co.boxnetwork.data.image.Image>();
		for(Image dbimg:dbImages){						
			ret.add(toData(dbimg));			
		}		
		return ret;
		
	}
	public List<uk.co.boxnetwork.data.image.ClientImage>  findClientImages(SearchParam searchParam){
		if(searchParam.getImageStatus()==null){
			searchParam.setImageStatus(ImageStatus.APPROVED);
		}
		List<Image> dbImages=imageRepository.findImages(searchParam);
		List<uk.co.boxnetwork.data.image.ClientImage> ret=new ArrayList<uk.co.boxnetwork.data.image.ClientImage>();
		for(Image dbimg:dbImages){	
			uk.co.boxnetwork.data.image.ClientImage cimage=new uk.co.boxnetwork.data.image.ClientImage(dbimg,appConfig);			
			ret.add(cimage);			
		}		
		return ret;
		
	}
	public uk.co.boxnetwork.data.image.ClientImage findClientImageById(Long imgid){
		Image dbImage=imageRepository.findImageById(imgid);
		return new uk.co.boxnetwork.data.image.ClientImage(dbImage,appConfig);
	}
	
	public ImageSummaries buildImageSummaries(){
		
		return imageRepository.buildImageSummaries();
		
	}
	 
	public void copyImageToBoxMediaApp(MediaCommand mediaCommand){		
		Long imageid=mediaCommand.getImageId();
		if(imageid==null){
			logger.error("imageid is null in the mediacommand:"+mediaCommand);
			return;
		}
		Image dbImage=imageRepository.findImageById(imageid);
		String sourcepath=dbImage.getFilename();
		if(sourcepath==null){
			logger.error("failed to upload to media app:imagefile is null in the dbImage:"+dbImage);
			return;
		}
		int ib=sourcepath.lastIndexOf(".");
		if(ib<=0 || (ib+1)>=sourcepath.length()){
			logger.error("failed to upload to media app:imagefie extension is is null in the dbImage:"+dbImage);
			return;
		}
		String fileextension=sourcepath.substring(ib+1);
		
		if(dbImage.getImageSet()==null){
			logger.error("failed to upload to media app:imageset is null in the dbImage:"+dbImage);
			return;
		}
		if(dbImage.getImageSet()==null){
			logger.error("failed to upload to media app:imageset is null in the dbImage:"+dbImage);
			return;
		}
		
		if(dbImage.getImageSet().getBoxEpisode()==null){
			logger.error("failed to upload to media app:boxpeisode is null in the dbImage:"+dbImage);
			return;
		}
		String imageBucket=appConfig.getImageBucket();
		String programmeNumber=dbImage.getImageSet().getBoxEpisode().getProgrammeNumber();
		if(programmeNumber==null){
			logger.error("failed to upload to media app:programme number is null in the dbImage:"+dbImage);
			return;
		}
		String programmeNumberparts[]=programmeNumber.split("/");
		if(programmeNumberparts.length!=2){
			logger.error("failed to upload to media app:programme number part is in wrong format in the dbImage:"+dbImage);
			return;
		}
		String destfilename=programmeNumberparts[0]+"_"+programmeNumberparts[1]+"_001."+fileextension;		
		String destfilepath=appConfig.getImageMasterFolder()+"/"+destfilename;
		s3BucketService.copyFile(imageBucket, sourcepath, imageBucket, destfilepath);
		dbImage.setImageBoxMediaStatus(ImageBoxMediaStatus.UPLOADED);
		imageRepository.persist(dbImage);
	}
	
}
