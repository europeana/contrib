package freemarker.template;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.object.lifecycle.*;

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
public class ForTemplate implements TemplateDirectiveModel {
    
    private static final String PARAM_NAME_COUNT = "count";
    private static final String PARAM_NAME_HR = "hr";

    protected String getVar(Map params) throws TemplateModelException {
    	SimpleScalar sm = (SimpleScalar)params.get("var");
    	if(sm == null)
    		throw new TemplateModelException("Variable missing");
    	else return sm.getAsString();
    }

    protected int getStart(Map params) throws TemplateModelException {
    	TemplateNumberModel tnm = (TemplateNumberModel)params.get("start");
    	if(tnm != null) return tnm.getAsNumber().intValue();
    	else return 0;
    }

    protected int getEnd(Map params) throws TemplateModelException {
    	TemplateNumberModel tnm = (TemplateNumberModel)params.get("end");
    	if(tnm != null) return tnm.getAsNumber().intValue();
    	else return -1;
    }

    protected TemplateCollectionModel getCollection(Map params) throws TemplateModelException {
    	TemplateModel tm = (TemplateModel)params.get("col");
    	if(tm instanceof TemplateCollectionModel) {
    		return (TemplateCollectionModel)tm;
    	}
    	return null;
    }

    protected Lifecycle getLifecycle(TemplateCollectionModel tcm) {
    	if(tcm instanceof BeanModel) {
    		Object obj = ((BeanModel)tcm).getWrappedObject();
    		if(obj instanceof Lifecycle) return (Lifecycle)obj;
    	}
    	return null;
    }

    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException {
        
        // ---------------------------------------------------------------------
        // Processing the parameters:

    	String var = getVar(params);
    	int start = getStart(params);
    	int end = getEnd(params);
    	TemplateCollectionModel tcm = getCollection(params);
    	TemplateModelIterator iter = tcm.iterator();
    	Lifecycle lifecycle = getLifecycle(tcm);

    	try {
    		if(lifecycle != null) lifecycle.start();
	        Writer out = env.getOut();
	        if (body != null) {
	        	int index = 0;
	        	for(; index < start && iter.hasNext(); index++) iter.next();
	        	BeansWrapper bw = BeansWrapper.getDefaultInstance();
	        	if(end < 0) {
	        		while(iter.hasNext()) {
	        			env.setVariable(var, bw.wrap(iter.next()));
	        			if(loopVars.length > 0) loopVars[0] = new SimpleNumber(index++);
	                    body.render(out);
	        		}
	        	}
	        	else {
	        		for(; index < end && iter.hasNext(); index++) {
	        			env.setVariable(var, bw.wrap(iter.next()));
	        			if(loopVars.length > 0) loopVars[0] = new SimpleNumber(index);
	                    body.render(out);
	        		}
	        	}
	        }
    	}
    	finally {
    		if(lifecycle != null) lifecycle.stop();
    	}
    }

}