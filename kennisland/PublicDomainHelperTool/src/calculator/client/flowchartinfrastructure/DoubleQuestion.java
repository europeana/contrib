package calculator.client.flowchartinfrastructure;

import java.util.ArrayList;

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
 * Holds the interface and data for a double input question.
 * 
 * @author mzeinstra
 * 
 */
public class DoubleQuestion extends Question {

    private String question1;
    private String question2;
    private String information1;
    private String information2;
    private String eval;
    private String Parameter1;
    private String Parameter2;

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

    @Override
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

        // Request data from data package
        final String data1 = Codification.getInstance().getData(
                getClassName() + "." + Parameter1);
        final String data2 = Codification.getInstance().getData(
                getClassName() + "." + Parameter2);
        // Check if data exists.
        if (data1 != null && data2 != null) {
            // Find matching answer
            // TODO: check for integers only
            final String NOW = currentYear();
            final int q1 = Integer.valueOf(data1);
            final int q2 = Integer.valueOf(data2);

            String tmpEval = getExpression();
            tmpEval = tmpEval.replace("Q1", Integer.toString(q1));
            tmpEval = tmpEval.replace("Q2", Integer.toString(q2));
            tmpEval = tmpEval.replace("NOW", NOW);

            // TODO reset content
            History.getInstance().pushQuestion(
                    History.getInstance().getCurrentQuestion());

            if (evaluate(tmpEval.trim())) {
                for (final Answer a : answers) {
                    if (a.getAnswer().equals("True")) {
                        return a.getRedirect().showPanel();
                    }
                }
            } else {
                for (final Answer a : answers) {
                    if (a.getAnswer().equals("False")) {
                        return a.getRedirect().showPanel();
                    }
                }
            }
        }

        // If no data was found, request data
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
                tmpEval = tmpEval.replace("Q1", Integer.toString(q1));
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
        final Label questionLbl1 = new Label(question1);
        questionLbl1.setStyleName("QuestionLbl");
        panel.add(questionLbl1);
        if (!information1.isEmpty()) {
            final HorizontalPanel answerpanel1 = new HorizontalPanel();
            answerpanel1.add(answer1);
            answerpanel1.add(addInformation(information1, question1));
            panel.add(answerpanel1);
        } else {
            panel.add(answer1);
        }

        final Label questionLbl2 = new Label(question2);
        questionLbl2.setStyleName("QuestionLbl");
        panel.add(questionLbl2);
        if (!information2.isEmpty()) {
            final HorizontalPanel answerpanel2 = new HorizontalPanel();
            answerpanel2.add(answer2);
            answerpanel2.add(addInformation(information2, question2));
            panel.add(answerpanel2);
        } else {
            panel.add(answer2);
        }

        panel.add(submitBtn);
        return panel;
    }

    public Button showParameterEditor() {

        final Button openButton = new Button("Metadata");
        final CheckBox setAsClass = new CheckBox("Set as class.");
        final TextBox newParameter = new TextBox();
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
            result.append("\t<step nr=\"" + escapeString(questionNr) + "\">\n");
            result.append("\t\t<type>double</type>\n");
            result.append("\t\t<question>\n");
            result.append("\t\t\t<text>" + escapeString(question1)
                    + "</text>\n");
            result.append("\t\t\t<information>" + escapeString(information1)
                    + "</information>\n");
            result.append("\t\t\t<param>" + className + "." + Parameter1
                    + "</param>\n");
            result.append("\t\t</question>\n");
            result.append("\t\t<question>\n");
            result.append("\t\t\t<text>" + escapeString(question2)
                    + "</text>\n");
            result.append("\t\t\t<information>" + escapeString(information2)
                    + "</information>\n");
            if (!Parameter.isEmpty() && !className.isEmpty()) {
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
            doneWriting();
            return result.toString();
        }
        return "";
    }

}
