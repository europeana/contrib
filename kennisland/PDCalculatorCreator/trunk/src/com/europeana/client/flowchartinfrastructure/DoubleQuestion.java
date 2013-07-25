package com.europeana.client.flowchartinfrastructure;

import java.util.ArrayList;

import com.europeana.client.flowchartinfrastructure.Answer;
import com.europeana.client.flowchartinfrastructure.Question;

import com.europeana.client.History;
import com.europeana.client.Panel;
import com.europeana.client.datainfrastructure.Codification;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Holds the interface and data for a double input question.
 * @author mzeinstra
 *
 */
public class DoubleQuestion extends Question {

    private String question1 = "";
    private String question2 = "";
    private String information1 = "";
    private String information2 = "";
    private String eval = "";
    private String Parameter1  = "";
    private String Parameter2 = "";

    public DoubleQuestion(String number, String q1, String q2, String expr) {
        questionNr = number;
        question1 = q1;
        question2 = q2;
        eval = expr;

        information1 = "";
        information2 = "";

        answers = new ArrayList<Answer>();
        answers.add(new Answer("True", null, ""));
        answers.add(new Answer("False", null, ""));
    }

    private String getExpression() {
        return eval;
    }

    private String getInformation1() {
        return information1;
    }

    private String getInformation2() {
        return information2;
    }

    @Override
    public String getQuestion() {
        return question1 + " & " + question2;
    }

    public String getQuestion1() {
        return question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setAnswers(Question trueQ, Question falseQ) {
        answers.clear();
        answers.add(new Answer("True", trueQ, ""));
        answers.add(new Answer("False", falseQ, ""));
    }

    public void setExpression(String text) {
        eval = text;
    }

    public void setInformation(String info1, String info2) {
        information1 = info1;
        information2 = info2;
    }

    private void setInformation1(String i) {
        information1 = i;
    }

    private void setInformation2(String i) {
        information2 = i;
    }

    public void setParameter1(String text) {
        Parameter1 = text;
    }

    public void setParameter2(String text) {
        Parameter2 = text;
    }

    public void setQuestion1(String text) {
        question1 = text;
    }

    public void setQuestion2(String text) {
        question2 = text;
    }

    public VerticalPanel showEditPanel() {
        // get the list of answers already filled in

        final TextBox questionBox1 = new TextBox();
        final TextBox questionBox2 = new TextBox();
        final TextBox patternBox = new TextBox();
        final TextArea informationBox1 = new TextArea();
        final TextArea informationBox2 = new TextArea();
        informationBox1.setVisibleLines(4);
        informationBox2.setVisibleLines(4);

        questionBox1.setValue(getQuestion1());
        questionBox1.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setQuestion1(questionBox1.getText());

            }

        });

        questionBox2.setValue(getQuestion2());
        questionBox2.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setQuestion2(questionBox2.getText());

            }

        });

        patternBox.setValue(getExpression());
        patternBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setExpression(patternBox.getText());

            }

        });

        informationBox1.setValue(getInformation1());
        informationBox1.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setInformation1(informationBox1.getText());

            }

        });

        informationBox2.setValue(getInformation2());
        informationBox2.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setInformation2(informationBox2.getText());

            }

        });

        // Build interface
        final VerticalPanel panel = new VerticalPanel();
        final HorizontalPanel base = new HorizontalPanel();
        final VerticalPanel left = new VerticalPanel();
        final VerticalPanel right = new VerticalPanel();

        left.add(new Label("Change questions and pattern"));
        left.add(new Label("Question 1"));
        left.add(questionBox1);
        left.add(new Label("Additional Notes and Information"));
        left.add(informationBox1);
        left.add(new Label("Question 2"));
        left.add(questionBox2);
        left.add(new Label("Additional Notes and Information"));
        left.add(informationBox2);
        left.add(new Label("Change Pattern"));
        left.add(patternBox);

        right.add(showParameterEditor());

        base.add(left);
        base.add(right);
        panel.add(base);

        return panel;

    }

    @Override
    public VerticalPanel showPanel() {
        History.getInstance().setCurrentQuestion(this);
        if (!getClassName().isEmpty()) {
            Codification.getInstance().setCurrentClass(getClassName());
        }
        final VerticalPanel panel = new VerticalPanel();

        final TextBox answer1 = new TextBox();
        final TextBox answer2 = new TextBox();
        final Button submitBtn = new Button("Submit");
        submitBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                
                // Return if no answer is given
                if (answer1.getText().isEmpty() || answer2.getText().isEmpty()) {
                    return;
                }
                // TODO: check for integers only
                final String NOW = currentYear();
                final int q1 = Integer.valueOf(answer1.getText());
                final int q2 = Integer.valueOf(answer2.getText());

                String tmpEval = getExpression();
                tmpEval = tmpEval.replace("Q1",Integer.toString(q1));
                tmpEval = tmpEval.replace("Q2", Integer.toString(q2));
                tmpEval = tmpEval.replace("NOW", NOW);
                
                // TODO reset content
                History.getInstance().pushQuestion(
                        History.getInstance().getCurrentQuestion());

                if (evaluate(tmpEval.trim())) {
                    for (final Answer a : answers) {
                        if (a.getAnswer().equals("True")) {
                            Panel.changeContent(a.getRedirect().showPanel());
                        }
                    }
                } else {
                    for (final Answer a : answers) {
                        if (a.getAnswer().equals("False")) {
                            Panel.changeContent(a.getRedirect().showPanel());
                        }
                    }
                }
            }
        });

        // Create interface
        Label questionLbl1 = new Label(question1);
        questionLbl1.setStyleName("QuestionLbl");
        panel.add(questionLbl1);
        final HorizontalPanel answerpanel1 = new HorizontalPanel();
        answerpanel1.add(answer1);
        answerpanel1.add(addInformation(information1, question1));
        answerpanel1.add(addQuestionEdit());
        panel.add(answerpanel1);


        Label questionLbl2 = new Label(question2);
        questionLbl2.setStyleName("QuestionLbl");
        panel.add(questionLbl2);

        final HorizontalPanel answerpanel2 = new HorizontalPanel();
        answerpanel2.add(answer2);
        answerpanel2.add(addInformation(information2, question2));
        answerpanel2.add(addQuestionEdit());
        panel.add(answerpanel2);

        panel.add(submitBtn);
        // Add answer shortcuts.
        for (final Answer a : answers) {
            Button tmpButton = new Button (a.getAnswer());
            tmpButton.addClickHandler(new ClickHandler(){

                @Override
                public void onClick(ClickEvent event) {
                    History.getInstance().pushQuestion(History.getInstance().getCurrentQuestion());
                    Panel.changeContent(a.getRedirect().showPanel());
                    
                    
                }});
           panel.add(tmpButton); 
            
        }
        
        return panel;
    }

    /**
     * Adds 'I' icon to panel, with information panel
     * 
     * @return
     */
    protected Image addQuestionEdit() {
        final Image openButton = new Image("edit.png");
        final Button closeButton = new Button("Close");
        final TextBox questionBox1 = new TextBox();
        questionBox1.setValue(getQuestion1());
        questionBox1.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                    setQuestion1(questionBox1.getText());
            }

        });        
        
        final TextBox questionBox2 = new TextBox();
        questionBox2.setValue(getQuestion2());
        questionBox2.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                    setQuestion2(questionBox2.getText());
            }

        }); 
        
        final TextBox evaluationBox = new TextBox();
        evaluationBox.setValue(getExpression());
        evaluationBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setExpression(evaluationBox.getText());

            }

        });        


        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(new Label(getQuestion()));
        infobox_content.add(new Label("Change First Question:"));
        infobox_content.add(questionBox1);
        infobox_content.add(new Label("Change Second Question:"));
        infobox_content.add(questionBox2);
        infobox_content.add(new Label("Change Expression:"));
        infobox_content.add(evaluationBox);
        infobox_content.add(new Label("Alter questions metadata:"));
        infobox_content.add(showParameterEditor());
        
        final VerticalPanel infobox_footer = new VerticalPanel();
        infobox_footer.add(closeButton);
        infobox_footer.addStyleName("infobox_footer");
        
        infobox.add(infobox_content);
        infobox.add(infobox_footer);
        
        dialogBox.setWidget(infobox);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {

            public void onClick(final ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");
                Panel.changeContent(showPanel());
            }
        });
        
        
        
        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                dialogBox.show();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "block");
            }
        });
        return openButton;
    }

    public Button showParameterEditor() {

        final Button openButton = new Button("Metadata");
        final Label q1 = new Label("For question: " + this.getQuestion1());
        final CheckBox setAsClass1 = new CheckBox("Set as class.");
        final TextBox newParameter1 = new TextBox();
        final ListBox schemaBox1 = Codification.getInstance().getStructure();
        
        final Label q2 = new Label("For question: " + this.getQuestion2());
        final CheckBox setAsClass2 = new CheckBox("Set as class.");
        final TextBox newParameter2 = new TextBox();
        final ListBox schemaBox2 = Codification.getInstance().getStructure();
        final Button saveBtn = new Button("Save");

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Metadata information");
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel panel = new VerticalPanel();
        panel.addStyleName("dialogVPanel");

        dialogBox.setWidget(panel);

        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                newParameter1.setText(getParameter1());
                newParameter2.setText(getParameter2());
                dialogBox.show();
            }
        });

        saveBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                // If set as class is selected
                if (setAsClass1.getValue()) {
                    setClassName("class");
                    setParameter1("classList");
                    Codification.getInstance().addClass("class");
                    Codification.getInstance().addParameter("class",
                            "classList");
                    for (final Answer a : getAnswers()) {
                        Codification.getInstance().addOption("class",
                                "classList", a.getAnswer());
                    }

                } else if (!newParameter1.getText().equals("")) {
                    setParameter1(newParameter1.getText());
                    if (History.getInstance().getCurrentQuestion() != null) {
                        setClassName(Codification.getInstance()
                                .getCurrentClass());
                        setParameter(newParameter1.getText());
                        Codification.getInstance().addParameter(
                                Codification.getInstance().getCurrentClass(),
                                newParameter1.getText());
                    }
                }
                
                // If set as class is selected
                if (setAsClass2.getValue()) {
                    setClassName("class");
                    setParameter2("classList");
                    Codification.getInstance().addClass("class");
                    Codification.getInstance().addParameter("class",
                            "classList");
                    for (final Answer a : getAnswers()) {
                        Codification.getInstance().addOption("class",
                                "classList", a.getAnswer());
                    }

                } else if (!newParameter2.getText().equals("")) {
                    setParameter2(newParameter2.getText());
                    if (History.getInstance().getCurrentQuestion() != null) {
                        setClassName(Codification.getInstance()
                                .getCurrentClass());
                        setParameter(newParameter2.getText());
                        Codification.getInstance().addParameter(
                                Codification.getInstance().getCurrentClass(),
                                newParameter2.getText());
                    }
                }
                
                dialogBox.hide();
            }

        });

        // Add metadata types.
        panel.add(q1);
        if (Codification.getInstance().getCurrentClass().isEmpty()) {
            panel.add(new Label("Additional information:"));
            panel.add(setAsClass1);
            if (schemaBox1.getItemCount() > 1) {
                panel.add(new Label(
                        "Or select one of the available parameters below"));
                panel.add(schemaBox1);
            }
        } else {
            panel.add(new Label("Set as class parameter of "
                    + Codification.getInstance().getCurrentClass()));
            panel.add(newParameter1);
            if (schemaBox1.getItemCount() > 1) {
                panel.add(new Label(
                        "Or select one of the available parameters below"));
                panel.add(schemaBox1);
            }
        }
        panel.add(q2);
        if (Codification.getInstance().getCurrentClass().isEmpty()) {
            panel.add(new Label("Additional information:"));
            panel.add(setAsClass2);
            if (schemaBox2.getItemCount() > 1) {
                panel.add(new Label(
                        "Or select one of the available parameters below"));
                panel.add(schemaBox2);
            }
        } else {
            panel.add(new Label("Set as class parameter of "
                    + Codification.getInstance().getCurrentClass()));
            panel.add(newParameter2);
            if (schemaBox2.getItemCount() > 1) {
                panel.add(new Label(
                        "Or select one of the available parameters below"));
                panel.add(schemaBox2);
            }
        }
        panel.add(saveBtn);
        return openButton;
    }

    protected String getParameter2() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getParameter1() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toXML() {
        if (!written()) {
            // internal semaphore protecting against recursive redundancy.
            final StringBuilder result = new StringBuilder();
            result.append("\t<step nr=\"" + escapeString(questionNr) + "\">\n");
            result.append("\t\t<type>double</type>\n");
            result.append("\t\t<question>\n");
            result.append("\t\t\t<text>" + escapeString(question1)
                    + "</text>\n");
            result.append("\t\t\t<information>" + escapeString(information1)
                    + "</information>\n");
            if (!Parameter1.isEmpty() && !className.isEmpty()) {
                result.append("\t\t\t<param>" + className + "." + Parameter1
                        + "</param>\n");
            }
            result.append("\t\t</question>\n");
            result.append("\t\t<question>\n");
            result.append("\t\t\t<text>" + escapeString(question2)
                    + "</text>\n");
            result.append("\t\t\t<information>" + escapeString(information2)
                    + "</information>\n");
            if (!Parameter2.isEmpty() && !className.isEmpty()) {
                result.append("\t\t\t<param>" + className + "." + Parameter2
                        + "</param>\n");
            }
            result.append("\t\t</question>\n");
            for (int i = 0; i < answers.size(); i++) {
                result.append("\t\t<answer>\n");
                result.append("\t\t\t<value>"
                        + escapeString(answers.get(i).getAnswer())
                        + "</value>\n");
                result.append("\t\t\t<gotoNr>"
                        + escapeString(answers.get(i).getRedirect()
                                .getQuestionNr()) + "</gotoNr>\n");
                result.append("\t\t</answer>\n");
            }
            result.append("\t\t<evaluate>" + escapeString(eval)
                    + "</evaluate>\n");
            result.append("\t</step>\n");
            for (int i = 0; i < answers.size(); i++) {
                result.append(answers.get(i).getRedirect().toXML());
            }
            return result.toString();
        }
        return "";
    }

    /**
     * Adds 'I' icon to panel, with information panel
     * @param q 
     * @param info 
     * 
     * @return
     */
    protected Image addInformation(final String info, final String q) {
        final Image openButton = new Image("info.png");
        final Button closeButton = new Button("Close");
        final Button editButton = new Button("Edit");

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Additional Information: " + q);
        
        dialogBox.setAnimationEnabled(true);

        final HTML text = new HTML(info.replace("\n", "<br />"));

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(text);
        
        final VerticalPanel infobox_footer = new VerticalPanel();
        infobox_footer.add(closeButton);
        infobox_footer.add(editButton);
        infobox_footer.addStyleName("infobox_footer");
        
        infobox.add(infobox_content);
        infobox.add(infobox_footer);
        
        dialogBox.setWidget(infobox);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {

            public void onClick(final ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");

            }
        });

        editButton.addClickHandler(new ClickHandler() {

            public void onClick(final ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");
                showEditInformation(info, q);
            }

        });
        
        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                dialogBox.show();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "block");
            }
        });
        return openButton;
    }

    private void showEditInformation(String info, String q) {
        final Button closeButton = new Button("Close");
        final TextArea informationBox = new TextArea();
        informationBox.setVisibleLines(4);
        
        
        informationBox.setValue(info);
        informationBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setInformation(informationBox.getText());

            }

        });
        
        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Additional Information: " + q);
        
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(informationBox);
        
        final VerticalPanel infobox_footer = new VerticalPanel();
        infobox_footer.add(closeButton);
        infobox_footer.addStyleName("infobox_footer");
        
        infobox.add(infobox_content);
        infobox.add(infobox_footer);
        
        dialogBox.setWidget(infobox);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {

            public void onClick(final ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");

            }
        });
        
    }
}
