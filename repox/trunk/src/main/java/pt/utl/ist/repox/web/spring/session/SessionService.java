package pt.utl.ist.repox.web.spring.session;

/**
 *
 * @author Georg Petz
 */
public interface SessionService {

    Session getRepoxSession();

    void setMessage(String message);

    String getMessage();

    void setSourcePage(String sourcePage);

    String getSourcePage();
}
