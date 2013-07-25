/*
 * Classname: PDCalculator
 * Entrypoint for GWT page.
 * This class serves as the communication point for the Public Domain Calculator
 * 
 * Version Info
 * 
 * 
 * Copyright Information
 * Maarten Zeinstra (Kennisland)
 */
package calculator.client;

import calculator.client.datainfrastructure.Codification;
import calculator.client.flowchartinfrastructure.RootQuestion;
import calculator.client.parser.CalculatorListParser;
import calculator.client.parser.Parser;
import calculator.client.parser.calculatorList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author mzeinstra The PDCalculator class forms the communication between end
 *         user and the data which runs the calculation.
 */
public class PDCalculator implements EntryPoint {
    // Switch this variable to switch from automatic to button-based (false) calculator
    static final boolean dataInput = true;

    /**
     * 
     * @param exception
     */
    private static void requestFailed(final Throwable exception) {
        Window.alert("Failed to send the message: " + exception.getMessage());
    }

    private final Parser parser;
    private final CalculatorListParser Calcparser;

    private calculatorList calculators;

    public PDCalculator() {
        Calcparser = new CalculatorListParser();
        parser = new Parser();
        calculators = new calculatorList();
    }

    /**
     * Loads the calculator and initiate the questionnaire structure
     * 
     * @param index
     */
    private void ChooseCalculator(final int index) {
        final String location = calculators.getLocation(index);
        // Load possible calculators
        try {
            final RequestBuilder requestBuilder = new RequestBuilder(
                    RequestBuilder.GET, location);
            requestBuilder.sendRequest(null, new RequestCallback() {

                public void onError(final Request request,
                        final Throwable exception) {
                    requestFailed(exception);
                }

                public void onResponseReceived(final Request request,
                        final Response response) {
                    try {
                        parser.parse(response.getText());
                    } catch (final Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    StartCalculator();
                }
            });
        } catch (final RequestException ex) {
            requestFailed(ex);
        }
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        start();
    }

    /**
     * Instantiates the first view (calculator selection) Draws the available
     * calculators, and creates a redirect button to execute that calculator
     */
    private void start() {
        Panel.clear();
        final VerticalPanel mainPanel = new VerticalPanel();
        final ListBox calculatorBox = new ListBox();
        final HorizontalPanel addPanel = new HorizontalPanel();
        final Button loadButton = new Button("Load");

        addPanel.add(calculatorBox);
        addPanel.add(loadButton);

        // Assemble Main panel.
        mainPanel.add(new Label("Please select your Calculator"));
        mainPanel.add(addPanel);

        // Load possible calculators
        final RequestBuilder requestBuilder = new RequestBuilder(
                RequestBuilder.GET, "calculators.xml");
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    requestFailed(exception);
                }

                public void onResponseReceived(Request request,
                        Response response) {
                    Calcparser.parse(response.getText());
                    calculators = Calcparser.getCalculatorList();
                    calculatorBox.clear();
                    for (int index = 0; index < calculators.len(); index++) {
                        calculatorBox.addItem(calculators.getName(index),
                                Integer.toString(index));
                    }
                }
            });
        } catch (final RequestException ex) {
            requestFailed(ex);
        }

        loadButton.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                ChooseCalculator(calculatorBox.getSelectedIndex());
            }
        });

        // Associate the Main panel with the HTML host page.
        Panel.changeContent(mainPanel);
    }

    public void StartCalculator() {
        if (dataInput) {
            History.getInstance().clear();
            Panel.changeContent(Codification.getInstance().showPanel());
        } else {
            Panel.changeContent(RootQuestion.getInstance().getRoot()
                    .showPanel());
        }
        // Create controls
        final HorizontalPanel panel = new HorizontalPanel();

        // Create Back Button, only if no data input is required
        if (!dataInput) {
            final Button backBtn = new Button("Back");
            backBtn.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    Panel.changeContent(History.getInstance().PopQuestion()
                            .showPanel());

                }

            });
            panel.add(backBtn);
        }

        final Button resetBtn = new Button("Reset");
        resetBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                History.getInstance().clear();
                start();
            }

        });
        panel.add(resetBtn);
        Panel.changeMenu(panel);

    }
}
