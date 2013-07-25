package at.researchstudio.dme.imageannotation.client.image;

import at.researchstudio.dme.imageannotation.client.image.shape.Shape;

import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.user.client.ui.Composite;

/**
 * Base class for image composites
 * 
 * @author Christian Sadilek
 */
public abstract class ImageComposite extends Composite implements
		ImageFragmentContainer, HasLoadHandlers {

	protected ImageFragmentSelectionListener imageFragmentSelectionListener = null;

	@Override
	public void setImageFragmentSelectionListener(ImageFragmentSelectionListener listener) {
		this.imageFragmentSelectionListener = listener;
	}

	@Override
	public ImageFragment getImageFragment() {
		Shape shape = getActiveShape();
		if (shape == null)
			return null;

		return new ImageFragment(getVisibleRect(), getImageRect(),
				getActiveShape());
	}

	public abstract ImageRect getImageRect();
	public abstract ImageRect getVisibleRect();
	public abstract Shape getActiveShape();

	public abstract void showFragment(ImageFragmentContainer container);
	public abstract void hideFragment(ImageFragmentContainer container);

	public abstract void showActiveFragmentPanel(
			ImageFragmentContainer container, boolean forceVisible);
	public abstract void hideActiveFragmentPanel();
}
