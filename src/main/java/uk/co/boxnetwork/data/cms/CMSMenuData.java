package uk.co.boxnetwork.data.cms;

import java.util.ArrayList;
import java.util.List;

import uk.co.boxnetwork.model.cms.CMSMenu;
import uk.co.boxnetwork.model.cms.CMSPlaylist;





public class CMSMenuData {
	
    private Long id;
	private String title;	
	private List<CMSPlaylistData> playlist;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<CMSPlaylistData> getPlaylist() {
		return playlist;
	}
	public void setPlaylist(List<CMSPlaylistData> playlist) {
		this.playlist = playlist;
	}
	
	public void exportAttributes(CMSMenu cmsmenu){
		cmsmenu.setTitle(this.title);
	}
	public void importData(CMSMenu cmsmenu){
		this.id=cmsmenu.getId();
		this.title=cmsmenu.getTitle();
		if(cmsmenu.getPlaylist()!=null && cmsmenu.getPlaylist().size()>0){
			this.playlist=new ArrayList<CMSPlaylistData>();
			for(CMSPlaylist cmsplaylist:cmsmenu.getPlaylist()){
				CMSPlaylistData cmlPlayListData=new CMSPlaylistData();
				cmlPlayListData.importData(cmsplaylist);
				this.playlist.add(cmlPlayListData);
			}
		}
		
	}
		
}
