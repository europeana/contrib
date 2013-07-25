package pt.utl.ist.repox.data;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.data.sorter.AggregatorSorter;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class DataProviderPageable implements Pageable {
    private AggregatorSorter sorter;
    private TreeSet<AggregatorRepox> orderedAggregatorRepoxes;

    public AggregatorSorter getSorter() {
        return sorter;
    }

    public TreeSet<AggregatorRepox> getOrderedAggregators() {
        return orderedAggregatorRepoxes;
    }

    public void setOrderedAggregators(TreeSet<AggregatorRepox> orderedAggregatorRepoxes) {
        this.orderedAggregatorRepoxes = orderedAggregatorRepoxes;
    }

    public DataProviderPageable(AggregatorSorter sorter) throws DocumentException, IOException {
        this.sorter = sorter;
        loadOrderedAggregators();
    }

    public void loadOrderedAggregators() throws DocumentException, IOException {
        List<AggregatorRepox> aggregators = RepoxContextUtil.getRepoxManager().getDataManager().loadAggregatorsRepox();
        orderedAggregatorRepoxes = sorter.orderAggregators(aggregators, false);
    }

    public List getItems(int index, int numberOfItems) {
        int currentIndex = 0;
        List returnList = new ArrayList();

        for (AggregatorRepox currentAggregatorRepox : orderedAggregatorRepoxes) {
            if(currentAggregatorRepox.getDataProviders() == null || currentAggregatorRepox.getDataProviders().size() == 0){
                returnList.add(currentAggregatorRepox);
            }
            else{
                for (DataProvider currentDataProvider : currentAggregatorRepox.getDataProviders()) {
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
            }
        }
        return returnList;
    }

    public int getTotalItems() {
        int total = 0;

        for (AggregatorRepox currentAggregatorRepox : orderedAggregatorRepoxes) {
            for (DataProvider dataProvider : currentAggregatorRepox.getDataProviders()) {
                if(dataProvider.getDataSources() == null || dataProvider.getDataSources().size() == 0) {
                    total += 1;
                }
                else {
                    total += dataProvider.getDataSources().size();
                }
            }
        }
        return total;
    }

}
