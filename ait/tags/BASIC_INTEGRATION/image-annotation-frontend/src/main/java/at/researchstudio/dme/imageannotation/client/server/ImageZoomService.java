package at.researchstudio.dme.imageannotation.client.server;

import at.researchstudio.dme.imageannotation.client.image.ImageRect;
import at.researchstudio.dme.imageannotation.client.server.exception.InvalidImageException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * interface to the image zoom service
 *  
 * @author Christian Sadilek
 */
@RemoteServiceRelativePath("zoom")
public interface ImageZoomService extends RemoteService {
	
	/**
	 * loads libraries and caches the image.
	 * 
	 * @param imageUrl
	 * @param rect
	 * @return the provided rect for the client to check if it changed in the meantime
	 * @throws InvalidImageException
	 */
	public ImageRect prepareZoom(String imageUrl, ImageRect rect) throws InvalidImageException;
}
