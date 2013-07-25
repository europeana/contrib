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

import com.google.gwt.i18n.client.Messages;

/**
 * Retrieves constants from properties files
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public interface Constants extends Messages {


    @Key("timeline.title")
    String timelineTitle();

    @Key("navigation.next")
    String navigationNext();

    @Key("navigation.previous")
    String navigationPrevious();

    @Key("navigation.creator")
    String navigationCreator();

    @Key("preview.html")
    String previewHtml(String title, String year, String creator);

    @Key("cache.url")
    String cacheUrl(String type, String uri);

    @Key("navigation.header")
    String navigationHeader(String offset, String limit, String totalItems, String query);

    @Key("error.solr")
    String errorSolr();
}
