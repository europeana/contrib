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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import eu.europeana.web.timeline.client.constants.Dimensions;
import eu.europeana.web.timeline.client.events.CarouselListener;
import eu.europeana.web.timeline.client.events.EventModel;
import eu.europeana.web.timeline.client.events.ItemListener;
import eu.europeana.web.timeline.client.events.ItemPreviewEvent;
import eu.europeana.web.timeline.client.events.TableListener;
import eu.europeana.web.timeline.client.ui.effects.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * The carousel is providing navigation for the CarouselTableImpl and it's items.<br/>
 * The layout consists of the following items:
 * <ul>
 * <li>CarouselTableImpl holding the items
 * <li>ScrollComponent for scrolling through the items
 * <li>Next and previous buttons for selecting the next or previous item
 * <li>Input field for refining the search
 * </ul>
 * <p/>
 * The carousel has one active item which is centered. If the active item is at<br/>
 * the end of the panel, the navigation button for that side will be disabled. Scrolling<br/>
 * in that direction will be disabled as well.
 * <p/>
 * The pipeline contains all fetched items from Solr. Not all the items in the pipeline
 * are visible, only items that are onStage will be visible and are accessible by getVisibleItems
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class CarouselImpl extends DockLayoutPanel implements Carousel<ItemPreview>, EventModel<CarouselListener> {

    private Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private final CarouselFlow carouselTableImpl = new CarouselFlow();
    private ItemPreview activeItem;
    private AbsolutePanel carouselHolder = new AbsolutePanel();
    private Image leftButton = new Image(dimensions.carouselLeftImage());
    private Image rightButton = new Image(dimensions.carouselRightImage());
    private List<CarouselListener> listeners = new ArrayList<CarouselListener>();

    {
        buildLayout();
        applyStyles();
        configureListeners();
    }

    public CarouselImpl() {
        super(Style.Unit.PX);
    }

    @Override
    public void addToEnd(ItemPreview itemPreview) {
        carouselTableImpl.addToEnd(itemPreview);
    }

    @Override
    public void addToBegin(ItemPreview itemPreview) {
        carouselTableImpl.addToBegin(itemPreview);
    }

    @Override
    public void insert(ItemPreview itemPreview, ItemPreview after) {
        //todo: insert method not defined in carouselTableImpl yet.
    }

    private void handleState(ItemPreview itemPreview, ItemPreviewEvent itemPreviewEvent) {
        switch (itemPreviewEvent) {
            case IN_PIPELINE:
                break;
            case ACTIVATED:
                itemPreview.addStyleName("active");
                if (null != itemPreview.previous()) {
                    itemPreview.previous().deactivate();
                }
                if (null != itemPreview.next()) {
                    itemPreview.next().deactivate();
                }
                itemPreview.enablePreview();
                break;
            case CACHED:
//                itemPreview.setScale(Property.scale(itemPreview.getWidth() * (index / 10), itemPreview.getHeight() * (index / 10)));
                break;
            case DEACTIVATED:
                itemPreview.removeStyleName("active");
                itemPreview.disablePreview();
//                itemPreview.setScale(Property.scale(itemPreview.getWidth() * (index / 10), itemPreview.getHeight() * (index / 10)));
                break;
            case ON_STAGE:
                itemPreview.cache();
                break;
            case REMOVED:
                itemPreview.tearDown();
                break;
        }
    }

    @Override
    public void addItems(List<ItemPreview> items, boolean clear) {
        if (clear) {
            this.carouselTableImpl.clear();
        }
        ItemPreview lastItem = null;
        for (final ItemPreview itemPreview : items) {
//            itemPreview.addClickHandler(
//                    new ClickHandler() {
//
//                        @Override
//                        public void onClick(ClickEvent clickEvent) {
//                            if (!itemPreview.isActive()) {
//                                activate(getCenterItem());
//                            }
//                            else {
//                                followLink();
//                            }
//                        }
//
//                        private void followLink() {
//                            Window.open(itemPreview.getUri(), "_blank", "");
//                        }
//                    }
//            );
            itemPreview.addListener(
                    new ItemListener<ItemPreview, ItemPreviewEvent>() {

                        @Override
                        public void activated(ItemPreview itemPreview) {
                        }

                        @Override
                        public void deactivated(ItemPreview itemPreview) {
                        }

                        @Override
                        public void stateChanged(ItemPreviewEvent itemPreviewEvent) {
                            handleState(itemPreview, itemPreviewEvent);
                        }
                    }
            );
//            itemPreview.dispatchEvent(ItemPreviewEvent.IN_PIPELINE);
            /**
             * @deprecated don't add to end, or don't move to end
             */

            //carouselTableImpl.addToEnd(itemPreview);
            itemPreview.setPreviousComponent(lastItem);
            if (null != itemPreview.previous()) {
                itemPreview.previous().setNextComponent(itemPreview);
            }
            lastItem = itemPreview;
        }
        carouselTableImpl.setItems(items);
        // todo: decide where to add item based on click
        activate(getCenterItem());
    }

    private void activate(ItemPreview itemPreview) {
        if (null == itemPreview) {
            return;
        }
        if (null != activeItem) {
            // todo: scale previous and next item if present
        }
        itemPreview.setScale(Property.scale(itemPreview.getWidth(), itemPreview.getHeight()));
        itemPreview.setActive(true);
        itemPreview.activate();
        activeItem = itemPreview;
        if (getCenterItem() != activeItem) { // todo: redundant
            centerItem(itemPreview);
        }
    }

    /**
     * Center the given item. First the position needs to be determined. If the item is
     * on the left side, an item should be added to the beginning.
     *
     * @param itemPreview The item to center
     * @deprecated do this while moving the items
     */
    @Deprecated
    private void centerItem(ItemPreview itemPreview) {
        switch (carouselTableImpl.getDirection(itemPreview)) {
            case CENTER:
                // nothing
                break;
            case LEFT:
                carouselTableImpl.addToBegin((carouselTableImpl.getFirstItem()).previous());
                break;
            case RIGHT:
                carouselTableImpl.addToEnd((carouselTableImpl.getLastItem()).next());
                break;
            default:
        }
    }

    // todo: review needed

    private ItemPreview getCenterItem() {
        int centerItem = (int) Math.ceil(carouselTableImpl.getPipeline().getVisibleItems().size() / 2);
        return ((null == carouselTableImpl.getPipeline().getVisibleItems() || 0 == centerItem) ? null : carouselTableImpl.getPipeline().getVisibleItems().get(centerItem));
    }

    @Override
    public ItemPreview selectNextItem() {
        return (null == activeItem.next() ? null : activeItem.next());
    }

    @Override
    public ItemPreview selectPreviousItem() {
        return (null == activeItem.previous() ? null : activeItem.previous());
    }

    @Override
    public void handleItemActivated(ItemPreview itemPreview) { // todo: redundant?
        activate(itemPreview);
    }

    @Override
    public ItemPreview getActiveItem() {
        return activeItem;
    }

    @Override
    public void buildLayout() {
        setHeight(dimensions.carouselHeight() + "px");
        carouselHolder.add(carouselTableImpl, 80, 20);
        carouselHolder.add(leftButton, 50, 150);
        carouselHolder.add(rightButton, 850, 150);
        add(carouselHolder);
    }

    @Override
    public void applyStyles() {
        rightButton.addStyleName("mouse-cursor");
        leftButton.addStyleName("mouse-cursor");
    }

    @Override
    public int getWidth() {
        return dimensions.carouselWidth();
    }

    @Override
    public int getHeight() {
        return dimensions.carouselHeight();
    }

    @Override
    public void configureListeners() {
        carouselTableImpl.addListener(
                new TableListener<ItemPreview>() {

                    @Override
                    public void cellAdded(int row, int col, ItemPreview itemPreview) {
                        itemPreview.setOnStage(true);
                        itemPreview.dispatchEvent(ItemPreviewEvent.ON_STAGE);
                    }

                    @Override
                    public void cellRemoved(int row, int col, ItemPreview itemPreview) {
                        itemPreview.setOnStage(false);
                        itemPreview.dispatchEvent(ItemPreviewEvent.REMOVED);
                        GWT.log("Removed! " + itemPreview);
                    }
                }
        );
        rightButton.addClickHandler(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        carouselTableImpl.selectNext();
                    }
                }
        );
        leftButton.addClickHandler(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        carouselTableImpl.selectPrevious();
                    }
                }
        );
    }

    @Override
    public void addListener(CarouselListener carouselListener) {
        listeners.add(carouselListener);
    }

    @Override
    public boolean removeListener(CarouselListener carouselListener) {
        return listeners.remove(carouselListener);
    }
}
