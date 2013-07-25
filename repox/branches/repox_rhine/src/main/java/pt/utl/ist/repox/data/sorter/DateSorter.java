package pt.utl.ist.repox.data.sorter;

import eu.europeana.repox2sip.models.Request;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 3/Mai/2010
 * Time: 14:23:15
 * To change this template use File | Settings | File Templates.
 */

public class DateSorter implements java.util.Comparator{

    public int compare(Object o1, Object o2){
        Request request1 = (Request)o1;
        Request request2 = (Request)o2;
        return request2.getCreationDate().compareTo(request1.getCreationDate());
    }

}
