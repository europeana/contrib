package com.europeana.client.parser;

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
    private HashMap<String, String> answers = new HashMap<String, String>();
    
    yEdQuestion () {
        this.question ="";
        this.questionNr = "";
    }
    
    public void setQuestionNr(String nr) {
        this.questionNr = nr;
    }
    
    public String getQuestionNr() {
        return this.questionNr;
    }
    
    public void setQuestion(String q) {
        this.question = q;
    }
    
    public String getQuestion() {
        return this.question;
    }
    
    public void addAnswer(String name, String redirect) {
        this.answers.put(name, redirect);
        
    }
    
    public HashMap<String, String> getAnswers() {
        return this.answers;
    }
}
