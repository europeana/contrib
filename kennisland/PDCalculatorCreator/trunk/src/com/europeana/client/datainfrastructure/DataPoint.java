package com.europeana.client.datainfrastructure;

/**
 * 
 * @author mzeinstra
 *
 */
public class DataPoint {
    protected String name = "";
    
    public DataPoint(String dataPointName) {
        this.name = dataPointName;
    }
    
    public String toXML() {
        return "\t\t<param>" + this.name + "</param>\n";
    }
    
    public String getName() {
        return this.name;
    }
}
