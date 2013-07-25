package eu.europeana.sip.licensing.domain;

import com.thoughtworks.xstream.XStream;
import eu.europeana.sip.licensing.network.LicenseService;
import eu.europeana.sip.licensing.network.LicenseServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Test marshalling and unmarshalling of Answers with XStream.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class TestAnswers {

    private final String EXPECTED_ANSWER = "<answers>\n" +
            "  <locale>en</locale>\n" +
            "  <license-standard>\n" +
            "    <commercial>n</commercial>\n" +
            "    <derivatives>y</derivatives>\n" +
            "  </license-standard>\n" +
            "  <work-info>\n" +
            "    <title>The Title</title>\n" +
            "  </work-info>\n" +
            "</answers>";


    private Answers createAnswers() {
        Answers answers = new Answers();
        answers.setLocale("en");

        Answers.LicenseStandard licenseStandard = new Answers.LicenseStandard();
        licenseStandard.setCommercial("n");
        licenseStandard.setDerivatives("y");
        answers.setLicenseStandard(licenseStandard);

        Answers.WorkInfo workInfo = new Answers.WorkInfo();
        workInfo.setTitle("The Title");
        answers.setWorkInfo(workInfo);
        return answers;
    }

    @Test
    public void createAnswer() throws JAXBException, IOException {
        Answers answers = createAnswers();
        XStream xStream = new XStream();
        xStream.processAnnotations(Answers.class);
        Assert.assertEquals(EXPECTED_ANSWER, xStream.toXML(answers));
    }

    @Test
    public void requestLicense() throws JAXBException, IOException {
        Answers answers = createAnswers();
        LicenseService licenseService = new LicenseServiceImpl();
        License license = licenseService.requestLicense(answers);
    }

    @Test
    public void createQuestions() throws IOException {
        LicenseService licenseService = new LicenseServiceImpl();
        licenseService.retrieveStandardLicenseSet();
    }

    @Test
    public void findCommercial() throws IOException {
        LicenseService licenseService = new LicenseServiceImpl();
        CreativeCommonsModel creativeCommonsModel = licenseService.retrieveStandardLicenseSet();
        System.out.printf("--- Commercial from Creative Commons ---%n");
        CreativeCommonsModel.Field field = creativeCommonsModel.findFieldById("commercial");
        System.out.printf("%s%n", field.getLabel());
        for (CreativeCommonsModel.Field.Enum _enum : field.getEnum()) {
            System.out.printf("%s%n", _enum);
        }
        System.out.printf("%n%n");
    }


    @Test
    public void findJurisdictions() throws IOException {
        LicenseService licenseService = new LicenseServiceImpl();
        CreativeCommonsModel creativeCommonsModel = licenseService.retrieveStandardLicenseSet();
        System.out.printf("--- Jurisdictions from Creative Commons ---%n");
        CreativeCommonsModel.Field field = creativeCommonsModel.findFieldById("jurisdiction");
        System.out.printf("%s%n", field.getLabel());
        for (CreativeCommonsModel.Field.Enum _enum : field.getEnum()) {
            System.out.printf("%s%n", _enum);
        }
        System.out.printf("%n%n");
    }

    @Test
    public void findDerivatives() throws IOException {
        LicenseService licenseService = new LicenseServiceImpl();
        CreativeCommonsModel creativeCommonsModel = licenseService.retrieveStandardLicenseSet();
        CreativeCommonsModel.Field field = creativeCommonsModel.findFieldById("derivatives");
        System.out.printf("--- Derivatives from Creative Commons ---%n");
        System.out.printf("%s%n", field.getLabel());
        for (CreativeCommonsModel.Field.Enum _enum : field.getEnum()) {
            System.out.printf("%s%n", _enum);
        }
        System.out.printf("%n%n");
    }

    @Test
    public void fillForm() throws IOException {
        Answers answers = new Answers();
        answers.setLocale("en");
        Answers.LicenseStandard licenseStandard = new Answers.LicenseStandard();
        LicenseService licenseService = new LicenseServiceImpl();
        CreativeCommonsModel creativeCommonsModel = licenseService.retrieveStandardLicenseSet();

        // Commercial
        CreativeCommonsModel.Field commercialField = creativeCommonsModel.findFieldById("commercial");
        System.out.printf("%s%n", commercialField.getLabel());
        System.out.printf("\t%s%n", commercialField.getDescription());
        for (CreativeCommonsModel.Field.Enum _enum : commercialField.getEnum()) {
            System.out.printf("\t%s%n\t\t%s%n", _enum.getDescription(), _enum.getLabel());
        }
        int randomCommercialId = (int) Math.ceil(Math.random() * commercialField.getEnum().size());
        licenseStandard.setCommercial(commercialField.getEnum().get(randomCommercialId - 1).getId());

        // Modifications
        CreativeCommonsModel.Field derivativesField = creativeCommonsModel.findFieldById("derivatives");
        System.out.printf("%s%n", derivativesField.getLabel());
        System.out.printf("\t%s%n", derivativesField.getDescription());
        for (CreativeCommonsModel.Field.Enum _enum : derivativesField.getEnum()) {
            System.out.printf("\t%s%n\t\t%s%n", _enum.getDescription(), _enum.getLabel());
        }
        int randomDerivativesId = (int) Math.ceil(Math.random() * derivativesField.getEnum().size());
        licenseStandard.setDerivatives(derivativesField.getEnum().get(randomDerivativesId - 1).getId());

        // Jurisdiction
        CreativeCommonsModel.Field jurisdictionField = creativeCommonsModel.findFieldById("jurisdiction");
        System.out.printf("%s%n", jurisdictionField.getLabel());
        System.out.printf("\t%s%n", jurisdictionField.getDescription());
        for (CreativeCommonsModel.Field.Enum _enum : jurisdictionField.getEnum()) {
            System.out.printf("\t%s:%s%n", _enum.getId(), _enum.getLabel());
        }
        int randomJurisdictionId = (int) Math.ceil(Math.random() * jurisdictionField.getEnum().size());
        licenseStandard.setJurisdiction(jurisdictionField.getEnum().get(randomJurisdictionId - 1).getId());

        answers.setLicenseStandard(licenseStandard);
        System.out.printf("Transmitting the following answer to the server:%n%s%n", answers.toXML());
        License license = licenseService.requestLicense(answers);
        System.out.printf("Response from server:%n%s%n%s%n", license.getLicenseName(), license.getLicenseUri());
    }
}
