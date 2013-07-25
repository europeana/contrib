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
 * Notify the listeners about the state of the item.
 * <p/>
 * The states represent the following behaviour:
 * <ul>
 * <li> <b>IN_PIPELINE</b> in pipeline
 * <li> <b>ON_STAGE</b> visible
 * <li> <b>DEACTIVATED</b> scaled down, not clickable
 * <li> <b>ACTIVATED</b> scaled up, clickable
 * <li> <b>CACHED</b> thumbnail has been cached
 * <li> <b>REMOVED</b> removed from pipeline
 * </ul>
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public enum ItemPreviewEvent {

    /**
     * The item is added to the pipeline, next state will be on_stage.
     */
    IN_PIPELINE,
    /**
     * The item is visible, next state will be cached.
     */
    ON_STAGE,
    /**
     * The item is deactivated, so not clickable but onstage, added and cached.
     */
    DEACTIVATED,
    /**
     * The item is activated, so clickable, onstage, in added and cached.
     */
    ACTIVATED,
    /**
     * The thumbnail has been retrieved from the server.
     */
    CACHED,
    /**
     * The item has been removed from the timeline.
     */
    REMOVED
}
