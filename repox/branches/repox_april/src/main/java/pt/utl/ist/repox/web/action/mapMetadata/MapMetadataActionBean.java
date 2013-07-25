package pt.utl.ist.repox.web.action.mapMetadata;

import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import pt.utl.ist.repox.dataProvider.DataProviderManager;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.metadataTransformation.ManualMetadataTransformationManager;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.util.RepoxContextUtil;
import pt.utl.ist.repox.web.action.RepoxActionBean;
import pt.utl.ist.util.FileUtil;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MapMetadataActionBean extends RepoxActionBean {
	private static final Logger log = Logger.getLogger(MapMetadataActionBean.class);
	private static final String SAMPLE_FILENAME = "sample.xml";
	private static final String ID_REGULAR_EXPRESSION = "[a-zA-Z][a-zA-Z_0-9]*";
	private static final String[] SESSION_ATTRIBUTES = { "allMetadataTransformations", "editing", "dcTags", "parsedTags",
		"dcMappings", "sampleNamespace", "tagGroup", "tagGroupAllTags", "tagPrefixes", "mappedTags", "tempFilename", "sampleXml",
		"resultXslt", "resultTransformation", "metadataTransformation", "otherDestinationFormat", "rootElement", "namespaces" };

	private Boolean editing;
	private List<Tag> dcTags;
	private FileBean sampleFile1;
	private List<TagParsed> parsedTags;
	private Map<String, Set<String>> dcMappings;
	private String sampleNamespace;

	private TagGroup tagGroup;
	private List<Tag> tagGroupAllTags;
	private List<String> tagPrefixes;
	private Tag newDcTag;
	private TagParsed newParsedTag;

	private int tagGroupPos;

	// Selections
	private String mappedTags;
	private List<String> selectedTags;

	// Results
	private String tempFilename;
	private String sampleXml;
	private String resultXslt;
	private String resultTransformation;
	private String otherDestinationFormat;
	private String rootElement;
	private String namespaces;

	// Transformation data
	private List<MetadataTransformation> allMetadataTransformations;
	private MetadataTransformation metadataTransformation;

	public Boolean getEditing() {
		return editing;
	}

	public void setEditing(Boolean editing) {
		this.editing = editing;
	}

	public List<Tag> getDcTags() {
		return dcTags;
	}

	public void setDcTags(List<Tag> dcTags) {
		this.dcTags = dcTags;
	}

	public FileBean getSampleFile1() {
		return sampleFile1;
	}

	public void setSampleFile1(FileBean sampleFile1) {
		this.sampleFile1 = sampleFile1;
	}

	public List<TagParsed> getParsedTags() {
		return parsedTags;
	}

	public void setParsedTags(List<TagParsed> parsedTags) {
		this.parsedTags = parsedTags;
	}

	public String getSampleNamespace() {
		return sampleNamespace;
	}

	public void setSampleNamespace(String sampleNamespace) {
		this.sampleNamespace = sampleNamespace;
	}

	public TagGroup getTagGroup() {
		return tagGroup;
	}

	public void setTagGroup(TagGroup tagGroup) {
		this.tagGroup = tagGroup;
	}

	public List<Tag> getTagGroupAllTags() {
		return tagGroupAllTags;
	}

	public void setTagGroupAllTags(List<Tag> tagGroupAllTags) {
		this.tagGroupAllTags = tagGroupAllTags;
	}

	public Tag getNewDcTag() {
		return newDcTag;
	}

	public void setNewDcTag(Tag newDcTag) {
		this.newDcTag = newDcTag;
	}

	public TagParsed getNewParsedTag() {
		return newParsedTag;
	}

	public void setNewParsedTag(TagParsed newParsedTag) {
		this.newParsedTag = newParsedTag;
	}

	public List<String> getTagPrefixes() {
		return tagPrefixes;
	}

	public void setTagPrefixes(List<String> tagPrefixes) {
		this.tagPrefixes = tagPrefixes;
	}

	public int getTagGroupPos() {
		return tagGroupPos;
	}

	public void setTagGroupPos(int tagGroupPos) {
		this.tagGroupPos = tagGroupPos;
	}

	public Map<String, Set<String>> getDcMappings() {
		return dcMappings;
	}

	public void setDcMappings(Map<String, Set<String>> dcMappings) {
		this.dcMappings = dcMappings;
	}

	public String getMappedTags() {
		return mappedTags;
	}

	public void setMappedTags(String mappedTags) {
		this.mappedTags = mappedTags;
	}

	public List<String> getSelectedTags() {
		return selectedTags;
	}

	public void setSelectedTags(List<String> selectedTags) {
		this.selectedTags = selectedTags;
	}

	public String getTempFilename() {
		return tempFilename;
	}

	public void setTempFilename(String tempFilename) {
		this.tempFilename = tempFilename;
	}

	public String getSampleXml() {
		return sampleXml;
	}

	public void setSampleXml(String sampleXml) {
		this.sampleXml = sampleXml;
	}

	public String getResultXslt() {
		return resultXslt;
	}

	public void setResultXslt(String resultXslt) {
		this.resultXslt = resultXslt;
	}

	public String getResultTransformation() {
		return resultTransformation;
	}

	public void setResultTransformation(String resultTransformation) {
		this.resultTransformation = resultTransformation;
	}

	public List<MetadataTransformation> getAllMetadataTransformations() {
		return allMetadataTransformations;
	}

	public void setAllMetadataTransformations(List<MetadataTransformation> allMetadataTransformations) {
		this.allMetadataTransformations = allMetadataTransformations;
	}

	public String getOtherDestinationFormat() {
		return otherDestinationFormat;
	}

	public void setOtherDestinationFormat(String otherDestinationFormat) {
		this.otherDestinationFormat = otherDestinationFormat;
	}

	public String getRootElement() {
		return rootElement;
	}

	public void setRootElement(String rootElement) {
		this.rootElement = rootElement;
	}

	public String getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(String namespaces) {
		this.namespaces = namespaces;
	}

	public MetadataTransformation getMetadataTransformation() {
		return metadataTransformation;
	}

	public void setMetadataTransformation(MetadataTransformation metadataTransformation) {
		this.metadataTransformation = metadataTransformation;
	}

	public File getTempFile() {
		if (tempFilename == null) {
			Random generator = new Random(new Date().getTime());
			tempFilename = Math.abs(generator.nextInt()) + "temp.xml";
		}
		return new File(RepoxContextUtil.getRepoxManager().getConfiguration().getTempDir(), tempFilename);
	}

	public MapMetadataActionBean() {
		super();
	}

	/**
	 * Loads all the variables defined in sessionAttributes from the session if they are not sent in the current request
	 *
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void loadSessionVariables() throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		for (String currentVariable : SESSION_ATTRIBUTES) {
			Field declaredField = this.getClass().getDeclaredField(currentVariable);

			boolean parameterSent = false;
			if (this.getContext().getRequest().getParameter(currentVariable) != null) {
				parameterSent = true;
			} else {
				for (Object parameter : this.getContext().getRequest().getParameterMap().keySet()) {
					String currentParameter = (String) parameter;
					if(currentParameter.startsWith(currentVariable + ".")
							|| currentParameter.startsWith(currentVariable + "[")) {
						parameterSent = true;
						break;
					}
				}
			}

			if (!parameterSent) {
				declaredField.set(this, this.getContext().getRequest().getSession().getAttribute(currentVariable));
			}
		}
	}

	/**
	 * Saves all the variables defined in sessionAttributes to Session
	 *
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void saveSessionVariables() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		for (String currentVariable : SESSION_ATTRIBUTES) {
			Field declaredField = this.getClass().getDeclaredField(currentVariable);
			this.getContext().getRequest().getSession().setAttribute(currentVariable, declaredField.get(this));
		}
	}

	/**
	 * Resets all variables defined in sessionAttributes to Session
	 *
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void resetSessionVariables() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		for (String currentVariable : SESSION_ATTRIBUTES) {
			Field declaredField = this.getClass().getDeclaredField(currentVariable);
			declaredField.set(this, null);
			this.getContext().getRequest().getSession().setAttribute(currentVariable, null);
		}
	}

	private void saveTransformation() throws DocumentException, IOException {
		// Create a folder with name equal to this metadata mapping identifier
		File metadataMappingDir = getMetadataMappingDir();
		metadataMappingDir.mkdir();

		// Write XSLT
		File xsltFile = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltFile(metadataTransformation.getId());
		Document document = DocumentHelper.parseText(resultXslt);
		XMLWriter writer = new XMLWriter(new FileWriter(xsltFile), OutputFormat.createPrettyPrint());
		writer.write(document);
		writer.close();

		// Move sample file to mappings folder
		File sampleFile = getTempFile();
		sampleFile.renameTo(new File(metadataMappingDir, SAMPLE_FILENAME));

		// Save mappings for edition purposes in an XML (with dcMappings)
		// Save dc tags, extracted tags (use indexes for both) and save mappings
		// (by index)
		// <mapping>
		// <dc>
		// <tag index="0" description="Short description of the
		// field."><!--CDATA /dc:identifier
		// <extracted>
		// <tag index="0" occurrences="1"><!--CDATA
		// /record/controldata[@tag='100']
		// <relations>
		// <relation><dc>0</dc><extracted>0</extracted>
		// ...
		Document mappingsDocument = DocumentHelper.createDocument(DocumentHelper.createElement("mappings"));
		Element rootElement = mappingsDocument.getRootElement();

		Element dcElement = rootElement.addElement("dc");
		int i = 0;
		for (Tag currentDcTag : dcTags) {
			Element currentDcElement = dcElement.addElement("tag");
			currentDcElement.addAttribute("index", String.valueOf(i));
			currentDcElement.addAttribute("description", currentDcTag.getDescription());
			currentDcElement.add(DocumentHelper.createCDATA(currentDcTag.getXpath()));
			i++;
		}

		Element extractedTagsElement = rootElement.addElement("parsed");
		int j = 0;
		for (TagParsed currentExtractedTag : parsedTags) {
			Element currentExtractedTagElement = extractedTagsElement.addElement("tag");
			currentExtractedTagElement.addAttribute("index", String.valueOf(j));
			currentExtractedTagElement.addAttribute("occurrences", String.valueOf(currentExtractedTag.getOccurrences()));
			currentExtractedTagElement.addElement("examples").setText(currentExtractedTag.getExamples() == null ? "" : currentExtractedTag.getExamples());
			currentExtractedTagElement.add(DocumentHelper.createCDATA(currentExtractedTag.getXpath()));
			j++;
		}

		Element relationsElement = rootElement.addElement("relations");
		String[] mappedTagsArray = (mappedTags != null ? mappedTags.split(",")
				: null);
		if (mappedTagsArray != null) {
			for (String currentMappedTagItem : mappedTagsArray) {
				String[] mappedTag = currentMappedTagItem.split("-");
				Element currentRelationElement = relationsElement.addElement("relation");
				currentRelationElement.addElement("dc").setText(mappedTag[0]);
				currentRelationElement.addElement("parsed").setText(mappedTag[1]);
			}
		}

		String mappingsFilename = "mappings.xml";
		File mappingsFile = new File(metadataMappingDir, mappingsFilename);
		XMLWriter mappingsWriter = new XMLWriter(new FileWriter(mappingsFile), OutputFormat.createPrettyPrint());
		mappingsWriter.write(mappingsDocument);
		mappingsWriter.close();

		// Save mapping in MetadataTransformationManager mappings
//		metadataTransformation.setDestinationFormat("tel");
		metadataTransformation.setStylesheet(xsltFile.getName());
		metadataTransformation.setEditable(true);
		RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().saveMetadataTransformation(metadataTransformation);
	}

	private File getMetadataMappingDir() {
		File xsltDir = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltDir();
		File metadataMappingDir = new File(xsltDir, metadataTransformation.getId());
		return metadataMappingDir;
	}

	private boolean loadTransformation(String identifier) throws DocumentException, IOException {
		// Load mapping from MetadataTransformationManager mappings
		metadataTransformation = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().loadMetadataTransformation(identifier);
		if (metadataTransformation == null) {
			return false;
		}
		if(!metadataTransformation.getDestinationFormat().equals(MetadataFormat.tel.toString())
				&& !metadataTransformation.getDestinationFormat().equals(MetadataFormat.oai_dc.toString())) {
			otherDestinationFormat = metadataTransformation.getDestinationFormat();
			metadataTransformation.setDestinationFormat("other");
		}

		// Get the folder with this metadata mapping dir
		File xsltDir = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltDir();
		File metadataMappingDir = new File(xsltDir, identifier);

		// Move sample file to temp dir
		File sampleFile = new File(metadataMappingDir, "sample.xml");
		if(sampleFile.exists()) {
			File tempFile = getTempFile();
			pt.utl.ist.util.FileUtil.copyFile(sampleFile, tempFile);
			SAXReader sampleReader = new SAXReader();
			Document sampleDocument = sampleReader.read(new FileInputStream(tempFile));
			sampleNamespace = sampleDocument.getRootElement().getNamespace().getText();
		}

		// Load Mappings
		String mappingsFilename = "mappings.xml";
		File mappingsFile = new File(metadataMappingDir, mappingsFilename);
		SAXReader reader = new SAXReader();
		Document document = reader.read(new FileInputStream(mappingsFile));

		dcTags = new ArrayList<Tag>();
		Element dcElement = document.getRootElement().element("dc");
		for (Object dcTag : dcElement.elements("tag")) {
			Element currentDcTag = (Element) dcTag;
			dcTags.add(new Tag(null, currentDcTag.attributeValue("description"), currentDcTag.getText()));
		}

		parsedTags = new ArrayList<TagParsed>();
		Element extractedElement = document.getRootElement().element("parsed");
		for (Object extractedTag : extractedElement.elements("tag")) {
			Element currentExtractedTag = (Element) extractedTag;
			parsedTags.add(new TagParsed(null, null, currentExtractedTag.getText(),
					Integer.valueOf(currentExtractedTag.attributeValue("occurrences")), currentExtractedTag.elementText("examples")));
		}

		mappedTags = new String();
		Element relationsElement = document.getRootElement().element("relations");
		for (Object relationTag : relationsElement.elements("relation")) {
			Element currentRelationTag = (Element) relationTag;
			String currentDcPos = currentRelationTag.element("dc").getText();
			String currentExtractedPos = currentRelationTag.element("parsed").getText();
			mappedTags += currentDcPos + "-" + currentExtractedPos + ",";
		}

		initTags();

		return true;
	}

	private void initDcTags() {
		XmlParser parser = new XmlParser();
		dcTags = parser.getDcTags();
	}

	private void initTags() throws FileNotFoundException, IOException {
		if(sampleNamespace.equals("http://www.bs.dk/standards/MarcXchange")
				|| sampleNamespace.equals("info:lc/xmlns/marcxchange-v1")) {
			XmlParser parser = new XmlParser();
			parser.associateMarcXchangeFields(parsedTags);
		}
	}

	@DefaultHandler
	public Resolution start() throws IOException, DocumentException, NoSuchFieldException, IllegalAccessException {
		loadMetadataTransformations();

		return new ForwardResolution("/jsp/mapMetadata/start.jsp");
	}

	private void loadMetadataTransformations() throws IOException, DocumentException, NoSuchFieldException,
			IllegalAccessException {
		allMetadataTransformations = new ArrayList<MetadataTransformation>();

		Map<String, List<MetadataTransformation>> metadataTransformationMap = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().loadMetadataTransformations();
		for (List<MetadataTransformation> metadatatransformations : metadataTransformationMap.values()) {
			for (MetadataTransformation currentMetadataTransformation : metadatatransformations) {
				allMetadataTransformations.add(currentMetadataTransformation);
			}
		}

		Collections.sort(allMetadataTransformations, RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getTransformationComparator());

		saveSessionVariables();
	}

	public Resolution cancel() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		return new ForwardResolution("/jsp/mapMetadata/start.jsp");
	}

	public Resolution cancelToMappings() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}

	@ValidationMethod(on = { "edit" })
	public void validateEdit(ValidationErrors errors) {
		if (metadataTransformation == null
				|| metadataTransformation.getId() == null
				|| metadataTransformation.getId().isEmpty()) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.editWithoutIdentifier"));
		}
	}

	public Resolution edit() throws NoSuchFieldException, IllegalAccessException, IOException, DocumentException {
		loadSessionVariables();
		if (!loadTransformation(metadataTransformation.getId())) {
			context.getValidationErrors().add("metadataTransformation", new LocalizableError("error.mapMetadata.loadingMetadataTransformation"));
			return new ForwardResolution("/jsp/mapMetadata/start.jsp");
		}
		editing = true;

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}


	public Resolution delete() throws NoSuchFieldException, IllegalAccessException, IOException, DocumentException {
		loadSessionVariables();

		MetadataTransformation loadedMetadataTransformation = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().loadMetadataTransformation(metadataTransformation.getId());
		if (loadedMetadataTransformation == null) {
			context.getValidationErrors().add("metadataTransformation", new LocalizableError("error.mapMetadata.loadingMetadataTransformation"));
			return new ForwardResolution("/jsp/mapMetadata/start.jsp");
		}
		File xsltFile = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltFile(metadataTransformation.getId());
		if(!xsltFile.delete()) {
			log.error("Unable to delete file: " + xsltFile.getAbsolutePath());
		}

		if(getMetadataMappingDir().exists()) {
			FileUtil.deleteDir(getMetadataMappingDir());
		}
		RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().deleteMetadataTransformation(metadataTransformation.getId());
		saveSessionVariables();

		context.getMessages().add(new LocalizableMessage("metadataTransformation.deleteSuccess", metadataTransformation.getId()));

		return start();
	}

	public Resolution downloadXslt() throws IOException, DocumentException {
		MetadataTransformation loadedMetadataTransformation = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().loadMetadataTransformation(metadataTransformation.getId());
		if (loadedMetadataTransformation == null) {
			context.getValidationErrors().add("metadataTransformation", new LocalizableError("error.mapMetadata.loadingMetadataTransformation"));
		}

		File xsltFile = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltFile(metadataTransformation.getId());

		return new StreamingResolution("text/xml") {
		    @Override
			public void stream(HttpServletResponse response) throws IOException, DocumentException {
		    	File xsltFile = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltFile(metadataTransformation.getId());
		    	FileInputStream fileInputStream = new FileInputStream(xsltFile);
		    	OutputStream out = response.getOutputStream();
				byte[] buf = new byte[1024];
				int len;
				while ((len = fileInputStream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
		    }
		}.setFilename(xsltFile.getName());
	}

	@ValidationMethod(on = { "submitSampleData" })
	public void validateSubmitSampleData(ValidationErrors errors) throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		if (sampleFile1 == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.noFileSent"));
		}
		saveSessionVariables();
	}

	public Resolution submitSampleData() throws NoSuchFieldException, IllegalAccessException, IOException {
		resetSessionVariables();
		metadataTransformation = new MetadataTransformation();
		metadataTransformation.setSourceFormat(MetadataFormat.MarcXchange.toString());
		metadataTransformation.setDestinationFormat(MetadataFormat.tel.toString());

		SAXReader reader = new SAXReader();
		try {
			parsedTags = new ArrayList<TagParsed>();

			if (sampleFile1.getFileName().endsWith(".zip")) {
				File tempDir = new File(RepoxContextUtil.getRepoxManager().getConfiguration().getTempDir(),
										String.valueOf(new Date().getTime()));
				tempDir.mkdirs();
				File tempFile = new File(tempDir, "temp.zip");
				sampleFile1.save(tempFile);

				ZipInputStream in = new ZipInputStream(new FileInputStream(tempFile));
				ZipEntry entry;
				boolean first = true;

				while ((entry = in.getNextEntry()) != null) {
					if (entry.isDirectory()) {
						continue;
					}

					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						if (first) {
							FileOutputStream fileOutputStream = new FileOutputStream(getTempFile());
							byte[] buf = new byte[1024];
							int len;
							while ((len = in.read(buf)) > 0) {
								fileOutputStream.write(buf, 0, len);
								out.write(buf, 0, len);
							}
							fileOutputStream.close();
							out.close();

							first = false;
						}
						else {
							out = new ByteArrayOutputStream();
							byte[] buf = new byte[1024];
							int len;
							while ((len = in.read(buf)) > 0) {
								out.write(buf, 0, len);
							}
							out.close();
						}

						XmlParser parser = new XmlParser();
						parsedTags = parser.parseTags(out.toString(), parsedTags);
						if (sampleNamespace == null || sampleNamespace.isEmpty()) {
							Document document = DocumentHelper.parseText(out.toString());
							sampleNamespace = document.getRootElement().getNamespace().getText();
						}
					} catch (DocumentException e) {
						log.error(e);
						getContext().getMessages().add(new LocalizableError("error.mapMetadata.errorParsingZipEntry", entry.getName()));
					}
				}
			} else {
				sampleFile1.save(getTempFile());

				XmlParser parser = new XmlParser();
				parsedTags = parser.parseTags(getTempFile(), parsedTags);
				Document document = reader.read(getTempFile());
				sampleNamespace = document.getRootElement().getNamespace().getText();
			}

			initTags();
			initDcTags();
			saveSessionVariables();
		} catch (DocumentException e) {
			log.error("Error parsing sampleData File", e);
			context.getValidationErrors().addGlobalError(new LocalizableError("error.mapMetadata.invalidFile"));
			return new ForwardResolution("/jsp/mapMetadata/start.jsp");
		}

		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}

	public Resolution createFromDataSource() throws NoSuchFieldException, IllegalAccessException, IOException, DocumentException, SQLException {
		resetSessionVariables();

		String dataSourceId = getContext().getRequest().getParameter("id");
		DataProviderManager dataProviderManager = RepoxContextUtil.getRepoxManager().getDataProviderManager();
		if(dataSourceId != null && dataProviderManager.isIdValid(dataSourceId)
				&& dataProviderManager.getDataSource(dataSourceId).getNumberRecords() > 0) {

			DataSource dataSource = dataProviderManager.getDataSource(dataSourceId);
			OaiListResponse oaiListResponse = RepoxContextUtil.getRepoxManager().getAccessPointsManager().getOaiRecordsFromDataSource(dataSource, null, null, null, 100, false);

			parsedTags = new ArrayList<TagParsed>();

			boolean first = true;

			for (OaiItem currentItem : oaiListResponse.getOaiItems()) {
				byte[] currentRecord = currentItem.getMetadata();
				String currentRecordString = new String(currentRecord, "UTF-8");
				try {
					if (first) {
						OutputFormat format = OutputFormat.createPrettyPrint();
						XMLWriter writer = new XMLWriter(new FileOutputStream(getTempFile()), format);
						Document document = DocumentHelper.parseText(currentRecordString);
						writer.write(document);
						writer.close();

						first = false;
					}

					XmlParser parser = new XmlParser();
					parsedTags = parser.parseTags(currentRecordString, parsedTags);
					if (sampleNamespace == null || sampleNamespace.isEmpty()) {
						Document document = DocumentHelper.parseText(currentRecordString);
						sampleNamespace = document.getRootElement().getNamespace().getText();
					}
				} catch (DocumentException e) {
					log.error("Error parsing Record from Data Source with id " + dataSourceId, e);
					getContext().getMessages().add(new LocalizableError("error.mapMetadata.errorParsingDataSourceRecord", dataSourceId));
				}
			}

			initTags();
			initDcTags();
			saveSessionVariables();

			return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
		}
		else {
			getContext().getMessages().add(new LocalizableError("error.mapMetadata.errorDataSource", dataSourceId));
			return getContext().getSourcePageResolution();
		}
	}

	public Resolution createGroup() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		if (selectedTags == null) {
			this.getContext().getValidationErrors().add("tagGroupAllTags", new LocalizableError("error.mapMetadata.groupNotEnoughTags"));
			return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
		}

		tagGroupAllTags = new ArrayList<Tag>();
		for (int i = 0; i < selectedTags.size(); i++) {
			if (selectedTags.get(i) != null && !selectedTags.get(i).isEmpty()) {
				if (selectedTags.get(i).startsWith(TagGroup.GROUP_DELIMITER_START)) {
					this.getContext().getValidationErrors().add("tagGroupAllTags", new LocalizableError("error.mapMetadata.groupCannotSelectGroup"));
					return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
				}
				tagGroupAllTags.add(parsedTags.get(i));
			}
		}

		if (tagGroupAllTags.size() < 1) {
			this.getContext().getValidationErrors().add("tagGroupAllTags", new LocalizableError("error.mapMetadata.groupNotEnoughTags"));
			return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
		}

		tagGroup = new TagGroup();
		String commonXpath = XmlParser.getCommonXpath(tagGroupAllTags);
		tagGroup.setCommonXpath(commonXpath);

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/createGroup.jsp");
	}

	public Resolution removeFromGroup() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		tagGroupAllTags.remove(tagGroupPos);

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/createGroup.jsp");
	}

	public Resolution riseInGroup() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		Tag removedTag = tagGroupAllTags.remove(tagGroupPos);
		tagGroupAllTags.add(tagGroupPos - 1, removedTag);

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/createGroup.jsp");
	}

	public Resolution lowerInGroup() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		Tag removedTag = tagGroupAllTags.remove(tagGroupPos);
		tagGroupAllTags.add(tagGroupPos + 1, removedTag);

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/createGroup.jsp");
	}

	@ValidationMethod(on = { "submitNewGroup" })
	public void validateSubmitNewGroup(ValidationErrors errors) throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		if (tagGroup == null || tagGroup.getDescription() == null || tagGroup.getDescription().trim().isEmpty()) {
			errors.add("tagGroup", new LocalizableError("error.mapMetadata.required.description"));
		}
		if((tagGroup.getInitialPrefix() != null && tagGroup.getInitialPrefix().equals(TagGroup.TAG_SEPARATOR))
				|| (tagGroup.getFinalSuffix() != null && tagGroup.getFinalSuffix().equals(TagGroup.TAG_SEPARATOR))) {
			errors.add("tagGroup", new LocalizableError("error.mapMetadata.separatorUsed"));
		}
		else if (tagPrefixes != null) {
			for (String currentPrefix : tagPrefixes) {
				if(currentPrefix != null && currentPrefix.equals(TagGroup.TAG_SEPARATOR)) {
					errors.add("tagGroup", new LocalizableError("error.mapMetadata.separatorUsed"));
					break;
				}
			}
		}
		saveSessionVariables();
	}

	public Resolution submitNewGroup() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		tagGroup.setTags(tagGroupAllTags);
		tagGroup.setTagPrefixes(tagPrefixes);
		// context.getMessages().add(new SimpleMessage("Grupo submetido com
		// sucesso"));
		parsedTags.add(new TagParsed(null, tagGroup.getDescription(), tagGroup.getXpath(), 0, null));
		tagGroup = new TagGroup();
		tagGroupAllTags = new ArrayList<Tag>();
		tagPrefixes = new ArrayList<String>();
		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}

	public Resolution createParsedTag() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		saveSessionVariables();
		return new ForwardResolution("/jsp/mapMetadata/createParsedTag.jsp");
	}

	public Resolution createDcTag() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		saveSessionVariables();
		return new ForwardResolution("/jsp/mapMetadata/createDcTag.jsp");
	}

	@ValidationMethod(on = { "submitNewDcTag" })
	public void validateSubmitNewDcTag(ValidationErrors errors) throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		if (newDcTag == null || newDcTag.getXpath() == null || newDcTag.getXpath().trim().isEmpty()) {
			errors.add("newDcTag", new LocalizableError("error.mapMetadata.required.xpath"));
		}
		else {
			if (newDcTag == null && (newDcTag.getName() == null || newDcTag.getName().trim().isEmpty())) {
				newDcTag.setName(newDcTag.getXpath());
			}
			if (newDcTag != null && (newDcTag.getDescription() == null || newDcTag.getDescription().trim().isEmpty())) {
				newDcTag.setDescription(newDcTag.getXpath());
			}
		}

		saveSessionVariables();
	}
	public Resolution submitNewDcTag() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		dcTags.add(newDcTag);
		newDcTag = new Tag();

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}

	@ValidationMethod(on = { "submitNewParsedTag" })
	public void validateSubmitNewParsedTag(ValidationErrors errors) throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		if (newParsedTag == null || newParsedTag.getXpath() == null || newParsedTag.getXpath().trim().isEmpty()) {
			errors.add("newParsedTag", new LocalizableError("error.mapMetadata.required.xpath"));
		}

		else {
			if (newParsedTag != null && (newParsedTag.getName() == null || newParsedTag.getName().trim().isEmpty())) {
				newParsedTag.setName(newParsedTag.getXpath());
			}
			if (newParsedTag != null || newParsedTag.getDescription() == null || newParsedTag.getDescription().trim().isEmpty()) {
				newParsedTag.setDescription(newParsedTag.getXpath());
			}
		}

		saveSessionVariables();
	}
	public Resolution submitNewParsedTag() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();

		parsedTags.add(newParsedTag);
		newParsedTag = new TagParsed();

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}

	public Resolution deleteDcTag() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		String dcTagIndex = context.getRequest().getParameter("dcTagIndex");
		if(dcTagIndex != null) {
			int dcIndexToDelete = Integer.parseInt(dcTagIndex);
			String correctedMappedTags = "";

			String[] mappedTagsArray = (mappedTags != null ? mappedTags.split(",") : null);
			if (mappedTagsArray != null) {
				for (String currentMappedTagItem : mappedTagsArray) {
					String[] mappedTag = currentMappedTagItem.split("-");
					int mappedDcPos = Integer.parseInt(mappedTag[0]);
					int mappedParsedTagPos = Integer.parseInt(mappedTag[1]);

					String newMapping = (correctedMappedTags.isEmpty() ? "" : ",");
					if(mappedDcPos < dcIndexToDelete) {
						newMapping = newMapping + mappedDcPos + "-" + mappedParsedTagPos;
					}
					// if it's equal it's a connection to be removed because it's a connection to the tag being deleted
					else if(mappedDcPos > dcIndexToDelete){
						newMapping = newMapping + (mappedDcPos - 1) + "-" + mappedParsedTagPos;
					}

					correctedMappedTags = correctedMappedTags + newMapping;
				}
			}
			dcTags.remove(dcIndexToDelete);
			mappedTags = correctedMappedTags;
		}

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}

	public Resolution deleteParsedTag() throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		String parsedTagIndex = context.getRequest().getParameter("parsedTagIndex");
		if(parsedTagIndex != null) {
			int parsedIndexToDelete = Integer.parseInt(parsedTagIndex);
			String correctedMappedTags = "";

			String[] mappedTagsArray = (mappedTags != null ? mappedTags.split(",") : null);
			if (mappedTagsArray != null) {
				for (String currentMappedTagItem : mappedTagsArray) {
					String[] mappedTag = currentMappedTagItem.split("-");
					int mappedDcPos = Integer.parseInt(mappedTag[0]);
					int mappedParsedTagPos = Integer.parseInt(mappedTag[1]);

					String newMapping = (correctedMappedTags.isEmpty() ? "" : ",");
					if(mappedParsedTagPos < parsedIndexToDelete) {
						newMapping = newMapping + mappedDcPos + "-" + mappedParsedTagPos;
					}
					// if it's equal it's a connection to be removed because it's a connection to the tag being deleted
					else if(mappedParsedTagPos > parsedIndexToDelete){
						newMapping = newMapping + mappedDcPos + "-" + (mappedParsedTagPos - 1);
					}

					correctedMappedTags = correctedMappedTags + newMapping;
				}
			}
			parsedTags.remove(parsedIndexToDelete);
			mappedTags = correctedMappedTags;
		}

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
	}

	public Resolution submitMappings() throws NoSuchFieldException, IllegalAccessException, DocumentException, IOException, TransformerException, NoSuchAlgorithmException {
		String[] mappedTagsArray = (mappedTags != null ? mappedTags.split(",") : null);
		loadSessionVariables();

		dcMappings = new TreeMap<String, Set<String>>();
		for (Tag currentDcTag : dcTags) {
			dcMappings.put(currentDcTag.getXpath(), new TreeSet<String>());
		}

		if (mappedTagsArray != null) {
			for (String currentMappedTagItem : mappedTagsArray) {
				String[] mappedTag = currentMappedTagItem.split("-");
				int mappedDcPos = Integer.parseInt(mappedTag[0]);
				int mappedParsedTagPos = Integer.parseInt(mappedTag[1]);

				Set<String> currentParsedTagsSet = dcMappings.get(dcTags.get(mappedDcPos).getXpath());
				if (currentParsedTagsSet == null) {
					currentParsedTagsSet = new TreeSet<String>();
				}
				currentParsedTagsSet.add(parsedTags.get(mappedParsedTagPos).getXpath());
				dcMappings.put(dcTags.get(mappedDcPos).getXpath(), currentParsedTagsSet);
			}
		} else {
			getContext().getMessages().add(new LocalizableError("error.mapMetadata.noMappings"));
			return new ForwardResolution("/jsp/mapMetadata/mapping.jsp");
		}

		SAXReader reader = new SAXReader();
		Document document = reader.read(getTempFile());
		OutputFormat format = OutputFormat.createPrettyPrint();
		OutputStream outputStream = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(outputStream, format);
		writer.write(document);

		sampleXml = outputStream.toString();

		setTransformations(document);

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mappingResult.jsp");
	}

	private void setTransformations(Document document) throws IOException, DocumentException, NoSuchAlgorithmException,
			TransformerException {
		ManualMetadataTransformationManager manager = new ManualMetadataTransformationManager(dcMappings);
		String transformationRoot = null;
		String transformationNamespaces = null;
		if(metadataTransformation == null || metadataTransformation.getDestinationFormat().equals(MetadataFormat.tel.toString())) {
			transformationRoot = ManualMetadataTransformationManager.TEL_ROOT;
			transformationNamespaces = ManualMetadataTransformationManager.TEL_NAMESPACES;
		}
		else if(metadataTransformation == null || metadataTransformation.getDestinationFormat().equals(MetadataFormat.oai_dc.toString())) {
			transformationRoot = ManualMetadataTransformationManager.OAI_DC_ROOT;
			transformationNamespaces = ManualMetadataTransformationManager.OAI_DC_NAMESPACES;
		}
		else {
			transformationRoot = rootElement;
			transformationNamespaces = namespaces;
		}

		resultXslt = manager.getTransformationText(document, transformationRoot, transformationNamespaces);
		resultTransformation = manager.transform(getTempFile(), transformationRoot, transformationNamespaces);
	}

	@ValidationMethod(on = { "submitNewSampleFile" })
	public void validateSubmitNewSampleFile(ValidationErrors errors) throws NoSuchFieldException, IllegalAccessException {
		loadSessionVariables();
		if (sampleFile1 == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.noFileSent"));
		}

		saveSessionVariables();
	}

	public Resolution submitNewSampleFile() throws NoSuchFieldException, IllegalAccessException, IOException, TransformerException, DocumentException, NoSuchAlgorithmException {
		loadSessionVariables();

		sampleFile1.save(getTempFile());
		Document document;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(getTempFile());
		} catch (DocumentException e) {
			log.error("Error parsing sampleData File", e);
			context.getValidationErrors().addGlobalError(new LocalizableError("error.mapMetadata.invalidFile"));
			return new ForwardResolution("/jsp/mapMetadata/mappingResult.jsp");
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		OutputStream outputStream = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(outputStream, format);
		writer.write(document);

		sampleXml = outputStream.toString();

		setTransformations(document);

		saveSessionVariables();

		return new ForwardResolution("/jsp/mapMetadata/mappingResult.jsp");
	}

	@ValidationMethod(on = { "submitConfirmation" })
	public void validateSubmitConfirmation(ValidationErrors errors) throws NoSuchFieldException, IllegalAccessException, IOException, DocumentException {
		loadSessionVariables();

		if (metadataTransformation == null || metadataTransformation.getId() == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.id"));
		} else if (!Pattern.compile(ID_REGULAR_EXPRESSION).matcher(metadataTransformation.getId()).matches()) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.metadataTransformationInvalidId"));
		} else {
			MetadataTransformationManager metadataTransformationManager = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager();
			if ((editing == null || !editing)
					&& (metadataTransformationManager.loadMetadataTransformation(metadataTransformation.getId()) != null)) {
				errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.metadataTransformationExistingId", metadataTransformation.getId()));
			}
		}

		if (metadataTransformation == null || metadataTransformation.getDescription() == null
				|| metadataTransformation.getDescription().trim().isEmpty()) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.description"));
		}
		if (metadataTransformation == null || metadataTransformation.getSourceFormat() == null
				|| metadataTransformation.getSourceFormat().trim().isEmpty()) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.sourceFormat"));
		}
		if (metadataTransformation == null || metadataTransformation.getDestinationFormat() == null
				|| metadataTransformation.getDestinationFormat().trim().isEmpty()) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.destinationFormat"));
		}
		else if(!metadataTransformation.getDestinationFormat().equals(MetadataFormat.tel.toString())
				&& !metadataTransformation.getDestinationFormat().equals(MetadataFormat.oai_dc.toString())) {
			if(otherDestinationFormat == null || otherDestinationFormat.trim().isEmpty()) {
				errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.destinationFormat"));
			}
			if(rootElement == null || rootElement.trim().isEmpty()) {
				errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.rootElement"));
			}
/*			if(namespaces == null || namespaces.trim().isEmpty()) {
				errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.namespaces"));
			}
*/
		}

		saveSessionVariables();
	}

	public Resolution submitConfirmation() throws NoSuchFieldException, IllegalAccessException, DocumentException, IOException, TransformerException, NoSuchAlgorithmException {
		loadSessionVariables();

		SAXReader reader = new SAXReader();
		Document document = reader.read(getTempFile());
		if(!metadataTransformation.getDestinationFormat().equals(MetadataFormat.tel.toString())
				&& !metadataTransformation.getDestinationFormat().equals(MetadataFormat.oai_dc.toString())) {
			metadataTransformation.setDestinationFormat(otherDestinationFormat);
		}
		setTransformations(document);

		saveTransformation();

		context.getMessages().add(new LocalizableMessage("mapMetadata.success", metadataTransformation.getId()));

		return new RedirectResolution("/Homepage.action");
	}


	@ValidationMethod(on = { "submitXsltFile" })
	public void validateSubmitXsltFile(ValidationErrors errors) throws NoSuchFieldException, IllegalAccessException, IOException {
		loadSessionVariables();

		if (metadataTransformation == null || metadataTransformation.getId() == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.id"));
		} else if (!Pattern.compile(ID_REGULAR_EXPRESSION).matcher(metadataTransformation.getId()).matches()) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.metadataTransformationInvalidId"));
		}

		if (metadataTransformation == null || metadataTransformation.getDescription() == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.description"));
		}
		if (metadataTransformation == null || metadataTransformation.getSourceFormat() == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.sourceFormat"));
		}
		if (metadataTransformation == null || metadataTransformation.getDestinationFormat() == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.required.destinationFormat"));
		}
		if (sampleFile1 == null) {
			errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.noFileSent"));
		} else {
			try {
				Document document = new SAXReader().read(sampleFile1.getInputStream());
				Transformer transformer = TransformerFactory.newInstance().newTransformer(new DocumentSource(document));
			} catch (DocumentException e) {
				errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.invalidXmlFile"));
			} catch (TransformerConfigurationException e) {
				errors.add("metadataTransformation", new LocalizableError("error.mapMetadata.invalidTransformationFile"));
			}
		}

		saveSessionVariables();
	}

	public Resolution submitXsltFile() throws NoSuchFieldException, IllegalAccessException, IOException, DocumentException {
		loadSessionVariables();

		// Create a folder with name equal to this metadata mapping identifier
		File xsltDir = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltDir();
		File metadataMappingDir = getMetadataMappingDir();
		metadataMappingDir.mkdir();

		// Write XSLT
		File xsltFile = RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().getXsltFile(metadataTransformation.getId());
		sampleFile1.save(xsltFile);
		Document document = new SAXReader().read(xsltFile);
		String encoding = (document.getXMLEncoding() != null ? document.getXMLEncoding() : "UTF-8");
		log.debug("XML Encoding: " + document.getXMLEncoding());
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding(encoding);
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xsltFile), encoding), outputFormat);
		writer.write(document);
		writer.close();

		// Save mapping in MetadataTransformationManager mappings
		metadataTransformation.setStylesheet(xsltFile.getName());
		metadataTransformation.setEditable(false);
		RepoxContextUtil.getRepoxManager().getMetadataTransformationManager().saveMetadataTransformation(metadataTransformation);

		loadMetadataTransformations();
		
		context.getMessages().add(new LocalizableMessage("mapMetadata.success", metadataTransformation.getId()));

		return new ForwardResolution("/jsp/mapMetadata/start.jsp");
	}


	public static int digitsMaxNumberOccurences(List<TagParsed> tags) {
		int digits = 1;

		for (TagParsed currentTag : tags) {
			int currentDigits = String.valueOf(currentTag.getOccurrences()).length();
			if(currentDigits > digits) {
				digits = currentDigits;
			}
		}

		return digits;
	}

	public static void main(String[] args) {
		MapMetadataActionBean bean = new MapMetadataActionBean();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("", "", "/record/asd fg/g/hh2"));
		tags.add(new Tag("", "", "/record/asd fg/g/hh2"));
		tags.add(new Tag("", "", "/record/asd fg/g/hh2/3"));
		String commonXpath = XmlParser.getCommonXpath(tags);
	}

}
