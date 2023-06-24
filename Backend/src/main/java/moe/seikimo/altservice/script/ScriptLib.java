package moe.seikimo.altservice.script;

public final class ScriptLib {
    /**
     * Logs an info message to the console.
     *
     * @param message The message to log.
     */
    public void info(String message) {
        ScriptLoader.getLogger().info(message);
    }

    /**
     * Logs a warning message to the console.
     *
     * @param message The message to log.
     */
    public void warn(String message) {
        ScriptLoader.getLogger().warn(message);
    }

    /**
     * Logs an error message to the console.
     *
     * @param message The message to log.
     */
    public void error(String message) {
        ScriptLoader.getLogger().error(message);
    }
}
