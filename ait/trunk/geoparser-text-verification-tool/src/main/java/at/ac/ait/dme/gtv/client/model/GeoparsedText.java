package at.ac.ait.dme.gtv.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 * This object represents a geoparsed text and contains the recognized locations
 *
 * @author Manuel Bernhardt
 */
public class GeoparsedText implements IsSerializable {

    private static final long serialVersionUID = 4841663127142060392L;

    private String url;

    private String html;

    private List<Location> locations;

    public GeoparsedText() {

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public String getUrl() {
        return url;
    }

    public String getHtml() {
        return html;
    }

    public List<Location> getLocations() {
        return locations;
    }

}
