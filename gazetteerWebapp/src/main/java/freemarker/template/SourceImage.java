package freemarker.template;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.*;

import freemarker.core.*;
import freemarker.ext.beans.*;

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
public class SourceImage implements TemplateDirectiveModel {
    
    protected String getVar(Map params) throws TemplateModelException {
    	SimpleScalar sm = (SimpleScalar)params.get("var");
    	if(sm == null) return null;
    	else return sm.getAsString();
    }

    protected String getValue(Map params) throws TemplateModelException {
    	SimpleScalar sm = (SimpleScalar)params.get("value");
    	if(sm == null) return "";
    	else return sm.getAsString();
    }

    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException {

        // ---------------------------------------------------------------------
        // Processing the parameters:

    	String var = getVar(params);
    	String text = getValue(params);
    	text = text.replaceAll("[:/#.]+", ".");
    	if(var != null) {
    		env.setVariable(var, BeansWrapper.getDefaultInstance().wrap(text));
    	}
    	else {
    		Writer out = env.getOut();
    		out.write(text);
    	}
    }
}