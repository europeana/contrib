package at.ac.ait.dme.gtv.client.selection;

import com.google.gwt.dom.client.Element;

/**
 * A 'least common denominator' abstract base class providing access 
 * to the selection-related methods commonly supported by both Mozilla
 * and IE.
 *  
 * @author Rainer Simon
 */
public abstract class CrossBrowserSelection {

	public static CrossBrowserSelection getSelection(Element context) {
		if (Range.isSupported()) {
			return new CrossBrowserSelectionMozillaImpl(context);
		} else if (TextRange.isSupported()) {
			return new CrossBrowserSelectionIEImpl(context);
		}
		return null;
	}
	
	public abstract boolean isEmpty();
	
	public abstract String getText(); 
	
	public abstract int getOffset(); 

}
