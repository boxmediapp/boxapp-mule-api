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
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.Image;
import uk.co.boxnetwork.model.ImageSet;




@Service
public class ImageService {
	static final protected Logger logger=LoggerFactory.getLogger(ImageService.class);
	@Autowired
	ImageRepository imageRepository;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	S3BucketService s3BucketService;
	

	
	public List<uk.co.boxnetwork.data.image.Episode> findEpisodesNotProcessed(SearchParam searchParam){
		return toDataEpisodes(imageRepository.findEpisodesNotProcessed(searchParam),appConfig);
	}
	private  List<uk.co.boxnetwork.data.image.Episode>  toDataEpisodes(List<Episode> eposides, AppConfig appConfig){
		List<uk.co.boxnetwork.data.image.Episode> ret=new ArrayList<uk.co.boxnetwork.data.image.Episode>();
		for(Episode episode:eposides){
			uk.co.boxnetwork.data.image.Episode dep=new uk.co.boxnetwork.data.image.Episode(episode);			
			ret.add(dep);			
		}
		return ret;
	}
	
	public  uk.co.boxnetwork.data.image.Episode findEpisodeById(Long id){
		Episode episode=imageRepository.findEpisodeById(id);
		if(episode!=null){
			uk.co.boxnetwork.data.image.Episode ret=new uk.co.boxnetwork.data.image.Episode(episode);
			List<uk.co.boxnetwork.model.ImageSet> imageSetsdb=imageRepository.findImageSetByEpisodeId(episode.getId());			
			ret.setImageSets(toData(imageSetsdb, appConfig));
			return ret;
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
		imageSet.update(dbImageSet);
		imageRepository.persist(dbImageSet);
		return new  uk.co.boxnetwork.data.image.ImageSet(dbImageSet);
	}
	
	public  uk.co.boxnetwork.data.image.Image createImage(uk.co.boxnetwork.data.image.Image image){		
		uk.co.boxnetwork.model.Image dbImage=new uk.co.boxnetwork.model.Image();
		image.update(dbImage);
		Long  imgSetId=image.getImageSet().getId();
		ImageSet imageSet=imageRepository.findImageSetById(imgSetId);
		dbImage.setImageSet(imageSet);
		imageRepository.persist(dbImage);
		return toData(dbImage);
	}
	public void updateImage(Long id,uk.co.boxnetwork.data.image.Image image){
       Image dbImage=imageRepository.findImageById(id);		
       image.update(dbImage);
		imageRepository.persist(dbImage);
	}
	public uk.co.boxnetwork.data.image.Image deleteImageById(Long id){
		Image dbimage=imageRepository.findImageById(id);
		uk.co.boxnetwork.data.image.Image image=toData(dbimage);
		imageRepository.deleteImageById(id);
		logger.info("The image is deleted:"+image);
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
}
