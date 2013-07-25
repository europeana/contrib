package licenseSelection.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author mzeinstra
 */
public class License_Selection_Tool implements EntryPoint {

    
    private RootPanel ccWidget = RootPanel.get("cc_js_widget_container");
    
    public void onModuleLoad() {
        isPD();
    }

    private static void clearPanel(){
        RootPanel.get("body").clear();
        RootPanel.get("lowerbody").clear();
        RootPanel.get("cc_js_widget_container").setVisible(false);
    }
    
    private void isPD() {
        clearPanel();
        VerticalPanel panel = new VerticalPanel();
        panel.add(new Label("Is the work in the public domain?"));
        RadioButton yes = new RadioButton("PD", "Yes");
        RadioButton no = new RadioButton("PD", "No");
        panel.add(yes);
        panel.add(no);
        yes.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String pd = "The work you have chosen is in the public domain, use the following ESE string:";
                String url = "www.europeana.eu/rights/pd";
                showResult(pd,url);
            }
        });
        no.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isKnown();
            }
        });
        RootPanel.get("body").add(panel);
    }

    private void isKnown() {
        clearPanel();
        VerticalPanel panel = new VerticalPanel();
        panel.add(new Label("Is the copyrightholder known?"));
        RadioButton yes = new RadioButton("PD", "Yes");
        RadioButton no = new RadioButton("PD", "No");
        panel.add(yes);
        panel.add(no);
        yes.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isCC();
            }
        });
        no.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String unknown = "The rights statement for this work is 'unkown' Use the following ESE string:";
                String url = "www.europeana.eu/rights/unknown";
                showResult(unknown,url);
            }
        });
        Button back = new Button("Back");
        panel.add(back);
        
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isPD();
            }
        });
        RootPanel.get("body").add(panel);
    }    
    
    private void isCC() {
        clearPanel();
        final VerticalPanel panel = new VerticalPanel();
        panel.add(new Label("Are CC licenses applicable?"));
        RadioButton yes = new RadioButton("PD", "Yes");
        RadioButton no = new RadioButton("PD", "No");
        panel.add(yes);
        panel.add(no);
        
                yes.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                determineCC();
            }
        });
        no.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isAccess();
            }
        });
        Button back = new Button("Back");
        panel.add(back);
        
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isKnown();
            }
        });
        RootPanel.get("body").add(panel);
    }

    private void determineCC(){
        clearPanel();
        ccWidget.setVisible(true);
        VerticalPanel upperpanel = new VerticalPanel();
        upperpanel.add(new Label("Use the Creative Commons License Selection Tool to determine your license:")); 
        
        VerticalPanel lowerpanel = new VerticalPanel();
        Button back = new Button("Back");
        lowerpanel.add(back);
        
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isCC();
            }
        });
        Button choose = new Button("Choose this license");
        lowerpanel.add(choose);
        
        choose.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String result = getCCResult();
                showResult("Using the following text in the ESE", result);
            }
        });
               
        RootPanel.get("body").add(upperpanel);
        RootPanel.get("lowerbody").add(lowerpanel);
    }
    
  
    private void isAccess() {
        clearPanel();
        VerticalPanel panel = new VerticalPanel();
        panel.add(new Label("What kind of access will users have to this work?"));
        RadioButton free = new RadioButton("PD", "Free");
        RadioButton restricted = new RadioButton("PD", "Restricted");
        RadioButton paid = new RadioButton("PD", "Paid");
        panel.add(free);
        panel.add(restricted);
        panel.add(paid);
        free.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showResult("A rights reserved statement is applicable, use:", "www.europeana.eu/rights/rr-f");
            }
        });
        restricted.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showResult("A rights reserved statement is applicable, use:", "www.europeana.eu/rights/rr-r");
            }
        });
        paid.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showResult("A rights reserved statement is applicable, use:", "www.europeana.eu/rights/rr-p");
            }
        });
        
        Button back = new Button("Back");
        panel.add(back);
        
        back.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isCC();
            }
        });
        RootPanel.get("body").add(panel);
    }
    
    private void showResult(String result, String code){

        // Create the a result page
        clearPanel();
        VerticalPanel panel = new VerticalPanel();
        panel.add(new Label(result));
        panel.add(new Label(code));
        Button again = new Button("Again");
        panel.add(again);
        
        again.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                isPD();
            }
        });
        
        RootPanel.get("body").add(panel);
        
    }
    
    private static String getCCResult(){
        if (DOM.getElementById("cc_js_result_uri") != null){
            return DOM.getElementById("cc_js_result_uri").getAttribute("value");
        }
        
        return "An Error has occured, please try again";
    };
}
