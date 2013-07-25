package eu.europeana.sip.licensing.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import eu.europeana.sip.api.XStreamable;

/**
 * Launching custom views
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("view")
public class View implements Navigable, XStreamable {

    private String name;
    private Navigable previous;

    public View(String name) {
        this.name = name;
    }

    @Override
    public Navigable previous() {
        return previous;
    }

    @Override
    public void setPrevious(Navigable navigable) {
        this.previous = navigable;
    }

    @Override
    public String toXML() {
        XStream xStream = new XStream();
        xStream.processAnnotations(this.getClass());
        return xStream.toXML(this);
    }

    @Override
    public String toString() {
        return name;
    }
}