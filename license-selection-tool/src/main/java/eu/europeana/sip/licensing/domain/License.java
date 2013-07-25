package eu.europeana.sip.licensing.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Creative Commons License implementation.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("result")
public class License {

    @XStreamAlias("license-uri")
    private String licenseUri;

    @XStreamAlias("license-name")
    private String licenseName;

    @XStreamAlias("rdf")
    private Rdf rdf;

    @XStreamAlias("licenserdf")
    private LicenseRdf licenseRdf;

    private String html;

    static class Rdf {

        @XStreamAlias("rdf:RDF")
        private String rdf;
    }

    static class LicenseRdf {

        @XStreamAlias("rdf:RDF")
        private String rdf;

    }

    public String getLicenseUri() {
        return licenseUri;
    }

    public String getLicenseName() {
        return licenseName;
    }
}
