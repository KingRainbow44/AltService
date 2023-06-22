package moe.seikimo.altservice.script.event;

public enum EventType {
    BLOCK_CHANGE,
    ENTITY_MOVE,
    PLAYER_MOVE,
    TAKE_DAMAGE,
    INV_CHANGE,
    MESSAGE_SENT;

    /**
     * @param event The event to get the type of.
     * @return The event type.
     */
    static EventType value(int event) {
        return EventType.values()[event];
    }
}
