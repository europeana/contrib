import net.sf.json.*;
import gr.ntua.ivml.mint.mapping.model.*;

// Get the mapping template handler for a schema

schema_name = "EuScreen EDM";

schema = DB.xmlSchemaDAO.simpleGet("name = '$schema_name'")
json = schema.jsonTemplate
mappings = new Mappings(json)           // top level mappings model
template = mappings.getTemplate()       // root element of mapping
