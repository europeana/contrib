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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


// ESCA-JAVA0136:
/**
 * @author mzeinstra
 * The PDCalculator class forms the communication between 
 * end user and the data which runs the calculation.
 */
public class PDCalculator implements EntryPoint {

    private HashMap<String, String> history = new HashMap<String, String>();  
    private Stack<String> previousQuestion = new Stack<String>();
    private VerticalPanel mainPanel = new VerticalPanel();
    private HorizontalPanel addPanel = new HorizontalPanel();
    private Button chooseButton = new Button("Choose");
    private ListBox calculatorBox = new ListBox();
    private Label messageLabel = new Label("Choose your Calculator:");
    private PDCParser Calcparser = new PDCParser();

    /**
     * Entrypoint of the module. 
     * Instantiates the first view (calculator selection)
     */
    public void onModuleLoad() {
        clearMainPanel();
        this.history = new HashMap<String, String>();

        // Assemble Add Stock panel.
        addPanel.add(calculatorBox);
        addPanel.add(chooseButton);

        // Assemble Main panel.
        mainPanel.add(messageLabel);
        mainPanel.add(addPanel);

        // Load possible calculators
        if (Calcparser.getCalculatorList().len() == 0) {
            RequestBuilder requestBuilder = new RequestBuilder(
                    RequestBuilder.GET, "calculators.xml");
            try {
                requestBuilder.sendRequest(null, new RequestCallback() {
                    public void onError(Request request, Throwable exception) {
                        requestFailed(exception);
                    }

                    public void onResponseReceived(Request request,
                            Response response) {
                        Calcparser.parse(response.getText());
                        updateCalculatorDropDown();
                    }
                });
            } catch (RequestException ex) {
                requestFailed(ex);
            }
        }

        chooseButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ChooseCalculator();
            }
        });

        // Associate the Main panel with the HTML host page.
        RootPanel.get("public_domain_calculator").add(mainPanel);
    }

    /**
     * Updates the calculator dropdown
     */
    private void updateCalculatorDropDown() {
        calculatorList calculators = Calcparser.getCalculatorList();
        calculatorBox.clear();
        for (int index = 0; index < calculators.len(); index++) {
            calculatorBox.addItem(calculators.getName(index), Integer
                    .toString(index));
        }
    }
    
    /**
     * 
     * @param exception
     */
    private static void requestFailed(Throwable exception) {
        Window.alert("Failed to send the message: " + exception.getMessage());
    }
    
    /**
     * Draws the available calculators, and creates a redirect button to execute that calculator
     */
    private void ChooseCalculator() {
        int index = calculatorBox.getSelectedIndex();
        String location = Calcparser.getCalculatorList().getLocation(index);
        // Load possible calculators
        try {
            RequestBuilder requestBuilder = new RequestBuilder(
                    RequestBuilder.GET, location);
            requestBuilder.sendRequest(null, new RequestCallback() {

                public void onError(Request request, Throwable exception) {
                    requestFailed(exception);
                }

                public void onResponseReceived(Request request,
                        Response response) {
                    Calcparser.parse(response.getText());
                    nextQuestion("1");
                }
            });
        } catch (RequestException ex) {
            requestFailed(ex);
        }
    }

    /**
     * Clears the main panel of buttons and labels
     */
    private void clearMainPanel() {
        RootPanel.get("public_domain_calculator").clear();
        mainPanel.clear();
    }

    /**
     * 
     * @param newQuestion contains numbers only, a question redirect method, 
     * gets the data of a question and diverts to corresponding view.
     */
    private void nextQuestion(String newQuestion) {
        newQuestion = newQuestion.trim();
        String type = Calcparser.getQuestion(newQuestion).getType();
        if (type.equals("multiplechoice")) {
            drawMultiplechoice(newQuestion);
        } else if (type.equals("single")) {
            drawSingle(newQuestion);
        } else if (type.equals("double")) {
            drawDouble(newQuestion);
        } else if (type.equals("result")) {
            drawResult(newQuestion);
        } else {
            Window.alert("Malformed XML, cannot find:" + type);
        }
    }

    /**
     * Draws a question that needs a double textfield
     * @param questionNr question number that needs answering
     */
    private void drawDouble(String questionNr) {
        clearMainPanel();
        Question q = Calcparser.getQuestion(questionNr);
        String question1 = q.getQuestion().get(0);
        String question2 = q.getQuestion().get(1);
        // if both are already answered before, skip the input and go straight to the evaluation
        if (checkHistory(question1) && checkHistory(question2))
        {
            String eval = q.getEvaluate();
            String NOW = currentYear();
            int q1 = Integer.valueOf(getHistory(question1));
            int q2 = Integer.valueOf(getHistory(question2));
            String tmpEval = eval.replace("Q1", Integer.toString(q1));
            tmpEval = tmpEval.replace("Q2", Integer.toString(q2));
            tmpEval = tmpEval.replace("NOW", NOW);
            Boolean check = evaluate(tmpEval.trim());
            redirect(questionNr, check);
        }
        else
        {   
            // maybe one of the question is answered, and continue with normal process
            TextBox result = new TextBox();
            if (!checkHistory(question1))
            {
                addQuestion(q,0);
            }
            else // in history
            {
                // set result in textbox and make invisble
                result.setText(getHistory(question1));
                result.setVisible(false);
            }
            mainPanel.add(result);
            
            TextBox result2 = new TextBox();
            if (!checkHistory(question2))
            {
                addQuestion(q,1);
            }
            else
            {
                result2.setText(getHistory(question2));
                result2.setVisible(false);
            }
            
            previousQuestion.add(questionNr);
    
            mainPanel.add(result2);
            addBackButton();
            addDoubleEvaluateButton(result, result2, q.getEvaluate(), questionNr);
            RootPanel.get("public_domain_calculator").add(mainPanel);
        }
    }
    
    /**
     * Gets results from textboxes, gets and runs the evaluation and redirects question
     * @param result first textbox to get the answer(Q1) from
     * @param result2 second textbox to get answer (Q2) from
     * @param eval Evaluation string that needs to be executed
     * @param questionNr Current questionnumber that needs redirecting depending on eval result
     */
    private void addDoubleEvaluateButton(final TextBox result,
            final TextBox result2, final String eval, final String questionNr) {
        Button submitButton = new Button("Submit");
        submitButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // Add to history
                Question q = Calcparser.getQuestion(questionNr);
                addToHistory(q.getQuestion().get(0), result.getText());
                addToHistory(q.getQuestion().get(1), result2.getText());
                // Calendar cal = Calendar.getInstance();
                String NOW = currentYear();
                int q1 = Integer.valueOf(result.getText());
                int q2 = Integer.valueOf(result2.getText());
                String tmpEval = eval.replace("Q1", Integer.toString(q1));
                tmpEval = tmpEval.replace("Q2", Integer.toString(q2));
                tmpEval = tmpEval.replace("NOW", NOW);
                Boolean check = evaluate(tmpEval.trim());
                redirect(questionNr, check);
            }
        });
        mainPanel.add(submitButton);

    }

    /**
     * Draws a question that needs a single text field
     * @param questionNr question number that needs answering
     */
    private void drawSingle(String questionNr) {
        clearMainPanel();
        Question q = Calcparser.getQuestion(questionNr);
        String question = q.getQuestion().get(0);
        // Check if Question has already been answered
        if (checkHistory(question))
        {
            String eval = q.getEvaluate();
            String NOW = currentYear();
            int q1 = Integer.valueOf(getHistory(question));
            String tmpEval = eval.replace("Q1", Integer.toString(q1));
            tmpEval = tmpEval.replace("NOW", NOW);
            Boolean check = evaluate(tmpEval.trim());
            redirect(questionNr, check);
        }
        else
        {
            previousQuestion.add(questionNr);
            addQuestion(q);
            TextBox result = new TextBox();
            mainPanel.add(result);
            addBackButton();
            addEvaluateButton(result, q.getEvaluate(), questionNr);
            RootPanel.get("public_domain_calculator").add(mainPanel);
        }
    }
    
    /**
     * Adds an evaluation button that calls the evaluation, and redirects to new question
     * In service of drawSingle
     * @param result Textbox result has the answer that needs to be accessed when calculating
     * @param eval  evaluation is the evaluation string
     * @param questionNr Questionnumber is the current question that needs redirecting given the eval result
     */
    private void addEvaluateButton(final TextBox result, final String eval,
            final String questionNr) {
        Button submitButton = new Button("Submit");
        submitButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // Add to history
                Question q = Calcparser.getQuestion(questionNr);
                addToHistory(q.getQuestion().get(0), result.getText());
                // do evaluation
                String NOW = currentYear();
                int q1 = Integer.valueOf(result.getText());
                String tmpEval = eval.replace("Q1", Integer.toString(q1));
                tmpEval = tmpEval.replace("NOW", NOW);
                Boolean check = evaluate(tmpEval.trim());
                redirect(questionNr, check);
            }
        });
        mainPanel.add(submitButton);
    }
    
    private void addBackButton() {
        Button backButton = new Button("Back");
       
        backButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // pop current question
                previousQuestion.pop();
 
                String newQuestion = previousQuestion.pop();
                
                // remove previous question(s) from history
                ArrayList<String> questions = Calcparser.getQuestion(newQuestion).getQuestion();
                for (int j = 0; j < questions.size(); j++)
                {
                    history.remove(questions.get(j));
                }
                nextQuestion(newQuestion);
            }
        });
        if(previousQuestion.size() > 1){
            mainPanel.add(backButton);
        }
        
    }
    
    /**
     * redirects in case of drawSingle and drawMultiple 
     * @param questionNr Current question number that needs redirecting
     * @param check boolean that is used to choose a redirection.
     */
    private void redirect(String questionNr, Boolean check) {
        ArrayList<Answer> answers = Calcparser.getQuestion(questionNr)
                .getAnswerList();
        for (int i = 0; i < answers.size(); i++) {
            if (Boolean.parseBoolean(answers.get(i).getAnswer()) == check) {
                nextQuestion(answers.get(i).getRedirect());
                return;
            }
        }
        nextQuestion("1");
    }

    /**
     * Performs an evaluation (javascript side)
     * @param arg argument that contains code to be executed (Example: 1984<2010-50)
     * @return boolean that is the result of an evaluation
     */
    public static native boolean evaluate(String arg) /*-{
        return eval(arg);
    }-*/;

    /**
     * Draws a multiple choice question, with a variable number of buttons
     * @param questionNr question number that needs answering
     */
    private void drawMultiplechoice(String questionNr) {
        clearMainPanel();
        Question q = Calcparser.getQuestion(questionNr);
        final ArrayList<Answer> answers = q.getAnswerList();   
        // Checks if question has already been asked
        if (checkHistory(q.getQuestion().get(0)))
        {
            String answer = getHistory(q.getQuestion().get(0));
            int i = 0;
            while (!answers.get(i).getAnswer().equals(answer))
            {
                i++;
            }
            nextQuestion(answers.get(i).getRedirect());
        }
        else // Question has not been answered in this session
        {
            previousQuestion.add(questionNr);
            addQuestion(q);
            for (int i = 0; i < answers.size(); i++) {
                HorizontalPanel panel = new HorizontalPanel();
                panel.add(addAnswerButton(answers.get(i).getAnswer(), answers.get(i).getRedirect(), q.getQuestion().get(0)));
                if (!"".equals(answers.get(i).getInformation())) {
                    panel.add(addInformation(answers.get(i).getInformation()));
                }
                mainPanel.add(panel);
            }
        
            addBackButton();
            RootPanel.get("public_domain_calculator").add(mainPanel);
        }
    }

    /**
     * Adds an answer button, in service of drawMultiplechoice
     * @param answer answer to be printed on the button
     * @param redirect question number to redirect to when button is clicked
     * @param question saves selected question to history
     * @return button
     */
    private Button addAnswerButton(final String answer, final String redirect, final String question) {
        
        Button optionButton = new Button(answer);
        optionButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addToHistory(question, answer);
                nextQuestion(redirect);
            }
        });
        return optionButton;
    }

    /**
     * Draws result, only a label field
     * @param questionNr question number to get the result tag from
     */
    private void drawResult(String questionNr) {
        previousQuestion.add(questionNr);
        clearMainPanel();
        Question q = Calcparser.getQuestion(questionNr);
        addQuestion(q);
        Button again = new Button("Again");

        again.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onModuleLoad();
            }
        });
        addBackButton();
        mainPanel.add(again);
        RootPanel.get("public_domain_calculator").add(mainPanel);
    }

    private void addQuestion (Question q){
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Label(q.getQuestion().get(0)));
        if (!"".equals(q.getInformation())) {
            panel.add(addInformation(q.getInformation()));
        }
        mainPanel.add(panel);
    }

    private void addQuestion (Question q, int index){
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Label(q.getQuestion().get(index)));
        if (!"".equals(q.getInformation())) {
            panel.add(addInformation(q.getInformation()));
        }
        mainPanel.add(panel);
    }
    
    private static Image addInformation (String information){
        Image openButton = new Image("information_icon.jpg");
        Button closeButton = new Button("Close");
        
        
        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Additional Information");
        dialogBox.setAnimationEnabled(true);
       

        Label text = new Label(information);

        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(text);

        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        
        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.show();
            }
        });
        return openButton;     
    }
    
    private void addToHistory(String q, String a) {
        this.history.put(q, a);
    }
    
    private boolean checkHistory(String q)
    {
        return this.history.containsKey(q);
    }
    
    private String getHistory(String q)
    {
        if (checkHistory(q))
        {
            return this.history.get(q);
        }
        return null;
    }
    
    
    public static String currentYear(){
        Date today = new Date();
        String year = DateTimeFormat.getFormat("yyyy").format(today);
        return year;
}
    
}
