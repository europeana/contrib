package eu.europeana.sip.licensing.gui;

import eu.europeana.sip.licensing.model.Answer;

import javax.swing.*;

/**
 * todo: add class description
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class AnswerRadioButton extends JRadioButton {

    private Answer answer;

    public AnswerRadioButton(Answer answer) {
        super(answer.getText());
        this.answer = answer;
    }

    public AnswerRadioButton(Answer answer, boolean selected) {
        super(answer.getText(), selected);
        this.answer = answer;
    }

    @Override
    public boolean isSelected() {
        return super.isSelected();
    }

    public Answer getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return answer.getText();
    }
}
