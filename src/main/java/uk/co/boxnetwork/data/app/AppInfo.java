package uk.co.boxnetwork.data.app;

import uk.co.boxnetwork.model.AppConfig;

public class AppInfo {
	private AppConfig appconfig;
	private BCSettings bcSettings;
	

	public AppConfig getAppconfig() {
		return appconfig;
	}

	public void setAppconfig(AppConfig appconfig) {
		this.appconfig = appconfig;
	}

	public BCSettings getBcSettings() {
		return bcSettings;
	}

	public void setBcSettings(BCSettings bcSettings) {
		this.bcSettings = bcSettings;
	}
	
	
}
