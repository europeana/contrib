package pt.utl.ist.repox.web.action.mapMetadata;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.RepoxConfiguration;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class XmlParser {
	private boolean useAttributes = true;

	public boolean isUseAttributes() {
		return useAttributes;
	}

	public void setUseAttributes(boolean useAttributes) {
		this.useAttributes = useAttributes;
	}

	public XmlParser() {
		super();
	}

	public XmlParser(boolean useAttributes) {
		super();
		this.useAttributes = useAttributes;
	}

	/**
	 * @return a list of Tags for the Dublin Core fields
	 * @throws IOException
	 */
	public List<Tag> getDcTags() {
		List<Tag> dcTags = new ArrayList<Tag>();
		dcTags.add(new Tag(null, "An entity primarily responsible for making the resource.", "dc:creator"));
		dcTags.add(new Tag(null, "An entity responsible for making contributions to the resource.", "dc:contributor"));
		dcTags.add(new Tag(null, "The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant.", "dc:coverage"));
		dcTags.add(new Tag(null, "A point or period of time associated with an event in the lifecycle of the resource.", "dc:date"));
		dcTags.add(new Tag(null, "An account of the resource.", "dc:description"));
		dcTags.add(new Tag(null, "The file format, physical medium, or dimensions of the resource.", "dc:format"));
		dcTags.add(new Tag(null, "An unambiguous reference to the resource within a given context.", "dc:identifier"));
		dcTags.add(new Tag(null, "A language of the resource.", "dc:language"));
		dcTags.add(new Tag(null, "An entity responsible for making the resource available.", "dc:publisher"));
		dcTags.add(new Tag(null, "A related resource.", "dc:relation"));
		dcTags.add(new Tag(null, "Information about rights held in and over the resource.", "dc:rights"));
		dcTags.add(new Tag(null, "A related resource from which the described resource is derived.", "dc:source"));
		dcTags.add(new Tag(null, "The topic of the resource.", "dc:subject"));
		dcTags.add(new Tag(null, "A name given to the resource.", "dc:title"));
		dcTags.add(new Tag(null, "The nature or genre of the resource.", "dc:type"));

		return dcTags;
	}

	/**
	 * Associates MarcXchange description to a list of parsedTags of a MarcXchange file
	 * @throws IOException
	 */
	public void associateMarcXchangeFields(List<TagParsed> parsedTags) throws IOException {
		Map<String, String> marcXchangeTagsMap = getMarcXchangeTagsMap();

		if(parsedTags != null) {
			for (TagParsed currentParsedTag : parsedTags) {
				currentParsedTag.setDescription("-"); // Set initially to prevent empty descriptions
				for (String currentMarcXchangeTag : marcXchangeTagsMap.keySet()) {
					if (currentParsedTag != null && currentParsedTag.getXpath() != null
							&& currentParsedTag.getXpath().indexOf(currentMarcXchangeTag) >= 0) {
						String marcXchangeTagNumber = currentMarcXchangeTag.substring(currentMarcXchangeTag.indexOf("'") + 1, currentMarcXchangeTag.length() - 1);
						currentParsedTag.setDescription(marcXchangeTagNumber + " - " + marcXchangeTagsMap.get(currentMarcXchangeTag));
						break;
					}
				}
			}
		}
	}

	private Map<String, String> getMarcXchangeTagsMap() throws IOException {
		Map<String, String> marcXchangeTagsMap = new HashMap<String, String>();
		File marcXchangeTagsFile = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getXmlConfigPath(),
				RepoxConfiguration.METADATA_TRANSFORMATIONS_DIRNAME + "/unimarcTags.txt");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(marcXchangeTagsFile));
		String currentLine;
		while ((currentLine = bufferedReader.readLine()) != null) {
			String[] parsedString = currentLine.split("\t");
			marcXchangeTagsMap.put(parsedString[0], parsedString[1]);
		}
		bufferedReader.close();

		return marcXchangeTagsMap;
	}

	public static String getCommonXpath(List<Tag> tagGroupAllTags) {
		if (tagGroupAllTags.size() < 2) {
			return "";
		}

		String[] referenceXpath = tagGroupAllTags.get(0).getXpath().trim().substring(1).split("/");
		int maxCommonElements = referenceXpath.length + 1;

		for (int i = 1; i < tagGroupAllTags.size(); i++) {
			String[] currentXpath = tagGroupAllTags.get(i).getXpath().trim().substring(1).split("/");
			int shortestPath = (referenceXpath.length < currentXpath.length ? referenceXpath.length : currentXpath.length);
			if (maxCommonElements > shortestPath + 1) {
				maxCommonElements = shortestPath + 1;
			}
			for (int j = 0; j < shortestPath; j++) {
				if (!currentXpath[j].equals(referenceXpath[j])) {
					maxCommonElements = (maxCommonElements < j + 1 ? maxCommonElements : j + 1);
				}
			}
		}

		String commonXpath = "";
		for (int i = 0; i < maxCommonElements - 1; i++) {
			commonXpath += "/" + referenceXpath[i];
		}

		return commonXpath;
	}

	public List<TagParsed> parseTags(File tempFile, List<TagParsed> parsedTags) throws DocumentException {
		Document document = new SAXReader().read(tempFile);

		return parseTags(document, parsedTags);
	}

	public List<TagParsed> parseTags(String recordString, List<TagParsed> parsedTags) throws DocumentException {
		Document document = DocumentHelper.parseText(recordString);

		return parseTags(document, parsedTags);
	}

	public List<TagParsed> parseTags(Document document, List<TagParsed> parsedTags){
		List<Element> elementsWithText = getAllElementsWithText(document.getRootElement());
		Map<String, TagParsed> parsedTagsMap = new TreeMap<String, TagParsed>();

		if(parsedTags != null) {
			for (TagParsed currentTag : parsedTags) {
				parsedTagsMap.put(currentTag.getXpath(), currentTag);
			}
		}

        for (Element currentElement : elementsWithText) {
            String elementPath = getFullPath(currentElement);
            TagParsed currentParsedTag = parsedTagsMap.get(elementPath);

            int tagPosition = -1;
            if (currentParsedTag != null) {
                tagPosition = parsedTags.indexOf(currentParsedTag);
                parsedTags.remove(tagPosition);
            }

            int numberOccurs = (currentParsedTag == null ? 0 : currentParsedTag.getOccurrences());
            if (numberOccurs > 0) {
                currentParsedTag.setXpath(elementPath);
                currentParsedTag.setOccurrences(numberOccurs + 1);
                if (numberOccurs < 5) {
                    currentParsedTag.setExamples(currentParsedTag.getExamples() + "; Ex: " + currentElement.getTextTrim());
                }
            } else {
                currentParsedTag = new TagParsed();
                currentParsedTag.setXpath(elementPath);
                currentParsedTag.setOccurrences(1);
                currentParsedTag.setExamples("Ex: " + currentElement.getTextTrim());
            }

            if (tagPosition == -1) {
//				if(parsedTags.size() < i) {
                parsedTags.add(currentParsedTag);
//				}
//				else {
//					parsedTags.add(i, currentParsedTag);
//				}
            } else {
                parsedTags.add(tagPosition, currentParsedTag);
            }
            parsedTagsMap.put(elementPath, currentParsedTag);
        }

//		return new ArrayList<TagParsed>(parsedTagsMap.values());
		return parsedTags;
	}

	private List<Element> getAllElementsWithText(Element element) {
		List<Element> allElements = new ArrayList<Element>();
		if (!element.getTextTrim().isEmpty()) {
			allElements.add(element);
		}

		for (Object currentChild : element.elements()) {
			allElements.addAll(getAllElementsWithText((Element) currentChild));
		}

		return allElements;
	}

	private String getFullPath(Element element) {
		if (element.getParent() != null) {
			return getFullPath(element.getParent()) + "/" + getElementIdentifier(element);
		} else {
			return "/" + element.getName();
		}
	}

	private String getElementIdentifier(Element element) {
		List<Attribute> attributes = getNonEmptyAttributes(element.attributes());

		if (useAttributes && !attributes.isEmpty()) {
			String elementName = element.getName() + "[";
			Collections.sort(attributes, new AttributeComparator());
			for (int i = 0; i < attributes.size(); i++) {
				Attribute currentAttribute = attributes.get(i);
				elementName += "@" + currentAttribute.getName();
				if (!currentAttribute.getText().isEmpty()) {
					elementName += "='" + currentAttribute.getText() + "'";
				}
				elementName += (i < (attributes.size() - 1) ? " and " : "");
			}
			elementName += "]";
			return elementName;
		} else {
			return element.getName();
		}
	}

	private List<Attribute> getNonEmptyAttributes(List<Attribute> attributes) {
		List<Attribute> nonEmptyAttributes = new ArrayList<Attribute>();

        for (Attribute currentAttribute : attributes) {
            if (currentAttribute.getText() != null && !currentAttribute.getText().trim().isEmpty()) {
                nonEmptyAttributes.add(currentAttribute);
            }
        }

		return nonEmptyAttributes;
	}

}

class XpathTagComparator implements Comparator<Tag> {
	public int compare(Tag tagA, Tag tagB) {
		return tagA.getXpath().compareTo(tagB.getXpath());
	}
}

class AttributeComparator implements Comparator<Attribute> {
	public int compare(Attribute attrA, Attribute attrB) {
		return attrA.getName().compareTo(attrB.getName());
	}
}
