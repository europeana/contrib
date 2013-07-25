package eu.europeana.sip.licensing.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import eu.europeana.sip.api.XStreamable;

/**
 * An answer can point to a <tt>followUp</tt> {@link Question} or
 * a <tt>result</tt> {@link Result}.
 * <p/>
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("answer")
public class Answer implements XStreamable, Navigable {

    @XStreamAsAttribute
    private String value;
    private String text;

    @XStreamAlias("follow-up")
    private Navigable followUp;

    @XStreamAlias("icon-url")
    private String iconUrl;

    private Navigable previous;

    public Answer() {
    }

    public Answer(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public Answer(String value, String text, Navigable followUp) {
        this.value = value;
        this.text = text;
        this.followUp = followUp;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public Navigable getFollowUp() {
        return followUp;
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
        return "Answer{" +
                "value='" + value + '\'' +
                ", text='" + text + '\'' +
                ", followUp='" + followUp + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
