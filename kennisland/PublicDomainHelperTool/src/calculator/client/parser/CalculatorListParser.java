package calculator.client.parser;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * 
 * @author mzeinstra
 * 
 */
public class CalculatorListParser {

    private Document parserDOM;
    private calculatorList calculators;

    public CalculatorListParser() {
        calculators = new calculatorList();
    }

    /**
     * Returns a parse list of possible calculators
     * 
     * @return
     */
    public calculatorList getCalculatorList() {
        return calculators;
    }

    /**
     * Main method that initiates parse method.
     * 
     * @param text
     */
    public void parse(String text) {
        calculators = new calculatorList();
        parserDOM = XMLParser.parse(text);

        if (parserDOM.getDocumentElement().getNodeName().equals("calculators")) {
            parseCalculatorList();
        }

    }

    /**
     * Parses the calculatorList XML file, starts from Root node in DOM
     */
    private void parseCalculatorList() {
        String name = "";
        String location = "";
        String language = "";
        NodeList questions = null;
        Node currentNode = null;
        String nodeName = "";
        String nodeValue = "";
        String dataLocation = "";

        // parse children of calculators (name, language, location)
        final NodeList calculatorNodes = parserDOM.getDocumentElement()
                .getChildNodes();

        for (int i = 0; i < calculatorNodes.getLength(); i++) {
            questions = calculatorNodes.item(i).getChildNodes();

            int complete = 0;
            for (int j = 0; j < questions.getLength(); j++) {

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

                if (nodeName.equals("name")) {
                    name = nodeValue;
                    complete++;
                } else if (nodeName.equals("location")) {
                    location = nodeValue;
                    complete++;
                } else if (nodeName.equals("language")) {
                    language = nodeValue;
                    complete++;
                } else if (nodeName.equals("dataLocation")) {
                    dataLocation = nodeValue;
                    complete++;
                }

            }
            if (complete > 1) {
                calculators.addCalculator(name, location, language,
                        dataLocation);
            }

        }
    }

}
