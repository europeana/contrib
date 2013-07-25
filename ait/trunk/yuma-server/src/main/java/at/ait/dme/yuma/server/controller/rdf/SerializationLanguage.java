package at.ait.dme.yuma.server.controller.rdf;

public enum SerializationLanguage {
	
	RDF_XML("RDF/XML"),
	N3("N3"),
	N_TRIPLE("N-TRIPLE"),
	TURTLE("TURTLE");
	
	private String jenaLanguage; 
	
	private SerializationLanguage(String jenaLanguage) {
		this.jenaLanguage = jenaLanguage;
	}
	
	@Override
	public String toString() {
		return jenaLanguage;
	}
}
