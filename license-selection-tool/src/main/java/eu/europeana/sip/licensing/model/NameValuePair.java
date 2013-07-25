package eu.europeana.sip.licensing.model;

/**
* todo: add class description
*
* @author Serkan Demirel <serkan@blackbuilt.nl>
*/
@Deprecated
public class NameValuePair {

    private String value;
    private String name;

    public NameValuePair(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}
