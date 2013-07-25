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

import eu.europeana.web.timeline.client.events.EventModel;
import eu.europeana.web.timeline.client.events.ItemListener;
import eu.europeana.web.timeline.client.network.Cacheable;

/**
 * Items used by the Carousel.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 * @see eu.europeana.web.timeline.client.network.Cacheable
 */
public interface CarouselItem<E, T> extends Cacheable, NavigationIterator, EventModel<ItemListener<E, T>> {

    /**
     * Is this item on the stage?
     *
     * @return True if it is
     */
    boolean isOnStage();

    /**
     * Activate the component
     */
    void activate();

    /**
     * Deactivate the component
     */
    void deactivate();

    /**
     * Tests whether the component is active
     *
     * @return is it active?
     */
    boolean isActive();
}
