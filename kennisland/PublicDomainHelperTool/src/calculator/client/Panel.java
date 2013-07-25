package calculator.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mzeinstra
 * 
 */
public class Panel {
    private static final String controls = "controls";
    private static final String content = "content";

    public static void addContent(Widget w) {
        RootPanel.get(Panel.content).add(w);
    }

    public static void changeContent(Widget w) {
        RootPanel.get(Panel.content).clear();
        RootPanel.get(Panel.content).add(w);
    }

    public static void changeMenu(Widget w) {
        RootPanel.get(Panel.controls).clear();
        RootPanel.get(Panel.controls).add(w);
    }

    public static void clear() {
        RootPanel.get(Panel.controls).clear();
        RootPanel.get(Panel.content).clear();
    }

    private Panel() {
        // Exists only to defeat instantiation.
    }
}
