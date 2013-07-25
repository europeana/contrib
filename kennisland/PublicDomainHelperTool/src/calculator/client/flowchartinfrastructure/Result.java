package calculator.client.flowchartinfrastructure;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Extension of Question to reflect results.
 * 
 * @author mzeinstra
 * 
 */
public class Result extends Question {

    private String result;

    public Result(String questionNumber, String text) {
        questionNr = questionNumber;
        result = text;
    }

    @Override
    public Answer getEmptyAnswer() {
        // No further empty questions
        return null;
    }

    @Override
    public Question getNextNode() {
        // No new nodes
        return null;
    }

    @Override
    public String getQuestion() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public VerticalPanel showEditPanel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VerticalPanel showPanel() {
        final VerticalPanel resultPanel = new VerticalPanel();
        final HorizontalPanel panel = new HorizontalPanel();
        final Label questionLbl = new Label(result);
        questionLbl.setStyleName("QuestionLbl");
        panel.add(questionLbl);
        if (!information.isEmpty()) {
            panel.add(addInformation(information, result));
        }
        resultPanel.add(panel);
        return resultPanel;
    }

    @Override
    public String toXML() {
        if (!written()) {
            // internal semaphore protecting against recursive redundancy.
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\t<step nr=\"" + escapeString(questionNr)
                    + "\">\n");
            stringBuilder.append("\t\t<type>result</type>\n");
            stringBuilder.append("\t\t<result>\n");
            stringBuilder.append("\t\t\t<text>" + escapeString(result)
                    + "</text>\n");
            stringBuilder.append("\t\t\t<information>"
                    + escapeString(information) + "</information>\n");
            stringBuilder.append("\t\t</result>\n");
            stringBuilder.append("\t</step>\n");
            doneWriting();
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

}
