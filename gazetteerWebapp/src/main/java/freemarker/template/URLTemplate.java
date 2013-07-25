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
public class URLTemplate implements TemplateDirectiveModel {
    
    protected String getVar(Map params) throws TemplateModelException {
    	SimpleScalar sm = (SimpleScalar)params.get("_var");
    	if(sm == null)
    		throw new TemplateModelException("Variable missing");
    	else return sm.getAsString();
    }

    protected String getUrl(Map params) throws TemplateModelException {
    	SimpleScalar sm = (SimpleScalar)params.get("_url");
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
    	String url = getUrl(params);
    	boolean first = true;
    	Map<String,TemplateModel> pars = (Map<String,TemplateModel>) params;
    	for(Entry<String,TemplateModel> entry : pars.entrySet()) {
    		String key = entry.getKey();
    		if(!key.startsWith("_")) {
    			String value = ((TemplateScalarModel)entry.getValue()).getAsString();
    			if(first) {
    				url += "?";
    				first = false;
    			}
    			else url += "&";
    			url += key + "=" + URLEncoder.encode(value,"UTF-8");
    		}
    	}
    	env.setVariable(var, BeansWrapper.getDefaultInstance().wrap(url));
    }

}