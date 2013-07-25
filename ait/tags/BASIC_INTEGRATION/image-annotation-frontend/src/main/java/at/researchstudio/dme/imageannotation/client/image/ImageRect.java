package at.researchstudio.dme.imageannotation.client.image;

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
		if(this.left!=rect.getLeft()) return false;		
		if(this.top!=rect.getTop()) return false;
		if(this.width!=rect.getWidth()) return false;
		if(this.height!=rect.getHeight()) return false;
		
		return true;
	}

	public int hashCode() {
		return width ^ height ^ top ^ left;
	}
	
	public String toString() {
		return "left: " + left + " top: " + top + " width: " + width + " height: " + height;
	}
}
