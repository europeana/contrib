package calculator.client;


import java.util.HashMap;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

//ESCA-JAVA0166:
/**
 * 
 * @author mzeinstra
 *
 */
public class PDCParser {
    private Document parserDOM;
    private calculatorList calculators = new calculatorList();
    private HashMap<String, Question> questionList = new HashMap<String, Question>();
    
    PDCParser()
    {
        
    }
    /**
     * 
     */
    private void parseQuestions(){
        String questionNr = "-1";
        Node currentNode = null;
        Node step = null;
        String nodeName = "";
        String nodeValue = "";
        Question tmpQuestion = null;
        
        NodeList questionNodes = parserDOM.getDocumentElement().getChildNodes();
        
        
        // For each step in the XML file do:
        for (int i = 0; i < questionNodes.getLength(); i++){
            step = questionNodes.item(i);
            
            // we are not interested in white space text elements
            if (step.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
            }
            // we are not interested in empty nodes (invalid data)
            if (step.getFirstChild() == null) {
                    continue;
            } 
            
            try{
                questionNr = questionNodes.item(i).getAttributes().toString();
            }
            catch (Exception e) {
                questionNr = "-1";
            }
            
            if (questionNr.equals("-1")){
                continue;
            }
            
            tmpQuestion = new Question();
            for (int j = 0; j < step.getChildNodes().getLength(); j++){
                currentNode = step.getChildNodes().item(j);

                // we are not interested in white space text elements
                if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                }
                // we are not interested in empty nodes (invalid data)
                if (currentNode.getFirstChild() == null) {
                        continue;
                } 
                
                
                nodeName = currentNode.getNodeName();
                nodeValue = currentNode.getFirstChild().getNodeValue().trim();
                if (nodeName.equals("type")) {
                    tmpQuestion.setType(nodeValue);
                }else if (nodeName.equals("evaluate")){
                    tmpQuestion.setEvaluate(nodeValue);
                }else if (nodeName.equals("result")){
                        tmpQuestion.addQuestion(nodeValue);
                }else if (nodeName.equals("question"))
                {
                    // parse question
                    tmpQuestion = parseQuestion(currentNode,tmpQuestion);
                }else if (nodeName.equals("answer")){
                    // parse answer
                    tmpQuestion = parseAnswer(currentNode,tmpQuestion);
                    
                }
            }
            try{
                if (!tmpQuestion.getQuestion().get(0).equals("")){
                    questionList.put(questionNr, tmpQuestion);
                    //tmpQuestion = new Question();
                }
            }catch (Exception e){
                Window.alert("Parse Error:" + e.getMessage());
            }
        }
        
    }
    
    /**
     * 
     */
    private void parseCalculatorList(){
        String name = "";
        String location = "";
        String language = "";
        NodeList questions = null;
        Node currentNode = null;
        String nodeName = "";
        String nodeValue = "";

        
        // parse children of calculators (name, language, location)
        NodeList calculatorNodes = parserDOM.getDocumentElement().getChildNodes();

        for (int i = 0 ; i < calculatorNodes.getLength(); i++){
            questions = calculatorNodes.item(i).getChildNodes();
            
            int complete = 0;
            for (int j=0; j < questions.getLength(); j++){
            
                currentNode = questions.item(j);
                
                // we are not interested in white space text elements
                if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                }
                
                nodeName = currentNode.getNodeName();
                
                // we are not interested in empty nodes (invalid data)
                if (currentNode.getFirstChild() == null) {
                        continue;
                } 

                nodeValue = currentNode.getFirstChild().getNodeValue().trim(); 

                if (nodeName.equals("name")){
                    name = nodeValue;
                    complete++;
                }else if (nodeName.equals("location")){
                    location = nodeValue;
                    complete++;
                }else if (nodeName.equals("language"))
                {
                    language = nodeValue;
                    complete++;
                }
                if (complete == 3){
                    calculators.addCalculator(name, location, language);
                    complete = 0;
                }

            }
            
        }
    }
    
    // parse question
    public Question parseQuestion(Node currentNode, Question tmpQuestion)
    {
        
        Node currentAnswer = null;
        String nodeName = "";
        String nodeValue = "";
        
        // node question is always subdivided into a mandatory text and a optional APIparam
        for (int QuestionIndex=0; QuestionIndex < currentNode.getChildNodes().getLength(); QuestionIndex++){
            currentAnswer = currentNode.getChildNodes().item(QuestionIndex);
            
            // we are not interested in white space text elements
            if (currentAnswer.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
            }
            
            nodeName = currentAnswer.getNodeName();
            
            // we are not interested in empty nodes (invalid data)
            if (currentAnswer.getFirstChild() == null) {
                    continue;
            }
            
            nodeValue = currentAnswer.getFirstChild().getNodeValue().trim();
            
            if (nodeName.equals("text")){
                 if (!nodeValue.trim().isEmpty()){
                     //Window.alert("question: " + nodeValue );
                     tmpQuestion.addQuestion(nodeValue);
                 }      
            } else if (nodeName.equals("information")){
                tmpQuestion.setInformation(nodeValue);
            }
        }
        return tmpQuestion;
    }
    
    private static Question parseAnswer(Node currentNode, Question tmpQuestion)
    {
        // For each possible answer
        String redirect = "";
        String AnswerValue = "";
        String AnswerInformation = "";
        String nodeName = "";
        String nodeValue = "";
        Node currentAnswer = null;
        for (int AnswerIndex=0; AnswerIndex < currentNode.getChildNodes().getLength(); AnswerIndex++){
            currentAnswer = currentNode.getChildNodes().item(AnswerIndex);
            
            // we are not interested in white space text elements
            if (currentAnswer.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
            }
            
            nodeName = currentAnswer.getNodeName();
            
            // we are not interested in empty nodes (invalid data)
            if (currentAnswer.getFirstChild() == null) {
                    continue;
            }
            
            nodeValue = currentAnswer.getFirstChild().getNodeValue().trim();
            
             if (nodeName.equals("gotoNr")){
                 redirect = nodeValue;
             }else if (nodeName.equals("value")){
                 AnswerValue = nodeValue;
             }else if (nodeName.equals("information")){
                 AnswerInformation = nodeValue;
             }
        
        }
        try{
            if (redirect != null && redirect.length() != 0 && AnswerValue != null && AnswerValue.length() != 0){
                tmpQuestion.addAnswer(new Answer(AnswerValue, redirect, AnswerInformation));
            }
        } catch (Exception e)
        {
            Window.alert("exception processing questions:" + e.getMessage());
        }
        return tmpQuestion;
    }
    
    /**
     * 
     * @param text
     */
    public void parse(String text) {
        parserDOM = XMLParser.parse(text);
        
        if (parserDOM.getDocumentElement().getNodeName().equals("calculators")) {
            parseCalculatorList();
        }
        else if  (parserDOM.getDocumentElement().getNodeName().equals("questions"))
        {
            parseQuestions();
        }
        else
        {
            Window.alert("no node element could be found, trying to find: " + parserDOM.getDocumentElement().getNodeName());
        }
        
    }
        
    /**
     * 
     * @return
     */
    public calculatorList getCalculatorList(){
        return calculators;
    }
        
    /**
     * 
     * @param question
     * @return
     */
    public Question getQuestion (String question){
        // search question routine
        if (questionList.containsKey(question)){
            return questionList.get(question);
        }
        return null;
    }

    
}