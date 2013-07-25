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

package eu.europeana.web.timeline.client.constants;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Access to timeline constants from properties file
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public interface Dimensions extends com.google.gwt.i18n.client.Constants, IsSerializable {

    @Key("carousel.rightImage")
    String carouselRightImage();

    @Key("carousel.leftImage")
    String carouselLeftImage();

    @Key("carousel.width")
    int carouselWidth();

    @Key("carousel.height")
    int carouselHeight();

    @Key("carousel.maxVisibleItems")
    int carouselMaxVisibleItems();

    @Key("effects.scaleFactor")
    float effectsScaleFactor();

    @Key("timeline.rightImage")
    String timelineRightImage();

    @Key("timeline.leftImage")
    String timelineLeftImage();

    @Key("thumbnail.image")
    String thumbnailImage();

    @Key("thumbnail.audio")
    String thumbnailAudio();

    @Key("thumbnail.video")
    String thumbnailVideo();

    @Key("thumbnail.text")
    String thumbnailText();

    @Key("messageBox.width")
    String messageBoxWidth();

    @Key("messageBox.height")
    String messageBoxHeight();

    @Key("thumbnail.width")
    int thumbnailWidth();

    @Key("thumbnail.height")
    int thumbnailHeight();

    @Key("timeline.width")
    int timelineWidth();

    @Key("timeline.height")
    int timelineHeight();

    @Key("solr.address")
    String solrAddress();

    @Key("scrollbar.scroller")
    String scrollbarScroller();

    @Key("scrollbar.background")
    String scrollbarBackground();

    @Key("europeana.pleaseWait")
    String europeanaPleaseWait();

    @Key("carousel.cacheItems")
    int carouselCacheItems();

    @Key("tooltip.width")
    String tooltipWidth();

    @Key("tooltip.height")
    String tooltipHeight();

    @Key("tooltip.availableItems")
    String tooltipAvailableItems();

    @Key("carousel.maxScrollSpeed")
    int carouselMaxScrollSpeed();

    @Key("preloader.image")
    String preloaderImage();

    @Key("style.unit")
    String styleUnit();

    @Key("thumbnail.holder")
    String thumbnailHolder();

    @Key("query.year")
    String queryYear();

    @Key("button.close")
    String buttonClose();

    @Key("carousel.padding")
    int carouselPadding();
}
