package at.ait.dme.yuma.server.model;

public enum MediaType { 
	
	IMAGE, MAP, VIDEO, AUDIO; 

	public String getScreenName() {
		String first = this.name().substring(0, 1);
		String rest = this.name().substring(1).toLowerCase();
		return first + rest;
	}
	
}
