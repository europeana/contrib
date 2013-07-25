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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import eu.europeana.web.timeline.client.ui.effects.Tween;
import eu.europeana.web.timeline.client.ui.effects.Property;

/**
 * Represents a time period. Each period contains children that are accessible
 * via children. A period can have a parentPeriod and/or children. The parent
 * period is the higher level, the children the deeper level.
 * <p/>
 * For example; A century period has a parent which is a millenium, and has children
 * of the type decade.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@Deprecated
public class Period extends Composite implements Comparable<Period>, ClickHandler {

    private Period nextPeriod;
    private Period previousPeriod;
    private Period lastPeriod;
    private Period parentPeriod;
    private PeriodType periodType;
    private Label captionLabel;
    private String caption;
    private Listener listener;
    private long availableItemCount;
    private Tooltip tooltip;

    interface Listener {
        public void activated(Period period);
    }

    public Period(String caption, long availableItemCount) {
        this.caption = caption;
        this.availableItemCount = availableItemCount;
        captionLabel = new Label(caption);
        init();
    }

    public long getAvailableItemCount() {
        return availableItemCount;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private CellPanel createPanel() {
        return new DockPanel();
    }

    private void init() {
        DockPanel dockPanel = (DockPanel) createPanel();
        dockPanel.add(captionLabel, DockPanel.NORTH);
        initWidget(dockPanel);
        addStyleName("period-initial");
        addStyleName("mouse-cursor");
        captionLabel.addClickHandler(this);

        captionLabel.addMouseOverHandler(
                new MouseOverHandler() {

                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        showAvailableItems(event);
                        activate();
                    }
                }
        );

        captionLabel.addMouseOutHandler(
                new MouseOutHandler() {

                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        hideAvailableItems(event);
                        deactivate();
                    }
                }
        );
    }

    private void showAvailableItems(MouseOverEvent event) {
        tooltip = new Tooltip(availableItemCount + " items available", this.getAbsoluteLeft() - 55 + 20, this.getAbsoluteTop() - 60, 110, 60); // todo: hardcoded
        tooltip.setStyleName("tooltip");
        tooltip.addStyleName("invisible");
        tooltip.activate();
    }

    private void hideAvailableItems(MouseOutEvent event) {
        if (null != tooltip) {
            tooltip.deactivate();
        }
    }

    public Period getNextPeriod() {
        if (null != nextPeriod) {
            return nextPeriod;
        }
        else {
            return this;
        }
    }

    public void setNextPeriod(Period nextPeriod) {
        this.nextPeriod = nextPeriod;
    }

    public Period getPreviousPeriod() {
        if (null != previousPeriod) {
            return previousPeriod;
        }
        else {
            return this;
        }
    }

    public void setPreviousPeriod(Period previousPeriod) {
        this.previousPeriod = previousPeriod;
    }

    public Period getLastPeriod() {
        return lastPeriod;
    }

    public void setLastPeriod(Period lastPeriod) {
        this.lastPeriod = lastPeriod;
    }

    public Period getParentPeriod() {
        return parentPeriod;
    }

    public void setParentPeriod(Period parentPeriod) {
        this.parentPeriod = parentPeriod;
    }

    public PeriodType getPeriodRange() {
        return periodType;
    }

    public void setPeriodRange(PeriodType periodType) {
        this.periodType = periodType;
    }

    public Label getCaptionLabel() {
        return captionLabel;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void deactivate() {
        new Tween(Property.opacity(0.4), getElement()).start();
    }

    public void activate() {
        new Tween(Property.opacity(0.8), getElement()).start();
    }

    @Override
    public int compareTo(Period o) {
        return o.getCaption().compareTo(caption);
    }

    @Override
    public void setStyleName(String style) {
        captionLabel.setStyleName(style);
    }

    @Override
    public void onClick(ClickEvent event) {
        listener.activated(this);
    }

    @Override
    public String toString() {
        return "Period{" +
                "value=" + caption +
                ", hasNextPeriod=" + (null != nextPeriod) +
                ", hasPreviousPeriod=" + (null != previousPeriod) +
                '}';
    }
}
