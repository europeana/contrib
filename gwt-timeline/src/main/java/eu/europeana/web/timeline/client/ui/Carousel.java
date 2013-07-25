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

import java.util.List;

/**
 * Navigation for the CarouselTable. Controls the navigation buttons and scroller and
 * keeps track of the item states.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 * @see eu.europeana.web.timeline.client.ui.CarouselTable
 */
public interface Carousel<E extends CarouselItem> extends Visual {

    /**
     * Select the next item.
     *
     * @return The next item or null if last item
     */
    E selectNextItem();

    /**
     * Select the previous item.
     *
     * @return The previous item or null if first item.
     */
    E selectPreviousItem();

    /**
     * Handle the item after it's activated.
     *
     * @param e The activated item
     */
    void handleItemActivated(E e);

    /**
     * Get the current active item.
     *
     * @return The active item
     */
    E getActiveItem();

    /**
     * Sets the items of the carousel and clears any existing item.
     *
     * @param items The items
     * @param clear Remove the existing items
     */
    void addItems(List<E> items, boolean clear);

    /**
     * Add a single item to end of the carousel.
     *
     * @param e The item to add
     */
    void addToEnd(E e);

    /**
     * Add a single item to the beginning of the carousel.
     *
     * @param e The item to add
     */
    void addToBegin(E e);

    /**
     * Insert an item after another item.
     *
     * @param e     The item to insert
     * @param after Insert after this item
     */
    void insert(E e, E after);
}
