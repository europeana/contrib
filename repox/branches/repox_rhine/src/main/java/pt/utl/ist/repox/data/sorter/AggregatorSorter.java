package pt.utl.ist.repox.data.sorter;

import pt.utl.ist.repox.data.AggregatorRepox;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;


public abstract class AggregatorSorter {
	public TreeSet<AggregatorRepox> orderAggregators(Collection<AggregatorRepox> aggregators, boolean filterInvalid) {
		TreeSet<AggregatorRepox> orderedAggregatorRepoxes = new TreeSet<AggregatorRepox>(getComparator());

		for (AggregatorRepox aggregatorRepox : aggregators) {
			if(!filterInvalid || isAggregatorValid(aggregatorRepox)) {
				orderedAggregatorRepoxes.add(aggregatorRepox);
			}
		}

		return orderedAggregatorRepoxes;
	}

	protected abstract boolean isAggregatorValid(AggregatorRepox aggregatorRepox);
	protected abstract Comparator<AggregatorRepox> getComparator();
}