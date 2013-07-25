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

package at.ait.dme.yuma.suite.apps.core.shared.server;

import java.io.Serializable;

public class RESTfulServiceException extends Exception implements Serializable {

    private static final long serialVersionUID = 4734717068880067349L;

    private static final int HTTP_CONFLICT = 409;

    protected int statusCode;

    public RESTfulServiceException() {
        super();
    }

    public RESTfulServiceException(String message) {
        super(message);
    }

    public RESTfulServiceException(Throwable cause) {
        super(cause);
    }

    public RESTfulServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RESTfulServiceException(int statusCode) {
        this("unexpected response:" + statusCode);
        this.statusCode = statusCode;
    }

    public boolean isConflict() {
        return statusCode == HTTP_CONFLICT;
    }

}