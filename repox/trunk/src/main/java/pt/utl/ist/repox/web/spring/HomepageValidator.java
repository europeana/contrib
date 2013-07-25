package pt.utl.ist.repox.web.spring;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author Georg Petz
 */
public class HomepageValidator implements ConstraintValidator<Homepage, String> {

    @Override
    public void initialize(final Homepage constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return exists(value);
    }

    /**
     *
     * check whether a web page exists or not.
     *
     * @param URLName
     * @return
     * @throws IOException
     */
    public static boolean exists(String URLName) {
        boolean exists = true;
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return con.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException ex) {
            exists = false;
        }
        return exists;
    }
}
