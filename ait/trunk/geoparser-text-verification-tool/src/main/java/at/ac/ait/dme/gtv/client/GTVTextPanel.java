package at.ac.ait.dme.gtv.client;

import at.ac.ait.dme.gtv.client.listeners.TextMouseListener;
import at.ac.ait.dme.gtv.client.listeners.TextSelectionListener;
import at.ac.ait.dme.gtv.client.selection.CrossBrowserSelection;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A scroll-able text area with the various utility methods and
 * event handlers needed for the geoparser text verification tool.
 *
 * @author Rainer Simon
 */
public class GTVTextPanel extends ScrollPanel {

    /**
     * Needed for hack due to mouse event behavior on text de-selection
     */
    private static final int MOUSE_SELECTION_DELAY_MILLIS = 10;

    /**
     * The HTML text area
     */
    private final HTML textArea = new HTML();

    /**
     * Listener for reacting to selection events
     */
    private TextSelectionListener selectionListener = null;

    /**
     * Listener for reacting to mouse-over-highlighted-span events
     */
    private TextMouseListener mouseListener = null;

    /**
     * Hack due to strange event behavior: when user de-selects text
     * by left-clicking INTO an existing selection, the selection is removed
     * after the MouseUp event.
     */
    private final Timer checkSelectionTimer = new Timer() {
        @Override
        public void run() {
            if (selectionListener != null) {
                CrossBrowserSelection s = CrossBrowserSelection.getSelection(textArea.getElement());
                if (!s.isEmpty()) {
                    selectionListener.onSelect(s.getText(), s.getOffset());
                }
            }
        }
    };

    public GTVTextPanel() {
        super();
        textArea.setStyleName("ait-GTVTextPanel");
        this.add(textArea);

        // Check for selection on MouseUp events and call selection listener if needed
        textArea.addMouseUpHandler(createMouseUpHandler());
    }

    /**
     * Creates a handler for MouseUp events which checks for a valid, non-empty
     * selection and calls the selectionListener.
     *
     * @return the handler
     */
    private MouseUpHandler createMouseUpHandler() {
        return new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent arg0) {
                checkSelectionTimer.schedule(MOUSE_SELECTION_DELAY_MILLIS);
            }
        };
    }

    /**
     * Creates a listener for MouseOver and MouseOut events and calls
     * the appropriate methods on the mouseListener.
     *
     * @param id the span-ID which should be handed to the mouseListener in case of event
     * @return the listener
     */
    private EventListener createMouseHoverHandler(final String id) {
        return new EventListener() {
            @Override
            public void onBrowserEvent(Event evt) {
                if (mouseListener != null) {
                    int type = DOM.eventGetType(evt);
                    /*
                    if (type == Event.ONMOUSEOVER) {
                        mouseListener.onMouseOver(id, evt.getClientX(), evt.getClientY());
                    } else if (type == Event.ONMOUSEOUT) {
                        mouseListener.onMouseOut(id, evt.getClientX(), evt.getClientY());
                    } else */
                    if (type == Event.ONCLICK) {
                        mouseListener.onClick(id, evt.getClientX(), evt.getClientY());
                    }
                }
            }
        };
    }

    /**
     * Sets the HTML for this text panel (attaching mouse
     * listeners to all spans with IDs)
     *
     * @param html the HTML
     */
    public void setHTML(String html) {
        // Set HTML
        textArea.setHTML(html);

        // Attach mouse listeners to all spans with an ID
        NodeList<Element> allSpans = textArea.getElement().getElementsByTagName("span");
        Element el;
        for (int i = 0; i < allSpans.getLength(); i++) {
            el = allSpans.getItem(i);
            if (!el.getId().isEmpty()) {
                Event.sinkEvents(el, Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK);
                Event.setEventListener(el, createMouseHoverHandler(el.getId()));
            }
        }
    }

    /**
     * Sets a listener for text selection events.
     *
     * @param listener the listener
     */
    public void setTextSelectionListener(TextSelectionListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Sets a listener for MouseOver and MouseOut events on
     * any span in the HTML that has an ID assigned to it.
     *
     * @param listener the listener
     */
    public void setTextHoverListener(TextMouseListener listener) {
		this.mouseListener = listener;
	}

}
