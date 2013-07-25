package pt.utl.ist.repox.web.spring.session;

import org.springframework.stereotype.Service;

/**
 *
 * @author Georg Petz
 */
@Service("repoxSessionService")
public class SessionServiceImpl implements SessionService {

    private Session repoxSession;

    public void setRepoxSession(Session repoxSession) {
        this.repoxSession = repoxSession;
    }

    @Override
    public Session getRepoxSession() {
        return repoxSession;
    }

    @Override
    public void setMessage(String message) {
        repoxSession.setMessage(message);
    }

    @Override
    public String getMessage() {
        return repoxSession.getMessage();
    }

    @Override
    public void setSourcePage(String sourcePage) {
        repoxSession.setSourcePage(sourcePage);
    }

    @Override
    public String getSourcePage() {
        return repoxSession.getSourcePage();
    }
}
