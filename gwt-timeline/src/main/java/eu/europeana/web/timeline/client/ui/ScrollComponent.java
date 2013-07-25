/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.0 or? as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.europeana.web.timeline.client.ui;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import eu.europeana.web.timeline.client.constants.Dimensions;
import eu.europeana.web.timeline.client.events.ScrollDirection;
import eu.europeana.web.timeline.client.events.ScrollEvent;
import eu.europeana.web.timeline.client.events.ScrollEventListener;
import eu.europeana.web.timeline.client.ui.effects.Tween;
import eu.europeana.web.timeline.client.ui.effects.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Scrolls through the data bound to the scroller itself. When scrolling speed
 * exceeds MAX_SCROLLING_SPEED sets the scroller to busy and no items will
 * be cached during that time.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class ScrollComponent extends Composite {

    private Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private int idlePosition;
    private boolean busy;
    private AbsolutePanel absolutePanel = new AbsolutePanel();
    private Image scroller;
    private List<ScrollEventListener> scrollEventListeners = new ArrayList<ScrollEventListener>();

    private Timer timer = new Timer() {

        @Override
        public void run() {
            notifyScrolling(new ScrollEvent(determineSpeed(), determineDirection()));
        }
    };

    private static final int RATE = 50;   // todo: hardcoded

    private void init() {
        absolutePanel.addStyleName(DragClientBundle.INSTANCE.css().boundary());
        scroller = new Image(dimensions.scrollbarScroller());
        idlePosition = dimensions.carouselWidth() / 2 - (scroller.getWidth() / 2);
        absolutePanel.add(scroller);
        absolutePanel.setStyleName("scrollbar");
        absolutePanel.add(scroller);
        initWidget(absolutePanel);
        PickupDragController dragController = new PickupDragController(absolutePanel, true);
        dragController.makeDraggable(scroller);
        dragController.addDragHandler(
                new DragHandler() {

                    @Override
                    public void onDragEnd(DragEndEvent dragEndEvent) {
                        busy = false;
                        timer.cancel();
                        new Tween(Property.positionX(idlePosition), scroller.getElement()).start();
                        notifyDragEnd(new ScrollEvent(0, ScrollDirection.NONE));
                    }

                    @Override
                    public void onDragStart(DragStartEvent dragStartEvent) {
                        busy = true;
                        notifyDragStart(new ScrollEvent(0, determineDirection()));
                        timer.scheduleRepeating(RATE);
                    }

                    @Override
                    public void onPreviewDragEnd(DragEndEvent dragEndEvent) throws VetoDragException {
                    }

                    @Override
                    public void onPreviewDragStart(DragStartEvent dragStartEvent) throws VetoDragException {
                    }
                }
        );
    }

    private int determineSpeed() {
        return Math.abs((idlePosition + this.getAbsoluteLeft()) - scroller.getAbsoluteLeft());
    }

    private ScrollDirection determineDirection() {
        if (scroller.getAbsoluteLeft() < (idlePosition + this.getAbsoluteLeft())) {
            return ScrollDirection.LEFT;
        }
        else {
            return ScrollDirection.RIGHT;
        }
    }

    private void notifyDragStart(ScrollEvent scrollEvent) {
        for (ScrollEventListener scrollEventListener : scrollEventListeners) {
            scrollEventListener.scrollStart(scrollEvent);
        }
    }

    private void notifyDragEnd(ScrollEvent scrollEvent) {
        for (ScrollEventListener scrollEventListener : scrollEventListeners) {
            scrollEventListener.scrollEnd(scrollEvent);
        }
    }

    private void notifyScrolling(ScrollEvent scrollEvent) {
        for (ScrollEventListener scrollEventListener : scrollEventListeners) {
            scrollEventListener.scrolling(scrollEvent);
        }
    }

    public ScrollComponent() {
        init();
    }

    public void addListener(ScrollEventListener scrollEventListener) {
        scrollEventListeners.add(scrollEventListener);
    }

    public boolean removeListener(ScrollEventListener scrollEventListener) {
        return scrollEventListeners.remove(scrollEventListener);
    }

    public boolean isBusy() {
        return busy;
    }
}