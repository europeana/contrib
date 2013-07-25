/* Xslt2StylesheetCache.java - created on 17 de Fev de 2011, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.repox.metadataTransformation;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashMap;

/** Caches all XSLT stylesheets as Templates. It reloads the templates when the stylesheets' source file changes
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @date 17 de Fev de 2011
 */
public class Xslt2StylesheetCache {
	
	HashMap<File, Templates> templatesCache;
	HashMap<File, Long> templatesLoadTimestamp;
	
	TransformerFactory transformerFactory;

	/**
	 * Creates a new instance of this class.
	 */
	public Xslt2StylesheetCache() {
		templatesCache=new HashMap<File, Templates>();
		templatesLoadTimestamp=new HashMap<File, Long>();
        transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
	}

	/** Creates a new Transformer for the input stylesheet file.
	 * @param stylesheetFile the stylesheet
	 * @return the transformer
	 * @throws javax.xml.transform.TransformerException
	 */
	public Transformer createTransformer(File stylesheetFile) throws TransformerException {
		Templates templates;
		synchronized (templatesCache) {
			Long loadTimestamp = templatesLoadTimestamp.get(stylesheetFile);
			if(loadTimestamp==null || loadTimestamp.longValue()!=stylesheetFile.lastModified()) {
				templates=transformerFactory.newTemplates(new StreamSource(stylesheetFile));
				templatesCache.put(stylesheetFile, templates);
				templatesLoadTimestamp.put(stylesheetFile, stylesheetFile.lastModified());
			}else
				templates=templatesCache.get(stylesheetFile);
		}
		return templates.newTransformer();
	}
	
}
