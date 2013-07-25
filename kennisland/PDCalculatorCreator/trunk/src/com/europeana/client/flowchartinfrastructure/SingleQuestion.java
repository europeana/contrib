package com.europeana.client.flowchartinfrastructure;

import java.util.ArrayList;
import java.util.List;

import com.europeana.client.History;
import com.europeana.client.Panel;
import com.europeana.client.datainfrastructure.Codification;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
 * Holds the interface and data for a single input question.
 * @author mzeinstra
 *
 */
public class SingleQuestion extends Question {
    private String question;
    private String eval;

    public SingleQuestion(String number, String text, String pattern) {
        questionNr = number;
        question = text;
        eval = pattern;

        answers = new ArrayList<Answer>();
        answers.add(new Answer("True", null, ""));
        answers.add(new Answer("False", null, ""));
    }

    @Override
    public List<Answer> getAnswers() {
        return new ArrayList<Answer>(answers);
    }

    private String getExpression() {
        return eval;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    public void setExpression(String expr) {
        eval = expr;
    }

    public void setQuestion(String text) {
        question = text;
    }

    public VerticalPanel showEditPanel() {
        // get the list of answers already filled in

        final TextBox questionBox = new TextBox();
        final TextBox patternBox = new TextBox();
        final TextArea informationBox = new TextArea();
        informationBox.setVisibleLines(4);

        questionBox.setValue(getQuestion());
        questionBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setQuestion(questionBox.getText());

            }

        });

        patternBox.setValue(getExpression());
        patternBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setExpression(patternBox.getText());
            }

        });

        informationBox.setValue(getInformation());
        informationBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setInformation(informationBox.getText());

            }

        });

        // Build interface
        final VerticalPanel panel = new VerticalPanel();
        final HorizontalPanel base = new HorizontalPanel();
        final VerticalPanel left = new VerticalPanel();
        final VerticalPanel right = new VerticalPanel();

        left.add(new Label("Change questions and pattern"));
        left.add(new Label("Question"));
        left.add(questionBox);
        left.add(new Label("Change Pattern"));
        left.add(patternBox);
        left.add(new Label("Additional Notes and Information"));
        left.add(informationBox);

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
        HorizontalPanel qPanel = new HorizontalPanel();        
        Label questionLbl = new Label(question);
        qPanel.add(questionLbl);
        qPanel.add(addInformation());  
        qPanel.add(addQuestionEdit());
        

        final TextBox answer = new TextBox();
        final Button submitBtn = new Button("Submit");
        submitBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                // Return if no answer is given
                if (answer.getText().isEmpty()) {
                    return;
                }
                // TODO: check for integers only
                final String NOW = currentYear();
                final int q1 = Integer.valueOf(answer.getText());
                String tmpEval = getExpression().replace("Q1",
                        Integer.toString(q1));
                tmpEval = tmpEval.replace("NOW", NOW);

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
        
        panel.add(qPanel);
        panel.add(answer);
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

    public Button showParameterEditor() {

        final Button openButton = new Button("Metadata");
        final CheckBox setAsClass = new CheckBox("Set as class.");
        final TextBox newParameter = new TextBox();
        final ListBox schemaBox = Codification.getInstance().getStructure();
        final Button saveBtn = new Button("Save");
        final Button closeBtn = new Button("Close");

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Metadata information");
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel panel = new VerticalPanel();

        dialogBox.setWidget(panel);

        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                newParameter.setText(getParameter());
                dialogBox.show();
            }
        });

        saveBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                // If set as class is selected
                if (setAsClass.getValue()) {
                    setClassName("class");
                    setParameter("classList");
                    Codification.getInstance().addClass("class");
                    Codification.getInstance().addParameter("class",
                            "classList");
                    for (final Answer a : getAnswers()) {
                        Codification.getInstance().addOption("class",
                                "classList", a.getAnswer());
                    }

                } else if (!newParameter.getText().equals("")) {
                    if (History.getInstance().getCurrentQuestion() != null) {
                        setClassName(Codification.getInstance()
                                .getCurrentClass());
                        setParameter(newParameter.getText());
                        Codification.getInstance().addParameter(
                                Codification.getInstance().getCurrentClass(),
                                newParameter.getText());
                    }
                }
                dialogBox.hide();
            }

        });
        
        closeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                
            }});

        // Add metadata types.
        if (Codification.getInstance().getCurrentClass().isEmpty()) {
            panel.add(new Label("Additional information:"));
            panel.add(setAsClass);
            if (schemaBox.getItemCount() > 1) {
                panel.add(new Label(
                        "Or select one of the available parameters below"));
                panel.add(schemaBox);
            }
        } else {
            panel.add(new Label("Set as class parameter of "
                    + Codification.getInstance().getCurrentClass()));
            panel.add(newParameter);
            if (schemaBox.getItemCount() > 1) {
                panel.add(new Label(
                        "Or select one of the available parameters below"));
                panel.add(schemaBox);
            }
        }
        panel.add(saveBtn);
        panel.add(closeBtn);
        return openButton;
    }

    @Override
    public String toXML() {
        if (!written()) {
            // internal semaphore protecting against recursive redundancy.
            final StringBuilder result = new StringBuilder();
            result.append("\t<step nr=\"" + escapeString(questionNr) + "\">\n");
            result.append("\t\t<type>single</type>\n");
            result.append("\t\t<question>\n");
            result
                    .append("\t\t\t<text>" + escapeString(question)
                            + "</text>\n");
            result.append("\t\t\t<information>" + escapeString(information)
                    + "</information>\n");
            if (!Parameter.isEmpty() && !className.isEmpty()) {
                result.append("\t\t\t<param>" + className + "." + Parameter
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
        } else {
            return "";
        }
    }

    /**
     * Adds 'I' icon to panel, with information panel
     * 
     * @param information
     * @return
     */
    protected Image addInformation() {
        final Image openButton = new Image("info.png");
        final Button closeButton = new Button("Close");
        final Button editButton = new Button("Edit");

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Additional Information: " + this.getQuestion());
        
        dialogBox.setAnimationEnabled(true);

        final HTML text = new HTML(this.getInformation().replace("\n", "<br />"));

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(text);
        
        final HorizontalPanel infobox_footer = new HorizontalPanel();
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
                showEditInformation();
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

    private void showEditInformation() {
        final Button closeButton = new Button("Close");
        final TextArea informationBox = new TextArea();
        informationBox.setVisibleLines(20);
        informationBox.setWidth("600px");
        
        
        informationBox.setValue(getInformation());
        informationBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setInformation(informationBox.getText());

            }

        });
        
        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.show();
        dialogBox.setText("Additional Information: " + this.getQuestion());
        
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(informationBox);
        
        final HorizontalPanel infobox_footer = new HorizontalPanel();
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
        
    }
    
    /**
     * Adds 'I' icon to panel, with information panel
     * 
     * @return
     */
    protected Image addQuestionEdit() {
        final Image openButton = new Image("edit.png");
        final Button closeButton = new Button("Close");
        final TextBox questionBox = new TextBox();
        questionBox.setValue(getQuestion());
        questionBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setQuestion(questionBox.getText());

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
        infobox_content.add(new Label("Change Question:"));
        infobox_content.add(questionBox);
        infobox_content.add(new Label("Change Expression:"));
        infobox_content.add(evaluationBox);
        infobox_content.add(new Label("Alter question metadata:"));
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

}
