package pt.utl.ist.repox.web;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.DataProviderPageable;
import pt.utl.ist.repox.data.sorter.NameSorter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Pager {
	private static final int DEFAULT_MAX_PAGES_REFERENCES = 10; 
	private static final int DEFAULT_MAX_ITEMS_PER_PAGE = 20; 
	
	private Pageable pageable;
	private int maxPageReferences;
	private int maxItemsPerPage;
	private int pageIndex;
	private List pageItems;
	
	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public int getMaxPageReferences() {
		return maxPageReferences;
	}

	public void setMaxPageReferences(int maxPageReferences) {
		this.maxPageReferences = maxPageReferences;
	}

	public int getMaxItemsPerPage() {
		return maxItemsPerPage;
	}

	public void setMaxItemsPerPage(int maxItemsPerPage) {
		this.maxItemsPerPage = maxItemsPerPage;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public List getPageItems() {
		return pageItems;
	}

	public void setPageItems(List pageItems) {
		this.pageItems = pageItems;
	}
	
	public Pager(Pageable pageable, int maxPageReferences, int maxItemsPerPage, int pageIndex) {
		super();
		this.pageable = pageable;
		this.maxPageReferences = maxPageReferences;
		this.maxItemsPerPage = maxItemsPerPage;
		this.pageIndex = pageIndex;
		
		this.pageItems = pageable.getItems(pageIndex * maxItemsPerPage, maxItemsPerPage);
	}

	public Pager(Pageable pageable) {
		this(pageable, DEFAULT_MAX_PAGES_REFERENCES, DEFAULT_MAX_ITEMS_PER_PAGE, 0);
	}
	
	public Map<String, Integer> getPageReferences() {
		Map<String, Integer> pageReferences = new LinkedHashMap<String, Integer>();
		
		int numberPages = (int) Math.ceil((double)pageable.getTotalItems() / (double)maxItemsPerPage);
		
		if(numberPages <= 1) {
			return null;
		}
		else {
			if(numberPages > maxPageReferences) {
				if(pageIndex != 0) {
					pageReferences.put("<<", 0);
					pageReferences.put("<", pageIndex - 1);
				}
				
				int initPageReferenceIndex = (int) Math.floor(pageIndex / maxPageReferences) * maxPageReferences;
				
				for (int i = initPageReferenceIndex; i < initPageReferenceIndex + maxPageReferences; i++) {
					if(i >= numberPages) {
						break;
					}
					pageReferences.put(new Integer(i + 1).toString(), i);
				}
				
				if(pageIndex != (numberPages - 1)) {
					pageReferences.put(">", pageIndex + 1);
					pageReferences.put(">>", numberPages - 1);
				}
			}
			else {
				if(pageIndex != 0) {
					pageReferences.put("<", pageIndex - 1);
				}
				
				for (int i = 0; i < numberPages; i++) {
					pageReferences.put(new Integer(i + 1).toString(), i);
				}
				
				if(pageIndex != (numberPages - 1)) {
					pageReferences.put(">", pageIndex + 1);
				}
			}
		}
		
		return pageReferences;
	}

	public void updatePager(int pageIndex) {
		this.pageIndex = pageIndex;
		pageItems = pageable.getItems(pageIndex * maxItemsPerPage, maxItemsPerPage);
	}

	public static void main(String[] args) throws DocumentException, IOException {
		NameSorter sorter = new NameSorter();
		DataProviderPageable dPPageable = new DataProviderPageable(sorter);
		System.out.println("total " + dPPageable.getTotalItems());
		Pager pager = new Pager(dPPageable);
		System.out.println("index" + pager.getPageIndex());
		for (Object currentItem : pager.getPageItems()) {
			System.out.println("currentItem " + currentItem);
		}
		
		for (String currentKey : pager.getPageReferences().keySet()) {
			System.out.println("key: " + currentKey + " value: " + pager.getPageReferences().get(currentKey));
		}
	}
}
