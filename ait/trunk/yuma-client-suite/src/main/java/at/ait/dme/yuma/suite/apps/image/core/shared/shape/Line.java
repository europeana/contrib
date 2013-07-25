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

import java.io.Serializable;

/**
 * represents a line
 * 
 * @author Christian Sadilek
 */
public class Line implements Serializable {
	private static final long serialVersionUID = -480975723302615484L;
	
	private Point start;
	private Point end;

	public Line() {}
	
	public Line(int x1, int y1, int x2, int y2) {
		this.start = new Point(x1,y1);
		this.end = new Point(x2, y2);
	}
	
	public Line(Point start, Point end) {
		this.start = start;
		this.end = end;
	}
	
	public Point getStart() {
		return start;
	}
	public void setStart(Point start) {
		this.start = start;
	}
	public Point getEnd() {
		return end;
	}
	public void setEnd(Point end) {
		this.end = end;
	}		
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Line)) return false;
		if(this==obj) return true;
		
		Line line = (Line)obj;
		if(!(this.start.equals(line.getStart()))) return false;
		if(!(this.end.equals(line.getEnd()))) return false;
		
		return true;
	
	}
	public int hashCode() {
		return start.hashCode() ^ end.hashCode();
	}
}
