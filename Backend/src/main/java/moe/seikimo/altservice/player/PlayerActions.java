package moe.seikimo.altservice.player;

import lombok.Data;
import moe.seikimo.altservice.script.ScriptLoader;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.script.Bindings;
import java.util.ArrayList;
import java.util.List;

@Data
public final class PlayerActions {
    private boolean follow = false;
    private boolean attack = false;
    private boolean look = false;
    private boolean guard = false;
    private boolean behave = false;

    private boolean guardPlayers = true;
    private boolean guardMobs = true;
    private List<String> behaviors = new ArrayList<>();

    private final List<Bindings> functions = new ArrayList<>();

    /**
     * Initializes all behaviors.
     * Should be called when the behaviors are updated.
     *
     * @param player The player to initialize the behaviors for.
     */
    public void initBehaviors(Player player) {
        this.functions.clear();
        for (var behavior : this.behaviors) {
            try {
                var bindings = ScriptLoader.getBindings();
                bindings.put("Options", CoerceJavaToLua.coerce(this));
                bindings.put("Player", player.getUsername());

                // Get the script data.
                var result = ScriptLoader.invokeScript(
                        behavior, true, bindings);
                if (result == null) continue;

                // Call the init function.
                ScriptLoader.call(result.get("init"));
                // Add the bindings to the list.
                this.functions.add(bindings);
            } catch (Exception ignored) { }
        }
    }

    /**
     * Executes all behaviors.
     */
    public void tickBehaviors() {
        for (var function : this.functions) {
            // Call the tick function.
            ScriptLoader.call(function.get("tick"));
        }
    }
}
