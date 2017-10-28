package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;
import uk.co.boxnetwork.model.Image;
import uk.co.boxnetwork.model.ImageSet;




@Service
public class ImageService {
	@Autowired
	ImageRepository imageRepository;
	
	@Autowired
	private AppConfig appConfig;

	
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
			images.add(new uk.co.boxnetwork.data.image.Image(img));
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
		uk.co.boxnetwork.data.image.Image ret=new uk.co.boxnetwork.data.image.Image(dbImage);
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
}
