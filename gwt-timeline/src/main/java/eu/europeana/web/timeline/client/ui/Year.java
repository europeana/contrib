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

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

/**
 * A year item retrieved from Solr
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class Year implements Serializable {

    private String year;

    private long availableItemCount;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public long getAvailableItemCount() {
        return availableItemCount;
    }

    public void setAvailableItemCount(long availableItemCount) {
        this.availableItemCount = availableItemCount;
    }

    @Override
    public String toString() {
        return "Year{" +
                "year='" + year + '\'' +
                ", availableItemCount=" + availableItemCount +
                '}';
    }
}
