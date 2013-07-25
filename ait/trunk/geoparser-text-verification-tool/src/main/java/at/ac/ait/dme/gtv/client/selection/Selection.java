package at.ac.ait.dme.gtv.client.selection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;

/**
 * Implementation of the Mozilla Selection object. 
 * 
 * @author Rainer Simon
 */
public class Selection extends JavaScriptObject {

	protected Selection() {}
	
	public static native boolean isSupported() /*-{
		if ($wnd.window.getSelection) return true;
		return false;
	}-*/;
	
	public static native Selection getSelection() /*-{
		return $wnd.window.getSelection();
	}-*/;
	
	public final native boolean isEmpty() /*-{
		return (this.toString().length == 0);
	}-*/;
	
	public final native Node getAnchorNode() /*-{
		return this.anchorNode;
	}-*/;
	
	public final native int getAnchorOffset() /*-{
		return this.anchorOffset;
	}-*/;
	
	public final native Node getFocusNode() /*-{
		return this.focusNode;
	}-*/;
    
	public final native int getFocusOffset() /*-{
		return this.focusOffset;
	}-*/;
	
	public final native Range getRangeAt(int index) /*-{
		return this.getRangeAt(index);
	}-*/;

}
