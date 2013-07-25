package com.europeana.client.datainfrastructure;

import java.util.ArrayList;

/**
 * 
 * @author mzeinstra
 * Holds a list of datapoints.
 */
public class DataClass {
    private ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
    private String name = "";
    
    public DataClass(String className) {
        this.name = className;
    }
    
    public String toXML() {
        StringBuilder result = new StringBuilder();
        result.append("\t\t<name>" + this.name + "</name>\n");
        for (int i = 0; i < this.datapoints.size(); i++) {
            result.append(this.datapoints.get(i).toXML());
        }
        return result.toString();
    }
    
    public void addDataPoint(DataPoint datapoint)
    {
        this.datapoints.add(datapoint);
    }
    
    public void setName(String dataClassName) {
        this.name = dataClassName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ArrayList<DataPoint> getDataPoints() {
        return new ArrayList<DataPoint>(this.datapoints);
    }
    
    public boolean inList(String key) {
        for (int i = 0; this.datapoints.size() > i; i++) {
            if (this.datapoints.get(i).getName().equals(key)) {
                return true;
            }
        }
        return false;
    }
}
