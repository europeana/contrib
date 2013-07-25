/**********************************************************************
 * Class ResolvingService
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
import eu.europeanaconnect.erds.ResolverException.ResolverExceptionCode;

/**
 * Main service for resolving the URL for a given persistent identifier.
 * This class manages a list of known resolvers and iterates them
 * until the given identifier matches a registered pattern.
 * A ResolverException will be thrown if none of the registered
 * resolvers matches the pattern or can resolve the id.
 * 
 * @author Jürgen Kett (Original code)
 * @author Kadir Karaca Kocer (Refactoring, license text, comments & JavaDoc)
 */

public class ResolvingService implements DataProvider<ResolverResponse> {
	protected String id;
	protected List<DataProvider<ResolverResponse>> resolvers;
	
	/**
	 * @return List of all registered resolvers.
	 */
	public List<DataProvider<ResolverResponse>> getResolvers() {
		return this.resolvers;
	}

	/**
	 * @param resolvers All resolvers that resolvind service uses.
	 * @since 17.03.2010
	 */
	public void setResolvers(List<DataProvider<ResolverResponse>> resolvers) {
		this.resolvers = resolvers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * @param id Id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifierPattern() {
		//this operation makes no sense here!
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getSupportedNamespaces() {
		//this operation makes no sense here!
		throw new UnsupportedOperationException();
	}

	/**
	 * @param resolverRequest The request to resolve.
	 * @return Response of the resolver.
	 * @throws ResolverException
	 */
	@Override
	public ResolverResponse getResponse(ResolverRequest resolverRequest) throws ResolverException {
		ResolverResponse ret = null;
		ResolverException storedException = null;
		// resolve identifier
		
		for (DataProvider<ResolverResponse> resolver : this.resolvers) {
			if (resolverRequest.getIdentifier().toLowerCase().matches(resolver.getIdentifierPattern())) {
				try {
					ret = resolver.getResponse(resolverRequest);
					ret.setDataProviderId(resolver.getId());
					storedException = null;
					break;
				} catch (ResolverException e) {
					storedException = e;
				}
			}
		}
		
		// TODO: check if this works (if a resolver fails, the algorithms checks the next one)
        if (storedException != null) {
        	/*
            throw storedException;
            Stupid idea! If we find another resolver that resolves succesfully (ret != null),
            why should we throw an exception! Just log here or call the admin and say some
            resolver is down or smt like that ...*/
        }
        
		if (ret == null) {
			//if you are here all the registered resolvers had failed!
			throw new ResolverException(this.id, ResolverExceptionCode.UNSUPPORTED_IDENTIFIER_SCHEME);
		}
		
		//you resolved the id successfully, return it
		return ret;
	}
}
