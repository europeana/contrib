/**********************************************************************
 * Class ResourceDescription
 * Copyright (c) 2010, German National Library / Deutsche Nationalbibliothek
 * Adickesallee 1, D-60322 Frankfurt am Main, Federal Republic of Germany 
 *
 * This program is free software.
 * For your convenience it is dual licensed.
 * You can redistribute it and/or modify it under the terms of
 * one of the following licenses:
 * 
 * 1.)
 * The GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * You should have received a copy of the GNU General Public License
 * along with this program (gpl-3.0.txt); if not please read
 * http://www.gnu.org/licenses/gpl.html
 * 
 * 2.)
 * The European Union Public Licence as published by
 * The European Commission (executive body of the European Union);
 * either version 1.1 of the License, or (at your option) any later version.
 * You should have received a copy of the European Union Public Licence
 * along with this program (eupl_v1.1_en.pdf); if not please read
 * http://www.osor.eu/eupl
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the above licenses for more details.
 * 
 * Jürgen Kett, Kadir Karaca Kocer -- German National Library
 * 
 **********************************************************************/

/* ********************************************************************
 * CHANGELOG:
 * 
 * 2010-05-05 Refactoring + new package structure by Karaca Kocer
 * 2010-04-25 Code formatting, comments and license text by Karaca Kocer
 * Created on 2010-03-17 by Jürgen Kett
 ********************************************************************/

package eu.europeanaconnect.erds;

import java.util.List;

/**
 * Description of a resource.
 * 
 * @author Jürgen Kett (Original code)
 * @author Kadir Karaca Kocer (Refactoring, license text, comments & JavaDoc)
 */

public class ResourceDescription {
	private String rawContent;
	private String dataProviderId;
	private List<Property<String, String>> properties;

	/**
	 * @return Id of the data provider.
	 */
	public String getDataProviderId() {
		return this.dataProviderId;
	}

	/**
	 * @param dataProviderId
	 */
	public void setDataProviderId(String dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	/**
	 * @return The raw content.
	 */
	public String getRawContent() {
		return this.rawContent;
	}

	/**
	 * @param rawContent Raw content to set.
	 */
	public void setRawContent(String rawContent) {
		this.rawContent = rawContent;
	}

    /**
     * @return The properties.
     */
    public List<Property<String, String>> getProperties() {
        return this.properties;
    }

    /**
     * @param properties Properties to set.
     */
    public void setProperties(List<Property<String, String>> properties) {
        this.properties = properties;
    }

	
	
}
