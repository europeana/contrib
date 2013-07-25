/*
 * calculatorList
 * 
 * Version 1.0
 * Calculators container class, holds all possible xml calculator data
 * 
 * Copyright Information
 */
package calculator.client.parser;

import java.util.ArrayList;

/**
 * Holds the list of calculators, its location and language and dataschema's
 * 
 * @author mzeinstra
 * 
 */
public class calculatorList {
    private final ArrayList<String> names;
    private final ArrayList<String> locations;
    private final ArrayList<String> languages;
    private final ArrayList<String> dataLocations;

    /**
     * initializes calculator list
     */
    public calculatorList() {
        names = new ArrayList<String>();
        locations = new ArrayList<String>();
        languages = new ArrayList<String>();
        dataLocations = new ArrayList<String>();
    }

    /**
     * adds a calculator to calculator list
     * 
     * @param name
     * @param location
     * @param language
     * @param dataLocation
     */
    public void addCalculator(String name, String location, String language,
            String dataLocation) {
        names.add(name);
        locations.add(location);
        languages.add(language);
        dataLocations.add(dataLocation);
    }

    /**
     * get datalocation of calculator
     * 
     * @param l
     * @return
     */
    public String getDataLocation(int l) {
        if (l < 0) {
            return null;
        }
        if (dataLocations.size() > l && dataLocations.size() > 0) {
            return dataLocations.get(l);
        } else {
            return null;
        }

    }

    /**
     * gets specific language that the calculator data is presented in
     * 
     * @param l
     * @return
     */
    public String getLanguage(int l) {
        if (l < 0) {
            return null;
        }
        if (languages.size() > l && languages.size() > 0) {
            return languages.get(l);
        } else {
            return null;
        }
    }

    /**
     * get location/jurisdiction of calculator
     * 
     * @param l
     * @return
     */
    public String getLocation(int l) {
        if (l < 0) {
            return null;
        }
        if (locations.size() > l && locations.size() > 0) {
            return locations.get(l);
        }
        return null;
    }

    /**
     * sets name of calculator
     * 
     * @param l
     * @return
     */
    public String getName(int l) {
        if (l < 0) {
            return null;
        }
        if (names.size() > l && names.size() > 0) {
            return names.get(l);
        }
        return null;
    }

    /**
     * gets length of calculator list
     * 
     * @return
     */
    public int len() {
        return names.size();
    }
}
