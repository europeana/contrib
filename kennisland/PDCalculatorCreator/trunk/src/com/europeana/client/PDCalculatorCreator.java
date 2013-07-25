package com.europeana.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author mzeinstra
 */
public class PDCalculatorCreator implements EntryPoint {


    private creatorInterface cInterface = new creatorInterface();
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        this.start();       
    }
    
    private void start() {
        //make new/load/import interface
        VerticalPanel panel = new VerticalPanel();
        panel.add(new Label("Please select what you want to do:"));
        final ListBox options = new ListBox();
        options.addItem("");
        options.addItem("Load Calculator File");
        options.addItem("Load Flowchart File");
        panel.add(options);
        
        options.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                String selectedText = options.getItemText(options.getSelectedIndex());
                if (!selectedText.equals("")) {
                    if (selectedText.startsWith("Load Calculator")) {
                        loadCalculator();
                    } else if (selectedText.startsWith("Load Flowchart")) {
                        loadFlowchart();
                    }
                }
            }
            
        });
        Panel.changeContent(panel);
    }
    
    protected void loadCalculator() {
        cInterface.showCalculatorLoader();
    }
    
    protected void loadFlowchart() {
        cInterface.showFlowchartLoader();
    }
}
