/**********************************************************************
 * JUNIT Test for class ResolvingService
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

import java.util.ArrayList;
import java.util.List;

import eu.europeanaconnect.erds.ResolverException.ResolverExceptionCode;
import junit.framework.TestCase;

/**
 * JUNIT Test for class ResolvingService
 * 
 * @see ResolvingService
 * @see HTTPResolver
 * @see ResolverRequest
 * @author Jürgen Kett (Original code)
 * @author Kadir Karaca Kocer (Refactoring, license text, comments & JavaDoc)
 */

public class ResolvingServiceTest extends TestCase {

	/**
	 * Tests the class ResolvingService
	 * @throws ResolverException 
	 * @see ResolvingService
	 * @see HTTPResolver
	 * @see ResolverRequest
	 */
	public void test() throws ResolverException {
		ResolvingService metaResolver = new ResolvingService();
		ResolverRequest resolverRequest = null;
		ResolverResponse response = null;
		List<DataProvider<ResolverResponse>> resolvers = new ArrayList<DataProvider<ResolverResponse>>();
		
		//Germany - Austria - Switzerland
		HTTPResolver dnb_resolver = new HTTPResolver();
		dnb_resolver.setRequestUrlPattern("http://nbn-resolving.de/$identifier");
		dnb_resolver.setIdentifierPattern("urn:nbn:(de|ch|at).*");
		resolvers.add(dnb_resolver);
		
		//Sweden
		HTTPResolver se_resolver = new HTTPResolver();
		se_resolver.setRequestUrlPattern("http://urn.kb.se/resolve?urn=$identifier");
		se_resolver.setIdentifierPattern("urn:nbn:se.*");
		resolvers.add(se_resolver);
		
		//Nederlands
		HTTPResolver nl_resolver = new HTTPResolver();
		nl_resolver.setRequestUrlPattern("http://persistent-identifier.nl/?identifier=$identifier");
		nl_resolver.setIdentifierPattern("urn:nbn:nl.*");
		resolvers.add(nl_resolver);
		
		//Norway
		HTTPResolver no_resolver = new HTTPResolver();
		no_resolver.setRequestUrlPattern("http://urn.nb.no/$identifier");
		no_resolver.setIdentifierPattern("urn:nbn:no.*");
		resolvers.add(no_resolver);
		
		//add all the resolvers in the list
		metaResolver.setResolvers(resolvers);
				
		//Test urn:nbn service of the German National Library
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:de:gbv:089-3321752945");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("http://edok01.tib.uni-hannover.de/edoks/e01dh01/332175294.pdf", response.getUrl());
	
		//Test URN:NBN:AT
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:at:0001-03582");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("http://www.vorarlberg.at/pdf/vv58el1945.pdf", response.getUrl());
	
		//Test URN:NBN:CH
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:ch:bel-9039");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("http://www.unisg.ch/www/edis.nsf/wwwDisplayIdentifier/2738", response.getUrl());
 	
		//Test urn:nbn service of the National Library of Sweden (SE)
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:se:uu:diva-3475");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("http://uu.diva-portal.org/smash/record.jsf?pid=diva2:162968", response.getUrl()); 	

		//Test urn:nbn service of DANS (NL)
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:nl:ui:12-85062");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("http://dbiref.uvt.nl/iPort?request=full_record&db=wo&language=eng&query=doc_id=85062", response.getUrl());

		//Test urn:nbn service of the National Library of Norway (NO)
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:no-3132");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("http://idtjeneste.nb.no:80/urn:nbn:no-3132", response.getUrl());
		/*		
		//Testurn:nbn service of the National Library of Finland (FI)
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("", response.getUrl());
		
		//Test urn:nbn service in the NSZL (HU)
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("", response.getUrl());
		
		//Test The Digital Object Identifier (DOI®) System
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("");
		response = metaResolver.getResponse(resolverRequest);
		assertEquals("", response.getUrl());
*/		
		//Test unknown namespace
		resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier("urn:nbn:tr:onlyexample-1234567890-x");
		try {
			response = metaResolver.getResponse(resolverRequest);
			fail();
		} catch (ResolverException e) {
			assertEquals(ResolverExceptionCode.UNSUPPORTED_IDENTIFIER_SCHEME, e.getExceptionCode());
		}
	}
}
