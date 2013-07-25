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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbsolutePanel;
import eu.europeana.web.timeline.client.collections.PipelineImpl;
import eu.europeana.web.timeline.client.constants.Dimensions;
import eu.europeana.web.timeline.client.events.EventModel;
import eu.europeana.web.timeline.client.events.TableListener;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowPanel implementation of CarouselTable. This implementation generates HTML divs rather than
 * HTML tables.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class CarouselFlow extends AbsolutePanel implements Visual, CarouselTable<ItemPreview>, EventModel<TableListener<ItemPreview>> {

    private final Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);

    public PipelineImpl getPipeline() {
        return pipeline;
    }

    private PipelineImpl pipeline = new PipelineImpl();
    private List<TableListener<ItemPreview>> tableListeners = new ArrayList<TableListener<ItemPreview>>();

    private int Y_OFFSET = 100;
    private final static int X_OFFSET = 190;

    {
        buildLayout();
        applyStyles();
        configureListeners();
    }

    /**
     * Add items to the pipeline.
     *
     * @param items The items to add to the pipeline.
     */
    public void setItems(List<ItemPreview> items) {
        clear();
        pipeline.clear();
        pipeline.addAll(items);
        addToStage();
    }

    private void addToStage() {
        int distance = 0;
        for (ItemPreview itemPreview : pipeline.subList(0, dimensions.carouselMaxVisibleItems())) {
            add(itemPreview, distance, Y_OFFSET);
            distance += itemPreview.getWidth() + dimensions.carouselPadding();
            notifyCellAdded(itemPreview);
        }
    }

    @Override
    public void buildLayout() {
        setSize(dimensions.carouselWidth() + "px", dimensions.carouselHeight() + "px");
    }

    @Override
    public void applyStyles() {
        setStyleName("CarouselFlow");
    }

    @Override
    @Deprecated
    public int addToEnd(ItemPreview itemPreview) {
        // todo: scale each item
        if (pipeline.size() > 0) {
            add(itemPreview, pipeline.getLast().getAbsoluteLeft() + X_OFFSET, Y_OFFSET);
            itemPreview.setBehind(getLastItem());
        }
        else {
            add(itemPreview, 0, Y_OFFSET);
        }
        pipeline.addLast(itemPreview);
        notifyCellAdded(itemPreview);
        return pipeline.indexOf(itemPreview);
    }

    @Override
    @Deprecated
    public int addToBegin(ItemPreview itemPreview) {
        if (pipeline.size() > 0) {
            add(itemPreview, pipeline.getFirst().getAbsoluteLeft() - X_OFFSET, Y_OFFSET);
        }
        else {
            add(itemPreview, 0, Y_OFFSET);
        }
        pipeline.addFirst(itemPreview);
        notifyCellAdded(itemPreview);
        return pipeline.indexOf(itemPreview);
    }

    @Override
    public int insert(ItemPreview itemPreview, ItemPreview afterElement) {
        return 0;  // todo: implement
    }

    @Override
    public int insert(List<ItemPreview> itemPreviews, ItemPreview afterElement) {
        return 0;  // todo: implement
    }

    @Override
    public int getIndex(ItemPreview itemPreview) {
        return pipeline.indexOf(itemPreview);
    }

    @Override
    public ItemPreview getLastItem() {
        return pipeline.getLast();  // todo: wrong, visible items != pipeline
    }

    @Override
    public ItemPreview getFirstItem() {
        return pipeline.getFirst();
    }

    @Override
    public int getLastCellIndex() {
        throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public void addListener(TableListener<ItemPreview> itemPreviewTableListener) {
        tableListeners.add(itemPreviewTableListener);
    }

    @Override
    public boolean removeListener(TableListener<ItemPreview> itemPreviewTableListener) {
        return tableListeners.remove(itemPreviewTableListener);
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
        // todo: implement
    }

    public void clear() {
        super.clear();
        pipeline.clear();
    }

    public boolean isLastItem(ItemPreview itemPreview) {
        return itemPreview == pipeline.getLast();
    }

    public boolean isFirstItem(ItemPreview itemPreview) {
        return itemPreview == pipeline.getFirst();
    }

    private void notifyCellAdded(ItemPreview itemPreview) {
        for (TableListener<ItemPreview> listener : tableListeners) {
            listener.cellAdded(0, pipeline.indexOf(itemPreview), itemPreview);
        }
    }

    private void notifyCellRemoved(ItemPreview itemPreview) {
        for (TableListener<ItemPreview> listener : tableListeners) {
            listener.cellRemoved(0, pipeline.indexOf(itemPreview), itemPreview);
        }
    }

    public Direction getDirection(ItemPreview itemPreview) {
        return null;  // todo: implement
    }

    /**
     * Adding a new item to the end of the carousel. This is done by the following steps:
     * <ul>
     * <li>Remove first visible item
     * <li>Make room by shifting all to left
     * <li>Add next in cache to end of carousel
     * <li>New query to Solr and add to end of cache
     * </ul>
     */
    public void selectNext() {
        ItemPreview firstVisible = pipeline.getVisibleItems().getFirst();
        firstVisible.fadeOut();
        firstVisible.setOnStage(false);
        GWT.log(firstVisible.toString());
        pipeline.removeFirst();
        notifyCellRemoved(firstVisible);
        ItemPreview nextInCache = pipeline.getNextInCache();
        for (ItemPreview item : pipeline.getVisibleItems()) {
            setWidgetPosition(item, item.getAbsoluteLeft() - dimensions.thumbnailWidth(), Y_OFFSET);
        }
        GWT.log("Count of visible items " + pipeline.getVisibleItems().size());
        GWT.log("Last item " + pipeline.getVisibleItems().getLast());
        GWT.log("Adding after " + pipeline.getVisibleItems().getLast().getAbsoluteLeft());
        add(nextInCache, pipeline.getVisibleItems().getLast().getAbsoluteLeft(), Y_OFFSET);
        notifyCellAdded(nextInCache);
    }

    /**
     * Adding a new item to the beginning of the carousel. This is done by the following steps:
     * <ul>
     * <li>Remove last visible item
     * <li>Make room by shifting all to right
     * <li>Add first in cache to beginning of carousel
     * <li>New query to Solr and add to begin of cache
     * </ul>
     */
    public void selectPrevious() {
        ItemPreview lastVisible = pipeline.getVisibleItems().getLast();
        lastVisible.fadeOut();
        lastVisible.setOnStage(false);
        pipeline.removeLast();
        notifyCellRemoved(lastVisible);
        ItemPreview firstInCache = pipeline.getPreviousInCache();
        for (ItemPreview item : pipeline.getVisibleItems()) {
            setWidgetPosition(item, item.getAbsoluteLeft() + dimensions.thumbnailWidth(), Y_OFFSET);
        }
        notifyCellAdded(firstInCache);
        add(firstInCache, pipeline.getVisibleItems().getFirst().getAbsoluteLeft(), Y_OFFSET);
    }
}
