package pt.utl.ist.repox.web.action;

import net.sourceforge.stripes.action.ActionBeanContext;
import pt.utl.ist.repox.RepoxManager;

public abstract class RepoxActionBeanContext extends ActionBeanContext {
	//	public User getUser();
	//	public void setUser(User user);
	public abstract RepoxManager getRepoxManager();
}
