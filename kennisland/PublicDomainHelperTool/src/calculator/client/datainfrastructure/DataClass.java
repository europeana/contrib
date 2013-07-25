package calculator.client.datainfrastructure;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author mzeinstra Holds a list of datapoints.
 */
public class DataClass {
    // class has name, and parameters <name, options>
    HashMap<String, ArrayList<String>> parameters = new HashMap<String, ArrayList<String>>();

    private String name;

    public DataClass(String className) {
        setName(className);
    }

    public void addOption(String param, String option) {
        if (!parameters.containsKey(param)) {
            addParameter(param);
        }
        parameters.get(param).add(option);
    }

    public void addParameter(String paramName) {
        if (!parameters.containsKey(paramName)) {
            parameters.put(paramName, new ArrayList<String>());
        }
    }

    public void setName(String n) {
        name = n;
    }

    public String toXML() {
        final StringBuilder result = new StringBuilder();
        result.append("\t\t<name>" + name + "</name>\n");
        result.append("\t\t\t<param>\n");
        for (final String param : parameters.keySet()) {
            result.append("\t\t\t\t<param>\n");
            result.append("\t\t\t\t\t<name>" + param + "</name>\n");
            for (int i = 0; i < parameters.get(param).size(); i++) {
                result.append("\t\t\t\t\t<option>"
                        + parameters.get(param).get(i) + "</option>\n");
            }
            result.append("\t\t\t\t</param>\n");
        }

        result.append("\t\t\t</param>\n");

        return result.toString();
    }
}
