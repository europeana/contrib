package gr.ntua.ivml.mint.persistent;

import java.util.Date;

/**
 * This class records a publication event.
 * @author Arne Stabenau 
 *
 */
public class PublicationRecord {

	public static final String PUBLICATION_RUNNING = "RUNNING";
	public static final String PUBLICATION_FAILED = "FAILED";
	public static final String PUBLICATION_OK = "OK";
	
	Long dbID;
	
	Organization organization;
	
	// The original uploaded Dataset (DataUpload probably, could be something else later)
	Dataset originalDataset;
	
	// The dataset where the items are taken from
	Dataset publishedDataset;
	
	// when the publication started and when it finished
	Date startDate, endDate;
	
	// current Status of this publication
	String status;
	
	// anything to report
	String report;
	
	// this should be the valid item count in the publishedItems Dataset
	int publishedItemCount;

	// The user who did this
	User publisher;
	
	//
	//  Getters and setters down here
	//
	
	public Long getDbID() {
		return dbID;
	}

	public void setDbID(Long dbID) {
		this.dbID = dbID;
	}

	public Dataset getOriginalDataset() {
		return originalDataset;
	}

	public void setOriginalDataset(Dataset originalDataset) {
		this.originalDataset = originalDataset;
	}

	public Dataset getPublishedDataset() {
		return publishedDataset;
	}

	public void setPublishedDataset(Dataset publishedDataset) {
		this.publishedDataset = publishedDataset;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public int getPublishedItemCount() {
		return publishedItemCount;
	}

	public void setPublishedItemCount(int publishedItemCount) {
		this.publishedItemCount = publishedItemCount;
	}

	public User getPublisher() {
		return publisher;
	}

	public void setPublisher(User publisher) {
		this.publisher = publisher;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	
}
