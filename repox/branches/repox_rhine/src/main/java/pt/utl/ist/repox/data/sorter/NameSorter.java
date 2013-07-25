package pt.utl.ist.repox.data.sorter;

import pt.utl.ist.repox.data.AggregatorRepox;

import java.util.Comparator;


public class NameSorter extends AggregatorSorter {
	@Override
	protected Comparator<AggregatorRepox> getComparator() {
		return new Comparator<AggregatorRepox>() {
			public int compare(AggregatorRepox o1, AggregatorRepox o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};
	}

	@Override
	protected boolean isAggregatorValid(AggregatorRepox aggregatorRepox) {
		return true;
	}
}
