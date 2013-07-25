import gr.ntua.ivml.mint.concurrent.*;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.persistent.XmlSchema;

def validate_with_schema(du, xs) {
	System.out.println("Try: " + xs.name);

	try {
		validator = new Validator(du, xs);
		validator.setStopOnFirstInvalid true;
		validator.runInThread();
		if(du.schemaStatus.equals(Dataset.SCHEMA_OK)) {
			du.setSchema(xs);
			DB.commit();
		}
	} catch(e) {
	}
}

Queues.queue({

dus = DB.dataUploadDAO;
list = dus.findAll();

xsds = DB.xmlSchemaDAO.findAll();
System.out.println(xsds);

lido09proxy = xsds.get(1);
lido09 = xsds.get(2);
lido10draft = xsds.get(4);
lido10 = xsds.get(3);

for(DataUpload du: list) {
	   if(du.schemaStatus != Dataset.SCHEMA_FAILED) continue;
	System.out.println("Validating: " + du.name);
	
	if(du.schemaStatus == Dataset.SCHEMA_FAILED) validate_with_schema(du, lido09proxy);
	if(du.schemaStatus == Dataset.SCHEMA_FAILED) validate_with_schema(du, lido09);
	if(du.schemaStatus == Dataset.SCHEMA_FAILED) validate_with_schema(du, lido10draft);
	if(du.schemaStatus == Dataset.SCHEMA_FAILED) validate_with_schema(du, lido10);
}

}, "db");

//====

import gr.ntua.ivml.mint.persistent.*;

dus = DB.dataUploadDAO.findAll();
xsds = DB.xmlSchemaDAO.findAll();

for(DataUpload du: dus) {
	if(du.schemaStatus != Dataset.SCHEMA_OK) {
		ratio = du.data.length / du.noOfFiles
		System.out.println(du.data.length + "\t" + du.noOfFiles + "\n" + ratio + "\n" + du.name)	
	}
}

//====

import gr.ntua.ivml.mint.persistent.*;

dus = DB.dataUploadDAO.findAll();
xsds = DB.xmlSchemaDAO.findAll();

for(DataUpload du: dus) {
	if(du.schemaStatus != Dataset.SCHEMA_OK) {
		ratio = du.data.length / du.noOfFiles
		System.out.println(du.data.length + "\t" + du.noOfFiles + "\n" + ratio + "\n" + du.name)
	}
}

//========

import gr.ntua.ivml.mint.persistent.*;
import java.util.*;

dus = DB.dataUploadDAO.findAll();
map = new HashMap<String, String>();

for(DataUpload du: dus) {
	tag = du.schemaStatus;
	
	if(du.schemaStatus == Dataset.SCHEMA_OK) {
		tag = du.schema.toString();
	}
	
	if(map.get(tag) == null) {
		map.put(tag, new Integer(1));
	} else {
		map.put(tag, new Integer(map.get(tag).intValue() + 1));
	}
}

System.out.println(map);