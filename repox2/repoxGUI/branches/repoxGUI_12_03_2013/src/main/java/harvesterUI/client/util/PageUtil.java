package harvesterUI.client.util;

import com.extjs.gxt.ui.client.Registry;
import harvesterUI.client.panels.overviewGrid.MainGrid;
import harvesterUI.client.panels.overviewGrid.MyPagingToolBar;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-04-2012
 * Time: 19:17
 */
public class PageUtil {

    public static int getCurrentPageSize(){
        MyPagingToolBar myPagingToolBar = Registry.get(MainGrid.PAGING_TOOLBAR);
        return myPagingToolBar.getPageSize();
    }

    public static void setActivePage(int page){
        MyPagingToolBar myPagingToolBar = Registry.get(MainGrid.PAGING_TOOLBAR);
        myPagingToolBar.setActivePageAlwaysReload(page);
    }
}
