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

import eu.europeana.web.timeline.client.ui.NavigationIterator;

/**
 * Event notifier for CarouselPanel
 */
public interface PanelListener {
    /**
     * Event fired when a timeline component is selected
     *
     * @param navigationIterator selected timeline component
     */
    public void itemActivated(NavigationIterator navigationIterator);

    /**
     * Event fired when a timeline component is deselected
     *
     * @param navigationIterator deselected timeline component
     */
    public void itemDeactivated(NavigationIterator navigationIterator);

    /**
     * Event fired when a timeline component is highlighted
     *
     * @param navigationIterator highlighted timeline component
     */
    public void itemHighlighted(NavigationIterator navigationIterator);
}
