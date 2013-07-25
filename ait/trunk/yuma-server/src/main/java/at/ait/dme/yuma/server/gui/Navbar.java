package at.ait.dme.yuma.server.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;

/**
 * Utility class for creating a navigation bar GUI element.
 * 
 * @author Rainer Simon
 */
public class Navbar {

	private List<NavbarItem> items = new ArrayList<NavbarItem>();
	
	public void addNavbarItem(String label, Class<? extends Page> pageClass, boolean selected) {
		items.add(new NavbarItem(label, pageClass, selected));
	}
	
	public List<NavbarItem> getItems() {
		return items;
	}
	
	public class NavbarItem {
		private String label;
		private Class<? extends Page> pageClass;
		private boolean selected;
		
		public NavbarItem(String label, Class<? extends Page> pageClass) {
			this(label, pageClass, false);
		}
		
		public NavbarItem(String label, Class<? extends Page> pageClass, boolean selected) {
			this.label = label;
			this.pageClass = pageClass;
			this.selected = selected;
		}
		
		public String getLabel() {
			return label;
		}
		
		public Class<? extends Page> getPageClass() {
			return pageClass;
		}
		
		public boolean isSelected() {
			return selected;
		}
	}

}
