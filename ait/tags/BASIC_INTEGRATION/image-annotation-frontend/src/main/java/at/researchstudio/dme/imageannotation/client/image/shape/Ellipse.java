package at.researchstudio.dme.imageannotation.client.image.shape;

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
