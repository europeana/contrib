package at.ac.ait.dme.gtv.client.listeners;

/**
 * Listener class for handling the case when the user hovers the mouse
 * over a text fragment highlighted by the geoparser.
 *
 * @author Rainer Simon
 */
public abstract class TextMouseListener {

    //public abstract void onMouseOver(String id, int clientX, int clientY);

    //public abstract void onMouseOut(String id, int clientX, int clientY);

    public abstract void onClick(String id, int clientX, int clientY);


}
