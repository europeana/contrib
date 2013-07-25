package harvesterUI.shared.filters;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-02-2011
 * Time: 13:48
 */
public class FilterAttribute extends BaseTreeModel {

    public FilterAttribute() {}

    public FilterAttribute(String name, String value) {
        set("value", value);
        set("name", name);
    }

    public String getValue() {
        return (String) get("value");
    }

    public String getName() {
        return (String) get("name");
    }

}
