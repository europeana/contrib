package com.europeana.client.datainfrastructure;

import java.util.ArrayList;

/**
 * 
 * @author mzeinstra
 *
 */
public class MultipleDatapoints extends DataPoint {
    ArrayList<String> records = new ArrayList<String>();
    
    public MultipleDatapoints(String dataPointName) {
        super(dataPointName);
        this.name = dataPointName;
    }

    @Override
    public String toXML() {
        StringBuilder result = new StringBuilder();
        result.append("\t\t<param>\n");
        result.append("\t\t\t<name>" + this.name + "</name>\n");
        for(int i = 0; i < this.records.size(); i++) {
            result.append("\t\t\t<option>" + this.records.get(i).toString() + "</option>\n");
        }
        result.append("\t\t</param>\n");
        return result.toString();
    }
    
    public void addRecord(String record)
    {
        this.records.add(record);
    }
}
