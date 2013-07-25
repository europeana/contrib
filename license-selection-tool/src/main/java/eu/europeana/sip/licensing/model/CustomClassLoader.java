package eu.europeana.sip.licensing.model;

/**
 * A singleton instance of the ClassLoader.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class CustomClassLoader {

    private static ClassLoader instance;

    public CustomClassLoader(ClassLoader $instance) {
        if (null == instance) {
            instance = $instance;
        }
    }

    public static ClassLoader getInstance() {
        if (null == instance) {
            return CustomClassLoader.class.getClassLoader();
        }
        return instance;
    }

}
