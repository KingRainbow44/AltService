package moe.seikimo.altservice.script.event;

import lombok.Data;
import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.script.ScriptLoader;
import moe.seikimo.altservice.utils.EncodingUtils;
import org.cloudburstmc.math.vector.Vector3i;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.script.Bindings;

/**
 * The data backing a scripting event.
 */
@Data public final class ScriptEvent {
    public int event;
    public String condition;
    public String action;

    /* These are not required arguments. */
    public LuaTable pos;

    private transient Bindings bindings;

    /**
     * @return The event type.
     */
    public EventType getEvent() {
        return EventType.value(this.event);
    }

    /**
     * @return The position of the event.
     */
    public Vector3i getPosition() {
        return EncodingUtils.tableToBlock(this.pos);
    }

    /**
     * Calls the condition function.
     *
     * @param args The arguments to pass to the function.
     * @return Whether the condition is true.
     */
    public boolean callCondition(ScriptArgs args) {
        try {
            // Get the function.
            var func = bindings.get(this.getCondition());
            if (!(func instanceof LuaFunction function)) return false;

            // Call the function.
            var result = function.call(
                    ScriptLoader.scriptLibValue,
                    CoerceJavaToLua.coerce(args));

            return result instanceof LuaBoolean && result.toboolean();
        } catch (Exception exception) {
            AltBackend.getLogger().debug("Lua exception when calling condition.", exception);
            return false;
        }
    }

    /**
     * Calls the action function.
     *
     * @param args The arguments to pass to the function.
     */
    public void callAction(ScriptArgs args) {
        try {
            // Get the function.
            var func = bindings.get(this.getAction());
            if (!(func instanceof LuaFunction function)) return;

            // Call the function.
            function.call(
                    ScriptLoader.scriptLibValue,
                    CoerceJavaToLua.coerce(args));
        } catch (Exception exception) {
            AltBackend.getLogger().debug("Lua exception when calling action.", exception);
        }
    }
}
