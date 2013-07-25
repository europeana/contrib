package at.ait.dme.yuma.suite.apps.core.client.events;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;

/**
 * Event handler for reacting to various key events in a 
 * text edit form.
 * 
 * @author Rainer Simon
 */
public abstract class AbstractKeyboardHandler implements KeyDownHandler {
	
	/**
	 * The keyboard idleness timer
	 */
	private Timer timer = null;
	
	/**
	 * The delay threshold for triggering an idleness event
	 */
	private int delayMillis;
	
	public AbstractKeyboardHandler(int delayMillis) {
		this.delayMillis = delayMillis;
	}

	@Override
    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == 32)
        	onSpace();
        
		if (timer == null)
			timer = new Timer() {
				@Override
				public void run() {
					onIdle();				
				}
			};
			
		timer.schedule(delayMillis);
    }
	
	/**
	 * Override this to get notified when SPACE is pressed.
	 */
	public abstract void onSpace();
	
	/**
	 * Override this to get notified after a period of key idleness.
	 */
	public abstract void onIdle();
	
}
