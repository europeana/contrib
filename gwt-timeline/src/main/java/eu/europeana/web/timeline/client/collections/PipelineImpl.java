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

package eu.europeana.web.timeline.client.collections;

import com.google.gwt.core.client.GWT;
import eu.europeana.web.timeline.client.constants.Dimensions;
import eu.europeana.web.timeline.client.events.ItemListener;
import eu.europeana.web.timeline.client.events.ItemPreviewEvent;
import eu.europeana.web.timeline.client.ui.ItemPreview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of Pipeline.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class PipelineImpl extends Pipeline<ItemPreview, ItemPreviewEvent, Pipeline.ActivationListener<Pipeline.ActivationEvent, ItemPreview>> {

    private Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private List<ActivationListener<ActivationEvent, ItemPreview>> listeners = new ArrayList<ActivationListener<ActivationEvent, ItemPreview>>();
    private final int MAX_ITEMS = dimensions.carouselCacheItems();

    @Override
    public boolean add(ItemPreview item) {
        if (size() >= MAX_ITEMS) {
            throw new IndexOutOfBoundsException("Attempted to exceed limit of " + MAX_ITEMS + " items");
        }
        item.addListener(
                new ItemListener<ItemPreview, ItemPreviewEvent>() {

                    @Override
                    public void activated(ItemPreview itemPreview) {
                        handleActivation(itemPreview);
                    }

                    @Override
                    public void deactivated(ItemPreview itemPreview) {
                        dispatchEvent(ActivationEvent.ITEM_DEACTIVATED, itemPreview);
                    }

                    @Override
                    public void stateChanged(ItemPreviewEvent itemPreviewEvent) {
                        // not implemented
                    }
                }
        );
        return super.add(item);
    }

    private void handleActivation(ItemPreview itemPreview) {
        if (getLast() == itemPreview) {
            dispatchEvent(ActivationEvent.LAST_ITEM_SELECTED, itemPreview);
        }
        else if (getFirst() == itemPreview) {
            dispatchEvent(ActivationEvent.FIRST_ITEM_SELECTED, itemPreview);
        }
        else if (getVisibleItems().getLast() == itemPreview) {
            dispatchEvent(ActivationEvent.LAST_VISIBLE_ITEM_SELECTED, itemPreview);
        }
        else if (getVisibleItems().getFirst() == itemPreview) {
            dispatchEvent(ActivationEvent.FIRST_VISIBLE_ITEM_SELECTED, itemPreview);
        }
        else {
            dispatchEvent(ActivationEvent.ITEM_ACTIVATED, itemPreview);
        }
    }

    /**
     * Notify listeners about the event.
     *
     * @param activationEvent Which event?
     * @param itemPreview     The source item.
     */
    private void dispatchEvent(ActivationEvent activationEvent, ItemPreview itemPreview) {
        for (ActivationListener<ActivationEvent, ItemPreview> listener : listeners) {
            listener.activationEvent(activationEvent, itemPreview);
        }
    }

    @Override
    public LinkedList<ItemPreview> getVisibleItems() {
        LinkedList<ItemPreview> visibleItems = new LinkedList<ItemPreview>();
        for (ItemPreview itemPreview : this) {
            if (itemPreview.isOnStage()) {
                visibleItems.add(itemPreview);
            }
        }
        return visibleItems;
    }

    @Override
    public LinkedList<ItemPreview> getInvisibleItems() {
        LinkedList<ItemPreview> invisibleItems = new LinkedList<ItemPreview>();
        for (ItemPreview itemPreview : this) {
            if (!itemPreview.isOnStage()) {
                invisibleItems.add(itemPreview);
            }
        }
        return invisibleItems;
    }

    @Override
    public LinkedList<ItemPreview> getCachedItems() {
        LinkedList<ItemPreview> cachedItems = new LinkedList<ItemPreview>();
        for (ItemPreview itemPreview : this) {
            if (itemPreview.isCached()) {
                cachedItems.add(itemPreview);
            }
        }
        return cachedItems;
    }

    @Override
    public ItemPreview getNextInCache() {
        int lastVisibleItemIndex = indexOf(getVisibleItems().getLast());
        return get(lastVisibleItemIndex + 1);
    }

    @Override
    public ItemPreview getPreviousInCache() {
        int firstVisibleItemIndex = indexOf(getVisibleItems().getFirst());
        return get(firstVisibleItemIndex - 1);
    }

    @Override
    public void addListener(ActivationListener<ActivationEvent, ItemPreview> itemPreviewActivationListener) {
        listeners.add(itemPreviewActivationListener);
    }

    @Override
    public boolean removeListener(ActivationListener<ActivationEvent, ItemPreview> itemPreviewActivationListener) {
        return listeners.remove(itemPreviewActivationListener);
    }
}
