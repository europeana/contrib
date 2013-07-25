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


/**
 * represents a rectangle
 * 
 * @author Christian Sadilek
 */
public class Rectangle extends Shape {
	private static final long serialVersionUID = -5411462884807361565L;

	public Rectangle() {
		super();
	}
	
	public Rectangle(Shape shape) {
		super(shape);
	}

	public Rectangle(int width, int height, Color color, int strokeWidth) {
		super(width, height, color, strokeWidth);
	}

	public Rectangle(int left, int top,int width, int height, Color color, int strokeWidth) {
		super(left, top, width, height, color, strokeWidth);
	}
	
	@Override
	public Rectangle copy() {
		return new Rectangle(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Rectangle)) return false;
		if(this==obj) return true;
		
		Rectangle rectangle = (Rectangle)obj;				
		return super.equals(rectangle);
	
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
};
