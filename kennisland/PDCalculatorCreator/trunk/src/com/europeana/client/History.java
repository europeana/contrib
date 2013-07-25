package com.europeana.client;

import java.util.Stack;

import com.europeana.client.flowchartinfrastructure.Question;

/**
 * Singleton to keep history and communicated throughout calculation, editing, etc.
 * @author mzeinstra
 *
 */
public final class History {
    private Stack<Question> questions = new Stack<Question>();
    private Question currentQuestion = null;
    
    
    private static History instance = null;
    private History() {
       // Exists only to defeat instantiation.
    }
    
    public static History getInstance() {
       if(instance == null) {
          instance = new History();
       }
       return instance;
    }
    
    public void pushQuestion(Question q) {
        this.questions.push(q);
    }
    
    public Question PopQuestion() {
        if (this.questions.isEmpty()) {
            return null;
        }
        return this.questions.pop();
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }
}

