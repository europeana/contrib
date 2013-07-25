package eu.europeana.sip.licensing.gui;

import javax.swing.*;

/**
 * Events dispatched by {@link QuestionView}
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 * @see QuestionView
 * @deprecated Handled in a different way.
 */
@Deprecated
public interface QuestionListener<E> {

    /**
     * The user has selected a radio button.
     *
     * @param value The selected radio button.
     */
    public void selected(JRadioButton value);

    /**
     * The user has pressed the back button.
     */
    public void backButtonPressed();

    /**
     * The user has pressed the choose button.
     */
    public void chooseButtonPressed();

    /**
     * The user has pressed the choose button.
     *
     * @param e The event.
     */
    public void chooseButtonPressed(E e);
}
