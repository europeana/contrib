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
 * Generic carousel table.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public interface CarouselTable<Element extends CarouselItem> {

    /**
     * Add an element to the end of the table.
     *
     * @param element The element to add
     * @return The position of the added element
     */
    int addToEnd(Element element);

    /**
     * Add an element to the begin of the table.
     *
     * @param element The element to add
     * @return The position of the added element
     */
    int addToBegin(Element element);

    /**
     * Insert an element between two elements.
     *
     * @param element      The element to insert
     * @param afterElement Insert after this element
     * @return The position of the inserted element
     */
    int insert(Element element, Element afterElement);

    /**
     * Insert a list of elements between two elements.
     *
     * @param elements     The list of allements to insert
     * @param afterElement Add after this element
     * @return The position of the last inserted element
     */
    int insert(List<Element> elements, Element afterElement);

    /**
     * Get the position index of the element.
     *
     * @param element The element
     * @return It's position
     */
    int getIndex(Element element);

    /**
     * Get the last element from the table.
     *
     * @return The last element
     */
    Element getLastItem();

    /**
     * Get the first element from the table.
     *
     * @return The first element.
     */
    Element getFirstItem();

    /**
     * Get the last cell index.
     *
     * @return The last cell index
     * @deprecated No need for this method in CarouselFlow
     */
    @Deprecated
    int getLastCellIndex();
}
