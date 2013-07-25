package at.researchstudio.dme.imageannotation.client.dnd;

public interface Resizable {
	/**
	 * move the widget.
	 * 
	 * @param right
	 * @param down
	 */
	public void moveBy(int right, int down);

	/**
	 * set the size of the widget
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height);
	
	/**
	 * returns the width of the shape.
	 * 
	 * @return width
	 */
	public int getWidth();
	
	/**
	 * returns the height of the shape.
	 * 
	 * @return height
	 */
	public int getHeight();
	
	/**
	 * returns the absolute left position.
	 * 
	 * @return height
	 */
	public int getAbsoluteLeft();
	
	/**
	 * returns the absolute top position;
	 * 
	 * @return height
	 */
	public int getAbsoluteTop();
	
	/**
	 * returns the relative (to boundary) left position.
	 * 
	 * @return height
	 */
	public int getRelativeLeft();
	
	/**
	 * returns the relative (to boundary) top position;
	 * 
	 * @return height
	 */
	public int getRelativeTop();	
}
