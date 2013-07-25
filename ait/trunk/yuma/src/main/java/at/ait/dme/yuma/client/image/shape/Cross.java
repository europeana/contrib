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

import java.util.ArrayList;
import java.util.Collection;

/**
 * represents a cross
 * 
 * @author Christian Sadilek
 */
public class Cross extends Shape {		
	private static final long serialVersionUID = 5742165233174717094L;

	private Collection<Line> lines = new ArrayList<Line>();
	
	public Cross() {
		super();
	}
	
	public Cross(Shape shape) {
		super(shape);
		createLines();
	}
	
	public Cross(int width, int height, Color color, int strokeWidth) {
		super(width, height, color, strokeWidth);
		createLines();
	}

	public Cross(int left, int top,int width, int height, Color color, int strokeWidth) {
		super(left, top, width, height, color, strokeWidth);
		createLines();
	}
	
	public Cross(Cross shape) {
		super(shape);
		
		for(Line line : shape.getLines()) {
			this.lines.add(new Line(line.getStart().getX(),line.getStart().getY(),
					line.getEnd().getX(), line.getEnd().getY()));
		}
	}
	
	@Override
	public Cross copy() {
		return new Cross(this);
	}
	
	public void removeLines() {
		lines.clear();
	}
	
	public void addLine(int x1, int y1, int x2, int y2) {
		lines.add(new Line(new Point(x1,y1),new Point(x2,y2)));
	}
	
	public Collection<Line> getLines() {
		return lines;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
		createLines();
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
		createLines();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cross)) return false;
		if(this==obj) return true;
				
		Cross cross = (Cross)obj;
		if(!this.lines.equals(cross.getLines())) return false; 
			
		return super.equals(cross);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	private void createLines() {
		lines.clear();
		if (width >= 5) {
			int w1 = 2 * width / 5;
			int w2 = 3 * width / 5;
			addLine(0, 0 + height / 2, 0 + w1, 0 + height / 2);				
			addLine(0 + w2, 0 + height / 2, 0 + width, 0 + height / 2);
		} else {
			addLine(0, 0 + height / 2, 0 + width, 0 + height / 2);
		}
		if (height >= 5) {
			int h1 = 2 * height / 5;
			int h2 = 3 * height / 5;
			addLine(0 + width / 2, 0, 0 + width / 2, 0 + h1);				
			addLine(0 + width / 2, 0 + h2, 0 + width / 2, 0 + height);
		} else {
			addLine(0 + width / 2, 0, 0 + width / 2, 0 + height);
		}
	}
};
