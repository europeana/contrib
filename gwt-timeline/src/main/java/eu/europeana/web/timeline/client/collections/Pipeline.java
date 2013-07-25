package eu.europeana.web.timeline.client.collections;

import com.google.gwt.user.client.rpc.IsSerializable;
import eu.europeana.web.timeline.client.events.EventModel;
import eu.europeana.web.timeline.client.ui.CarouselItem;

import java.util.LinkedList;

/**
 * Underlying collection that holds all cached items from Solr. Diagrams of this collection
 * can be found <a href='http://europeanalabs.eu/attachment/ticket/1184/PipelineImpl.png' target='_blank'>here</a>
 * <p/>
 * The pipeline will fire an {@link eu.europeana.web.timeline.client.collections.Pipeline.ActivationEvent} when an
 * item is activated.
 * <p/>
 * <p/>
 * <p/>
 * The following sub-collections are available:
 * <p/>
 * <ul>
 * <li> Visible items via {@link #getVisibleItems()}
 * <li> Invisible items via {@link #getInvisibleItems()}
 * <li> Cached items via {@link #getCachedItems()}
 * </ul>
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 * @see eu.europeana.web.timeline.client.ui.CarouselItemImpl
 * @see eu.europeana.web.timeline.client.events.EventModel
 */
public abstract class Pipeline<I extends CarouselItem<I, E>, E, L> extends LinkedList<I> implements EventModel<L> {

    /**
     * Returns the visible items in the Pipeline.
     *
     * @return The visible items.
     */
    public abstract LinkedList<I> getVisibleItems();

    /**
     * The items that are in the pipeline but not visible. These items are appended
     *
     * @return The invisible items.
     */
    public abstract LinkedList<I> getInvisibleItems();

    /**
     * Returns alle the cached items in the current collection.
     *
     * @return The cached items in this collection.
     * @see eu.europeana.web.timeline.client.network.Cacheable
     */
    public abstract LinkedList<I> getCachedItems();

    /**
     * Find the next item in the cache. This is the first invisible item that exists
     * in the Pipeline after the last visible item.
     *
     * @return The next item in cache.
     */
    public abstract I getNextInCache();

    /**
     * Find the previous item in the cache. This is the first invisible item that exists
     * in the Pipeline before the first visible item.
     *
     * @return The previous item in cache.
     */
    public abstract I getPreviousInCache();


    /**
     * Fired by {@link Pipeline.ActivationListener}
     */
    public enum ActivationEvent {
        /**
         * The absolute last item of the Pipeline has been selected.
         */
        LAST_ITEM_SELECTED,
        /**
         * The absolute first item of the Pipeline has been selected.
         */
        FIRST_ITEM_SELECTED,
        /**
         * The last visible item of the Pipeline has been selected.
         */
        LAST_VISIBLE_ITEM_SELECTED,
        /**
         * The first visible item of the Pipeline has been selected.
         */
        FIRST_VISIBLE_ITEM_SELECTED,
        /**
         * An item in the Pipeline has been activated.
         */
        ITEM_ACTIVATED,
        /**
         * An item in the Pipeline has been deactivated.
         */
        ITEM_DEACTIVATED
    }

    public interface ActivationListener<E extends ActivationEvent, I> extends IsSerializable {

        /**
         * Activation event has been fired.
         *
         * @param event The event.
         * @param item  The source item.
         */
        public void activationEvent(E event, I item);
    }

}
