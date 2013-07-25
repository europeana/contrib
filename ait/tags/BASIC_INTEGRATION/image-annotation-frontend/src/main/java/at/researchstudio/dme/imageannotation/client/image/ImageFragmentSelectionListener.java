package at.researchstudio.dme.imageannotation.client.image;

/**
 * Interface to be implemented by classes that listen on image fragment
 * selections.
 * 
 * @author Christian Sadilek
 */
public interface ImageFragmentSelectionListener {
	public void onSelectImageFragment(ImageFragmentContainer container);
	public void onDeselectImageFragment(ImageFragmentContainer container);
}
