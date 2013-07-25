package at.ac.ait.dme.gtv.client.selection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Implementation of the IE TextRange object.
 * 
 * @author Rainer Simon
 */
public class TextRange extends JavaScriptObject {
	
	/**
	 * Strings specifying endpoint transfer targets for the moveToElementText method
	 */
	public static final String START_TO_END = "StartToEnd";
	public static final String START_TO_START = "StartToStart";
	public static final String END_TO_END = "EndToStart";
	public static final String END_TO_START = "EndToStart";
	
	protected TextRange() {}
	
	public static native boolean isSupported() /*-{
		if ($wnd.document.body.createTextRange) return true;
		return false;
	}-*/;
	
	public static native TextRange createTextRange() /*-{
		return $wnd.document.body.createTextRange();
	}-*/;
	
	public static native TextRange createTextRangeFromSelection() /*-{
		return $wnd.document.selection.createRange();
	}-*/;
	
	public final native boolean isEmpty() /*-{
		return (this.text.length == 0);
	}-*/;	
	
	public final native void moveToElementText(Element element) /*-{
		this.moveToElementText(element);
	}-*/;
	
	public final native void setEndPoint(String type, TextRange textRange) /*-{
		this.setEndPoint(type, textRange)
	}-*/;
	
	public final native String getText() /*-{
		return this.text;
	}-*/;

}
