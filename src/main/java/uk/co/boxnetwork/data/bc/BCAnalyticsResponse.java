package uk.co.boxnetwork.data.bc;

public class BCAnalyticsResponse {
    private Long  item_count;
    private BCAnalyticData[] items;
    private BCAnalyticData summary;
	public Long getItem_count() {
		return item_count;
	}
	public void setItem_count(Long item_count) {
		this.item_count = item_count;
	}
	public BCAnalyticData[] getItems() {
		return items;
	}
	public void setItems(BCAnalyticData[] items) {
		this.items = items;
	}
	public BCAnalyticData getSummary() {
		return summary;
	}
	public void setSummary(BCAnalyticData summary) {
		this.summary = summary;
	}
    
}
