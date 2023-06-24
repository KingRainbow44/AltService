package moe.seikimo.altservice.script;

import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.utils.EncodingUtils;
import org.luaj.vm2.LuaTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class ScriptLib {
    private final Map<String, Integer> globals
            = new ConcurrentHashMap<>();

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

    /**
     * Logs a table to the console.
     * This is used for debugging purposes.
     *
     * @param table The table to log.
     */
    public void logTable(LuaTable table) {
        var map = ScriptLoader.getSerializer()
                .toMap(table, Object.class);
        ScriptLoader.getLogger().info(
                EncodingUtils.jsonEncode(map));
    }

    /**
     * Sets a global value.
     *
     * @param key The key to set.
     * @param value The value to set.
     */
    public void setGlobal(String key, int value) {
        this.globals.put(key, value);
    }

    /**
     * Fetches a global value.
     *
     * @param key The key to fetch.
     * @return The value.
     */
    public int getGlobal(String key) {
        return this.globals.getOrDefault(key, -1);
    }

    /**
     * Fetches the position of a player.
     *
     * @param username The username of the player.
     * @return The position.
     */
    public LuaTable getPosition(String username) {
        // Fetch the player.
        var player = PlayerManager.createPlayer(username, -1);
        var position = player.getPosition();

        // Serialize the position.
        var serialized = new LuaTable();
        serialized.set("x", position.getX());
        serialized.set("y", position.getY());
        serialized.set("z", position.getZ());

        return serialized;
    }

    /**
     * Sends a message to a player.
     *
     * @param username The username of the player.
     * @param message The message to send.
     */
    public void sendMessage(String username, String message) {
        // Fetch the player.
        var player = PlayerManager.createPlayer(username, -1);
        // Send the message.
        player.sendMessage(message);
    }

    /**
     * Moves a player by a delta.
     *
     * @param username The username of the player.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     */
    public void move(String username, float x, float y, float z) {
        // Fetch the player.
        var player = PlayerManager.createPlayer(username, -1);

        // Get the current position.
        var current = player.getPosition();
        // Add the delta.
        current = current.add(x, y, z);

        // Move the player.
        player.move(current, player.getRotation());
    }

    /**
     * Rotates a player by a delta.
     *
     * @param username The username of the player.
     * @param pitch The pitch.
     * @param yaw The yaw.
     */
    public void rotate(String username, float pitch, float yaw) {
        // Fetch the player.
        var player = PlayerManager.createPlayer(username, -1);

        // Get the current rotation.
        var current = player.getRotation();
        // Add the delta.
        current = current.add(pitch, yaw, 0);

        // Move the player.
        player.move(player.getPosition(), current);
    }

    /**
     * Breaks a block within a delta.
     *
     * @param username The username of the player.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     */
    public void breakBlock(String username, int x, int y, int z) {
        // Fetch the player.
        var player = PlayerManager.createPlayer(username, -1);

        // Get the current position.
        var current = player.getPosition();
        // Add the delta.
        current = current.add(x, y, z);

        // Break the block.
        player._break(current.toInt());
    }
}
