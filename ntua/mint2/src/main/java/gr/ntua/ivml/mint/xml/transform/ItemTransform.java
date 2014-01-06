package gr.ntua.ivml.mint.xml.transform;

import javax.xml.transform.TransformerException;

/**
 * @author fxeni
 *
 *	Interface for preview transform classes
 *
 */
public interface ItemTransform {
	public String transform(String input) throws Exception;
}
