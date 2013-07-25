package gr.ntua.ivml.mint.actions;

import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.persistent.Organization;
import gr.ntua.ivml.mint.persistent.Publication;
import gr.ntua.ivml.mint.persistent.User;
import gr.ntua.ivml.mint.util.Config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

@Results({
	  @Result(name="input", location="report.jsp"),
	  @Result(name="error", location="report.jsp"),
	  @Result(name="success", location="report.jsp" ),
	  @Result(name="csv", type="stream", params={"inputName", "stream", "contentType", "application/x-zip-compressed","contentDisposition", "attachment; filename=${filename}", "contentLength", "${filesize}"})
	})

public class ReportSummary extends GeneralAction {
	public static final Logger log = Logger.getLogger(ReportSummary.class );
	private List<String> countries=new ArrayList<String>();
	String orgId;
	String download = "";
	StringBuffer buffer = new StringBuffer();
	private String contentType;
	private String contentDisposition;
	
	public String getDownload(){
		return download;
	}
	
	public void setDownload(String download){
		this.download = download ;
	}
	
	public String getOrgId() {
		return orgId;
	}

	

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}


	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}
	

	public List<Organization> getOrganizations() {
		return  user.getAccessibleOrganizations();
	}
	
	
	public List<String> getCountries(){
		countries = new ArrayList<String>(java.util.Arrays.asList("Austria", "Belgium", "Bulgaria","Cyprus", "Czech Rep.", "Denmark", "Estonia",
				"Finland", "France", "Germany", "Greece","Hungary", "Ireland", "Israel", "Italy", "Latvia",
				  "Lithuania","Luxembourg","Malta","Netherlands","Poland","Portugal",
				"Romania","Russia","Slovakia","Slovenia",
				"Spain","Sweden","Switzerland","United Kingdom","Europe","International"
					));
		return countries;
	}
	
	public String getFilename(){
		return Config.get("mint.title") + ".csv";
	}
	
	public int getFilesize() {
		return buffer.length();
	}
	
	public InputStream getStream(){
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}
	
		
	
	@Action("ReportSummary")
	public String execute() {
		Organization o = user.getOrganization();
		String reply = "success";
		if (getDownload().equalsIgnoreCase("csv")){
			buffer.append("Country");
			buffer.append(',');
			buffer.append("Organization");
			buffer.append(',');
			buffer.append("Users");
			buffer.append(',');
			buffer.append("Imports");
			buffer.append(',');
			buffer.append("Imported Items");
			buffer.append(',');
			buffer.append("Transformed Items");
			buffer.append(',');
			buffer.append("Published Items");
			buffer.append(',');
			buffer.append("Primary Contact");
			buffer.append('\n');

			for(int i=0;i<getCountries().size();i++){
				//buffer.append(getCountries().get(i));
				//	buffer.append(i+1);

				List<Organization> orgs=DB.getOrganizationDAO().findByCountry(countries.get(i));
				if (orgs.size()>0){
					for(int j=0;j<orgs.size();j++){
						buffer.append(getCountries().get(i)); 
						buffer.append(',');

						buffer.append(orgs.get(j).getEnglishName()); //english name
						buffer.append(',');

						

						buffer.append(Integer.toString(orgs.get(j).getUsers().size())); //users
						buffer.append(',');
						List<DataUpload> dus = orgs.get(j).getDataUploads();

						buffer.append(Integer.toString(dus.size())); //imports
						buffer.append(',');

						int result=0;
						int transformed=0;
						int totalorgpub=0;


						for( DataUpload du: dus ) {
							if( du.getItemXpath() != null ) {
								result += (int) du.getItemXpath().getCount();
								if(DB.getTransformationDAO().findByUpload(du).size()>0){

									transformed+=(int) du.getItemXpath().getCount();
								}
							}
						}
						buffer.append(Integer.toString(result)); //imported items
						buffer.append(',');

						buffer.append(Integer.toString(transformed)); //transformed items
						buffer.append(',');

						Publication p=DB.getPublicationDAO().findByOrganization(orgs.get(j));

						if(p!=null){
							totalorgpub=(int)p.sumInputItems();
						}
						buffer.append(Integer.toString(totalorgpub));//published items
						buffer.append(',');
						
						if(orgs.get(j).getPrimaryContact()!=null){
							buffer.append(orgs.get(j).getPrimaryContact().getFirstName()+" "+orgs.get(j).getPrimaryContact().getLastName()+ ", "+orgs.get(j).getPrimaryContact().getEmail());
							buffer.append(',');
						}
						else{
							buffer.append("Primary Contact not Set");
//							buffer.append(',');
						}

					}
				}
				else{
					buffer.append(getCountries().get(i));
				}
				buffer.append('\n');
			}

			reply  =  "csv";
		}
		return reply;
	}

	
	
}
