package com.europeana.client;

import com.europeana.client.datainfrastructure.Codification;
import com.europeana.client.flowchartinfrastructure.Question;
import com.europeana.client.flowchartinfrastructure.RootQuestion;
import com.europeana.client.parser.Parser;
import com.europeana.client.parser.yEdParser;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author mzeinstra
 *
 */
public class creatorInterface {
    private static final int VISIBLELINES = 50;
    private static final int CHARACTERWIDTH = 80;
    private final Codification dataSchema = Codification.getInstance();
    String currentClass = "";
                  
    public void clear() 
    {
        Panel.clear();
    }
 
    public void showXML() {
        clear();
        final VerticalPanel panel =  new VerticalPanel();
        panel.add(new Label("Please add the calculator XML file below to the other XML calculator files."));
        

        
        final String XML = "<calculator><questions>\n" + RootQuestion.getInstance().getRoot().toXML() + "</questions>" + this.dataSchema.toXML() + "</calculator>" ;
        final TextArea flowchart = new TextArea();
        flowchart.setCharacterWidth(CHARACTERWIDTH);
        flowchart.setVisibleLines(VISIBLELINES);
        flowchart.setText(XML);
        
        panel.add(flowchart);
        
        Button reImport = new Button("re-import");
        reImport.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                Parser parser = new Parser();
                parser.parse(flowchart.getText());
                runFlowchart(true, RootQuestion.getInstance().getRoot());
            }
            
        });
        
        panel.add(reImport);
        Panel.changeContent(panel);
    }    

    public void runFlowchart (final boolean edit, Question q) {
        clear();
        if (q == null) {
            q = RootQuestion.getInstance().getRoot();
        }
        // Set content
        if (q != null) {
            Panel.changeContent(q.showPanel());
        }
        else {
            Window.alert("No question found");
        }
        // Create controls
        HorizontalPanel panel = new HorizontalPanel();
        
        // Create Back Button
        Button backBtn = new Button("Back");
        backBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                runFlowchart(edit, History.getInstance().PopQuestion());
                
            }
            
        });
        panel.add(backBtn);
        
           
        Button saveBtn = new Button("Export");
        saveBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                showXML();                    
            }
                
        }); 
        panel.add(saveBtn);
        
        Button resetBtn = new Button("Reset");
        resetBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                runFlowchart(edit, RootQuestion.getInstance().getRoot());
                
            }
            
        });
        panel.add(resetBtn);
  
        Panel.changeMenu(panel);
    }
    
    public void showForm() {
        
    }
    
    
    public void showFlowchartLoader() {
        // Create Load Form 
        
        // TODO refactor to upload form.
        final TextArea input = new TextArea();
        final int width = 80;
        final int lines = 50;
        
        input.setCharacterWidth(width);
        input.setVisibleLines(lines);

        final Button submitBtn = new Button("Submit");
        submitBtn.addClickHandler(new ClickHandler(){
            
            public void onClick(ClickEvent event) {
                // TODO Auto-generated method stub
                yEdParser parser = new yEdParser();
                RootQuestion.getInstance().setRoot(parser.getCalculator(input.getText()));
                //input.setText(rootNode.toXML());
                runFlowchart(true, null);
            }  
        });    
        
        
        // interface
        clear();
        final VerticalPanel panel =  new VerticalPanel();
             
        panel.add(new Label("Copy the Flowchart file here: "));
        panel.add(input);
        panel.add(submitBtn);
        Panel.changeContent(panel);
    }

    public void showCalculatorLoader() {
        // Create Load Form 
        
        // TODO refactor to upload form.
        final TextArea input = new TextArea();
        input.setCharacterWidth(80);
        input.setVisibleLines(50);

        final Button submitBtn = new Button("Submit");
        submitBtn.addClickHandler(new ClickHandler(){
            
            public void onClick(ClickEvent event) {
                Parser parser = new Parser();
                parser.parse(input.getText());
                runFlowchart(true, null);
            }  
        });    
        
        
        // interface
        clear();
        final VerticalPanel panel =  new VerticalPanel();
             
        panel.add(new Label("Copy the Calculator file here: "));
        panel.add(input);
        panel.add(submitBtn);
        Panel.changeContent(panel);
        
    }

}
