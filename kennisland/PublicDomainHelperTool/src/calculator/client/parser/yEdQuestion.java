package calculator.client.parser;

import java.util.HashMap;

/**
 * 
 * @author mzeinstra
 * 
 */
public class yEdQuestion {
    private String questionNr;
    private String question;
    // name, redirect
    private final HashMap<String, String> answers = new HashMap<String, String>();

    yEdQuestion() {
        question = "";
        questionNr = "";
    }

    public void addAnswer(String name, String redirect) {
        answers.put(name, redirect);
    }

    public HashMap<String, String> getAnswers() {
        return answers;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionNr() {
        return questionNr;
    }

    public void setQuestion(String q) {
        question = q;
    }

    public void setQuestionNr(String nr) {
        questionNr = nr;
    }
}
