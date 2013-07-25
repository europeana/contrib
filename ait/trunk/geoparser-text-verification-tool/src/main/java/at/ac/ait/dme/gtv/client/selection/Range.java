package at.ac.ait.dme.gtv.client.selection;

import com.google.gwt.dom.client.Node;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Implementation of the Mozilla Range object.
 *
 * @author Rainer Simon
 */
public class Range extends JavaScriptObject {

	protected Range() {}
	
	public static native boolean isSupported() /*-{
		if ($wnd.document.createRange) return true;
		return false;
	}-*/;
	
	public static native Range createRange() /*-{
		return $wnd.document.createRange();
	}-*/;
	
	public final native void setStart(Node startNode, int startOffset) /*-{
		this.setStart(startNode, startOffset);
	}-*/;
	
	public final native void setEnd(Node endNode, int endOffset) /*-{
		this.setEnd(endNode, endOffset);
	}-*/;

	public final native void selectNode(Node node) /*-{
		this.selectNode(node);
	}-*/;

	public final native void selectNodeContents(Node node) /*-{
		this.selectNodeContents(node);
	}-*/;
	
	public final native String getText() /*-{
		return this.toString();
	}-*/;
	
	public final native void deleteContents() /*-{
		this.deleteContents();
	}-*/;
	
	public final native void insertNode(Node node) /*-{
		this.insertNode(node);
	}-*/;
	
	public final native void setHTML(String text) /*-{
		this.innerHTML = text;
	}-*/;

}
