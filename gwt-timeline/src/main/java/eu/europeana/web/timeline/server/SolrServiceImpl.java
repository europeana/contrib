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

package eu.europeana.web.timeline.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import eu.europeana.web.timeline.client.Item;
import eu.europeana.web.timeline.client.network.SolrService;
import eu.europeana.web.timeline.client.ui.Year;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.FacetParams;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Return JSON objects to client.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class SolrServiceImpl extends RemoteServiceServlet implements SolrService {

    private static CommonsHttpSolrServer solrServer = null;
    private final static Logger LOG = Logger.getLogger(SolrServiceImpl.class.getName());

    static {
        LOG.setLevel(Level.ALL);
        try {
            solrServer = new CommonsHttpSolrServer("http://dashboard.europeana.sara.nl/solr/");  // todo: move to properties file
        }
        catch (MalformedURLException e) {
            LOG.fine(String.format("Error while connecting to Solr: %s", e.getMessage()));
        }
    }

    @Override
    public List<Year> retrieveYears() {
        List<Year> years = new ArrayList<Year>();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.setFacet(true);
        solrQuery.addFacetField("YEAR");
        solrQuery.setRows(0);
        solrQuery.setFacetLimit(20);
        solrQuery.setFacetSort(FacetParams.FACET_SORT);
        solrQuery.setSortField("YEAR", SolrQuery.ORDER.desc);
        solrQuery.setFilterQueries("YEAR:[* TO *]", "-YEAR:0000");
        LOG.fine(String.format("Query : %s", solrQuery));
        QueryResponse response = null;
        try {
            response = solrServer.query(solrQuery);

            List<FacetField> facetFields = response.getFacetFields();
            for (FacetField field : facetFields) {
                for (FacetField.Count count : field.getValues()) {
                    Year year = new Year();
                    year.setYear(count.getName());
                    year.setAvailableItemCount(count.getCount());
                    if (count.getCount() > 0) {
                        years.add(year);
                    }
                }
            }
        }
        catch (SolrServerException e) {
            LOG.severe(String.format("Error during Solr query for years : %s", e));
        }
        return response == null ? null : years;
    }

    @Override
    public List<Item> retrieveBriefItems(String query, Integer offset, Integer limit) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.setParam("fl", "title,YEAR,TYPE,europeana_uri,europeana_object,dc_creator"); // todo: some of these fields are unused
        solrQuery.setFilterQueries("YEAR:[* TO *]", "title:[* TO *]", "-YEAR:0000");
        solrQuery.setRows(limit);
        solrQuery.setStart(offset);
        QueryResponse response = null;
        try {
            response = solrServer.query(solrQuery);
            LOG.fine(String.format("Query : %s Offset : %d Limit %d", query, offset, limit));
        }
        catch (SolrServerException e) {
            LOG.severe(String.format("Error during Solr query for items: %s", e));
        }
        if (response != null) {
            return response.getBeans(Item.class);
        }
        else {
            return null;
        }
    }
}