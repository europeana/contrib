package eu.europeana.sip.licensing.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import eu.europeana.sip.api.XStreamable;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic question.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
@XStreamAlias("question")
public class Question implements XStreamable, Navigable {

    @XStreamAsAttribute
    private String title;
    private String description;
    private List<Answer> answers = new ArrayList<Answer>();
    private Navigable previous;

    public Question(String title) {
        this.title = title;
    }

    public Question(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String toXML() {
        XStream xStream = new XStream();
        xStream.processAnnotations(new Class[]{
                this.getClass(), Answer.class
        });
        return xStream.toXML(this);
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
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", answers=" + answers.size() +
                '}';
    }
}
