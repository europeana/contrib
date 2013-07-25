package eu.europeana.sip.licensing.model;

import javax.swing.*;

/**
 * A button group with some adjusted behaviour.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class SelectableButtonGroup extends ButtonGroup {

    private static final String ERROR_SPECIFIC_TYPE = "This group was created with a specific type, adding or removing options is not allowed.";
    private static final String YES = "Yes";
    private static final String NO = "No";

    private boolean locked;

    public enum Type {
        YES_NO
    }

    public SelectableButtonGroup() {
    }

    /**
     * Create a button group with a specific type.
     *
     * @param type The specific type.
     */
    public SelectableButtonGroup(Type type) {
        switch (type) {
            case YES_NO:
                add(new JRadioButton(YES));
                add(new JRadioButton(NO));
                break;
        }
        locked = true;
    }

    @Override
    public void remove(AbstractButton b) {
        if (locked) {
            throw new IllegalStateException(ERROR_SPECIFIC_TYPE);
        }
        super.remove(b);
    }

    @Override
    public void add(AbstractButton b) {
        if (locked) {
            throw new IllegalStateException(ERROR_SPECIFIC_TYPE);
        }
        if (0 == getButtonCount()) {
            b.setSelected(true);
        }
        super.add(b);
    }

    /**
     * todo: use NamedJRadioButton
     *
     * @param buttonCaptions Create JRadioButtons based on the provided buttonCaptions.
     */
    public void add(String... buttonCaptions) {
        for (String buttonCaption : buttonCaptions) {
            add(new JRadioButton(buttonCaption));
        }
    }
}
