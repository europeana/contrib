package at.researchstudio.dme.imageannotation.server;

import at.researchstudio.dme.imageannotation.client.image.ImageRect;
import at.researchstudio.dme.imageannotation.client.server.ImageZoomService;
import at.researchstudio.dme.imageannotation.client.server.exception.InvalidImageException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap.EvictionPolicy;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * image zoom service implementation
 * 
 * @author Christian Sadilek
 */
public class ImageZoomServiceImpl extends RemoteServiceServlet implements ImageZoomService {	
	private static final long serialVersionUID = 1951774640603168357L;

	// the maximum number of cached images
	private static final int MAX_SIZE_IMAGE_CACHE = 5;
	
	private static final String WIDTH_PARAM_NAME = "width";
	private static final String HEIGHT_PARAM_NAME = "height";
	private static final String URL_PARAM_NAME = "imageURL";
	
	private static Logger logger = Logger.getLogger(ImageZoomServiceImpl.class);
	
	//from http://code.google.com/p/concurrentlinkedhashmap/
	private ConcurrentLinkedHashMap<String, byte[]> imageCache = ConcurrentLinkedHashMap.create(EvictionPolicy.LRU, MAX_SIZE_IMAGE_CACHE);
	
	public void init() throws ServletException {
		System.setProperty("jmagick.systemclassloader","no");
		super.init();
	}
			
	public ImageRect prepareZoom(String imageUrl, ImageRect rect) throws InvalidImageException {
		try {			
			if(imageCache.get(imageUrl)==null) {
				ImageInfo imageInfo = new ImageInfo(imageUrl);
				MagickImage image = new MagickImage(imageInfo);				
				imageCache.putIfAbsent(imageUrl, image.imageToBlob(new ImageInfo()));
			} else {
				// this is used to achieve an asynchronous sleep for the client which				
				// cannot be done in javascript. after that clients can check whether
				// the current rect of the image has changed due to the user zooming.
				// if no change is detected by the client it is assumed that the user
				// has stopped zooming and the server (this service) can be called to retrieve
				// a higher quality scaled image.
				Thread.sleep(100);
			}
		} catch (MagickException me) {
			logger.error(me.getMessage(), me);
			throw new InvalidImageException("Image Magick failed to read image!");
		} catch (InterruptedException e) {
			// can be safely ignored
		}
		return rect;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) 
		throws ServletException, IOException {			
		
		try {
			String imageUrl = req.getParameter(URL_PARAM_NAME);		
			Integer width = new Integer(req.getParameter(WIDTH_PARAM_NAME));
			Integer height = new Integer(req.getParameter(HEIGHT_PARAM_NAME));
						
			byte[] imageBlob = null;
			MagickImage image = null;
			if((imageBlob=(byte[])imageCache.get(imageUrl))==null) {
				ImageInfo imageInfo = new ImageInfo(imageUrl);
				image = new MagickImage(imageInfo);
				imageCache.putIfAbsent(imageUrl, image.imageToBlob(new ImageInfo()));
			} else {
				image = new MagickImage(new ImageInfo(), imageBlob);
			}
			
			MagickImage zoomedImage = image.scaleImage(width.intValue(), height.intValue());
			
			resp.setContentType("image/jpeg");
			resp.getOutputStream().write(zoomedImage.imageToBlob(new ImageInfo()));
		} catch(NumberFormatException e) {
			// bad request
			resp.sendError(400, "invalid width or height specified");
		} catch (MagickException me) {
			logger.error(me.getMessage(), me);
			resp.sendError(500, "failed to scale image");
		}		
	}	
}
