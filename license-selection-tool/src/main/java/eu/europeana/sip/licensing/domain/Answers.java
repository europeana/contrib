package eu.europeana.sip.licensing.domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of answers from the Creative Commons License selection panel.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("answers")
public class Answers {

    private String locale;

    @XStreamAlias("license-standard")
    private LicenseStandard licenseStandard;

    @XStreamAlias("work-info")
    private WorkInfo workInfo;

    @XStreamAlias("jurisdiction")
    private Jurisdiction jurisdiction;

    public static class LicenseStandard {

        private String commercial;
        private String derivatives;
        private String jurisdiction;

        public void setCommercial(String commercial) {
            this.commercial = commercial;
        }

        public void setDerivatives(String derivatives) {
            this.derivatives = derivatives;
        }

        public void setJurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
        }

    }

    static class WorkInfo {

        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

    }

    @XStreamAlias("jursdiction")
    static class Jurisdiction {

        @XStreamAlias("jurisdictions")
        private List<String> jurisdictions = new ArrayList<String>();

        public void add(String jurisdiction) {
            jurisdictions.add(jurisdiction);
        }

    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setLicenseStandard(LicenseStandard licenseStandard) {
        this.licenseStandard = licenseStandard;
    }

    public void setWorkInfo(WorkInfo workInfo) {
        this.workInfo = workInfo;
    }

    public String toXML() {
        XStream xStream = new XStream();
        xStream.processAnnotations(new Class[]{Answers.class, LicenseStandard.class});
        return xStream.toXML(this);
    }
}
