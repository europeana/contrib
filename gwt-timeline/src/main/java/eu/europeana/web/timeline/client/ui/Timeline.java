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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import eu.europeana.web.timeline.client.constants.Dimensions;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a range of periods, selecting a period fires activatePeriod() and becomes
 * the activated period.
 * <p/>
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class Timeline extends Composite implements Visual {

    private Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private Period activePeriod;
    private Listener listener;
    private final FlexTable flexTable = new FlexTable();
    private List<Period> periods = new ArrayList<Period>();

    {
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(flexTable);
        initWidget(flowPanel);
    }

    public Timeline(Listener listener) {
        this.listener = listener;
        applyStyles();
    }

    public void setYears(List<Year> years) {
        int counter = 0;
        periods.clear();
        for (Year year : years) {
            Period period = new Period(year.getYear(), year.getAvailableItemCount());
            period.setListener(new Period.Listener() {

                @Override
                public void activated(Period period) {
                    setActivePeriod(period);
                }
            });
            flexTable.setWidget(1, counter, period);
            periods.add(period);
            counter++;
        }
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public interface Listener {
        public void activatePeriod(Period activePeriod);
    }

    @Override
    public void buildLayout() {
        // todo: implement
    }

    @Override
    public void applyStyles() {
        setStyleName("Timeline");
    }

    @Override
    public int getWidth() {
        return 0;  // todo: implement
    }

    @Override
    public int getHeight() {
        return 0;  // todo: implement
    }

    @Override
    public void configureListeners() {
        // todo: implement
    }

    public void setActivePeriod(Period period) {
        if (null != activePeriod) {
            activePeriod.deactivate();
        }
        activePeriod = period;
        activePeriod.activate();
        listener.activatePeriod(activePeriod);
    }

    public Period getActivePeriod() {
        return activePeriod;
    }

}
