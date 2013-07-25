package at.ait.dme.yuma.server.controller.rdf.pelagios;

/**
 * A makeshift exception specifically for the Pelagios project. Thrown in case
 * an annotation does not contain a Pleiades reference. Made this a RuntimeException 
 * as to not mess up our FormatHandler interface.
 * 
 * @author Christian Mader, Rainer Simon
 */
@SuppressWarnings("serial")
public class NotAPelagiosAnnotationException extends RuntimeException {

	public NotAPelagiosAnnotationException() {
		super();
	}
	
}
