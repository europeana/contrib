/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.suite.apps.image.core.shared.model;

import at.ait.dme.yuma.suite.apps.core.shared.model.MediaFragment;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.Shape;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.VoidShape;

/**
 * A media fragment implementation for image fragments
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ImageFragment extends MediaFragment {
	
	private static final long serialVersionUID = -4686905856920588224L;

	/**
	 * The image rect visible to the user
	 */
	private ImageRect visibleRect;
	
	/**
	 * The image rect that indicates whether the image was zoomed or dragged
	 */
	private ImageRect imageRect;
	
	/**
	 * The shape that defines the fragment 
	 */
	private Shape shape;
	
	public ImageFragment() {
		shape = new VoidShape();
	}
	
	public ImageFragment(Shape shape) {
		this();
		this.shape=shape;
	}
	
	public ImageFragment(ImageRect imageRect, Shape shape) {
		this(shape);
		this.visibleRect = this.imageRect = imageRect;
	}
	
	public ImageFragment(ImageRect visibleRect, ImageRect imageRect, Shape shape) {
		this(imageRect, shape);
		this.visibleRect = visibleRect;
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
		if(shape==null)
			shape = new VoidShape();
		return shape;
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
	}	
	
	public boolean isVoid() {
		return (shape!=null && shape.equals(new VoidShape()));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ImageFragment)) return false;
		if(this==obj) return true;
		
		ImageFragment fragment = (ImageFragment)obj;
		if(visibleRect==null) {
			if(fragment.visibleRect!=null) return false;
		} else if(!this.visibleRect.equals(fragment.visibleRect)) {
			return false;		
		}
		if(imageRect==null) {
			if(fragment.imageRect!=null) return false;
		} else if(!this.imageRect.equals(fragment.imageRect)) {
			return false;
		}
		if(shape==null) {
			if(fragment.shape!=null) return false;
		} else if(!this.shape.equals(fragment.shape)){
			return false;
		}
	
		return true;
	}

	@Override
	public int hashCode() {
		return imageRect.hashCode() ^ shape.hashCode();
	}
}
