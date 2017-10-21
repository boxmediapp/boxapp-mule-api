package uk.co.boxnetwork.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.boxnetwork.data.SearchParam;
import uk.co.boxnetwork.model.AppConfig;
import uk.co.boxnetwork.model.Episode;




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
			return new uk.co.boxnetwork.data.image.Episode(episode);
		}
		else{
			return null;
		}
				
	}
}
