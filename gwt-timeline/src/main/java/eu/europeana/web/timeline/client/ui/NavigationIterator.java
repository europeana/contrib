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

/**
 * Generic visual timeline item, with linking to next and previous items.
 * Timeline items can be in activated or deactivated state.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public interface NavigationIterator<Item> {

    /**
     * Tests wheter the item has a next item.
     *
     * @return does it have a next item?
     */
    boolean hasNext();

    /**
     * Returns the next item.
     * 
     * @return the nex item
     */
    Item next();

    /**
     * Tests whether the item has a previous item
     *
     * @return does it have a previous item?
     */
    boolean hasPrevious();

    /**
     * Returns the previous item
     *
     * @return the previous item
     */
    Item previous();
}
