package at.ac.ait.dme.gtv.client.listeners;

/**
 * Listener class for handling selection events in the GTVTextPanel.
 * 
 * @author Rainer Simon
 */
public abstract class TextSelectionListener {
	
	public abstract void onSelect(String selectedText, int offset);

}
