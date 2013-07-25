package eu.europeana.sip.licensing.network;

import eu.europeana.sip.licensing.domain.Answers;
import eu.europeana.sip.licensing.domain.License;
import eu.europeana.sip.licensing.domain.CreativeCommonsModel;

import java.io.IOException;

/**
 * An implementation of the Creative Commons licensing framework.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public interface LicenseService {

    /**
     * Request the appropriate license based on the given answers.
     *
     * @param answers The result of the questions.
     * @return The Creative Commons License.
     * @throws java.io.IOException Error while executing POST-method.
     */
    License requestLicense(Answers answers) throws IOException;

    /**
     * Retrieve the Standard License set from Creative Commons.
     *
     * @return The Standard License set.
     * @throws IOException Error while retrieving Standard License set.
     */
    CreativeCommonsModel retrieveStandardLicenseSet() throws IOException;
}
