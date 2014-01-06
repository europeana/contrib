package gr.ntua.ivml.mint.db;

import gr.ntua.ivml.mint.concurrent.Queues;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Organization;
import gr.ntua.ivml.mint.persistent.Transformation;
import gr.ntua.ivml.mint.persistent.User;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.util.JSONUtils;

import org.apache.log4j.Logger;
import org.hibernate.Query;

public class DatasetDAO extends DAO<Dataset, Long> {
	public static final Logger log = Logger.getLogger(DatasetDAO.class);
		
	public List<Dataset> findByOrganizationUser( Organization o, User u ) {
		List<Dataset> l = getSession().createQuery( "from Dataset where organization = :org and creator = :user order by created DESC" )
			.setEntity("org", o)
			.setEntity("user", u)
			.list();
		return l;
	}
	
	public List<Dataset> findByOrganization( Organization o) {
		List<Dataset> l = getSession().createQuery( "from Dataset where organization = :org order by created DESC" )
			.setEntity("org", o)
			.list();
		return l;
	}
	
	public Dataset findByName( String name ) {
		Dataset ds = (Dataset) getSession().createQuery( "from Dataset where name=:name")
		 .setString("name", name)
		 .uniqueResult();
		return ds;
	}
	
	public List<Dataset> findPublishedByOrganization( Organization org ) {
		List<Dataset> l = getSession().createQuery( "originalDataset from PublicationRecord where organization = :org and status=:stat" )
		.setEntity("org", org)
		.setString( "stat", Dataset.PUBLICATION_OK)
		.list();
		return l;
	}
	
	
	public List<Dataset> findNonDerivedByOrganization( Organization org ) {
		List<Dataset> l = getSession().createQuery( "from Dataset ds where ds.class <> 'Transformation' and organization = :org order by created DESC" )
				.setEntity("org", org)
				.list();
			return l;		
	}
	
	public List<Dataset> findNonDerivedByOrganizationUser( Organization o, User u ) {
		List<Dataset> l = getSession().createQuery( "from Dataset ds where ds.class <> 'Transformation' and organization = :org and  creator = :user order by last_modified DESC" )
			.setEntity("org", o)
			.setEntity("user", u)
			.list();
		return l;
	}
	
	public List<Dataset> findNonDerivedByOrganizationFolders( Organization o, String... folders ) {
		StringBuilder sb = new StringBuilder();
		ArrayList<String> likeParams = new ArrayList<String>();
		int i =0;
		for( String folder: folders ) {
			String jsonFolder = "%" + JSONUtils.valueToString(folder) + "%";
			sb.append( " and jsonFolders like :param" + i );
			likeParams.add( jsonFolder);
			i++;
		}
		Query q = getSession().createQuery( "from Dataset ds where ds.class <> 'Transformation' and organization = :org " + sb.toString())
				.setEntity("org", o);
		for( int j=0; j<i; j++ ) {
			q.setString("param"+j, likeParams.get(j));
		}
		List<Dataset> l = 
				q.list();
		return l;
	}


	
	public void cleanup() {
		DB.getDataUploadDAO().cleanup();
		DB.getTransformationDAO().cleanup();
	}
	

}
