/*
 * Answer
 * 
 * Version 1.0
 * Simple answer container class holding an answer text 
 * and a number to redirect to a different question
 * 
 * Copyright Information
*/

package calculator.client;

/**
 * 
 * @author mzeinstra
 *
 */
public class Answer {
    private String AnswerString;
    private String Redirect;
    private String Information;

    /**
     * sets complete answer
     * @param answer
     * @param rd
     * @param inf
     */
    public Answer(String answer, String rd, String inf) {
        setAnswer(answer);
        setRedirect(rd);
        setInformation(inf);
    }

    /**
     * buids empty answer with default values
     */
    public Answer() {
        setAnswer("");
        setRedirect("-1");
        setInformation("");
    }

    /**
     * sets redirect number
     * @param rd
     */
    private void setRedirect(String rd) {
        Redirect = rd;
    }

    /**
     * gets redirect number
     * @return
     */
    public String getRedirect() {
        return Redirect;
    }

    /**
     * sets answer
     * @param answer
     */
    private void setAnswer(String answer) {
        AnswerString = answer;
    }

    /**
     * gets answer string
     * @return
     */
    public String getAnswer() {
        return AnswerString;
    }

    public void setInformation(String inf) {
        Information = inf;
    }

    public String getInformation() {
        return Information;
    }

}
