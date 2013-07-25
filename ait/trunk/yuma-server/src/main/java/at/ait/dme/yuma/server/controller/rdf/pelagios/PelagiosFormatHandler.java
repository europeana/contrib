package at.ait.dme.yuma.server.controller.rdf.pelagios;

import at.ait.dme.yuma.server.controller.rdf.SerializationLanguage;
import at.ait.dme.yuma.server.controller.rdf.oac.OACFormatHandler;
import at.ait.dme.yuma.server.model.tag.SemanticTag;

import com.hp.hpl.jena.rdf.model.Resource;

public class PelagiosFormatHandler extends OACFormatHandler {
	
	public PelagiosFormatHandler(SerializationLanguage format) {
		super(format);
	}

	@Override
	protected Resource createBodyResource() {
		return model.createResource(getBodyUri());
	}
	
	private String getBodyUri() throws NotAPelagiosAnnotationException {	
		if (annotation.getTags() == null)
			throw new NotAPelagiosAnnotationException();
			
		for (SemanticTag t : annotation.getTags()) {
			if (t.getURI().toString().contains("pleiades"))
				return t.getURI().toString();
		}
		
		throw new NotAPelagiosAnnotationException(); 
	}

}
