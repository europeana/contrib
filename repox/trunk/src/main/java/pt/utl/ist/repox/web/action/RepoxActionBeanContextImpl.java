package pt.utl.ist.repox.web.action;

import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.util.RepoxContextUtil;

public class RepoxActionBeanContextImpl extends RepoxActionBeanContext {
	protected RepoxManager repoxManager;

	public RepoxManager getRepoxManager() {
		if(repoxManager == null) {
			repoxManager = RepoxContextUtil.getRepoxManager();
		}

		return repoxManager;
	}

}
