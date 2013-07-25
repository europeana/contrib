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

/**
 * Contains detailed information about load progress
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class LoadEvent {

    private long bytesLoaded;
    private long bytesTotal;
    private long recordsLoaded;
    private long recordsTotal;

    public double getBytesLoaded() {
        return bytesLoaded;
    }

    public void setBytesLoaded(long bytesLoaded) {
        this.bytesLoaded = bytesLoaded;
    }

    public double getBytesTotal() {
        return bytesTotal;
    }

    public void setBytesTotal(long bytesTotal) {
        this.bytesTotal = bytesTotal;
    }

    public double getRecordsLoaded() {
        return recordsLoaded;
    }

    public void setRecordsLoaded(long recordsLoaded) {
        this.recordsLoaded = recordsLoaded;
    }

    public double getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }
}
