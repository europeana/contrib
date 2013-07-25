package calculator.client;

import java.util.Stack;

import calculator.client.flowchartinfrastructure.Question;

/**
 * Singleton to keep history and communicated throughout calculation, editing,
 * etc.
 * 
 * @author mzeinstra
 * 
 */
public final class History {
    private Stack<Question> questions = new Stack<Question>();
    private Question currentQuestion = null;

    private static History instance = null;

    public static History getInstance() {
        if (instance == null) {
            instance = new History();
        }
        return instance;
    }

    private History() {
        // Exists only to defeat instantiation.
    }

    public void clear() {
        questions = new Stack<Question>();
        currentQuestion = null;

    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public Question PopQuestion() {
        if (questions.isEmpty()) {
            return currentQuestion;
        }
        return questions.pop();
    }

    public void pushQuestion(Question q) {
        questions.push(q);
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
