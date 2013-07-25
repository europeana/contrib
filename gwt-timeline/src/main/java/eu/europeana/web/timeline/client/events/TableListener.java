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

package eu.europeana.web.timeline.client.events;

/**
 * Informs the listener about cell events and the widget that has been added to the table
 */
public interface TableListener<E> {

    /**
     * A new cell has been added to the table
     *
     * @param row which row?
     * @param col which column?
     * @param e   The item that has been added to the cell
     */
    public void cellAdded(int row, int col, E e);

    /**
     * A cell has been removed from the table
     *
     * @param row which row?
     * @param col which column?
     * @param e   The widget that has been removed from the cell
     */
    public void cellRemoved(int row, int col, E e);
}
