package pt.utl.ist.repox.web.action;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

public abstract class RepoxActionBean implements ActionBean {
	protected RepoxActionBeanContext context;

	public RepoxActionBeanContext getContext() {
		return this.context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = (RepoxActionBeanContext) context;
	}
}
