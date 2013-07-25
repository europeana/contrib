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

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

/**
 * Item retrieved from Solr
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 * @deprecated Use the bean from core
 */
@Deprecated
public class Item implements Serializable {

    @Field("TYPE")
    private String type;

    @Field("YEAR")
    private String[] year;

    @Field("title")
    private String[] title;

    @Field("europeana_uri")
    private String uri;

    @Field("europeana_object")
    private String[] thumbnail;

    @Field("dc_creator")
    private String[] creator;
    private static final int MAX_LENGTH = 40;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        // todo: don't show item if there is no year
        return (year == null ? "0000" : (year.length > 0 ? year[0] : "0000")); // todo: replace with Apache StringUtils
    }

    public void setYear(String[] year) {
        this.year = year;
    }

    public String getTitle() {
        return getString(title, MAX_LENGTH);
    }

    public void setTitle(String[] title) {
        this.title = title;
    }

    /**
     * Tests if the string array is empty. If not it will test if the string
     * length is within the bounds (defined by maxLength) and cut the
     * string when exceeding.
     *
     * @param array     The array of strings
     * @param maxLength The string length should not exceed this
     * @return The corrected string
     */
    private String getString(String[] array, int maxLength) {
        if (null == array) {
            return "empty";
        }
        String toReturn = array[0];
        if (toReturn.length() > maxLength) {
            return toReturn.substring(0, maxLength).concat(" ...");
        }
        return toReturn;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getThumbnail() {
        return (thumbnail == null ? null : thumbnail[0]);
    }

    public void setThumbnail(String[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreator() {
        return (creator == null ? "Unknown creator" : (creator.length > 0 ? creator[0] : "Unknown creator"));
    }

    public void setCreator(String[] creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "Item{" +
                "type='" + type + '\'' +
                ", year='" + year.length + '\'' +
                ", titles'" + title.length + '\'' +
                ", uri='" + uri + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }

}
