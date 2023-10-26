package moe.seikimo.altservice.plugin;

import moe.seikimo.altservice.event.Event;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public record PluginListener<T extends Event>(Plugin registrar, Consumer<T> listener) {
    /**
     * Invokes the listener.
     *
     * @param event The event.
     */
    public void invoke(Event event) {
        this.listener.accept((T) event);
    }
}
