package at.ait.dme.gwt.openlayers.client;

import at.ait.dme.gwt.openlayers.client.Map;
import at.ait.dme.gwt.openlayers.client.MapOptions;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;

public class MapWidget extends SimplePanel {
	
	private Map map = null;
	
	public MapWidget() {
		super(DOM.createElement("div"));
		setWidth("100%");
		setHeight("100%");
		map = Map.create(getElement());
	}
	
	public MapWidget(MapOptions options) {
		super(DOM.createElement("div"));
		setWidth("100%");
		setHeight("100%");
		if (options == null) {
			map = Map.create(getElement());
		} else {
			map = Map.create(getElement(), options);
		}
	}
	
	@Deprecated
	public MapWidget(int width, int height) {
		super(DOM.createElement("div"));
		setPixelSize(width, height);
		map = Map.create(getElement());
	}
	
	@Deprecated
	public MapWidget(int width, int height, MapOptions options) {
		super(DOM.createElement("div"));
		setPixelSize(width, height);
		map = Map.create(getElement());
	}
	
	@Deprecated
	public MapWidget(String applyTo) {
		super(DOM.getElementById(applyTo));
		map = Map.create(applyTo);
	}
	
	@Deprecated
	public MapWidget(String applyTo, MapOptions options) {
		super(DOM.getElementById(applyTo));
		map = Map.create(applyTo, options);
	}
	
	public Map getMap() {
		return map;
	}
	
	protected void onLoad() {
        super.onLoad();
		map.redraw();
	}

}
