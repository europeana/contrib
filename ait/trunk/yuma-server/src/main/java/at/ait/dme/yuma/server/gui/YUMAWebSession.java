package at.ait.dme.yuma.server.gui;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;

import at.ait.dme.yuma.server.config.Config;

public class YUMAWebSession extends AuthenticatedWebSession {

	private static final long serialVersionUID = -6703698595455093437L;

	public YUMAWebSession(Request request) {
        super(request);
    }

    @Override
    public boolean authenticate(final String username, final String password) {
    	Config config = Config.getInstance();
        return username.equals(config.getAdminUsername()) && password.equals(config.getAdminPassword());
    }

    @Override
    public Roles getRoles() {
        if (isSignedIn())
            return new Roles(Roles.ADMIN);

        return null;
    }
    
}