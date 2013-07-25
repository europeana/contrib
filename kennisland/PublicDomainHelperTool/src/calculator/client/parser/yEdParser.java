package calculator.client.parser;

import java.util.HashMap;

import calculator.client.flowchartinfrastructure.Question;
import calculator.client.flowchartinfrastructure.Result;
import calculator.client.flowchartinfrastructure.multichoice;

import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * Parses an yEd created Flowchart in xgml to the local structure made in the
 * package flowchartinfrastructure. Useful for initial uploads.
 * 
 * @author mzeinstra
 * 
 */
public class yEdParser {
    private HashMap<String, yEdQuestion> questionList = new HashMap<String, yEdQuestion>();

    private Question build(String rootkey) throws Exception {
        final yEdQuestion tmpQuestion = questionList.get(rootkey);
        // multiplechoice or result
        if (tmpQuestion == null) {
            Window.alert("Exception thrown");
            throw (new Exception("No correctly formatted flowchart found."));
        }
        if (tmpQuestion.getAnswers().size() > 0) {
            // multiplechoice
            final multichoice question = new multichoice(tmpQuestion
                    .getQuestionNr(), tmpQuestion.getQuestion(), null);
            for (final String key : tmpQuestion.getAnswers().keySet()) {
                question.addNode(tmpQuestion.getAnswers().get(key), build(key),
                        "");
            }
            return question;
        } else {
            // result
            return new Result(tmpQuestion.getQuestionNr(), tmpQuestion
                    .getQuestion());
        }
    }

    private Question convertToInfraStructure() throws Exception {
        String root = "";
        for (final String key : questionList.keySet()) {
            if (isRoot(key)) {
                root = key;
                break;
            }
        }
        final Question result = build(root);
        return result;
    }

    public Question getCalculator(final String xml) throws Exception {
        if (xml.length() == 0) {
            return null;
        }
        questionList = new HashMap<String, yEdQuestion>();
        final Document parserDOM = XMLParser.parse(xml);
        Node currentNode = null;
        Node questions = null;
        Node step = null;
        final StringBuilder stringBuilder = new StringBuilder();
        questions = parserDOM.getDocumentElement();
        stringBuilder.append(questions.getChildNodes().getLength());

        // For each step in the XML file do:

        try {

            for (int i = 0; i < questions.getChildNodes().getLength(); i++) {
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

                for (int j = 0; j < step.getChildNodes().getLength(); j++) {
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
                    } else {
                        stringBuilder.append(currentNode.getAttributes()
                                .toString());
                    }

                    if (currentNode.getAttributes().getNamedItem("name")
                            .getNodeValue().equals("node")) {
                        parseNode(currentNode);
                    } else if (currentNode.getAttributes().getNamedItem("name")
                            .getNodeValue().equals("edge")) {
                        parseEdge(currentNode);
                    }
                }
            }
        } catch (final Exception e) {
            throw new Exception("Parsing error: " + e.getMessage());
        }

        return convertToInfraStructure();

    }

    private boolean isRoot(String key) {
        for (final String index : questionList.keySet()) {
            if (questionList.get(index).getAnswers().containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    private void parseEdge(final Node node) throws Exception {
        try {
            yEdQuestion tmpQuestion = new yEdQuestion();
            String nodeValue = "";
            String nodeName = "";

            String source = "";
            String label = "";
            String target = "";
            Node currentNode = null;

            for (int j = 0; j < node.getChildNodes().getLength(); j++) {
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

                if (nodeName.equals("attribute")) {
                    if (currentNode.getFirstChild().getNodeValue() != null) {
                        nodeValue = currentNode.getFirstChild().getNodeValue()
                                .trim();

                        if (currentNode.getAttributes().getNamedItem("key")
                                .getNodeValue().equals("source")) {
                            source = nodeValue;
                        } else if (currentNode.getAttributes().getNamedItem(
                                "key").getNodeValue().equals("target")) {
                            target = nodeValue;
                        } else if (currentNode.getAttributes().getNamedItem(
                                "key").getNodeValue().equals("label")) {
                            label = nodeValue;
                        }
                    }
                }
            }

            if (!source.isEmpty() && !target.isEmpty() && !label.isEmpty()) {
                tmpQuestion = questionList.get(source);
                if (tmpQuestion != null) {
                    tmpQuestion.addAnswer(target, label);
                } else {
                    Window.alert("Not Found:" + source);
                }
                // this.questionList.remove(source);

                questionList.put(source, tmpQuestion);
            }
        } catch (final Exception e) {
            throw new Exception("edgeParsing failed: " + e.getMessage());
        }
    }

    private void parseNode(final Node node) throws Exception {
        if (node == null) {
            return;
        }
        if (node.getChildNodes() == null) {
            return;
        }
        try {
            final yEdQuestion tmpQuestion = new yEdQuestion();
            tmpQuestion.setQuestion("");

            String nodeValue = "";
            String nodeName = "";
            Node currentNode = null;

            for (int j = 0; j < node.getChildNodes().getLength(); j++) {
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

                if (nodeName.equals("attribute")) {
                    if (currentNode.getFirstChild().getNodeValue() != null) {
                        nodeValue = currentNode.getFirstChild().getNodeValue()
                                .trim();
                        if (currentNode.getAttributes().getNamedItem("key")
                                .getNodeValue().equals("label")) {
                            tmpQuestion.setQuestion(nodeValue);
                        } else if (currentNode.getAttributes().getNamedItem(
                                "key").getNodeValue().equals("id")) {
                            tmpQuestion.setQuestionNr(nodeValue);
                        }
                    }
                }

            }
            if (!tmpQuestion.getQuestion().isEmpty()
                    && !tmpQuestion.getQuestionNr().isEmpty()) {
                questionList.put(tmpQuestion.getQuestionNr(), tmpQuestion);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw new Exception("parseNode failed: " + e.getMessage());

        }

    }

    /**
     * Returns the parsed information as text.
     * 
     * @return
     */
    public String toText() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String key : questionList.keySet()) {
            stringBuilder.append(questionList.get(key).getQuestionNr());
            stringBuilder.append(": ");
            stringBuilder.append(questionList.get(key).getQuestion());
            stringBuilder.append("\n");
            for (final String answerKey : questionList.get(key).getAnswers()
                    .keySet()) {
                stringBuilder.append("\t answer ");
                stringBuilder.append(answerKey);
                stringBuilder.append(": ");
                stringBuilder.append(questionList.get(key).getAnswers().get(
                        answerKey));
                stringBuilder.append("\n");
            }

        }
        return stringBuilder.toString();
    }
}
