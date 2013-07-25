package pt.utl.ist.repox.web;

import java.util.List;

public interface Pageable {
	public abstract int getTotalItems();
	public abstract List getItems(int index, int numberOfItems);
}
