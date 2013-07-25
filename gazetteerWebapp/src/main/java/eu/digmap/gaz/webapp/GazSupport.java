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

import java.util.*;

import javax.thesaurus.*;

import com.opensymphony.xwork2.*;

import eu.digmap.gaz.*;
import eu.digmap.gaz.database.*;
import eu.digmap.gaz.schemas.SchemaConfig;
import eu.digmap.gaz.schemas.adlcs.*;
import eu.digmap.gaz.schemas.geo.*;
import eu.digmap.gaz.schemas.gn.*;

import freemarker.ext.dom.*;

/**
 * <code>Set welcome message.</code>
 */
public class GazSupport extends ActionSupport {

	private static final long serialVersionUID = 1L;

    public NodeModel getFeature(String id) throws Exception {
    	
    	DatabaseRecord record = getDatabase().getRecord(id);
    	if(record != null) {
    		return NodeModel.wrap(record.getRecordAsDocument());
    	}
    	else return null;
    }

    public List<SchemaConfig> getSchemas() throws Exception {
    	return (List<SchemaConfig>)GazSystem.getComponent("eu.digmap.gaz.schemas");
    }
    

    public Database getDatabase() throws Exception {
    	return (Database)GazSystem.getComponent("eu.digmap.gaz.database");
    }


    public Term getNullTerm() {
    	return new NullTerm();
    }

    public Thesaurus getClassThesaurus() throws Exception {
		return (Thesaurus)GazSystem.getComponent("eu.digmap.gaz.thesaurus");
    }

    public Iterable<Ontology> getFttOntologies() throws Exception {
    	Thesaurus t = getClassThesaurus();
    	Vector<Ontology> ret = new Vector<Ontology>();
    	ret.add(t.getOntology(ADLContentSchema.FTT_NAMESPACE));
    	ret.add(t.getOntology(GeonamesSchema.GEO_NAMESPACE));
    	ret.add(t.getOntology(GeoNetSchema.GN_NAMESPACE));
    	ret.add(t.getOntology(GeoNetSchema.GN_PT_NAMESPACE));
    	return ret;
    }

    public Iterable<Relation> getRelations(Ontology o, Term source, Term relType, Term target) throws Exception {
    	if(source instanceof NullTerm) source = null;
    	if(relType instanceof NullTerm) relType = null;
    	if(target instanceof NullTerm) target = null;
    	return o.getRelations(source, relType, target);
    }

    /*
    public String getSchemaTerm(String key) {
    	if(key.equals("http://xldb.di.fc.ul.pt/geo-net.owl#PRT")) {
    		return "part-of";
    	}
    	return key;
    }
    */
}