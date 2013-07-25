package pt.utl.ist.repox.dataProvider;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.sorter.DataProviderSorter;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class DataProviderPageable implements Pageable {
	private DataProviderSorter sorter;
	private TreeSet<DataProvider> orderedDataProviders;
	
	public DataProviderSorter getSorter() {
		return sorter;
	}

	public void setSorter(DataProviderSorter sorter) {
		this.sorter = sorter;
	}

	public TreeSet<DataProvider> getOrderedDataProviders() {
		return orderedDataProviders;
	}

	public DataProviderPageable(DataProviderSorter sorter) throws DocumentException, IOException {
		this.sorter = sorter;
		loadOrderedDataProviders();
	}

	public void loadOrderedDataProviders() throws DocumentException, IOException {
		List<DataProvider> dataProviders = RepoxContextUtil.getRepoxManager().getDataProviderManager().loadDataProviders();
		orderedDataProviders = sorter.orderDataProviders(dataProviders, false);
	}

	public List getItems(int index, int numberOfItems) {
		int currentIndex = 0;
		List returnList = new ArrayList();
		
		for (DataProvider currentDataProvider : orderedDataProviders) {
			if(currentDataProvider.getDataSources() == null || currentDataProvider.getDataSources().size() == 0) {
				if(currentIndex >= index) {
					returnList.add(currentDataProvider);
				}

				currentIndex += 1;
			}
			else {
				for (DataSource currentDataSource : currentDataProvider.getReversedDataSources()) {
					if(currentIndex >= index) {
						returnList.add(currentDataSource);
					}
					
					currentIndex += 1;
					if(returnList.size() == numberOfItems) {
						break;
					}
				}
			}
			
			if(returnList.size() == numberOfItems) {
				break;
			}
		}
		
		return returnList;
	}

	public int getTotalItems() {
		int total = 0;

		for (DataProvider currentDataProvider : orderedDataProviders) {
			if(currentDataProvider.getDataSources() == null || currentDataProvider.getDataSources().size() == 0) {
				total += 1;
			}
			else {
				total += currentDataProvider.getDataSources().size();
			}
		}

		return total;
	}
	
}
