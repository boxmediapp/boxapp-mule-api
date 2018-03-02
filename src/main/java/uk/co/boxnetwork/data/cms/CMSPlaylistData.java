package uk.co.boxnetwork.data.cms;

import java.util.ArrayList;
import java.util.List;

import uk.co.boxnetwork.model.cms.CMSEpisode;
import uk.co.boxnetwork.model.cms.CMSPlaylist;

public class CMSPlaylistData {
		private String id;
		private String title;
		private List<CMSPlayListEpisodeData> episodes;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public List<CMSPlayListEpisodeData> getEpisodes() {
			return episodes;
		}
		public void setEpisodes(List<CMSPlayListEpisodeData> episodes) {
			this.episodes = episodes;
		}
		public void exportAttributes(CMSPlaylist cmsPlaylist){
			cmsPlaylist.setId(this.id);
			cmsPlaylist.setTitle(this.title);
		}
		public void importData(CMSPlaylist cmsplaylist){
			this.id=cmsplaylist.getId();
			this.title=cmsplaylist.getTitle();
			if(cmsplaylist.getEpisodes()!=null && cmsplaylist.getEpisodes().size()>0){
					this.episodes=new ArrayList<CMSPlayListEpisodeData>();
					for(CMSEpisode episode:cmsplaylist.getEpisodes()){
						CMSPlayListEpisodeData episodeData=new CMSPlayListEpisodeData();
						episodeData.importData(episode);
						this.episodes.add(episodeData);
					}
			}
		}
}
