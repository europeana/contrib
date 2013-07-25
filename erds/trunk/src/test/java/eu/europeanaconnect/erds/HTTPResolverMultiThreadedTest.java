/**********************************************************************
 * JUNIT Test for class HTTPResolver
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

import eu.europeanaconnect.erds.ResolverException.ResolverExceptionCode;
import junit.framework.TestCase;

/**
 * JUNIT Test for class HTTPResolverMultiThreaded
 * 
 * @see HTTPResolverMultiThreaded
 * @author Nuno Freire (Original code)
 */

public class HTTPResolverMultiThreadedTest extends TestCase {
	/**
	 * Tests the class HTTPResolverMultiThreaded 
	 * @throws ResolverException 
	 * @see HTTPResolverMultiThreaded
	 */
	public void test() throws ResolverException {
		HTTPResolverMultiThreaded resolver = new HTTPResolverMultiThreaded();
		resolver.setRequestUrlPattern("http://nbn-resolving.de/$identifier");
		
		//Test German URN
		ResolverRequest resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:de:gbv:089-3321752945");
		ResolverResponse response = resolver.getResponse(resolverRequest);
		assertEquals("http://edok01.tib.uni-hannover.de/edoks/e01dh01/332175294.pdf", response.getUrl());
/*
*/		
		// Test DOI
		HTTPResolverMultiThreaded doiResolver = new HTTPResolverMultiThreaded();
		doiResolver.setRequestUrlPattern("http://dx.doi.org/$identifier");
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("doi:10.1000/182");
		response = doiResolver.getResponse(resolverRequest);
		assertEquals("http://www.doi.org/hb.html", response.getUrl());
		

		//Test malformed URN
		resolver = new HTTPResolverMultiThreaded();
		resolver.setRequestUrlPattern("http://nbn-resolving.de/$identifier");
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("invalidURN");
		try {
			response = resolver.getResponse(resolverRequest);
			fail();
		} catch (ResolverException e) {
			assertEquals(ResolverExceptionCode.INVALID_IDENTIFIER, e.getExceptionCode());
		}
	}
}
