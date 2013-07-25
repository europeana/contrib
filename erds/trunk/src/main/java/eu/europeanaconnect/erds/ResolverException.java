/**********************************************************************
 * Class ResolverException
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

/**
 * Exception type representing an error state during
 * resolving a persistent identifier.  
 * @author Jürgen Kett (Original code)
 * @author Kadir Karaca Kocer (Refactoring, license text, comments & JavaDoc)
 */
public class ResolverException extends Exception {
	private static final long serialVersionUID = 4687918635301317938L;

	/**
	 * Enumeratin of all possible error states and their appropriate message strings.
	 */
	public enum ResolverExceptionCode {
		//TODO: find better names & more error conditions!
		/** This type of identifier is not supported by this resolver */
		UNSUPPORTED_IDENTIFIER_SCHEME("The identifiers scheme is not supported by this resolver"),
		/** Given identifier has bad syntax */
		INVALID_IDENTIFIER("The given identifier is not valid"),
		/** The resolver is not reacheble */
		REMOTE_RESOLVER_ERROR("Could not connect to resolver"),
		/** HTTP Protocol Error */
		HTTP_PROTOCOL_ERROR("An error in the HTTP protocol occured (ClientProtocolException)."),
		/** No redirect Error */
		NO_REDIRECT_ERROR("Resolver did not return a redirect location."),
		/** Input / Output Error */
		IO_ERROR("Input / Output Error"),
		/** Runtime Error */
		SEVERE_RUNTIME_ERROR("Unknown Error"),
		/** Input / Output Error */
		UNKNOWN_ERROR("Unknown Error");
		
		private String message;

		private ResolverExceptionCode(String message) {
			this.message = message;
		}
		
		/**
		 * @return The error message.
		 */
		public String getMessage() {
			return this.message;
		}
	}

	private ResolverExceptionCode exceptionCode;
	
	private String dataProviderId;
	
	/**
	 * @return The id of the data provider.
	 */
	public String getDataProviderId() {
		return this.dataProviderId;
	}

	/**
	 * @param dataProviderId Sets the id of the data provider.
	 */
	public void setDataProviderId(String dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	/**
	 * @return The code for this error state.
	 * @see ResolverExceptionCode
	 */
	public ResolverExceptionCode getExceptionCode() {
		return this.exceptionCode;
	}

	/**
	 * @param resolverId
	 * @param exceptionCode Pre-defined code for this error state.
	 * @see ResolverExceptionCode
	 */
	public void setExceptionCode(String resolverId, ResolverExceptionCode exceptionCode ) {
		this.exceptionCode = exceptionCode;
	}

	/**
	 * @param resolverId Id of the resolver.
	 * @param error Pre-defined code of the occured error.
	 */
	public ResolverException(String resolverId, ResolverExceptionCode error) {
		super(error.getMessage());
		this.exceptionCode = error;
		this.dataProviderId = resolverId;
	}

	/**
	 * @param resolverId Id of the resolver.
	 * @param error Pre-defined code of the occured error.
	 * @param message Human readable message
	 */
	public ResolverException(String resolverId, ResolverExceptionCode error, String message) {
		super(message);
		this.exceptionCode = error;
		this.dataProviderId = resolverId;
	}

	/**
	 * @param resolverId Id of the resolver.
	 * @param error Pre-defined code of the occured error.
	 * @param cause Previous exception that caused this error state.
	 */
	public ResolverException(String resolverId, ResolverExceptionCode error, Exception cause) {
		super(error.getMessage());
		this.exceptionCode = error;
		this.initCause(cause);
		this.dataProviderId = resolverId;
	}

	/**
	 * @param resolverId Id of the resolver.
	 * @param error Pre-defined code of the occured error.
	 * @param message Human readable message
	 * @param cause Previous exception that caused this error state.
	 */
	public ResolverException(String resolverId,ResolverExceptionCode error, String message, Exception cause) {
		super(message);
		this.exceptionCode = error;
		this.initCause(cause);
		this.dataProviderId = resolverId;
	}

	/**
	 * @return The error message.
	 */
	public String getErrorMessage() {
		return this.exceptionCode.getMessage();
	}
}