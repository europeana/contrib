package com.europeana.client.datainfrastructure;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;


/**
 * Singleton to keep information about the metaflowchart in one place.
 * @author mzeinstra
 *
 */
public class Codification {
    ArrayList<DataClass> classes = new ArrayList<DataClass>();
    private String currentClass = "";
    
    private static Codification instance = null;
    private Codification() {
       // Exists only to defeat instantiation.
    }
    
    public static Codification getInstance() {
        if(instance == null) {
           instance = new Codification();
        }
        return instance;
     }
    
    public ArrayList<DataClass> getClasses() {
        return new ArrayList<DataClass>(this.classes);
    }
    
    public String toXML() {
        StringBuilder result = new StringBuilder();
        result.append("<dataSchema>\n");
        for (int i =0; i< this.classes.size(); i++) {
            result.append("\t<class>\n");
                result.append(this.classes.get(i).toXML());
            result.append("\t</class>\n");
        }
        result.append("</dataSchema>");
        return result.toString();
    }
    
    public String getCurrentClass() {
        return this.currentClass;
    }
    
    public void setCurrentClass(String className) {
        this.addClass(className);
    }
    
    public void addClass (String className) {
        this.currentClass = className;
       
        boolean found = false;
        for (int i = 0; i < this.classes.size(); i++) {
            if (this.classes.get(i).getName().equals(className)) {
                found = true;
                break;
            }
        }
        if (!found) {
            this.classes.add(new DataClass(className));
        }
    }

    public void addParameter(String className, String text) {
        // TODO Auto-generated method stub
        for (int i = 0; i < this.classes.size(); i++) {
            if (this.classes.get(i).getName().equals(className)) {
                if (!this.classes.get(i).inList(text)) {
                    this.classes.get(i).addDataPoint(new MultipleDatapoints(text));
                }
                break;
            }
        }
    }

    public void addOption(String className, String parameter, String text) {
       
        // Create dummy class
        DataClass foundClass = null;
        for (int i = 0; i < this.classes.size(); i++) {
            if (this.classes.get(i).getName().equals(className)) {
                // if class found then use that class
                foundClass = this.classes.get(i);
                break;
            }
        }
        // search for parameter
        if (foundClass != null) {
            int size = foundClass.getDataPoints().size();
            for (int k = 0; k < size; k++) {
                if (foundClass.getDataPoints().get(k).getName().equals(parameter)) {
                    // Parameter within class found, test if option can be added
                    if (foundClass.getDataPoints().get(k).getClass().equals(MultipleDatapoints.class)) {
                        // add to parameter
                        MultipleDatapoints currentParam = (MultipleDatapoints) foundClass.getDataPoints().get(k);
                        currentParam.addRecord(text);
                    } else {
                        Window.alert("Option could not be added to Class.");
                    }
                    break;
                }
            }
        }
    }
    
    public ListBox getStructure() {
        ListBox result = new ListBox();
        result.addItem("");
        String className = "";
        ArrayList<DataPoint> points =  new ArrayList<DataPoint>(); 
        for (int i = 0; i < this.classes.size(); i++) {
            className = this.classes.get(i).getName();
            points = this.classes.get(i).getDataPoints();
            for (int k = 0; k < points.size(); k++) {
                result.addItem(className + "." + points.get(k).getName());
            }
        }
        return result;
    }
    
    public void buildStructure() {
        // TODO
        //RootQuestion.getInstance().getRoot().
    }

    public void clear() {
        this.classes.clear();
        this.currentClass = "";
        
    }
}
