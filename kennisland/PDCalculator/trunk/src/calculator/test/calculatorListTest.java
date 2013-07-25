package calculator.test;

import static org.junit.Assert.*;

import org.junit.Test;

import calculator.client.calculatorList;



//ESCA-JAVA0076:
/**
 * @author mzeinstra
 * The CalculatorListTest is a unit test for the class calculatorList
 */
public class calculatorListTest {
    @Test
    public void testIntitialization(){
        // test if empty initialization returns null
        calculatorList testCL = new calculatorList();
        assertTrue(testCL.getLanguage(15) == null);
        assertTrue(testCL.getLocation(15) == null);
        assertTrue(testCL.getName(15) == null);
        assertTrue(testCL.getLanguage(0) == null);
        assertTrue(testCL.getLocation(0) == null);
        assertTrue(testCL.getName(0) == null);
        assertTrue(testCL.len() == 0);
        
        
        // test if filled in returns correct.
        String testName = "testName";
        String testLocation = "./";
        String testLanguage = "nl";
        testCL.addCalculator(testName, testLocation, testLanguage);
        assertTrue(testCL.len() == 1);
        assertTrue(testCL.getLanguage(0).equals(testLanguage));
        assertTrue(testCL.getLocation(0).equals(testLocation));
        assertTrue(testCL.getName(0).equals(testName));
        assertEquals(testCL.getLanguage(1), null);
        assertEquals(testCL.getLocation(1), null);
        assertEquals(testCL.getName(1), null);
        
        // second calculator
        String testName2 = "Nombre";
        String testLocation2 = "./test/";
        String testLanguage2 = "be";
        testCL.addCalculator(testName2, testLocation2, testLanguage2);
        assertTrue(testCL.len() == 2);
        assertTrue(testCL.getLanguage(0).equals(testLanguage));
        assertTrue(testCL.getLocation(0).equals(testLocation));
        assertTrue(testCL.getName(0).equals(testName));
        assertTrue(testCL.getLanguage(1).equals(testLanguage2));
        assertTrue(testCL.getLocation(1).equals(testLocation2));
        assertTrue(testCL.getName(1).equals(testName2));
        
        // underflow
        assertEquals(testCL.getLanguage(-1), null);
        assertEquals(testCL.getLocation(-1), null);
        assertEquals(testCL.getName(-1), null);
        
    }

}
