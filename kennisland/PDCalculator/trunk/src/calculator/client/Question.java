/**
 * Class Question
 * @author mzeinstra
 * 
 * Version 1.0
 * 
 * Question container class for Europeana's Public Domain Calculator, 
 * holds list of questions and a list possible answers,
 * question number, type and evaluation string
 * 
 * Copyright Information
*/

package calculator.client;

import java.util.ArrayList;

/**
 * @author mzeinstra
 * The Question class is a container for a question in the Public domain calculator
 */
public class Question {
    private ArrayList<String> Question;
    private String QuestionNr;
    private ArrayList<Answer> AnswerList;
    private String Type;
    private String Evaluate;
    private String Information;

    /**
     * Construction, set all variables to question container
     * @param QuestionString
     * @param answers
     * @param Type
     * @param Nr
     */
    public Question(String QuestionString, ArrayList<Answer> answers,
            String Type, String Nr) {
        Question = new ArrayList<String>();
        addQuestion(QuestionString);
        setType(Type);
        setAnswerList(answers);
        setQuestionNr(Nr);
        setInformation("");
    }
    
    public Question(String QuestionString, ArrayList<Answer> answers,
            String Type, String Nr, String eval) {
        Question = new ArrayList<String>();
        addQuestion(QuestionString);
        setType(Type);
        setAnswerList(answers);
        setQuestionNr(Nr);
        setEvaluate(eval);
        setInformation("");
    }

    /**
     * Set empty Question for construction usage
     */
    public Question() {
        Question = new ArrayList<String>();
        setType("");
        setAnswerList(new ArrayList<Answer>());
        setQuestionNr("-1");
        setEvaluate("");
        setInformation("");
    }

    /**
     * gets question number
     * @return
     */
    public String getQuestionNr() {
        if (QuestionNr != null){
            return QuestionNr;
        } else {
            return "-1";
        }
        
    }
    
    /**
     * set question number
     * @param Nr
     */
    public void setQuestionNr(final String Nr) {
        if (Nr == null){
            QuestionNr = "";
        } else {
            QuestionNr = Nr;
        }
    }

    /**
     * set question numer
     * @param Nr
     */
    public void setQuestionNr(final int Nr) {
        QuestionNr = Integer.toString(Nr);
    }

    /**
     * sets question
     * @param q
     */
    public void addQuestion(String q) {
        if (q != null) {
            Question.add(q);
        }
    }

    /**
     * sets evaluation string
     * @param eval
     */
    public void setEvaluate(String eval) {
        if (eval != null){
            Evaluate = eval;
        } else {
            Evaluate = "";
        }
    }

    /**
     * gets evaluation string
     * @return
     */
    public String getEvaluate() {
        return Evaluate;
    }
    
    /**
     * gets answerlist
     * @return
     */
    public ArrayList<String> getQuestion() {
        return new ArrayList<String>(Question);
    }

    /**
     * set type
     * @param t
     */
    public void setType(String t) {
        if (t != null) {
            Type = t;
        } else {
            Type = "";
        }
    }

    /**
     * gets type
     * @return
     */
    public String getType() {
        return Type;
    }

    /**
     * sets answerlist
     * @param al
     */
    public void setAnswerList(ArrayList<Answer> al) {
        if (al != null){
            AnswerList = new ArrayList<Answer>(al); 
        }   
    }

    /**
     * gets answerlist
     * @return
     */
    public ArrayList<Answer> getAnswerList() {
        return new ArrayList<Answer>(AnswerList);
    }

    /**
     * adds answers object to answer list
     * @param answer
     */
    public void addAnswer(Answer answer) {
        AnswerList.add(answer);
    }

    public void setInformation(String i) {
        if (i != null){
            Information = i;
        }  
    }

    public String getInformation() {
        return Information;
    }

}
