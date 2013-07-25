package at.researchstudio.dme.imageannotation.client.image.shape;

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
