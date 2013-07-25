package calculator.client.flowchartinfrastructure;

import java.util.ArrayList;
import java.util.List;

import calculator.client.History;
import calculator.client.Panel;
import calculator.client.datainfrastructure.Codification;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

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

    private ListBox getChangeDropDown() {
        // No Change to different question for
        if (answers.size() != 2) {
            return null;
        }
        final ListBox options = new ListBox();
        options.addItem("");
        options.addItem("Change to Single Input Question");
        options.addItem("Change to Double Input Question");

        options.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                // TODO Auto-generated method stub
                if (!options.getItemText(options.getSelectedIndex()).isEmpty()) {
                    final String text = options.getItemText(options
                            .getSelectedIndex());
                    if (text.equals("Change to Single Input Question")) {
                        Panel.changeContent(showChangeToSingleInput());

                    } else if (text.equals("Change to Double Input Question")) {
                        Panel.changeContent(showChangeToDoubleInput());
                    }
                }
            }

        });
        return options;

    }

    @Override
    public String getQuestion() {
        return question;
    }

    public void replaceSelf(Question q) {
        RootQuestion.getInstance().replaceQuestion(q);
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
    public VerticalPanel showEditPanel() {
        // get the list of answers already filled in
        final List<Answer> a = getAnswers();

        final TextBox questionBox = new TextBox();
        final TextArea informationBox = new TextArea();
        informationBox.setVisibleLines(4);

        questionBox.setValue(getQuestion());
        questionBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setQuestion(questionBox.getText());

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

        left.add(new Label("Change questions and answers"));
        left.add(new Label("Question"));
        left.add(questionBox);
        left.add(new Label("Additional Notes and Information"));
        left.add(informationBox);
        left.add(new Label("Answers"));
        for (int i = 0; i < a.size(); i++) {
            left.add(a.get(i).editAnswer());
        }

        final ListBox changes = getChangeDropDown();
        if (changes != null) {
            right.add(new Label("Change to another type of question."));
            right.add(changes);
        }
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
        // Request data from data package
        final String data = Codification.getInstance().getData(
                getClassName() + "." + Parameter);

        // Check if data exists.
        if (data != null) {
            // Find matching answer
            for (final Answer a : answers) {
                if (a.getAnswer().equals(data)) {
                    // goto answer.
                    return a.getRedirect().showPanel();
                }
            }
        }

        // If information has not been found, request data
        final Question currentQuestion = this;
        final VerticalPanel resultPanel = new VerticalPanel();
        final HorizontalPanel panel = new HorizontalPanel();

        final Label questionLbl = new Label(question);
        questionLbl.setStyleName("QuestionLbl");
        panel.add(questionLbl);
        if (!information.isEmpty()) {
            panel.add(addInformation(information, question));
        }
        resultPanel.add(panel);
        for (final Answer a : answers) {
            resultPanel.add(a.showButton(currentQuestion));
        }
        return resultPanel;
    }

    public Button showParameterEditor() {

        final Button openButton = new Button("Metadata");
        final CheckBox setAsClass = new CheckBox("Set as class.");
        final TextBox newParameter = new TextBox();
        if (!getClassName().isEmpty()) {
            newParameter.setText(getClassName());
        }
        final ListBox schemaBox = Codification.getInstance().getStructure();
        final Button saveBtn = new Button("Save");

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Metadata information");
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel panel = new VerticalPanel();
        panel.addStyleName("dialogVPanel");

        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        dialogBox.setWidget(panel);

        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
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
                        for (final Answer a : getAnswers()) {
                            Codification.getInstance().addOption(
                                    Codification.getInstance()
                                            .getCurrentClass(), getParameter(),
                                    a.getAnswer());
                        }
                    }
                }
                dialogBox.hide();
            }

        });

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
        return openButton;
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
            doneWriting();
            return result.toString();
        } else {
            return "";
        }
    }

}
