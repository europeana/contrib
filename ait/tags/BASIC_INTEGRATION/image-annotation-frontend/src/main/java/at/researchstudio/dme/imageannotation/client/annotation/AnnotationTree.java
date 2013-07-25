package at.researchstudio.dme.imageannotation.client.annotation;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;

// workaround for http://code.google.com/p/google-web-toolkit/issues/detail?id=369
public class AnnotationTree extends Tree{
    public void onBrowserEvent(Event event) {
    	if (DOM.eventGetType(event) == Event.ONMOUSEDOWN
        		|| DOM.eventGetType(event) == Event.ONMOUSEUP
        		|| DOM.eventGetType(event) == Event.ONCLICK
        		|| DOM.eventGetType(event) == Event.ONKEYDOWN
        		|| DOM.eventGetType(event) == Event.ONKEYUP
        		|| DOM.eventGetType(event) == Event.ONKEYPRESS)
        		
        	return;

         super.onBrowserEvent(event);
    }
}
