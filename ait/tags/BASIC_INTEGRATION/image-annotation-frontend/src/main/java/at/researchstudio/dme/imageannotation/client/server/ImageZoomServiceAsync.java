package at.researchstudio.dme.imageannotation.client.server;


import at.researchstudio.dme.imageannotation.client.image.ImageRect;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * asynchronous interface to the image zoom service
 *  
 * @author Christian Sadilek
 */
public interface ImageZoomServiceAsync {
	public void prepareZoom(String imageUrl, ImageRect rect, AsyncCallback<ImageRect> callback);
}
