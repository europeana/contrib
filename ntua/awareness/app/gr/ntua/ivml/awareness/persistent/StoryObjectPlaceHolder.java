package gr.ntua.ivml.awareness.persistent;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class StoryObjectPlaceHolder {
	public String StoryObjectID;
	public String scriptPart;
	public int position;
	public int order;
	
	public String getStoryObjectID() {
		return StoryObjectID;
	}
	public void setStoryObjectURL(String storyObjectID) {
		StoryObjectID = storyObjectID;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getScriptPart() {
		return scriptPart;
	}
	public void setScriptPart(String scriptPart) {
		this.scriptPart = scriptPart;
	}	
}
