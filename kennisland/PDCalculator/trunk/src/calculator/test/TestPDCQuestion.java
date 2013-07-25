package calculator.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import calculator.client.Answer;
import calculator.client.Question;



/**
 * @author mzeinstra
 * The TestPDCQuestion class is a Junit Test class for PDCQuestion
 */
public class TestPDCQuestion {

    @Test
    public void testQuestion(){
        // test empty question
        Question q = new Question();
        
        assertTrue(q.getQuestion().isEmpty());
        assertTrue(q.getEvaluate().equals(""));
        assertTrue(q.getQuestionNr().equals("-1"));
        assertTrue(q.getType().equals(""));
        
        String testQuestionNr = "25";
        String testEvaluate = "Q1>0";
        String testType = "single";
        String testQuestion1 = "Am I testing?";
        String testQuestion2 = "Are you testing?";
        String testAnswerString1 = "You are testing";
        String testRedirect1 = "13";
        String testInformation1 = "Test information, computing..";
        String testAnswerString2 = "I am testing";
        String testRedirect2 = "31";
        String testInformation2 = "Still computing..";
        
        q.setQuestionNr(testQuestionNr);
        Answer a1 = new Answer(testAnswerString1, testRedirect1, testInformation1);
        Answer a2 = new Answer(testAnswerString2, testRedirect2, testInformation2);
        q.addAnswer(a1);
        q.addAnswer(a2);
        q.setEvaluate(testEvaluate);
        q.setType(testType);
        q.addQuestion(testQuestion1);
        q.addQuestion(testQuestion2);
        
        Answer testAnswer1 = new Answer(testAnswerString1, testRedirect1, testInformation1);
        Answer testAnswer2 = new Answer(testAnswerString2, testRedirect2, testInformation2);
        
        ArrayList<Answer> tmpList = q.getAnswerList();
        assertEquals(tmpList.get(0).getAnswer(), testAnswer1.getAnswer());
        assertEquals(tmpList.get(1).getAnswer(), testAnswer2.getAnswer());
        assertEquals(tmpList.get(0).getRedirect(), testAnswer1.getRedirect());
        assertEquals(tmpList.get(1).getRedirect(), testAnswer2.getRedirect());
        assertEquals(q.getEvaluate(), testEvaluate);
        assertEquals(q.getQuestionNr(), testQuestionNr);
        
        int testQuestionNrInt = 4;
        q.setQuestionNr(testQuestionNrInt);
        assertEquals(q.getQuestionNr(), Integer.toString(testQuestionNrInt));
    }
    
    @Test
    public void testFilledQuestion(){
        // test filled question
        String testQuestionNr = "25";
        String testEvaluate = "Q1>0";
        String testType = "single";
        String testQuestion1 = "Am I testing?";
        String testQuestion2 = "Are you testing?";
        String testAnswerString1 = "You are testing";
        String testInformation1 = "Comense the test";
        String testRedirect1 = "13";
        String testAnswerString2 = "I am testing";
        String testRedirect2 = "31";
        String testInformation2 = "We have started";
        
        ArrayList<Answer> testAnswers = new ArrayList<Answer>();
        Answer a1 = new Answer(testAnswerString1, testRedirect1, testInformation1);
        Answer a2 = new Answer(testAnswerString2, testRedirect2, testInformation2);
        testAnswers.add(a1);
        testAnswers.add(a2);
        
        Question q = new Question(testQuestion1, testAnswers, testType, testQuestionNr);
        
        Answer testAnswer1 = new Answer(testAnswerString1, testRedirect1, testInformation1);
        Answer testAnswer2 = new Answer(testAnswerString2, testRedirect2, testInformation2);
        
        ArrayList<Answer> tmpList = q.getAnswerList();
        assertEquals(tmpList.get(0).getAnswer(), testAnswer1.getAnswer());
        assertEquals(tmpList.get(1).getAnswer(), testAnswer2.getAnswer());
        assertEquals(tmpList.get(0).getRedirect(), testAnswer1.getRedirect());
        assertEquals(tmpList.get(1).getRedirect(), testAnswer2.getRedirect());
        assertEquals(q.getQuestionNr(), testQuestionNr);
        
        int testQuestionNrInt = 4;
        q.setQuestionNr(testQuestionNrInt);
        assertEquals(q.getQuestionNr(), Integer.toString(testQuestionNrInt));
        
        // With extra evaluate option.
        q = new Question(testQuestion1, testAnswers, testType, testQuestionNr, testEvaluate);
        
        testAnswer1 = new Answer(testAnswerString1, testRedirect1, testInformation1);
        testAnswer2 = new Answer(testAnswerString2, testRedirect2, testInformation2);
        
        tmpList = q.getAnswerList();
        assertEquals(tmpList.get(0).getAnswer(), testAnswer1.getAnswer());
        assertEquals(q.getEvaluate(), testEvaluate);
        assertEquals(tmpList.get(1).getAnswer(), testAnswer2.getAnswer());
        assertEquals(tmpList.get(0).getRedirect(), testAnswer1.getRedirect());
        assertEquals(tmpList.get(1).getRedirect(), testAnswer2.getRedirect());
        assertEquals(q.getQuestionNr(), testQuestionNr);
        
        testQuestionNrInt = 6;
        q.setQuestionNr(testQuestionNrInt);
        assertEquals(q.getQuestionNr(), Integer.toString(testQuestionNrInt));
        
        // add second question.
        q.addQuestion(testQuestion2);
        assertTrue(q.getQuestion().get(0).equals(testQuestion1));
        assertTrue(q.getQuestion().get(1).equals(testQuestion2));    
    }
}
