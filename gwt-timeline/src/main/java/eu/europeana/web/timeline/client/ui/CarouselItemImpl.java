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

import com.google.gwt.query.client.Properties;
import eu.europeana.web.timeline.client.ui.effects.Tween;

/**
 * Modified GWT composite for use in CarouselTable *and Carousel.

 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public abstract class CarouselItemImpl<I, E> extends ExtendedWidget implements CarouselItem<I, E> {

    private boolean cached;
    private boolean onStage;
    private int zIndex;
    private int scale;

    @Override
    public boolean isCached() {
        return cached;
    }

    @Override
    public boolean isOnStage() {
        return onStage;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public void setOnStage(boolean onStage) {
        this.onStage = onStage;
    }

    /**
     * @deprecated move to ExtendedWidget
     */
    @Deprecated
    public void setScale(Properties properties) {
        new Tween(properties, getElement()).start();
    }

}
