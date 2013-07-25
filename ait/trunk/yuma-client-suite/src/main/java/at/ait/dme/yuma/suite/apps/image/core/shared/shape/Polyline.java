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
 * represents a polyline
 * 
 * @author Christian Sadilek
 */
public class Polyline extends Polygon {
	private static final long serialVersionUID = 789978389488436808L;

	public Polyline() {
		super();
	}
	
	public Polyline(Shape shape) {
		super(shape);
	}
	
	public Polyline(Polyline shape) {
		super(shape);
	}
	
	public Polyline(int left, int top,int width, int height, Color color, int strokeWidth) {
		super(left, top, width, height, color, strokeWidth);
	}
	
	@Override
	public Polyline copy() {
		return new Polyline(this);
	}

}
