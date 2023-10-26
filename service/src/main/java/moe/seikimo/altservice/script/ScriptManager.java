package moe.seikimo.altservice.script;

import lombok.Data;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerActions;
import moe.seikimo.altservice.script.event.EventType;
import moe.seikimo.altservice.script.event.ScriptArgs;
import moe.seikimo.altservice.script.event.ScriptEvent;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.script.Bindings;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages scripts for a player.
 */
@Data public final class ScriptManager {
    private final Player player;

    private final ScriptLib scriptLib
            = new ScriptLib(this.getPlayer());
    private final List<Bindings> scripts
            = new LinkedList<>();
    private final Map<EventType, List<ScriptEvent>> events
            = new ConcurrentHashMap<>();

    /**
     * @return The player's actions.
     */
    public PlayerActions getActions() {
        return this.getPlayer().getActions();
    }

    /**
     * Initializes all behaviors.
     * Should be called when the behaviors are updated.
     */
    public void initBehaviors() {
        this.scripts.clear();
        this.events.clear();

        var behaviors = new ArrayList<>(
                this.getActions().getBehaviors());
        for (var behavior : behaviors) {
            try {
                var bindings = ScriptLoader.getBindings();
                bindings.put("Options", CoerceJavaToLua.coerce(this));
                bindings.put("Player", this.getPlayer().getUsername());

                // Get the script data.
                var result = ScriptLoader.invokeScript(
                        behavior, true, bindings);
                if (result == null) continue;

                // Call the init function.
                ScriptLoader.call(result.get("init"));
                // Register events.
                this.registerEvents(result);

                // Add the bindings to the list.
                this.scripts.add(result);
            } catch (Exception ignored) { }
        }
    }

    /**
     * Executes all behaviors.
     */
    public void tickBehaviors() {
        for (var function : this.scripts) {
            // Call the tick function.
            ScriptLoader.call(function.get("tick"));
        }
    }

    /**
     * Registers events stated in the script.
     *
     * @param bindings The bindings to register events for.
     */
    public void registerEvents(Bindings bindings) {
        // Set the 'ScriptLib' variable.
        bindings.put("ScriptLib",
                CoerceJavaToLua.coerce(this.getScriptLib()));

        // Fetch the event bindings.
        var events = ScriptLoader.getSerializer()
                .toList(bindings.get("events"), ScriptEvent.class);
        if (events.isEmpty()) return;

        // Register the events.
        for (var event : events) {
            event.setBindings(bindings);

            var type = event.getEvent();
            // Create a new list if it doesn't exist.
            if (!this.getEvents().containsKey(type)) {
                this.getEvents().put(type, new LinkedList<>());
            }
            // Add the event to the list.
            this.getEvents().get(type).add(event);
        }
    }

    /**
     * Invokes an event.
     *
     * @param type The type of event to invoke.
     * @param args The arguments to pass to the event.
     */
    public void invokeEvent(EventType type, ScriptArgs args) {
        // Check if the event exists.
        if (!this.getEvents().containsKey(type)) return;

        // Invoke the registered events.
        var events = this.getEvents().get(type);
        for (var event : events) {
            var result = event.callCondition(args);
            if (result) event.callAction(args);
        }
    }
}
