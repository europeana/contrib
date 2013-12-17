package gr.ntua.ivml.mint.db;


import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Organization;
import gr.ntua.ivml.mint.persistent.PublicationRecord;

import java.util.List;

public class PublicationRecordDAO extends DAO<PublicationRecord, Long> {

	
	public List<PublicationRecord> findByOriginalDataset( Dataset ds ) {
		List<PublicationRecord> result = getSession().createQuery("from PublicationRecord where originalDataset = :ods" )
			.setEntity("ods", ds )
			.list();
		return result;
	}
	
	public List<PublicationRecord> findByOrganization( Organization org ) {
		List<PublicationRecord> result = getSession().createQuery("from PublicationRecord where organization = :org" )
				.setEntity("org", org )
				.list();
			return result;
		
	}

	public List<PublicationRecord> findByAnyDataset(Dataset ds) {
		List<PublicationRecord> result = getSession().createQuery("from PublicationRecord where originalDataset= :ods or publishedDataset = :ods" )
				.setEntity("ods", ds )
				.list();
			return result;
	}
	public PublicationRecord getByPublishedDataset(Dataset ds) {
		PublicationRecord result = (PublicationRecord) getSession().createQuery("from PublicationRecord where publishedDataset= :ods" )
				.setEntity("ods", ds )
				.uniqueResult();
		return result;
	}
}
