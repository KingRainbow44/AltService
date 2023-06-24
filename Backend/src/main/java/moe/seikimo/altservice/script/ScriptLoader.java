package moe.seikimo.altservice.script;

import lombok.Getter;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuajContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptLoader {
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Lua Script");

    @Getter private static final ScriptEngineManager manager
            = new ScriptEngineManager();
    @Getter private static final ScriptEngine engine
            = manager.getEngineByName("luaj");
    @Getter private static final ScriptEngineFactory factory
            = engine.getFactory();

    @Getter private static final ScriptSerializer serializer
            = new ScriptSerializer();
    @Getter private static final ScriptLib scriptLib
            = new ScriptLib();

    private static final Map<String, CompiledScript> cache
            = new ConcurrentHashMap<>();

    /**
     * Attempts to initialize the script engine.
     */
    public static synchronized void initialize() {
        try {
            var context = ScriptLoader.getContext();

            context.globals.set("ScriptLib", CoerceJavaToLua.coerce(scriptLib));
            ScriptLoader.getLogger().info("Initialized LuaJ.");
        } catch (Exception ignored) {
            ScriptLoader.getLogger().warn("Failed to initialize LuaJ.");
        }
    }

    /**
     * @return The current LuaJ context.
     */
    public static LuajContext getContext() {
        return (LuajContext) ScriptLoader.getEngine().getContext();
    }

    /**
     * Fetches a compiled script from the cache.
     * If the script is not cached, it will be compiled and cached.
     *
     * @param path The path to the script.
     * @return The compiled script.
     */
    public static CompiledScript getScript(String path) {
        return ScriptLoader.getScript(path, true);
    }

    /**
     * Fetches a compiled script from the cache.
     * If the script is not cached, it will be compiled and cached.
     *
     * @param path The path to the script.
     * @param useCache Whether to use the cache.
     * @return The compiled script.
     */
    public static CompiledScript getScript(String path, boolean useCache) {
        if (!useCache) ScriptLoader.cache.remove(path);
        return ScriptLoader.cache.computeIfAbsent(path, k -> {
            try (var file = Files.newBufferedReader(new File("scripts", path).toPath())) {
                var script = ((Compilable) ScriptLoader.getEngine()).compile(file);
                ScriptLoader.getLogger().debug("Loaded script {}.", path);
                return script;
            } catch (Exception ignored) {
                ScriptLoader.getLogger().warn("Unable to load script {}.", path);
                return null;
            }
        });
    }

    /**
     * @return A new bindings instance.
     */
    public static Bindings getBindings() {
        return ScriptLoader.getEngine().createBindings();
    }

    /**
     * Invokes a Lua script.
     *
     * @param path The path to the script.
     * @return The bindings from the script.
     */
    public static Bindings invokeScript(String path) {
        return ScriptLoader.invokeScript(path, true);
    }

    /**
     * Invokes a Lua script.
     *
     * @param path The path to the script.
     * @param useCache Whether to use the cache.
     * @return The bindings from the script.
     */
    public static Bindings invokeScript(String path, boolean useCache) {
        try {
            // Fetch the script.
            var script = ScriptLoader.getScript(path, useCache);
            if (script == null) {
                return null;
            }

            // Invoke the script.
            var bindings = ScriptLoader.getBindings();
            script.eval(bindings);

            return bindings;
        } catch (Exception exception) {
            ScriptLoader.getLogger().warn("Unable to invoke script {}.", path);
            ScriptLoader.getLogger().info("Unable to invoke script.", exception);
        }

        return null;
    }
}
