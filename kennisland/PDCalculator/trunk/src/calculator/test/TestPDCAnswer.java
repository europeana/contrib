package calculator.test;

import static org.junit.Assert.*;
import org.junit.*;

import calculator.client.Answer;


/**
 * @author mzeinstra
 * The TestPDCAnswer class is a Junit Test class for PDCAnswer
 */
public class TestPDCAnswer {
    
    @Test
    public void testSetters(){
        String testAnswer = "test";
        String testRedirect = "1";
        String information = "information test";
        Answer a = new Answer(testAnswer, testRedirect, information);
        // standard test
        assertEquals(a.getAnswer(), testAnswer);
        assertEquals(a.getRedirect(), testRedirect);
    }
    
    
}
