package freemarker.ext.dom;

import java.io.IOException;
import java.util.*;

import org.w3c.dom.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.gml.*;
import com.vividsolutions.jts.io.WKTWriter;

import freemarker.core.*;
import freemarker.template.*;

/**
 * FreeMarker user-defined directive for repeating a section of a template,
 * optionally with separating the output of the repetations with
 * <tt>&lt;hr></tt>-s.
 *
 * 
 * <p><b>Directive info</b></p>
 * 
 * <p>Parameters:
 * <ul>
 *   <li><code>count</code>: The number of repetations. Required!
 *       Must be a non-negative number. If it is not a whole number then it will
 *       be rounded <em>down</em>.
 *   <li><code>hr</code>: Tells if a HTML "hr" element could be printed between
 *       repetations. Boolean. Optional, defaults to <code>false</code>. 
 * </ul>
 *
 * <p>Loop variables: One, optional. It gives the number of the current
 *    repetation, starting from 1.
 * 
 * <p>Nested content: Yes
 */
public class WKTTemplate implements TemplateDirectiveModel {

	protected static WKTWriter wktWriter = new WKTWriter();
    
	protected String getVar(Map params) throws TemplateModelException {
    	SimpleScalar sm = (SimpleScalar)params.get("var");
    	if(sm == null)
    		throw new TemplateModelException("Variable missing");
    	else return sm.getAsString();
    }

    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException {

    	ElementModel model = (ElementModel)env.getVariable(getVar(params));
    	Geometry geo = GMLUtils.readGeometry((Element)model.getNode());
    	if(geo != null) env.getOut().write(wktWriter.write(geo));
    }

}