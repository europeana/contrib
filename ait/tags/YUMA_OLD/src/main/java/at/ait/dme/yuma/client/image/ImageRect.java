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

package at.ait.dme.yuma.client.image;

import java.io.Serializable;

/**
 * represents an image rect
 * 
 * @author Christian Sadilek
 */
public class ImageRect implements Serializable {
	private static final long serialVersionUID = 1257578761265352546L;
	
	private int left;
	private int top;	
	private int width;
	private int height;
	
	public ImageRect() {}
	
	public ImageRect(int left, int top, int width, int height) {
		setRect(left, top, width, height);
	}
	
	public void setRect(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof ImageRect)) return false;
		if(this==obj) return true;
		
		ImageRect rect = (ImageRect)obj;
		if(this.left!=rect.left) return false;		
		if(this.top!=rect.top) return false;
		if(this.width!=rect.width) return false;
		if(this.height!=rect.height) return false;
		
		return true;
	}

	public int hashCode() {
		return width ^ height ^ top ^ left;
	}
	
	public String toString() {
		return "left: " + left + " top: " + top + " width: " + width + " height: " + height;
	}
}
