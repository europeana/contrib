package at.researchstudio.dme.imageannotation.client.image.shape;

import java.util.ArrayList;
import java.util.Collection;

/**
 * represents a polygon
 * 
 * @author Christian Sadilek
 */
public class Polygon extends Shape {
	private static final long serialVersionUID = 3532912721985143584L;

	private int maxX=-1, maxY=-1, minX=-1, minY=-1;
	private Collection<Point> points = new ArrayList<Point>();

	public Polygon() {
		super();
	}
	
	public Polygon(Shape shape) {
		super(shape);
	}
	
	public Polygon(Polygon shape) {
		super(shape);
		
		this.maxX = shape.maxX;
		this.maxY = shape.maxY;
		this.minX = shape.minX;
		this.minY = shape.minY;
		
		for(Point point : shape.getPoints()) {
			this.points.add(new Point(point.getX(), point.getY()));
		}
	}
	
	public Polygon(int width, int height, Color color, int strokeWidth) {
		super(width, height, color, strokeWidth);
	}

	public Polygon(int left, int top,int width, int height, Color color, int strokeWidth) {
		super(left, top, width, height, color, strokeWidth);
	}
	
	public Polygon(int left, int top, int width, int height, Color color, int strokeWidth, 
			Collection<Point> points) {
		super(left, top, width, height, color, strokeWidth);
		this.points=points;	
	}
	
	@Override
	public Polygon copy() {
		return new Polygon(this);
	}
		
	public void addPoint (Point point) {
		if (points==null) points = new ArrayList<Point>();
				
		minX = (minX==-1)?point.getX():Math.min(point.getX(), minX);
		minY = (minY==-1)?point.getY():Math.min(point.getY(), minY);
		
		maxX = Math.max(point.getX(), maxX);
		maxY = Math.max(point.getY(), maxY);
		
		points.add(point);
	}
	
	public Collection<Point> getPoints() {
		return points;
	}

	public void setPoints(Collection<Point> points) {
		this.points = points;
	}	
	
	@Override
	public int getWidth() {
		return (maxX==-1)?width:maxX-minX;
	}
	
	@Override
	public int getHeight() {
		return (maxY==-1)?height:maxY-minY;
	}
	
	public int getRelativeLeft() {
		return (minX==-1)?left:minX;
	}
	
	public int getRelativeTop() {
		return (minY==-1)?top:minY;
	}
	
	public void resize(int newWidth, int newHeight) {
		if(points.isEmpty()) return;
		
		float widthChange = (float) newWidth / getWidth();
		float heightChange = (float) newHeight / getHeight();
		maxX=-1; maxY=-1; minX=-1; minY=-1;		
				
		for(Point p : points) {
			p.setX(Math.round(p.getX()*widthChange));
			p.setY(Math.round(p.getY()*heightChange));
			
			minX = (minX==-1)?p.getX():Math.min(p.getX(), minX);
			minY = (minY==-1)?p.getY():Math.min(p.getY(), minY);		
			maxX = Math.max(p.getX(), maxX);
			maxY = Math.max(p.getY(), maxY);
		}		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Polygon)) return false;
		if(this==obj) return true;
				
		Polygon polygon = (Polygon)obj;
		if (points == null) {
			if (polygon.getPoints() != null)
				return false;
		} else if (!points.equals(polygon.getPoints()))
			return false;
		
		return super.equals(polygon);
	
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
