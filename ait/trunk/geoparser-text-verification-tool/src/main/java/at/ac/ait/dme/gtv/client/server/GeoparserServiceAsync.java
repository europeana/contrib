package at.ac.ait.dme.gtv.client.server;

import at.ac.ait.dme.gtv.client.model.GeoparsedText;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GeoparserServiceAsync {

    public void getGeoparsedText(String url, AsyncCallback<GeoparsedText> callback);

}
