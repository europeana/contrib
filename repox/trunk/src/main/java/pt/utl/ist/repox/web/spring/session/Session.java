package pt.utl.ist.repox.web.spring.session;

import java.io.Serializable;

/**
 *
 * @author Georg Petz
 */
public class Session implements Serializable {

    private String sourcePage;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public Session() {
        sourcePage = "";
        message = "";
    }
}
