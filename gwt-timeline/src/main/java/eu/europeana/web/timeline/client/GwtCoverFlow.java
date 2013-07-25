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

package eu.europeana.web.timeline.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import eu.europeana.web.timeline.client.constants.Constants;
import eu.europeana.web.timeline.client.constants.Dimensions;
import eu.europeana.web.timeline.client.events.CarouselListener;
import eu.europeana.web.timeline.client.events.LoadEvent;
import eu.europeana.web.timeline.client.events.LoadListener;
import eu.europeana.web.timeline.client.network.SolrStub;
import eu.europeana.web.timeline.client.ui.CarouselImpl;
import eu.europeana.web.timeline.client.ui.Period;
import eu.europeana.web.timeline.client.ui.Timeline;
import eu.europeana.web.timeline.client.ui.Visual;

/**
 * The main panel holding both carousel and timeline.
 * Create a method called display.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class GwtCoverFlow implements EntryPoint, Visual {

    private Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private Constants constants = (Constants) GWT.create(Constants.class);
    private final RootPanel rootPanel = RootPanel.get();
    private Label statusLabel = new Label(dimensions.europeanaPleaseWait());
    private SolrStub stub = new SolrStub();
    private CarouselImpl carouselPanel = new CarouselImpl();
    private int offset = 0;
    private int limit = dimensions.carouselMaxVisibleItems() + (2 * dimensions.carouselCacheItems());

    private Timeline timeline = new Timeline(
            new Timeline.Listener() {

                @Override
                public void activatePeriod(Period activePeriod) {
                    loadItems(dimensions.queryYear() + activePeriod.getCaption(), offset, limit, true);
                    offset += limit;
                }
            }
    );

    public void onModuleLoad() {
        buildLayout();
        applyStyles();
        configureListeners();
        stub.setCarouselPanel(carouselPanel);
        stub.setTimeline(timeline);
        stub.loadYears();
    }

    private void loadItems(final String year, final Integer offset, final Integer limit, boolean clear) {
        stub.loadItems(year, offset, limit, clear);
        stub.addListener(
                new LoadListener() {

                    @Override
                    public void loadStart(LoadEvent loadEvent) {
                    }

                    @Override
                    public void loadEnd(LoadEvent loadEvent) {
//                        popupPanel.hide();
                        statusLabel.setText(constants.navigationHeader(offset - dimensions.carouselMaxVisibleItems() + 1 + "", limit + offset + "", timeline.getActivePeriod().getAvailableItemCount() + "", timeline.getActivePeriod().getCaption()));
                    }
                }
        );
    }

    @Override
    public void buildLayout() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setSize(dimensions.carouselWidth() + "px", dimensions.carouselHeight() + "px");
        verticalPanel.add(statusLabel);
        verticalPanel.add(carouselPanel);
        verticalPanel.add(timeline);
        rootPanel.add(verticalPanel);
    }

    @Override
    public void applyStyles() {
        statusLabel.setStyleName("status-label");
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
        carouselPanel.addListener(
                new CarouselListener() {

                    @Override
                    public void lastItemSelected() {
                        GWT.log("********LAST");
                        offset += 1;
                        loadItems(dimensions.queryYear() + timeline.getActivePeriod().getCaption(), offset, 1, false);
                    }

                    @Override
                    public void firstItemSelected() {
                        GWT.log("*******FIRST");
                        if (offset == 0) {
                            return;
                        }
                        offset -= 1;
                        loadItems(dimensions.queryYear() + timeline.getActivePeriod().getCaption(), offset, 1, false);
                    }
                }
        );
    }
}
