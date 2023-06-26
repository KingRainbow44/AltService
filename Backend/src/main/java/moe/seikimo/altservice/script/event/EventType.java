package moe.seikimo.altservice.script.event;

public enum EventType {
    BREAK_BLOCK,
    PLACE_BLOCK,
    ENTITY_MOVE,
    PLAYER_MOVE,
    TAKE_DAMAGE;

    /**
     * @param event The event to get the type of.
     * @return The event type.
     */
    static EventType value(int event) {
        return EventType.values()[event];
    }
}
