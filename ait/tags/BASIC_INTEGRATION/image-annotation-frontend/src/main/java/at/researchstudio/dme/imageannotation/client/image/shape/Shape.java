package at.researchstudio.dme.imageannotation.client.image.shape;

import java.io.Serializable;

/**
 * base class of all shapes
 * 
 * @author Christian Sadilek
 */
public abstract class Shape implements Serializable {	
	protected Color color;
	protected int strokeWidth;	
	protected int left,top,width,height;
	
	protected Shape() {};
	
	protected Shape(int width, int height,Color color, int strokeWidth) {
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
		if(this.left!=shape.getLeft()) return false;
		if(this.top!=shape.getTop()) return false;
		if(this.width!=shape.getWidth()) return false;
		if(this.height!=shape.getHeight()) return false;
		if(this.strokeWidth!=shape.getStrokeWidth()) return false;
		
		if (color == null) {
			if (shape.getColor() != null)
				return false;
		} else if (!color.equals(shape.getColor()))
			return false;
		
		return true;
	
	}

	@Override
	public int hashCode() {
		return left ^ top ^ width ^ height ^ strokeWidth ^ color.hashCode();
	}
}
