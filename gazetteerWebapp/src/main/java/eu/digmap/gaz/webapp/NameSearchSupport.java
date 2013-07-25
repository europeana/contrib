/*
 * $Id: HelloWorld.java 471756 2006-11-06 15:01:43Z husted $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.digmap.gaz.webapp;

import javax.xml.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import eu.digmap.gaz.*;
import eu.digmap.gaz.database.*;
import eu.digmap.gaz.service.adlgp.*;

import java.util.*;

/**
 * <code>Set welcome message.</code>
 */
public class NameSearchSupport extends GazSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Iterable<String> _result;

	protected long _total;

	protected String _name;

	protected long _page;

	protected int _margin;

	public NameSearchSupport() {
		_name = null;
		_page = 1;
		_margin = getMaxMargin();
	}

    public void setName(String name) {
    	_name = name;
    }

    public String getName() {
    	return _name;
    }

    public void setMargin(int margin) {
    	int max = getMaxMargin();
    	_margin = margin > max ? max : margin;
    }

    public int getMargin() {
    	return _margin;
    }

    public int getMaxMargin() {
    	return 20;
    }

    public long getTotal() {
    	return _total;
    }

    public long getLowerBound() {
    	return ((_page - 1) * _margin) + 1;
    }

    public long getUpperBound() {
    	long upper = _page * _margin;
    	long total = getTotal();
    	if(upper > total) upper = total;
    	return upper;
    }

    public long getTotalPages() {
    	return (long)Math.ceil((double)getTotal() / getMargin());
    }

    public long getPage() {
    	return _page;
    }

    public void setPage(long page) {
    	_page = page;
    }
    
    public void fixPage() {
    	long total = getTotalPages();
    	if(_page > total) _page = total;
    	else if(_page <= 0) _page = 1;
    }

    public Iterable<String> getResult() {
    	return _result;
    }

    protected Document getSearch() throws Exception {
    	Document doc = SourceUtils.getDocument(new StreamSource(getClass().getResourceAsStream("name.xml")));
    	Element elem = DOMUtils.getElementByPath(doc.getDocumentElement(), 
    			"query-request.gazetteer-query.name-query");
    	elem.setAttribute("text",_name);
    	return doc;
    }

    public String execute() throws Exception {
        Source search = new DOMSource(getSearch());

        GPInterface gp = (GPInterface)GazSystem.getComponent("eu.digmap.gaz.service.adlgp");
    	DatabaseResult<String> result = gp.search(search);
    	try {
    		_total = result.getCount();
    		if(_total > 0) {
    			fixPage();

    			long skip = getLowerBound();
	        	if(skip > 1) result.skip(skip - 1);

	    		Collection<String> col = new Vector<String>(_margin);
	    		for(long i = getLowerBound(); i <= getUpperBound(); i++) {
	    			col.add(result.next());
	    		}
	    		_result = col;
    		}
    		else _result = Collections.EMPTY_LIST;
    	}
    	finally {
    		result.close();
    	}
        return SUCCESS;
    }
}