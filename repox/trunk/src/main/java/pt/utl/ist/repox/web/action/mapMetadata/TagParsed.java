package pt.utl.ist.repox.web.action.mapMetadata;

public class TagParsed extends Tag {
	private int occurrences;
	private String examples;

	public int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}

	public String getExamples() {
		return examples;
	}

	public void setExamples(String examples) {
		this.examples = examples;
	}

	public TagParsed() {
		super();
	}
	
	public TagParsed(String name, String description, String xpath, int occurrences, String examples) {
		super(name, description, xpath);
		this.occurrences = occurrences;
		this.examples = examples;
	}
	
	
}
