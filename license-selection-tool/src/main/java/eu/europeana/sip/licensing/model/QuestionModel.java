package eu.europeana.sip.licensing.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import eu.europeana.sip.api.XStreamable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of QuestionModel. Questions are added as a key-value pair with
 * an identifier and the qu\estion object. Navigating through the questions will happen
 * based on the <tt>current</tt> question.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("questionnaire")
public class QuestionModel implements XStreamable {

    private List<Result> results = new ArrayList<Result>();
    private List<Answer> answers = new ArrayList<Answer>();
    private LinkedList<Question> questions = new LinkedList<Question>();
    private View view;
    transient private Question current;

    /**
     * The first question in the list. This question becomes the current question.
     *
     * @return The first question.
     */
    public Question getFirst() {
        current = questions.getFirst();
        return current;
    }

    /**
     * The last question in the list. This question becomes the last question.
     *
     * @return The last question.
     */
    public Question getLast() {
        current = questions.getLast();
        return current;
    }

    /**
     * Linking the questions.
     * <p/>
     * <ul>
     * <li>When the current question is absent, the provided
     * question will become the current question.
     * </ul>
     *
     * @param question A new question.
     */
    public void add(Question question) {
        questions.add(question);
        if (null == current) {
            current = question;
        }
    }

    public void add(Result result) {
        results.add(result);
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    /**
     * Determine if a previous question is present relative to the current question.
     *
     * @return True if there is.
     */
    public boolean hasPrevious() {
        return (questions.indexOf(current) > 0);
    }

    /**
     * Rerturns the previous question if available.
     *
     * @return The previous question.
     */
    public Question previous() {
        if (0 == questions.size()) {
            throw new NullPointerException("There are no questions.");
        }
        current = questions.get(questions.indexOf(current) - 1);
        System.out.printf("Question %d/%d%n", questions.indexOf(current), questions.size());
        return current;
    }

    /**
     * Determine if a next question is present relative to the current question.
     *
     * @return True if there is.
     */
    public boolean hasNext() {
        return ((questions.indexOf(current) + 1) < questions.size());
    }

    /**
     * Return the next question if available. This question becomes the current question.
     *
     * @return The next question.
     */
    public Question next() {
        if (0 == questions.size()) {
            throw new NullPointerException("There are no questions.");
        }
        if (null != current) {
            current = questions.get(questions.indexOf(current) + 1);
        }
        else {
            current = questions.get(0);
        }
        System.out.printf("Question %d/%d%n", questions.indexOf(current), questions.size());
        return current;
    }

    /**
     * Returns the current question.
     *
     * @return The current question.
     */
    public Question getCurrent() {
        return current;
    }

    public List<Result> getResults() {
        return results;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @Override
    public String toXML() {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(this.getClass());
        return xStream.toXML(this);
    }

    @Override
    public String toString() {
        return "QuestionModel{" +
                "questions=" + (null != questions ? questions.size() : null) +
                " results=" + (null != results ? results.size() : null) +
                '}';
    }
}
