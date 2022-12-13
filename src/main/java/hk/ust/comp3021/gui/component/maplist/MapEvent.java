package hk.ust.comp3021.gui.component.maplist;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event related to opening or deleting a map.
 */
public class MapEvent extends Event {
    /**
     * The event type of opening a map.
     */
    public static EventType<MapEvent> OPEN_MAP_EVENT_TYPE = new EventType<>("OPEN_MAP");

    private final MapModel model;

    /**
     * @param type  The type of the event.
     * @param model The map model.
     */
    public MapEvent(EventType<? extends MapEvent> type, MapModel model) {
        super(type);
        this.model = model;
    }

    /**
     * @return The map model related to the event.
     */
    public MapModel getModel() {
        return model;
    }
}
