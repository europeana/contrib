package calculator.client.parser;

import java.util.ArrayList;
import java.util.HashMap;

import calculator.client.datainfrastructure.Codification;
import calculator.client.flowchartinfrastructure.Answer;
import calculator.client.flowchartinfrastructure.DoubleQuestion;
import calculator.client.flowchartinfrastructure.Question;
import calculator.client.flowchartinfrastructure.Result;
import calculator.client.flowchartinfrastructure.RootQuestion;
import calculator.client.flowchartinfrastructure.SingleQuestion;
import calculator.client.flowchartinfrastructure.multichoice;

import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * XML parser for internal XML files. <calculator> files
 * 
 * @author mzeinstra
 * 
 */
public class Parser {

    private static void addParamsToClass(final Node setting,
            final String currentClass) {
        String name = "";
        final ArrayList<String> options = new ArrayList<String>();

        for (int i = 0; i < setting.getChildNodes().getLength(); i++) {

            final Node currentNode = setting.getChildNodes().item(i);
            if (!validData(currentNode)) {
                continue;
            }

            final String nodeName = currentNode.getNodeName();
            final String nodeValue = currentNode.getFirstChild().getNodeValue()
                    .trim();
            if (nodeName.equals("name")) {
                name = nodeValue;
            } else if (nodeName.equals("option")) {
                options.add(nodeValue);
            } else {
                Window.alert(nodeName);
            }
        }

        if (!name.isEmpty() && options.size() > 0) {
            Codification.getInstance().addParameter(
                    Codification.getInstance().getCurrentClass(), name);
            for (final String option : options) {
                Codification.getInstance()
                        .addOption(currentClass, name, option);
            }
        }
        if (!name.isEmpty()) { // Name only, no options
            Codification.getInstance().addParameter(currentClass, name);
        }
    }

    private static void parseDataSchema(final Node root) {
        // Clear dataschema object
        final Codification c = Codification.getInstance();
        c.clear();

        // Initiate parser
        Node setting = null;
        Node classNode = null;
        String settingName = "";
        String nodeValue = "";

        final NodeList questionNodes = root.getChildNodes();

        // For each class in the XML file do:
        for (int i = 0; i < questionNodes.getLength(); i++) {
            classNode = questionNodes.item(i);

            if (!validData(classNode)) {
                continue;
            }

            for (int j = 0; j < classNode.getChildNodes().getLength(); j++) {
                setting = classNode.getChildNodes().item(j);

                if (!validData(setting)) {
                    continue;
                }
                if (setting.getFirstChild() == null) {
                    continue;
                }
                if (setting.getFirstChild().getNodeValue() == null) {
                    continue;
                }
                settingName = setting.getNodeName();
                nodeValue = setting.getFirstChild().getNodeValue().trim();
                if (settingName.equals("name")) {
                    Codification.getInstance().addClass(nodeValue);
                } else if (settingName.equals("param")) {
                    Parser.addParamsToClass(setting, Codification.getInstance()
                            .getCurrentClass());
                }
            }
        }

    }

    static boolean validData(final Node n) {
        // we are not interested in white space text elements
        if (n.getNodeType() != Node.ELEMENT_NODE) {
            return false;
        }

        // we are not interested in empty nodes (invalid data)
        if (n.getFirstChild() == null) {
            return false;
        }

        return true;
    }

    private Document parserDOM;

    private final HashMap<String, Question> questionList;

    public Parser() {
        questionList = new HashMap<String, Question>();
    }

    private void addAnswerToQuestion(final Node node, final Question q) {
        String text = "";
        String gotoNr = "";
        String information = "";

        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            final Node currentNode = node.getChildNodes().item(i);
            if (!validData(currentNode)) {
                continue;
            }

            final String nodeName = currentNode.getNodeName();
            final String nodeValue = currentNode.getFirstChild().getNodeValue()
                    .trim();
            if (nodeName.equals("value")) {
                text = nodeValue;
            } else if (nodeName.equals("gotoNr")) {
                gotoNr = nodeValue;
            } else if (nodeName.equals("information")) {
                information = nodeValue;
            }
        }
        if (!text.isEmpty() && !gotoNr.isEmpty()) {
            q.addNode(text, questionList.get(gotoNr), information);
        }
    }

    private Question getRoot() {
        if (questionList == null) {
            Window.alert("Questionlist is null");
            return null;
        }

        if (questionList.size() == 0) {
            Window.alert("Questionlist is empty");
            return null;
        }

        for (final Question isRoot : questionList.values()) {
            int parents = 0;
            for (final Question q : questionList.values()) {
                if (q.getAnswers() == null) {
                    continue;
                }
                for (final Answer a : q.getAnswers()) {
                    // If one of the answers refers to the isRoot question
                    if (a.getRedirect() == null) {
                        Window.alert("No Redirect at: " + q.getQuestionNr());
                    }

                    if (a.getRedirect().getQuestionNr().equals(
                            isRoot.getQuestionNr())) {
                        parents++;
                    }
                }
            }
            // If no parents are found, assume that Rootnode has been found.
            if (parents == 0) {
                return isRoot;
            }
        }
        // if circular tree.
        return null;
    }

    /**
     * Main method that initiates parse method and chooses specific parser,
     * depending on the type of file.
     * 
     * @param text
     * @throws Exception
     */
    public void parse(final String text) throws Exception {
        if (text.length() == 0) {
            return;
        }
        parserDOM = XMLParser.parse(text);

        if (parserDOM == null) {
            throw (new Exception("Incorrect XML formatting (1)"));
        }

        if (parserDOM.getDocumentElement().getNodeName().equals("calculator")) {
            final NodeList questionNodes = parserDOM.getDocumentElement()
                    .getChildNodes();

            for (int i = 0; i < questionNodes.getLength(); i++) {
                final Node setup = questionNodes.item(i);

                if (!validData(setup)) {
                    continue;
                }

                final String nodeName = setup.getNodeName();
                if (nodeName.equals("questions")) {
                    RootQuestion.getInstance().setRoot(parseQuestions(setup));
                } else if (nodeName.equals("dataSchema")) {
                    Parser.parseDataSchema(setup);
                }
            }
        } else {
            throw (new Exception("Incorrect XML formatting (2)"));
        }
    }

    private void parseDouble(final Node step) {
        Node currentNode = null;
        String nodeName = "";

        String number = "";
        String text = "";
        String information = "";
        String text1 = "";
        String information1 = "";
        String className = "";
        String paramName1 = "";
        String paramName2 = "";
        String pattern = "";
        int questionCount = 0;
        int informationCount = 0;
        int paramCount = 0;

        String nodeValue = "";
        number = step.getAttributes().getNamedItem("nr").getNodeValue().trim();
        for (int j = 0; j < step.getChildNodes().getLength(); j++) {
            currentNode = step.getChildNodes().item(j);

            if (!validData(currentNode)) {
                continue;
            }
            if (currentNode.getFirstChild() == null) {
                continue;
            }

            nodeName = currentNode.getNodeName();
            if (nodeName.equals("question")) {
                // node Result is always subdivided into a mandatory text and a
                // optional Information

                for (int QuestionIndex = 0; QuestionIndex < currentNode
                        .getChildNodes().getLength(); QuestionIndex++) {
                    final Node currentAnswer = currentNode.getChildNodes()
                            .item(QuestionIndex);

                    // we are not interested in white space text elements
                    if (currentAnswer.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    nodeName = currentAnswer.getNodeName();
                    if (currentAnswer.getFirstChild() != null) {
                        nodeValue = currentAnswer.getFirstChild()
                                .getNodeValue().trim();
                    } else {
                        nodeValue = "";
                    }

                    if (nodeName.equals("text") && !nodeValue.trim().isEmpty()) {
                        if (questionCount == 0) {
                            text = nodeValue;
                            questionCount++;
                        } else {
                            text1 = nodeValue;
                        }

                    } else if (nodeName.equals("information")
                            && !nodeValue.trim().isEmpty()) {
                        if (informationCount == 0) {
                            information = nodeValue;
                            informationCount++;
                        } else {
                            information1 = nodeValue;
                        }
                    } else if (nodeName.equals("param")
                            && !nodeValue.trim().isEmpty()) {

                        if (paramCount == 0) {
                            className = nodeValue.split("\\.")[0];
                            paramName1 = nodeValue.split("\\.")[1];
                            paramCount++;
                        } else {
                            className = nodeValue.split("\\.")[0];
                            paramName2 = nodeValue.split("\\.")[1];
                        }

                    }
                }
            } else if (nodeName.equals("evaluate")) {

                pattern = currentNode.getFirstChild().getNodeValue().trim();
            }

        }

        if (!number.isEmpty() && !text.isEmpty()) {
            final DoubleQuestion q = new DoubleQuestion(number, text, text1,
                    pattern);
            q.setClassName(className);
            q.setParameter1(paramName1);
            q.setParameter2(paramName2);
            q.setInformation(information, information1);
            q.setExpression(pattern);
            questionList.put(number, q);
        } else {
            Window.alert("Parsing went wrong at (double): " + step.toString()
                    + "(" + number + ", " + text + ',' + text1 + ")");
        }

    }

    private void parseMultiplechoice(final Node step) {

        Node currentNode = null;
        String nodeName = "";

        String number = "";
        String text = "";
        String information = "";
        String className = "";
        String paramName = "";

        number = step.getAttributes().getNamedItem("nr").getNodeValue().trim();
        for (int j = 0; j < step.getChildNodes().getLength(); j++) {
            currentNode = step.getChildNodes().item(j);

            if (!validData(currentNode)) {
                continue;
            }
            if (currentNode.getFirstChild() == null) {
                continue;
            }
            if (currentNode.getFirstChild().getNodeValue() == null) {
                continue;
            }
            nodeName = currentNode.getNodeName();
            String nodeValue = currentNode.getFirstChild().getNodeValue()
                    .trim();

            if (nodeName.equals("question")) {

                for (int QuestionIndex = 0; QuestionIndex < currentNode
                        .getChildNodes().getLength(); QuestionIndex++) {
                    final Node currentAnswer = currentNode.getChildNodes()
                            .item(QuestionIndex);

                    if (!validData(currentAnswer)) {
                        continue;
                    }

                    if (currentAnswer.getFirstChild().getNodeValue() == null) {
                        continue;
                    }
                    nodeName = currentAnswer.getNodeName();
                    nodeValue = currentAnswer.getFirstChild().getNodeValue()
                            .trim();

                    if (nodeName.equals("text") && !nodeValue.trim().isEmpty()) {
                        text = nodeValue;
                    } else if (nodeName.equals("information")
                            && !nodeValue.trim().isEmpty()) {
                        information = nodeValue;
                    } else if (nodeName.equals("param")
                            && !nodeValue.trim().isEmpty()) {
                        className = nodeValue.split("\\.")[0];
                        paramName = nodeValue.split("\\.")[1];
                    }
                }
            }
            if (!number.isEmpty() && !text.isEmpty()) {
                final multichoice q = new multichoice(number, text, null);
                q.setInformation(information);
                q.setClassName(className);
                q.setParameter(paramName);
                questionList.put(number, q);
            } else {
                // Window.alert("Parsing went wrong at (multiple): number(" +
                // number + "), text(" + text+ ")" + currentNode.toString());
            }
        }

    }

    /**
     * Parses questions encoded in XML, starts with the top most node of DOM
     * 
     * @param root
     *            root for questions
     * @return Returns the object that holds the root question
     */
    private Question parseQuestions(final Node root) {
        Node currentNode = null;
        Node step = null;
        String nodeName = "";
        String nodeValue = "";

        final NodeList questionNodes = root.getChildNodes();

        // Run 1: Get questions
        // For each step in the XML file do:
        for (int i = 0; i < questionNodes.getLength(); i++) {
            step = questionNodes.item(i);

            if (!validData(step)) {
                continue;
            }

            for (int j = 0; j < step.getChildNodes().getLength(); j++) {
                currentNode = step.getChildNodes().item(j);

                if (!validData(currentNode)) {
                    continue;
                }
                if (currentNode.getFirstChild() == null) {
                    continue;
                }
                if (currentNode.getFirstChild().getNodeValue() == null) {
                    continue;
                }
                nodeName = currentNode.getNodeName();
                nodeValue = currentNode.getFirstChild().getNodeValue().trim();
                if (nodeName.equals("type")) {
                    if (nodeValue.equals("single")) {
                        parseSingle(step);
                    } else if (nodeValue.equals("double")) {
                        parseDouble(step);
                    } else if (nodeValue.equals("multiplechoice")) {
                        parseMultiplechoice(step);
                    } else if (nodeValue.equals("result")) {
                        parseResult(step);
                    }
                }
            }
        }

        // Run 2: add answers and connect questions.
        for (int i = 0; i < questionNodes.getLength(); i++) {
            step = questionNodes.item(i);

            if (!validData(step)) {
                continue;
            }

            final String qNr = step.getAttributes().getNamedItem("nr")
                    .getNodeValue().trim();
            if (questionList.get(qNr) == null) {
                continue;
            }

            questionList.get(qNr).clearAnswers();
            for (int j = 0; j < step.getChildNodes().getLength(); j++) {
                currentNode = step.getChildNodes().item(j);

                if (!validData(currentNode)) {
                    continue;
                }

                nodeName = currentNode.getNodeName();
                if (nodeName.equals("answer")) {
                    addAnswerToQuestion(currentNode, questionList.get(qNr));
                    continue;
                }
            }
        }
        if (getRoot() == null) {
            Window.alert("No Root found");
        }
        return getRoot();
    }

    private void parseResult(final Node step) {
        /*
         * example: <step nr="3"> <type>result</type> <result> <text>Public
         * Domain</text> <information></information> </result> </step>
         */

        Node currentNode = null;
        String nodeName = "";
        String nodeValue = "";

        String number = "";
        String text = "";
        String information = "";

        number = step.getAttributes().getNamedItem("nr").getNodeValue().trim();
        for (int j = 0; j < step.getChildNodes().getLength(); j++) {
            currentNode = step.getChildNodes().item(j);

            if (!validData(currentNode)) {
                continue;
            }

            if (currentNode.getFirstChild() == null) {
                continue;
            }

            nodeName = currentNode.getNodeName();
            if (nodeName.equals("result")) {
                // node Result is always subdivided into a mandatory text and a
                // optional Information
                for (int QuestionIndex = 0; QuestionIndex < currentNode
                        .getChildNodes().getLength(); QuestionIndex++) {
                    final Node currentAnswer = currentNode.getChildNodes()
                            .item(QuestionIndex);

                    if (!validData(currentAnswer)) {
                        continue;
                    }

                    if (currentAnswer.getFirstChild().getNodeValue() == null) {
                        continue;
                    }
                    nodeName = currentAnswer.getNodeName();
                    nodeValue = currentAnswer.getFirstChild().getNodeValue()
                            .trim();

                    if (nodeName.equals("text") && !nodeValue.trim().isEmpty()) {
                        text = nodeValue;
                    } else if (nodeName.equals("information")
                            && !nodeValue.trim().isEmpty()) {
                        information = nodeValue;
                    }
                }
            }
        }

        if (!number.isEmpty() && !text.isEmpty()) {
            final Result q = new Result(number, text);
            q.setInformation(information);
            questionList.put(number, q);
        } else {
            Window.alert("Parsing went wrong at (result): " + step.toString());
        }

    }

    private void parseSingle(final Node step) {

        Node currentNode = null;
        String nodeName = "";

        String number = "";
        String text = "";
        String information = "";
        String className = "";
        String paramName = "";
        String pattern = "";

        String nodeValue = "";

        number = step.getAttributes().getNamedItem("nr").getNodeValue().trim();
        for (int j = 0; j < step.getChildNodes().getLength(); j++) {
            currentNode = step.getChildNodes().item(j);

            if (!validData(currentNode)) {
                continue;
            }
            if (currentNode.getFirstChild() == null) {
                continue;
            }

            nodeName = currentNode.getNodeName();
            if (nodeName.equals("question")) {
                // node Result is always subdivided into a mandatory text and a
                // optional Information and Parameter
                for (int QuestionIndex = 0; QuestionIndex < currentNode
                        .getChildNodes().getLength(); QuestionIndex++) {
                    final Node currentAnswer = currentNode.getChildNodes()
                            .item(QuestionIndex);

                    if (!validData(currentNode)) {
                        continue;
                    }

                    if (currentAnswer.getFirstChild() == null) {
                        continue;
                    }

                    if (currentAnswer.getFirstChild().getNodeValue() == null) {
                        continue;
                    }
                    nodeName = currentAnswer.getNodeName();
                    nodeValue = currentAnswer.getFirstChild().getNodeValue()
                            .trim();

                    if (nodeName.equals("text") && !nodeValue.trim().isEmpty()) {
                        text = nodeValue;
                    } else if (nodeName.equals("information")
                            && !nodeValue.trim().isEmpty()) {
                        information = nodeValue;
                    } else if (nodeName.equals("param")
                            && !nodeValue.trim().isEmpty()) {
                        className = nodeValue.split("\\.")[0];
                        paramName = nodeValue.split("\\.")[1];
                    }
                }
            } else if (nodeName.equals("evaluate")) {
                pattern = currentNode.getFirstChild().getNodeValue().trim();
            }
        }

        if (!number.isEmpty() && !text.isEmpty()) {
            final SingleQuestion q = new SingleQuestion(number, text, pattern);
            q.setInformation(information);
            q.setClassName(className);
            q.setParameter(paramName);
            questionList.put(number, q);
        } else {
            Window.alert("Parsing went wrong at (single): " + step.toString()
                    + "(" + number + ", " + text + ")");
        }

    }
}
