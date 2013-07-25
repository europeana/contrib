/**********************************************************************
 * Class ResolverGUI
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
 * 2010-05-07 Plain Text output by Timo Heck
 * 2010-05-05 Refactoring + new package structure by Karaca Kocer
 * 2010-04-25 Code formatting, comments and license text by Karaca Kocer
 * Created on 2010-03-17 by Jürgen Kett
 ********************************************************************/

package eu.europeanaconnect.erds;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring controller to provide web access to the Resolvers.
 * A simple graphical user interface is also provided for
 * manual testing 
 * 
 * @see HTTPResolver
 * @see org.springframework.web.servlet.ModelAndView
 * 
 * @author Jürgen Kett (Original code)
 * @author Kadir Karaca Kocer (Refactoring, license text, comments & JavaDoc)
 * @author Timo Heck (Plain Text output)
 */

public class ResolverGUI {
	private final static Logger logger = Logger.getLogger(ResolverGUI.class);
	private ResolvingService metaResolver;

	/**
	 * @return This MetaResolver as EesolvingService object 
	 */
	public ResolvingService getMetaResolver() {
		return this.metaResolver;
	}

	/**
	 * @param metaResolver ResolvingService to set.
	 */
	public void setMetaResolver(ResolvingService metaResolver) {
		this.metaResolver = metaResolver;
	}

	
	/**
	 * @param req The HTTP request.
	 * @param res The HTTP response.
	 * @return A Model and View object. It will be rendered by Velocity.
	 * @throws IOException
	 */
	public ModelAndView showResource(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    //look for "identifier" attribute and send a redirect to requestURI/<identifier>
		String identifier = req.getParameter("identifier");
		if (identifier == null || identifier.trim().length() == 0) {
			return showForm(req, res);
		}
		logger.debug("identifier: " + identifier);
		
		ResolverRequest resolverRequest = new ResolverRequest();
		resolverRequest.setIdentifier(identifier);
		if (this.metaResolver == null) {
			throw new RuntimeException("metaResolver not set");
		}
		ResolverResponse resolverResponse;
		ModelAndView mov = new ModelAndView();
		mov.setViewName("response");
		try {
			resolverResponse = this.metaResolver.getResponse(resolverRequest);
			mov.addObject("resolverResponse", resolverResponse);
	
		} catch (ResolverException e) {
			mov.addObject("exception", e);

		}
        mov.addObject("req", req);
		return mov;
	}
	
	/**
	 * @param req The HTTP request.
	 * @param res The HTTP response.
	 * @return A Model and View object. It will be rendered by Velocity.
	 * @throws IOException 
	 */	
	public ModelAndView showPlainResource(HttpServletRequest req, HttpServletResponse res) throws IOException {
		ModelAndView ret = new ModelAndView();
		ret.setViewName("resolver");
		ResolverRequest resolverRequest = new ResolverRequest();
		String identifier = req.getServletPath();
		identifier = identifier.substring(1);
		
		if (identifier == null || identifier.equalsIgnoreCase("")) {
			res.sendRedirect(req.getRequestURI() + "gui.htm");
			return null;
		}
		
		logger.debug("identifier: " + identifier);
		
		resolverRequest.setIdentifier(identifier);
		ResolverResponse resolverResponse;
		try {
			resolverResponse = this.metaResolver.getResponse(resolverRequest);
			ret.addObject("resolverResponse", resolverResponse);
			ret.addObject("url", resolverResponse.getUrl());
	
		} catch (ResolverException e) {
			ret.addObject("exception", e);

		}
		
		
		return ret;
		
	}

	private ModelAndView showForm(HttpServletRequest req, HttpServletResponse res) {
		ModelAndView mov = new ModelAndView();
		mov.addObject("resolvers", this.metaResolver.getResolvers());
		mov.addObject("req", req);
		mov.setViewName("form");
		return mov;
	}
}
