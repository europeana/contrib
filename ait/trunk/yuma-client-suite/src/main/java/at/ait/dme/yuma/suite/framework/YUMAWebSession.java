/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
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

package at.ait.dme.yuma.suite.framework;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

import at.ait.dme.yuma.suite.apps.core.shared.model.User;

/**
 * Custom Web Session for the YUMA Suite.
 * 
 * @author Rainer Simon
 */
public final class YUMAWebSession extends WebSession {
	
	private static final long serialVersionUID = -70708036400304230L;

	private User user;

	public YUMAWebSession(Request request) {
		super(request);
	}

	/**
	 * Returns the user, if stored in the session, or null.
	 * @return the user (if any) or null
	 */
	public final User getUser() {
		return user;
	}

	/**
	 * Sets a user for this session.
	 * @param user the user
	 */
	public final void setUser(User user) {
		this.user = user;
		this.bind();
	}

	/**
	 * Utility method that returns the session.
	 * @return the session
	 */
	public static YUMAWebSession get() {
		return (YUMAWebSession) Session.get();
	}
	
}
