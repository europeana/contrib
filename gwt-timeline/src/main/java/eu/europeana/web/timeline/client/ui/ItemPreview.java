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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import eu.europeana.web.timeline.client.Item;
import eu.europeana.web.timeline.client.constants.Constants;
import eu.europeana.web.timeline.client.constants.Dimensions;
import eu.europeana.web.timeline.client.events.ItemListener;
import eu.europeana.web.timeline.client.events.ItemPreviewEvent;
import eu.europeana.web.timeline.client.network.Cacheable;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual representation of a timeline item. By default the thumbnails are not cached but a<br/>
 * placeholder image is used. When the cache method is invoked, the thumbnail will be fetched<br/>
 * from the server.
 * <p/>
 * Items can have previous or next items.
 * <p/>
 * If there is no thumbnail available, a default thumbnail will be used based<br/>
 * on the item type.
 * <p/>
 * In active state the item :
 * <ul>
 * <li>is clickable
 * <li>shows the preview
 * <li>is on stage
 * </ul>
 * <p/>
 * An item is eligible for caching when the item :
 * <ul>
 * <li>is on stage OR in the cache range
 * <li>the scroll speed is < MAX_SCROLL_SPEED
 * </ul>
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 * @see eu.europeana.web.timeline.client.Item
 * @see eu.europeana.web.timeline.client.ui.ScrollComponent
 * @see eu.europeana.web.timeline.client.network.Cacheable
 */
public class ItemPreview extends CarouselItemImpl<ItemPreview, ItemPreviewEvent> implements Visual, Cacheable {

    private Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private Constants constants = (Constants) GWT.create(Constants.class);
    private int width;
    private int height;
    private int originalWidth;
    private int originalHeight;
    private boolean active;
    private boolean onStage;
    private boolean cached;
    private ItemPreview nextComponent;
    private ItemPreview previousComponent;
    private CarouselImage thumbnail = new CarouselImage();
    private String uri;
    private Tooltip itemDescription;
    private Item item;
    private List<ItemListener<ItemPreview, ItemPreviewEvent>> listeners = new ArrayList<ItemListener<ItemPreview, ItemPreviewEvent>>();
    private String thumbnailUrl;
    private boolean scaledDown = false;

    public ItemPreview(Item item) {
        this.item = item;
        thumbnailUrl = constants.cacheUrl(item.getType(), item.getThumbnail());
        buildLayout();
        applyStyles();
        configureListeners();
    }

    @Override
    public void configureListeners() {

    }

    public void dispatchEvent(ItemPreviewEvent itemPreviewEvent) {
        for (ItemListener<ItemPreview, ItemPreviewEvent> listener : listeners) {
            listener.stateChanged(itemPreviewEvent);
        }
    }

    public void enablePreview() {
//        if (null != itemDescription) {
//            itemDescription.activate();
//            return;
//        }
        itemDescription = new Tooltip(this,
                new HTML(constants.previewHtml(item.getTitle(), item.getYear(), item.getCreator()), true),
                this.getAbsoluteLeft(), this.getAbsoluteTop(), dimensions.thumbnailWidth(), dimensions.thumbnailHeight());
        itemDescription.setStyleName("tooltip-item");
        itemDescription.addStyleName("invisible");
        itemDescription.setWidth(dimensions.thumbnailWidth() + "px");
        itemDescription.activate();
    }

    @Override
    public void applyStyles() {
        setStyleName("thumbnail");
    }

    @Override
    public int getWidth() {
        return dimensions.thumbnailWidth();
    }

    @Override
    public int getHeight() {
        return dimensions.thumbnailHeight();
    }

    @Override
    public void buildLayout() {
        add(thumbnail);
        width = dimensions.thumbnailWidth();
        height = dimensions.thumbnailHeight();
    }

    public void addClickHandler(ClickHandler clickHandler) {
        thumbnail.addClickHandler(clickHandler);
    }

    public void setNextComponent(ItemPreview nextComponent) {
        this.nextComponent = nextComponent;
    }

    public void setPreviousComponent(ItemPreview previousComponent) {
        this.previousComponent = previousComponent;
    }

    public String getUri() {
        return item.getUri();
    }

    @Override
    public void activate() {
        active = true;
        addStyleName("mouse-cursor");
        dispatchEvent(ItemPreviewEvent.ACTIVATED);
    }

    @Override
    public void deactivate() {
        if (!active) {
            return;
        }
        active = false;
        removeStyleName("mouse-cursor");
        dispatchEvent(ItemPreviewEvent.DEACTIVATED);
        for (ItemListener<ItemPreview, ItemPreviewEvent> listener : listeners) {
            listener.deactivated(this);
        }
        if (null != itemDescription) {
            itemDescription.hide();
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean hasPrevious() {
        return null != previous();
    }

    @Override
    public ItemPreview previous() {
        return previousComponent;
    }

    @Override
    public boolean hasNext() {
        return null != next();
    }

    @Override
    public ItemPreview next() {
        return nextComponent;
    }

    @Override
    public void cache() {
        if (isCached()) {
            return;
        }
//        final Tooltip preloader = new Tooltip(this, new Image(dimensions.preloaderImage()));
//        preloader.show();
        thumbnail.setUrl(thumbnailUrl);
        thumbnail.addLoadHandler(
                new LoadHandler() {

                    @Override
                    public void onLoad(LoadEvent loadEvent) {
//                        preloader.hide();
                        setOriginalWidth(thumbnail.getWidth());
                        setOriginalHeight(thumbnail.getHeight());
                        dispatchEvent(ItemPreviewEvent.CACHED);
                        int centerX = getWidth() / 2 - (thumbnail.getWidth() / 2);
                        int centerY = getHeight() / 2 - (thumbnail.getHeight() / 2);
                        setWidgetPosition(thumbnail, centerX, centerY);
                        GWT.log("Size of thumbnail is now : " + getOriginalWidth() + "x" + getOriginalHeight());
                        setCached(true);
                    }
                }
        );
    }


    /**
     * Destroy item
     */
    public void tearDown() {
        removeStyleName("mouse-cursor");
        if (null != itemDescription) {
            itemDescription.deactivate();
        }
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalWidth(int originalWidth) {
        this.originalWidth = originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(int originalHeight) {
        this.originalHeight = originalHeight;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    @Override
    public boolean isCached() {
        return cached;
    }

    public boolean isOnStage() {
        return onStage;
    }

    @Deprecated
    public void setOnStage(boolean onStage) {
        this.onStage = onStage;
    }

    @Override
    public void addListener(ItemListener<ItemPreview, ItemPreviewEvent> itemListener) {
        listeners.add(itemListener);
    }

    @Override
    public boolean removeListener(ItemListener<ItemPreview, ItemPreviewEvent> itemListener) {
        return listeners.remove(itemListener);
    }

    public void disablePreview() {
        itemDescription.deactivate();
    }

    private class CarouselImage extends Image {

        private List<LoadHandler> listeners = new ArrayList<LoadHandler>();

        @Override
        public HandlerRegistration addLoadHandler(LoadHandler handler) {
            if (listeners.size() > 1) {
                throw new IndexOutOfBoundsException("CarouselImage can only have 0 or 1 loadHandler");
            }
            HandlerRegistration registration = super.addLoadHandler(handler);
            listeners.add(handler);
            return registration;
        }
    }

    @Override
    public String toString() {
        return "ItemPreview{" +
                "height=" + height +
                ", width=" + width +
                ", active=" + active +
                ", onStage=" + onStage +
                ", hasNext=" + hasNext() +
                ", hasPrevious=" + hasPrevious() +
                '}';
    }
}
