package at.ait.dme.yuma.server.controller.rss;

import java.util.List;

import at.ait.dme.yuma.server.controller.FormatHandler;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.AnnotationTree;

public class GeoRSSFormatHandler implements FormatHandler {

	@Override
	public Annotation parse(String serialized)
			throws UnsupportedOperationException {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public String serialize(Annotation annotation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serialize(AnnotationTree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serialize(List<Annotation> annotations)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
