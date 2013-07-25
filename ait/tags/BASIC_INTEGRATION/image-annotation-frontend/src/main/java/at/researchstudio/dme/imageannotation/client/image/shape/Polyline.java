package at.researchstudio.dme.imageannotation.client.image.shape;

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
