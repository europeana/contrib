package at.researchstudio.dme.imageannotation.client.image;

import at.researchstudio.dme.imageannotation.client.image.shape.Shape;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * represents an image fragment
 * 
 * @author Christian Sadilek
 */
public class ImageFragment implements IsSerializable {
	private static final long serialVersionUID = 8505694614569600054L;
	
	// the image rect visible to the user
	private ImageRect visibleRect;
	
	// the image rect that indicates whether the image was zoomed or dragged
	private ImageRect imageRect;
	
	// the shape that defines the fragment 
	private Shape shape;
	
	public ImageFragment() {}
	
	public ImageFragment(ImageRect visibleRect, ImageRect imageRect, Shape shape) {
		this.visibleRect = visibleRect;
		this.imageRect = imageRect;
		this.shape = shape;
	}
	
	public ImageRect getVisibleRect() {
		return visibleRect;
	}
	public void setVisibleRect(ImageRect visibleRect) {
		this.visibleRect = visibleRect;
	}
	public ImageRect getImageRect() {
		return imageRect;
	}
	public void setImageRect(ImageRect imageRect) {
		this.imageRect = imageRect;
	}
	public Shape getShape() {
		return shape;
	}
	public void setShape(Shape shape) {
		this.shape = shape;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ImageFragment)) return false;
		if(this==obj) return true;
		
		ImageFragment fragment = (ImageFragment)obj;
		if(visibleRect==null) {
			if(fragment.getVisibleRect()!=null) return false;
		} else if(!this.visibleRect.equals(fragment.getVisibleRect())) {
			return false;		
		}
		if(imageRect==null) {
			if(fragment.getImageRect()!=null) return false;
		} else if(!this.imageRect.equals(fragment.getImageRect())) {
			return false;
		}
		if(shape==null) {
			if(fragment.getShape()!=null) return false;
		} else if(!this.shape.equals(fragment.getShape())){
			return false;
		}
	
		return true;
	}

	@Override
	public int hashCode() {
		return imageRect.hashCode() ^ shape.hashCode();
	}
}
