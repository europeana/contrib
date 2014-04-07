import gr.ntua.ivml.mint.Custom;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.persistent.Organization;
import gr.ntua.ivml.mint.util.EuscreenIndexHelper;
import net.minidev.json.JSONObject;

import org.apache.solr.common.SolrInputDocument;


public class CustomBehaviour extends Custom {
	@Override
	public void customModifySolarizedItem( Item item, SolrInputDocument sid ) {
		EuscreenIndexHelper.modifyDocument(item, sid);
	}
	
	public String customSanitizeSolrXpath( String xpath ) {
		// remove namespace
		// replace / with _
		// remove text() node marker
		// remove the wrapper element name
		xpath = xpath.replaceAll("/[^:]+:", "/" ).replace( "/", "_" ).replaceAll( "_text\\(\\)", "" );
		xpath = xpath.replaceAll("_EUScreen_metadata", "" );
		return xpath;
	}

	public void customOrganizationStats( JSONObject ajson ) {
		long orgId = (Long) ajson.get( "organizationId");
		Organization org = DB.getOrganizationDAO().findById(orgId, false);
		ajson.put("target", org.getType());
	}
}
