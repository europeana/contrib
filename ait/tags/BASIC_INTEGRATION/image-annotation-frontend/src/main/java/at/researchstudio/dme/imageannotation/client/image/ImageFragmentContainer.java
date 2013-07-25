package at.researchstudio.dme.imageannotation.client.image;

/**
 * An image fragment container is any class that contains an {@link ImageFragment}
 * 
 * @author Christian Sadilek
 */
public interface ImageFragmentContainer {
	/**
	 * retrieve the contained image fragment
	 * 
	 * @return image fragment
	 */
	public ImageFragment getImageFragment();
	
	/**
	 * set a selection listener for the contained image fragment
	 * 
	 * @param listener
	 */
	public void setImageFragmentSelectionListener(ImageFragmentSelectionListener listener);
}
