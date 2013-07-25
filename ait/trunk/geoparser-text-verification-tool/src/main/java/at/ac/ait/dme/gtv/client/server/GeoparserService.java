package at.ac.ait.dme.gtv.client.server;

import at.ac.ait.dme.gtv.client.model.GeoparsedText;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("geoparser")
public interface GeoparserService extends RemoteService {

    /**
     * Parses a plain text
     * @param url the URL at which the plain text document is available
     * @return a {@link at.ac.ait.dme.gtv.client.model.GeoparsedText}
     */
    public GeoparsedText getGeoparsedText(String url);

}
