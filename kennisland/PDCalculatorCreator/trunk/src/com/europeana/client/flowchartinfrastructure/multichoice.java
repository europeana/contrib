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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
 * 
 * @author mzeinstra
 *
 */
public class multichoice extends Question {
    String question;

    public multichoice(final String number, final String text,
            final List<Answer> answersList) {
        if (answersList != null) {
            answers = new ArrayList<Answer>(answersList);
        } else {
            answers = new ArrayList<Answer>();
        }

        questionNr = number;
        question = text;
    }

    @Override
    public List<Answer> getAnswers() {
        return new ArrayList<Answer>(answers);
    }

    @Override
    public String getQuestion() {
        return question;
    }

    public void replaceSelf(Question q) {
        RootQuestion.getInstance().replaceQuestion(q);
        Panel.changeContent(q.showPanel());
    }

    public void setQuestion(final String text) {
        question = text;
    }

    public VerticalPanel showChangeToDoubleInput() {
        // get the list of answers already filled in
        final String qNr = getQuestionNr();
        final List<Answer> a = getAnswers();

        final Label[] answerLabels = new Label[a.size()];
        for (int i = 0; i < a.size(); i++) {
            answerLabels[i] = new Label(a.get(i).getAnswer());
        }

        final TextBox questionBox1 = new TextBox();
        final TextBox questionBox2 = new TextBox();
        final TextBox pattern = new TextBox();

        // Build interface
        final VerticalPanel panel = new VerticalPanel();
        final HorizontalPanel base = new HorizontalPanel();
        final VerticalPanel left = new VerticalPanel();
        final VerticalPanel right = new VerticalPanel();

        panel.add(new Label("Choose a setup"));
        panel.add(new Label("Original question"));
        panel.add(new Label(getQuestion()));
        panel.add(new Label("New question Q1"));
        panel.add(questionBox1);
        panel.add(new Label("New question Q2"));
        panel.add(questionBox2);
        panel.add(new Label("Create an evaluation query"));
        panel.add(pattern);
        left.setSpacing(10);
        right.setSpacing(10);

        left.add(new Label("Answers"));
        left.add(new Label(answerLabels[0].getText() + " : True"));
        left.add(new Label(answerLabels[1].getText() + " : False"));
        final Button leftBtn = new Button("Select");
        leftBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final DoubleQuestion q = new DoubleQuestion(qNr, questionBox1
                        .getText(), questionBox2.getText(), pattern.getText());
                // replace answers
                q.answers.clear();
                q.answers.add(new Answer("True", answers.get(0).getRedirect(),
                        ""));
                q.answers.add(new Answer("False", answers.get(1).getRedirect(),
                        ""));
                replaceSelf(q);
            }

        });
        left.add(leftBtn);

        right.add(new Label("Answers"));
        right.add(new Label(answerLabels[0].getText() + " : False"));
        right.add(new Label(answerLabels[1].getText() + " : True"));
        final Button rightBtn = new Button("Select");
        rightBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final DoubleQuestion q = new DoubleQuestion(qNr, questionBox1
                        .getText(), questionBox2.getText(), pattern.getText());
                // replace answers
                q.answers.clear();
                q.answers.add(new Answer("True", answers.get(1).getRedirect(),
                        ""));
                q.answers.add(new Answer("False", answers.get(0).getRedirect(),
                        ""));
                replaceSelf(q);
            }

        });
        right.add(rightBtn);

        base.add(left);
        base.add(right);
        panel.add(base);

        return panel;
    }

    public VerticalPanel showChangeToSingleInput() {
        // get the list of answers already filled in
        final String qNr = getQuestionNr();
        final List<Answer> a = getAnswers();

        final Label[] answerLabels = new Label[a.size()];
        for (int i = 0; i < a.size(); i++) {
            answerLabels[i] = new Label(a.get(i).getAnswer());
        }

        final TextBox questionBox = new TextBox();

        questionBox.setValue(getQuestion());
        questionBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setQuestion(questionBox.getText());

            }

        });

        final TextBox pattern = new TextBox();

        // Build interface
        final VerticalPanel panel = new VerticalPanel();
        final HorizontalPanel base = new HorizontalPanel();
        final VerticalPanel left = new VerticalPanel();
        final VerticalPanel right = new VerticalPanel();

        panel.add(new Label("Choose a setup"));
        panel.add(new Label("Question"));
        panel.add(questionBox);
        panel.add(new Label("Create an evaluation query"));
        panel.add(pattern);
        left.setSpacing(10);
        right.setSpacing(10);

        left.add(new Label("Answers"));
        left.add(new Label(answerLabels[0].getText() + " : True"));
        left.add(new Label(answerLabels[1].getText() + " : False"));
        final Button leftBtn = new Button("Select");
        leftBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final SingleQuestion q = new SingleQuestion(qNr, questionBox
                        .getText(), pattern.getText());
                // replace answers
                q.answers.clear();
                q.answers.add(new Answer("True", answers.get(0).getRedirect(),
                        ""));
                q.answers.add(new Answer("False", answers.get(1).getRedirect(),
                        ""));
                replaceSelf(q);
            }

        });
        left.add(leftBtn);

        right.add(new Label("Answers"));
        right.add(new Label(answerLabels[0].getText() + " : False"));
        right.add(new Label(answerLabels[1].getText() + " : True"));
        final Button rightBtn = new Button("Select");
        rightBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final SingleQuestion q = new SingleQuestion(qNr, questionBox
                        .getText(), pattern.getText());
                // replace answers
                q.answers.clear();
                q.answers.add(new Answer("True", answers.get(1).getRedirect(),
                        ""));
                q.answers.add(new Answer("False", answers.get(0).getRedirect(),
                        ""));
                replaceSelf(q);
            }

        });
        right.add(rightBtn);

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
        final Question currentQuestion = this;
        final VerticalPanel resultPanel = new VerticalPanel();
        final HorizontalPanel panel = new HorizontalPanel();

        Label questionLbl = new Label(question);
        questionLbl.setStyleName("QuestionLbl");
        panel.add(questionLbl);
        panel.add(addInformation());
        panel.add(addQuestionEdit());
        resultPanel.add(panel);
        for (final Answer a : answers) {
            final HorizontalPanel answerPanel = new HorizontalPanel();
            answerPanel.add(a.showButton(currentQuestion));
            answerPanel.add(addAnwserEdit(a));
            resultPanel.add(answerPanel);
        }
        return resultPanel;
    }

    /**
     * Adds 'I' icon to panel, with information panel
     * 
     * @param information
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

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Change Question:");
        
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(new Label(getQuestion()));
        infobox_content.add(questionBox);
        
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

        if (this.getAnswers().size() == 2) {
            final ListBox options = new ListBox();
            options.addItem("");
            options.addItem("Change to Single Input Question");
            options.addItem("Change to Double Input Question");
    
            options.addChangeHandler(new ChangeHandler() {
    
                public void onChange(ChangeEvent event) {
                    if (!options.getItemText(options.getSelectedIndex()).isEmpty()) {
                        final String text = options.getItemText(options
                                .getSelectedIndex());
                        if (text.equals("Change to Single Input Question")) {
                            Panel.changeContent(showChangeToSingleInput());
    
                        } else if (text.equals("Change to Double Input Question")) {
                            Panel.changeContent(showChangeToDoubleInput());
                        }
                        dialogBox.hide();
                        RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");
                    }
                }
    
            });
            infobox_content.add(new Label("Change to another type of question."));
            infobox_content.add(options);
        }
        
        infobox_content.add(showParameterEditor());
        
        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                dialogBox.show();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "block");
            }
        });
        return openButton;
    }

    /**
     * Adds 'I' icon to panel, with information panel
     * 
     * @return
     */
    protected Image addAnwserEdit(final Answer a) {
        final Image openButton = new Image("edit.png");
        final Button closeButton = new Button("Close");
        final TextBox answerBox = new TextBox();

        answerBox.setValue(a.getAnswer());
        answerBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                a.setAnswer(answerBox.getText());

            }

        });        

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Change Answer:");
        
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(new Label(a.getAnswer()));
        infobox_content.add(answerBox);
        
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
    
    public VerticalPanel showParameterEditor() {

        final Button setAsClass = new Button("Set as class.");
        final TextBox newParameter = new TextBox();
        if (!getClassName().isEmpty()) {
            newParameter.setText(getParameter());
        }
        final ListBox schemaBox = Codification.getInstance().getStructure();

        final VerticalPanel panel = new VerticalPanel();

        setAsClass.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                setClassName("class");
                setParameter("classList");
                Codification.getInstance().addClass("class");
                Codification.getInstance().addParameter("class","classList");
                for (final Answer a : getAnswers()) {
                    Codification.getInstance().addOption("class", "classList", a.getAnswer());
                }
            }
        });
        
        newParameter.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                if (History.getInstance().getCurrentQuestion() != null) {
                    setClassName(Codification.getInstance().getCurrentClass());
                    setParameter(newParameter.getText());
                    Codification.getInstance().addParameter(Codification.getInstance().getCurrentClass(), newParameter.getText());
                    for (final Answer a : getAnswers()) {
                        Codification.getInstance().addOption(Codification.getInstance().getCurrentClass(), getParameter(), a.getAnswer());
                    }
                }
            }
        });
        
        schemaBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                if (!schemaBox.getItemText(schemaBox.getSelectedIndex()).isEmpty()) {
                    String[] temp;
                    temp = schemaBox.getItemText(schemaBox.getSelectedIndex()).split(".");
                    setClassName(temp[0]);
                    setParameter(temp[1]);        
                }
                
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
        
        return panel;
    }

    @Override
    public String toXML() {
        if (!written()) {
            // internal semaphore protecting against recursive redundancy.
            final StringBuilder result = new StringBuilder();
            result.append("\t<step nr=\"" + questionNr + "\">\n");
            result.append("\t\t<type>multiplechoice</type>\n");
            result.append("\t\t<question>\n");
            result.append("\t\t\t<text>" + question + "</text>\n");
            result.append("\t\t\t<information>" + information
                    + "</information>\n");
            if (!Parameter.isEmpty() && !className.isEmpty()) {
                result.append("\t\t\t<param>" + className + "." + Parameter
                        + "</param>\n");
            }
            result.append("\t\t</question>\n");
            for (int i = 0; i < answers.size(); i++) {
                result.append(answers.get(i).toXML());
            }
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
}
