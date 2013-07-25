package at.researchstudio.dme.imageannotation.client.image.shape;

import java.io.Serializable;

/**
 * represents a point
 * 
 * @author Christian Sadilek
 */
public class Point implements Serializable {
	private static final long serialVersionUID = -8573004893208810833L;
	
	private int x,y;

	public Point() {}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Point)) return false;
		if(this==obj) return true;
		
		Point point = (Point)obj;
		if(this.x!=point.getX()) return false;
		if(this.y!=point.getY()) return false;
		
		return true;
	
	}

	@Override
	public int hashCode() {
		return x ^ y;
	}
	
	@Override
	public String toString() {
		return "x:"+x+" y:"+y;
	}
	
}
