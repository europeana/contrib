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

package at.ait.dme.yuma.suite.apps.image.core.shared.shape;

/**
 * represents an ellipse
 * 
 * @author Christian Sadilek
 */
public class Ellipse extends Shape {
	private static final long serialVersionUID = -666841621893574877L;
	
	public Ellipse() {
		super();
	}
	
	public Ellipse(Shape shape) {
		super(shape);
	}

	public Ellipse(int width, int height, Color color, int strokeWidth) {
		super(width, height, color, strokeWidth);
	}

	public Ellipse(int left, int top,int width, int height, Color color, int strokeWidth) {
		super(left, top, width, height, color, strokeWidth);
	}
	
	@Override
	public Ellipse copy() {
		return new Ellipse(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Ellipse)) return false;
		if(this==obj) return true;
		
		Ellipse ellipse = (Ellipse)obj;				
		return super.equals(ellipse);
	
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public int getCx() {
		return left + (width / 2);
	}

	public int getCy() {
		return top + (height / 2);
	}

	public int getRx() {
		return (width / 2);
	}

	public int getRy() {
		return (height / 2);
	}
};	
