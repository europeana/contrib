package at.ait.dme.yuma.server.gui.search;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import at.ait.dme.yuma.server.model.MediaType;

/**
 * Advanced search.
 * 
 * @TODO implement this! 
 * 
 * @author Rainer Simon
 */
public class AdvancedSearch extends WebPage {

	public static final String QUERY_PARAM = "q";
	
	public AdvancedSearch(final PageParameters parameters) {
        add(new AdvancedSearchForm("form", new AdvancedQuery()));
    }
        
	private class AdvancedSearchForm extends StatelessForm<AdvancedQuery> {

		private static final long serialVersionUID = -7070901543076806931L;

		public AdvancedSearchForm(String id, AdvancedQuery q) {
			super(id, new CompoundPropertyModel<AdvancedQuery>(q));
			add(new TextField<String>("query"));
			add(new CheckBox("searchTitle"));
			add(new CheckBox("searchText"));
			add(new CheckBox("searchTags"));
			add(new CheckBox("searchUser"));
		}
		
		@Override
		protected void onSubmit() {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("q", getModelObject().toString());
            setResponsePage(new Results(new PageParameters(params)));
		}
    	
    }

    /**
     * Simple wrapper for the search form model.
     */
    class AdvancedQuery {
    	
    	/**
    	 * The query
    	 */
    	private String query;
    	
    	/**
    	 * 'Search title' checkbox
    	 */
    	private boolean searchTitle; 
    	
    	/**
    	 * 'Search annotation text' checkbox
    	 */
    	private boolean searchText;
    	
    	/**
    	 * 'Search tags' checkbox
    	 */
    	private boolean searchTags;
    	
    	/**
    	 * 'Search username' checkbox
    	 */
    	private boolean searchUser;
    	
    	/**
    	 * Last modification time interval start date
    	 */
    	private Date fromDate;
    	
    	/**
    	 * Last modification time interval end date
    	 */
    	private Date toDate;
    	
    	/**
    	 * List of types to include
    	 */
    	private List<MediaType> searchTypes;
    	
    	public String getQuery() {
    		return query;
    	}
    	
    	public void setQuery(String query) {
    		this.query = query; 
    	}

		public void setSearchTitle(boolean searchTitle) {
			this.searchTitle = searchTitle;
		}

		public boolean isSearchTitle() {
			return searchTitle;
		}

		public void setSearchText(boolean searchText) {
			this.searchText = searchText;
		}

		public boolean isSearchText() {
			return searchText;
		}

		public void setSearchTags(boolean searchTags) {
			this.searchTags = searchTags;
		}

		public boolean isSearchTags() {
			return searchTags;
		}

		public void setSearchUser(boolean searchUser) {
			this.searchUser = searchUser;
		}

		public boolean isSearchUser() {
			return searchUser;
		}

		public void setFromDate(Date fromDate) {
			this.fromDate = fromDate;
		}

		public Date getFromDate() {
			return fromDate;
		}

		public void setToDate(Date toDate) {
			this.toDate = toDate;
		}

		public Date getToDate() {
			return toDate;
		}

		public void setSearchTypes(List<MediaType> searchTypes) {
			this.searchTypes = searchTypes;
		}

		public List<MediaType> getSearchTypes() {
			return searchTypes;
		}
    	
    }
    
}
