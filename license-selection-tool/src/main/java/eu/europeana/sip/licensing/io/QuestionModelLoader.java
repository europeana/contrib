package eu.europeana.sip.licensing.io;

import com.thoughtworks.xstream.XStream;
import eu.europeana.sip.licensing.model.QuestionModel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Map the XML source to a QuestionModel.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public abstract class QuestionModelLoader {

    public static QuestionModel load(String file, ClassLoader classLoader) throws IOException {
        return load(new File(file), classLoader);
    }

    public static QuestionModel load(File file, ClassLoader classLoader) throws IOException {
        FileReader fileReader = new FileReader(file);
        int i;
        StringBuffer buffer = new StringBuffer();
        // todo: read buffered
        while (-1 != (i = fileReader.read())) {
            buffer.append((char) i);
        }
        XStream xStream = new XStream();
        xStream.setClassLoader(classLoader);
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(QuestionModel.class);
        return (QuestionModel) xStream.fromXML(buffer.toString());
    }
}
