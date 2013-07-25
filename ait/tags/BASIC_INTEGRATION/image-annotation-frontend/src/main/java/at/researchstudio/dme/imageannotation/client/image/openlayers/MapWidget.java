package at.researchstudio.dme.imageannotation.client.image.openlayers;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapWidget extends SimplePanel {
	
	private Map map;
	
	public MapWidget(MapOptions options) {
		super(DOM.createDiv());
		this.map = Map.create(getElement(), options);
	}
	
	public Map getMap() {
		return map;
	}
	
    protected void onLoad() {
        super.onLoad();
        map.redraw();
        map.zoomToMaxExtent();
    }

}
