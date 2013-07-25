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

package at.ait.dme.yuma.client.image.shape;

import java.io.Serializable;

/**
 * base class of all shapes
 * 
 * @author Christian Sadilek
 */
public abstract class Shape implements Serializable {	
	private static final long serialVersionUID = -4874292545711777517L;
	
	protected Color color;
	protected int strokeWidth;	
	protected int left,top,width,height;
	
	protected Shape() {};
	
	protected Shape(int width, int height, Color color, int strokeWidth) {
		this.width = width;
		this.height = height;
		this.color = color;
		this.strokeWidth = strokeWidth;
	}

	protected Shape(int left, int top, int width, int height, Color color, int strokeWidth) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.color = color;
		this.strokeWidth = strokeWidth;
	}
	
	protected Shape(Shape shape) {
		this(shape.getLeft(), shape.getTop(), shape.getWidth(), shape.getHeight(), 
				new Color(shape.getColor().getR(), shape.getColor().getG(), shape.getColor().getB()),
				shape.getStrokeWidth());
	}

	public abstract Shape copy();
	
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Shape)) return false;
		if(this==obj) return true;
		
		Shape shape = (Shape)obj;
		if(this.left!=shape.left) return false;
		if(this.top!=shape.top) return false;
		if(this.width!=shape.width) return false;
		if(this.height!=shape.height) return false;
		if(this.strokeWidth!=shape.strokeWidth) return false;
		
		if (color == null) {
			if (shape.color != null)
				return false;
		} else if (!color.equals(shape.color))
			return false;
		
		return true;
	
	}

	@Override
	public int hashCode() {
		return left ^ top ^ width ^ height ^ strokeWidth ^ color.hashCode();
	}
}
