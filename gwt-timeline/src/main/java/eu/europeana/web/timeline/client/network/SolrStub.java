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

package eu.europeana.web.timeline.client.network;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.europeana.web.timeline.client.Item;
import eu.europeana.web.timeline.client.constants.Constants;
import eu.europeana.web.timeline.client.constants.Dimensions;
import eu.europeana.web.timeline.client.events.EventModel;
import eu.europeana.web.timeline.client.events.LoadEvent;
import eu.europeana.web.timeline.client.events.LoadListener;
import eu.europeana.web.timeline.client.ui.CarouselImpl;
import eu.europeana.web.timeline.client.ui.ErrorMessage;
import eu.europeana.web.timeline.client.ui.ItemPreview;
import eu.europeana.web.timeline.client.ui.Timeline;
import eu.europeana.web.timeline.client.ui.Year;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 *         todo: refactoring needed
 */
public class SolrStub implements EventModel<LoadListener> {

    private List<LoadListener> listeners = new ArrayList<LoadListener>();
    private SolrServiceAsync solrServiceAsync = (SolrServiceAsync) GWT.create(SolrService.class);
    private Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private Constants constants = (Constants) GWT.create(Constants.class);
    private Timeline timeline;
    private CarouselImpl carousel;

    @Override
    public void addListener(LoadListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean removeListener(LoadListener listener) {
        return listeners.remove(listener);
    }

    public void setCarouselPanel(CarouselImpl carousel) {
        this.carousel = carousel;
    }

    /**
     * Retrieve all years from Solr.
     *
     * @see eu.europeana.web.timeline.client.network.SolrServiceAsync
     */
    public void loadYears() {
        solrServiceAsync.retrieveYears(
                new AsyncCallback<List<Year>>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("Error loading years " + throwable);
                    }

                    @Override
                    public void onSuccess(List<Year> years) {
                        if (null == years) {
                            ErrorMessage errorMessage = new ErrorMessage(constants.errorSolr());
                            errorMessage.show();
                        }
                        timeline.setYears(years);
                        timeline.setActivePeriod(timeline.getPeriods().get(0));
                        for (LoadListener listener : listeners) {
                            listener.loadEnd(new LoadEvent());
                        }
                    }
                }
        );
    }

    /**
     * Retrieve items from Solr.
     *
     * @param query  The query to execute
     * @param offset Skip loading items
     * @param limit  The maximum number of items to load
     * @param clear  Clear any existing items
     * @see eu.europeana.web.timeline.client.network.SolrServiceAsync
     */
    public void loadItems(String query, Integer offset, Integer limit, final boolean clear) {
        solrServiceAsync.retrieveBriefItems(query, offset, limit,
                new AsyncCallback<List<Item>>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log("Error retrieving brief items" + throwable);
                    }

                    @Override
                    public void onSuccess(List<Item> items) {
                        List<ItemPreview> itemPreviews = new ArrayList<ItemPreview>();
                        for (Item item : items) {
                            itemPreviews.add(new ItemPreview(item));
                        }
                        carousel.addItems(itemPreviews, clear);
                        LoadEvent loadEvent = new LoadEvent();
                        for (LoadListener listener : listeners) {
                            listener.loadEnd(loadEvent);
                        }
                    }
                }
        );
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
}
