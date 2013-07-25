/*
 * calculatorList
 * 
 * Version 1.0
 * Calculators container class, holds all possible xml calculator data
 * 
 * Copyright Information
*/
package calculator.client;

import java.util.ArrayList;

/**
 * 
 * @author mzeinstra
 *
 */
public class calculatorList {
    private ArrayList<String> names;
    private ArrayList<String> locations;
    private ArrayList<String> languages;

    /**
     * initializes calculator list
     */
    public calculatorList() {
        names = new ArrayList<String>();
        locations = new ArrayList<String>();
        languages = new ArrayList<String>();
    }

    /**
     * adds a calculator to calculator list
     * @param name
     * @param location
     * @param language
     */
    public void addCalculator(String name, String location, String language) {
        names.add(name);
        locations.add(location);
        languages.add(language);
    }

    /**
     * gets length of calculator list
     * @return
     */
    public int len() {
        return names.size();
    }

    /**
     * sets name of calculator
     * @param l
     * @return
     */
    public String getName(int l) {
        if (l < 0){
            return null;
        }
        if (names.size() > l && names.size() > 0){
            return names.get(l);
        }else{
            return null;
        }
    }

    /**
     * get location/jurisdiction of calculator
     * @param l
     * @return
     */
    public String getLocation(int l) {
        if (l < 0){
            return null;
        }
        if (locations.size() > l && locations.size() > 0){
            return locations.get(l);
        }else {
            return null;
        }
        
    }

    /**
     * gets specific language that the calculator data is presented in
     * @param l
     * @return
     */
    public String getLanguage(int l) {
        if (l < 0){
            return null;
        }
        if (languages.size() > l && languages.size() > 0){
            return languages.get(l);
        } else {
            return null;
        }
    }
}
