package eu.europeana.sip.licensing.model;

import com.thoughtworks.xstream.XStream;
import eu.europeana.sip.licensing.io.QuestionModelLoader;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Serializing Questions from and to files.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class TestPersistence {

    private File questionnaireFile = new File("questionnaire.xml");

    @Test
    public void saveQuestionnaire() throws IOException {
        QuestionModel questionModel = new QuestionModel();

        // Results
        Result freeAccessResult = new Result("Rights reserved statement", "The following license will be used 'http://www.europeana.eu/rights/rr-f'", "http://www.europeana.eu/rights/rr-f");
        Result restrictedAccessResult = new Result("Rights reserved statement", "The following license will be used 'http://www.europeana.eu/rights/rr-r'", "http://www.europeana.eu/rights/rr-r");
        Result paidAccessResult = new Result("Rights reserved statement", "The following license will be used 'http://www.europeana.eu/rights/rr-p'", "http://www.europeana.eu/rights/rr-p");
        Result unknownCopyrightHolderResult = new Result("The rights statement for this work is 'unkown'", "The following license will be used 'http://www.europeana.eu/rights/unknown'", "http://www.europeana.eu/rights/unknown");
        Result publicDomainResult = new Result("The work you have chosen is in the public domain", "The following license will be used 'http://creativecommons.org/publicdomain/mark/1.0/'", "http://creativecommons.org/publicdomain/mark/1.0/");

        // Questions
        Question publicDomainQuestion = new Question("Is the work in the public domain?");
        Question copyrightHolderQuestion = new Question("Is the copyrightholder known?");
        Question creativeCommonsQuestion = new Question("Are CC licenses applicable?");
        Question accessTypeQuestion = new Question("What kind of access will users have to this work?");

        // Custom views
        View creativeCommonsView = new View("renderCreativeCommonsView");
        creativeCommonsView.setPrevious(creativeCommonsQuestion);

        // Answers
        Answer publicDomainYes = new Answer("yes", "Yes", publicDomainResult);
        Answer publicDomainNo = new Answer("no", "No", copyrightHolderQuestion);
        Answer copyrightYes = new Answer("yes", "Yes it is known", creativeCommonsQuestion);
        Answer copyrightNo = new Answer("no", "No it is not known", unknownCopyrightHolderResult);
        Answer creativeCommonsNo = new Answer("no", "No", accessTypeQuestion);
        Answer creativeCommonsYes = new Answer("yes", "Yes", creativeCommonsView);
        Answer accessTypeFree = new Answer("free", "Free", freeAccessResult);
        Answer accessTypeRestricted = new Answer("restricted", "Restricted", restrictedAccessResult);
        Answer accessTypePaid = new Answer("paid", "Paid", paidAccessResult);

        // Set the previous screens for navigation
        copyrightHolderQuestion.setPrevious(publicDomainQuestion);
        creativeCommonsQuestion.setPrevious(copyrightHolderQuestion);
        accessTypeQuestion.setPrevious(creativeCommonsQuestion);
        unknownCopyrightHolderResult.setPrevious(copyrightHolderQuestion);
        publicDomainResult.setPrevious(publicDomainQuestion);
        freeAccessResult.setPrevious(accessTypeQuestion);
        paidAccessResult.setPrevious(accessTypeQuestion);
        restrictedAccessResult.setPrevious(accessTypeQuestion);

        // Add the possible answers to the questions
        publicDomainQuestion.addAnswer(publicDomainYes);
        publicDomainQuestion.addAnswer(publicDomainNo);
        copyrightHolderQuestion.addAnswer(copyrightYes);
        copyrightHolderQuestion.addAnswer(copyrightNo);
        creativeCommonsQuestion.addAnswer(creativeCommonsYes);
        creativeCommonsQuestion.addAnswer(creativeCommonsNo);
        accessTypeQuestion.addAnswer(accessTypeFree);
        accessTypeQuestion.addAnswer(accessTypeRestricted);
        accessTypeQuestion.addAnswer(accessTypePaid);

        // Add the items to the question model
        questionModel.add(publicDomainQuestion);
        questionModel.add(copyrightHolderQuestion);
        questionModel.add(creativeCommonsQuestion);
        questionModel.add(accessTypeQuestion);

        questionModel.add(publicDomainResult);
        questionModel.add(unknownCopyrightHolderResult);
        questionModel.add(freeAccessResult);
        questionModel.add(paidAccessResult);
        questionModel.add(restrictedAccessResult);

        questionModel.add(publicDomainYes);
        questionModel.add(publicDomainNo);
        questionModel.add(copyrightYes);
        questionModel.add(copyrightNo);
        questionModel.add(creativeCommonsYes);
        questionModel.add(creativeCommonsNo);
        questionModel.add(accessTypeFree);
        questionModel.add(accessTypePaid);
        questionModel.add(accessTypeRestricted);

        System.out.printf("%s%n", questionModel.toXML());

        FileWriter fileWriter = new FileWriter(questionnaireFile);
        fileWriter.write(questionModel.toXML());
        fileWriter.flush();
        fileWriter.close();
        System.out.printf("%d bytes written to %s%n", questionnaireFile.length(), questionnaireFile.getAbsolutePath());
    }

    @Test
    public void loadQuestionnaire() throws IOException {
        FileReader fileReader = new FileReader(questionnaireFile);
        int i;
        StringBuffer buffer = new StringBuffer();
        while (-1 != (i = fileReader.read())) {
            buffer.append((char) i);
        }
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(QuestionModel.class);
        QuestionModel questionModel = (QuestionModel) xStream.fromXML(buffer.toString());
        System.out.printf("=== %s ===%n%n", questionModel);
        for (Question question : questionModel.getQuestions()) {
            System.out.printf("* %s%n%n", question);
            for (Object $answer : question.getAnswers()) {
                Answer answer = (Answer) $answer;
                System.out.printf("\t%s%n", answer);
                if (answer.getFollowUp() instanceof Result) {
                    System.out.printf("\t\tAnswer %s results in %s%n", answer.getValue(), answer.getFollowUp());
                    System.out.printf("\t\tWill use the following value %s%n", ((Result) answer.getFollowUp()).getValue());
                }
                if (answer.getFollowUp() instanceof Question) {
                    System.out.printf("\t\tAnswer %s forwards to question %s%n", answer.getValue(), answer.getFollowUp());
                }
            }
            System.out.printf("%n%n");
        }
        for (Result result : questionModel.getResults()) {
            System.out.printf("%s%n", result);
        }
    }

    @Test
    public void testQuestionModelLoader() throws IOException {
        QuestionModelLoader.load("questionnaire.xml", getClass().getClassLoader());
    }
}
