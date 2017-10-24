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

	
	public List<uk.co.boxnetwork.data.image.Episode> findAllEpisodes(SearchParam searchParam){
		return toDataEpisodes(imageRepository.findAllEpisodes(searchParam),appConfig);
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
			List<uk.co.boxnetwork.data.image.ImageSet> imageSets=new ArrayList<uk.co.boxnetwork.data.image.ImageSet>();
			for(uk.co.boxnetwork.model.ImageSet imgset:imageSetsdb){
				imageSets.add(createImageSetData(imgset));
			}
			ret.setImageSets(imageSets);
			return ret;
		}
		else{
			return null;
		}
				
	}
	private uk.co.boxnetwork.data.image.ImageSet createImageSetData(uk.co.boxnetwork.model.ImageSet imgset){
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
	public  uk.co.boxnetwork.data.image.Image createImage(Long setid,uk.co.boxnetwork.data.image.Image image){
		uk.co.boxnetwork.model.Image dbImage=new uk.co.boxnetwork.model.Image();
		image.update(dbImage);
		imageRepository.persist(setid,dbImage);
		return new uk.co.boxnetwork.data.image.Image(dbImage);
	}
	public void updateImageSet(uk.co.boxnetwork.data.image.ImageSet imageSet){
		ImageSet dbImageSet=imageRepository.findImageSetById(imageSet.getId());
		imageSet.update(dbImageSet);
		imageRepository.persist(dbImageSet);
	}
}
