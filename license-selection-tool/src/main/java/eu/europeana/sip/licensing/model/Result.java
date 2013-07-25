package eu.europeana.sip.licensing.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import eu.europeana.sip.api.XStreamable;

/**
 * And end result. This is the final item displayed in a questionnaire.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("result")
public class Result implements XStreamable, Navigable {

    private String title;
    private String description;
    private String value;
    private Navigable previous;

    @XStreamAlias("icon-url")
    private String iconUrl;

    /**
     * @param title       The title.
     * @param description The description of the result.
     * @deprecated A value is necessary.
     */
    @Deprecated
    public Result(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * @param title       The title.
     * @param description The description of the result.
     * @param value       The value of the result.
     */
    public Result(String title, String description, String value) {
        this.title = title;
        this.description = description;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public Navigable previous() {
        return previous;
    }

    @Override
    public void setPrevious(Navigable navigable) {
        previous = navigable;
    }

    @Override
    public String toXML() {
        XStream xStream = new XStream();
        xStream.processAnnotations(this.getClass());
        return xStream.toXML(this);
    }

    @Override
    public String toString() {
        return "Result{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", value='" + value + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
