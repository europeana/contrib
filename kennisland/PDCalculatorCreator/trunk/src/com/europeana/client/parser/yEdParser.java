package com.europeana.client.parser;

import java.util.HashMap;

import com.europeana.client.flowchartinfrastructure.Question;
import com.europeana.client.flowchartinfrastructure.Result;
import com.europeana.client.flowchartinfrastructure.multichoice;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;


/**
 * Parses an yEd created Flowchart in xgml to the local structure made in the package flowchartinfrastructure.
 * Useful for initial uploads.
 * @author mzeinstra
 *
 */
public class yEdParser {
    private final HashMap<String, yEdQuestion> questionList = new HashMap<String, yEdQuestion>();
    
    
    public Question getCalculator (final String xml) {
        final Document parserDOM = XMLParser.parse(xml);
        int count = 0;
        Node currentNode = null;
        Node questions = null;
        Node step = null;
        final StringBuilder stringBuilder = new StringBuilder();
        questions = parserDOM.getDocumentElement();
        stringBuilder.append(questions.getChildNodes().getLength());
        
        
        // For each step in the XML file do:
        
        for (int i = 0; i < questions.getChildNodes().getLength(); i++){
            step = questions.getChildNodes().item(i);
            
            // we are not interested in white space text elements
            if (step.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
            }
            // we are not interested in empty nodes (invalid data)
            if (step.getFirstChild() == null) {
                    continue;
            } 
            if (step.getAttributes() == null) {
                continue;
            }
            if (step.getAttributes().getNamedItem("name") == null) {
                continue;
            }
            
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
                
                if (currentNode.getAttributes() == null) {
                    continue;
                }
                if (currentNode.getAttributes().getNamedItem("name") == null) {
                    
                    continue;
                }
                else {
                    stringBuilder.append(currentNode.getAttributes().toString());
                }
                               
                if (currentNode.getAttributes().getNamedItem("name").getNodeValue().equals("node")){
                    this.parseNode(currentNode);
                    count++;
                } else if (currentNode.getAttributes().getNamedItem("name").getNodeValue().equals("edge")) {
                    this.parseEdge(currentNode);
                }
            }
        }

        return this.convertToInfraStructure();

    }

    private void parseNode(final Node node) {
        final yEdQuestion tmpQuestion = new yEdQuestion();
        tmpQuestion.setQuestion("");
        
        String nodeValue = "";
        String nodeName = "";
        Node currentNode = null;
        
        for (int j = 0; j < node.getChildNodes().getLength(); j++){
            currentNode = node.getChildNodes().item(j);

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
            nodeValue = nodeValue.replace("\n", " ");
            nodeValue = nodeValue.replace("  ", " ");
            
            if (nodeName.equals("attribute")) {
                if (currentNode.getAttributes().getNamedItem("key").getNodeValue().equals("label")){
                    tmpQuestion.setQuestion(nodeValue);
                } else if (currentNode.getAttributes().getNamedItem("key").getNodeValue().equals("id")) {
                    tmpQuestion.setQuestionNr(nodeValue);
                }
            }
            
        }
        if (!tmpQuestion.getQuestion().isEmpty() && !tmpQuestion.getQuestionNr().isEmpty()) {
            this.questionList.put(tmpQuestion.getQuestionNr(), tmpQuestion);
        }        
    }
    
    private void parseEdge(final Node node) {
        yEdQuestion tmpQuestion = new yEdQuestion();
        String nodeValue = "";
        String nodeName = "";
        
        String source = "";
        String label = "";
        String target = "";
        Node currentNode = null;
        
        for (int j = 0; j < node.getChildNodes().getLength(); j++){
            currentNode = node.getChildNodes().item(j);

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
            nodeValue = nodeValue.replace("\n", " ");
            nodeValue = nodeValue.replace("  ", " ");
            
            if (nodeName.equals("attribute")) {
                if (currentNode.getAttributes().getNamedItem("key").getNodeValue().equals("source")){
                    source = nodeValue;
                } else if (currentNode.getAttributes().getNamedItem("key").getNodeValue().equals("target")) {
                    target = nodeValue;
                } else if (currentNode.getAttributes().getNamedItem("key").getNodeValue().equals("label")) {
                    label = nodeValue;
                }
            }            
        }
        
        
        if (!source.isEmpty() && !target.isEmpty() && !label.isEmpty()) {
            tmpQuestion = this.questionList.get(source);
   
            if (tmpQuestion != null) {
                tmpQuestion.addAnswer(label, target);
            } else {
                Window.alert("Not Found:" + source);
            }
            
            this.questionList.put(source, tmpQuestion);
        }        
    }
    
    /**
     * Returns the parsed information as text.
     * @return
     */
    public String toText() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String key: this.questionList.keySet()) {
            stringBuilder.append(this.questionList.get(key).getQuestionNr());
            stringBuilder.append(": ");
            stringBuilder.append(this.questionList.get(key).getQuestion());
            stringBuilder.append("\n");
            for (final String answerKey: this.questionList.get(key).getAnswers().keySet()) {
                stringBuilder.append("\t answer ");
                stringBuilder.append(answerKey);
                stringBuilder.append(": ");
                stringBuilder.append(this.questionList.get(key).getAnswers().get(answerKey));
                stringBuilder.append("\n");
            }
            
        }
        return stringBuilder.toString();
    }
    
    private Question convertToInfraStructure() {
        String root = "";
        Window.alert(Integer.toString((this.questionList.size())));
        for (final String key: this.questionList.keySet()) {
            if (isRoot(key)) {
                root = key;
                break;
            }
        }
        return this.build(root);
    }
    
    private Question build(String rootkey) {
        yEdQuestion tmpQuestion = this.questionList.get(rootkey);
        // multiplechoice or result
        if (tmpQuestion == null) {
            Window.alert("No question found at rootkey: " + rootkey);
            return null;
        }
        if(tmpQuestion.getAnswers().size() > 0) {
            // multiplechoice
            final multichoice question = new multichoice(tmpQuestion.getQuestionNr(), tmpQuestion.getQuestion(), null);
            for (final String text: tmpQuestion.getAnswers().keySet()) {
                question.addNode(text, build(tmpQuestion.getAnswers().get(text)), "");
            }
            return question;
        } else {
            // result
            return new Result(tmpQuestion.getQuestionNr(), tmpQuestion.getQuestion());
        }
    }

    private boolean isRoot(String key) {
        for (final String index: this.questionList.keySet()) {
            if (this.questionList.get(index).getAnswers().containsValue(key)) {
                return false;
            } 
        }
        // check if orphaned (like a copyright mark)
        if (this.questionList.get(key).getAnswers().size() == 0) {
            return false;
        }
        return true;
    }
}

