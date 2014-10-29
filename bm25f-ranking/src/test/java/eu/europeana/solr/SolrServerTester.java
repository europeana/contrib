/**
 *  Copyright 2012 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.europeana.solr;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.util.RefCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.SAXException;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Nov 7, 2012
 */
public class SolrServerTester extends SolrServer implements Closeable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SolrServerTester.class);
	// private static final String CORE1 = "collection1";
	private static final String CORE1 = "collection2";
	private static final long serialVersionUID = 1L;
	private File solrdir = null;
	private File datadir = null;
	private SolrServer delegate = null;
	private transient SolrCore core = null;

	/**
	 * <p>
	 * Sets your Solr root directory. In Solr’s documentation, this is generally
	 * referred to as “/solr-root”. Your “conf” directory (containing your
	 * schema, stopwords, synonyms ...) will be a subdirectory of this.
	 * </p>
	 * .
	 * 
	 * @param solrdir
	 */
	@Required
	public final void setSolrdir(final File solrdir) {
		this.solrdir = solrdir;

		System.setProperty("solr.home", solrdir.getPath());

		if (this.datadir == null) {
			File dataDir = FileUtils.getTempDirectory();
			logger.info("created tmp data dir in {}/{}",
					dataDir.getAbsolutePath(), "data");
			setDatadir(new File(solrdir, "data"));
		}
	}

	/**
	 * <p>
	 * Sets your Solr data directory. This is the parent directory of your
	 * “index” and “spellchecker” directories.
	 * </p>
	 * 
	 * @param datadir
	 */
	public final void setDatadir(final File datadir) {
		this.datadir = datadir;
		System.setProperty("solr.data.dir", datadir.getPath());
	}

	/**
	 * <p>
	 * The only @SolrServer method that you need to override. This method passes
	 * all queries and indexing events on to an in-process delegate.
	 * </p>
	 * 
	 * @param req
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@Override
	public NamedList<Object> request(final SolrRequest req)
			throws SolrServerException, IOException {
		try {
			return getDelegate().request(req);
		} catch (final SolrServerException e) {
			throw e;
		} catch (final IOException e) {
			throw e;
		} catch (final Exception e) {
			throw new SolrServerException(e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public void close() throws IOException {
		if (core != null) {
			core.close();
			core = null;
		}

	}

	// public void index(SolrInputDocument doc) throws SolrServerException,
	// IOException{
	// getDelegate(); // force the delegate to be created
	// logger.info("indexing doc {} ",doc);
	// delegate.add(doc);
	// delegate.commit();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.solr.client.solrj.SolrServer#shutdown()
	 */
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method creates an in-process Solr server that otherwise behaves just
	 * as you’d expect.
	 */
	private synchronized SolrServer getDelegate() throws SolrServerException {
		if (delegate != null) {
			return delegate;
		}

		try {
			// File solrconfigXml = new File(new File(solrdir, "conf"),
			// "solrconfig.xml");
			//
			// logger.info("solrconfig = {}",solrconfigXml);
			//
			// CoreContainer container = new CoreContainer(solrdir.getPath(),
			// solrconfigXml);
			// CoreDescriptor descriptor = new CoreDescriptor(container,
			// "collection1",
			// solrdir.getCanonicalPath());
			//
			// core = container.create(descriptor);
			// container.register("collection1", core, false);
			// delegate = new EmbeddedSolrServer(container, "collection1");
			//
			// return delegate;

			final File dir = FileUtils.getTempDirectory();
			System.setProperty("solr.solr.home", dir.getAbsolutePath());
			final File conf = new File(new File(solrdir, "conf"),
					"solrconfig.xml");

			final CoreContainer cc = new CoreContainer();
			final SolrConfig sc = new SolrConfig(conf.getAbsolutePath());
			final CoreDescriptor cd = new CoreDescriptor(cc, CORE1,
					solrdir.getAbsolutePath());

			core = cc.create(cd);

			// cc.register(CORE1, core, false);
			delegate = new EmbeddedSolrServer(cc, CORE1);
			return delegate;

		} catch (ParserConfigurationException ex) {
			throw new SolrServerException(ex);
		} catch (SAXException ex) {
			throw new SolrServerException(ex);
		} catch (IOException ex) {
			throw new SolrServerException(ex);
		}
	}

	/**
	 * SolrIndexSearcher adds schema awareness and caching functionality over
	 * the Lucene IndexSearcher.
	 * 
	 * @return
	 * @throws SolrServerException
	 */
	public RefCounted<SolrIndexSearcher> getIndexSearcher()
			throws SolrServerException {
		// http://lucene.apache.org/solr/api/org/apache/solr/search/SolrIndexSearcher.html
		getDelegate(); // force the delegate to be created

		return core.getSearcher();
	}

	/**
	 * Returns the index schema used by this Solr server
	 * 
	 * @return
	 * @throws SolrServerException
	 */
	public IndexSchema getIndexSchema() throws SolrServerException {
		getDelegate(); // force the delegate to be created

		return core.getLatestSchema();
	}

	// just to see if things work
	public static void main(String[] args) throws SolrServerException,
			IOException {
		SolrServerTester tester = new SolrServerTester();
		tester.setSolrdir(new File(new File(new File(new File("src"), "test"),
				"resources"), "solr/" + CORE1));
		SolrServerIndexer indexer = new SolrServerIndexer();
		indexer.index(tester);
		tester.commit();

		SolrQuery q = new SolrQuery("Watermark");
		q.set("debugQuery", "on");
		q.set("defType", "bm25f");
		q.set("qf", "title text");

		q.setRows(10); // don't actually request any data

		QueryResponse qr = tester.query(q);
		Map<String, String> explainmap = qr.getExplainMap();
		System.out.println("results " + qr.getResults().getNumFound());
		for (SolrDocument doc : qr.getResults()) {
			System.out.println("Title: " + doc.getFieldValue("title"));
			System.out.println("Expl: "
					+ explainmap.get(doc.getFieldValue("europeana_id")));
		}

		tester.close();
	}

}
