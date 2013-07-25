package eu.europeana.sip.licensing.model;

/**
 * Adds navigation support to an item.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public interface Navigable {

    Navigable previous();

    void setPrevious(Navigable navigable);
}
