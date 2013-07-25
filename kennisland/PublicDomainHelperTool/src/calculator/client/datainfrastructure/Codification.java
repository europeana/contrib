package calculator.client.datainfrastructure;

import java.util.ArrayList;
import java.util.HashMap;

import calculator.client.Panel;
import calculator.client.flowchartinfrastructure.RootQuestion;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Singleton to keep information about the metaflowchart in one place.
 * 
 * @author mzeinstra
 * 
 */
public class Codification {
    private static HashMap<String, String> data = new HashMap<String, String>();

    public static Codification getInstance() {
        if (instance == null) {
            instance = new Codification();
        }
        return instance;
    }

    private final HashMap<String, DataClass> classes = new HashMap<String, DataClass>();

    private String currentClass = "";

    private static Codification instance = null;

    private Codification() {
        // Exists only to defeat instantiation.
    }

    public void addClass(String className) {
        if (!classes.containsKey(className)) {
            classes.put(className, new DataClass(className));
        }
        setCurrentClass(className);
    }

    private HorizontalPanel addInputField(final String className,
            final String param) {
        if (!param.isEmpty()) {
            final HorizontalPanel panel = new HorizontalPanel();
            final TextBox result = new TextBox();

            panel.add(new Label(param));

            result.addChangeHandler(new ChangeHandler() {

                @Override
                public void onChange(final ChangeEvent event) {
                    data.put(className + "." + param, result.getText());
                }

            });
            panel.add(result);

            return panel;
        }
        return null;
    }

    private HorizontalPanel addListBox(final String className,
            final String param) {
        if (!param.isEmpty()) {
            final HorizontalPanel panel = new HorizontalPanel();
            final ListBox result = new ListBox();
            final ArrayList<String> options = classes.get(className).parameters
                    .get(param);
            result.addItem("");
            for (int i = 0; i < options.size(); i++) {
                result.addItem(options.get(i));
            }

            result.addChangeHandler(new ChangeHandler() {

                @Override
                public void onChange(final ChangeEvent event) {
                    data.put(className + "." + param, result.getItemText(result
                            .getSelectedIndex()));
                }

            });

            panel.add(new Label(param));
            panel.add(result);
            return panel;
        }
        return null;
    }

    public void addOption(String className, String parameter, String text) {
        addParameter(className, parameter);
        classes.get(className).addOption(parameter, text);
    }

    public void addParameter(String className, String text) {
        addClass(className);
        classes.get(className).addParameter(text);
    }

    public void clear() {
        classes.clear();
        currentClass = "";

    }

    public String getCurrentClass() {
        return currentClass;
    }

    public String getData(String param) {
        if (data.containsKey(param)) {
            return data.get(param);
        }
        return null;
    }

    public ListBox getStructure() {
        final ListBox result = new ListBox();
        result.addItem("");

        for (final String className : classes.keySet()) {
            final HashMap<String, ArrayList<String>> points = classes
                    .get(className).parameters;
            for (final String paramName : points.keySet()) {
                result.addItem(className + "." + paramName);
            }
        }
        return result;
    }

    public void setCurrentClass(String className) {
        currentClass = className;

    }

    private void showInputForm(String className) {

        // Build interface and interaction
        final VerticalPanel panel = new VerticalPanel();

        for (final String paramName : classes.get(className).parameters
                .keySet()) {
            if (classes.get(className).parameters.get(paramName).size() == 0) {
                panel.add(addInputField(className, paramName));
            } else {
                panel.add(addListBox(className, paramName));
            }
        }

        final Button submitBtn = new Button("Submit");
        submitBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // Goto root question and run.
                Panel.changeContent(RootQuestion.getInstance().getRoot()
                        .showPanel());

            }

        });

        panel.add(submitBtn);
        Panel.changeContent(panel);
    }

    public VerticalPanel showPanel() {
        // Clear all data first.
        data.clear();

        // Build panel
        final VerticalPanel panel = new VerticalPanel();
        final ListBox result = new ListBox();
        result.addItem("");
        for (final String c : classes.keySet()) {
            if (!c.equals("class")) {
                result.addItem(c);
            }
        }

        result.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                data.put("class.classList", result.getItemText(result
                        .getSelectedIndex()));
                showInputForm(result.getItemText(result.getSelectedIndex()));

            }

        });

        panel.add(new Label("Select your type of work"));
        panel.add(result);
        return panel;
    }

    public String toXML() {
        final StringBuilder result = new StringBuilder();
        result.append("<dataSchema>\n");
        for (int i = 0; i < classes.size(); i++) {
            result.append("\t<class>\n");
            result.append(classes.get(i).toXML());
            result.append("\t</class>\n");
        }
        result.append("</dataSchema>");
        return result.toString();
    }
    
    public void Put (String className, String parameterName) {
        data.put(className, parameterName);
    }
}
