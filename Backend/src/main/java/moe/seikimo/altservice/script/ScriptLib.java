package moe.seikimo.altservice.script;

import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.utils.EncodingUtils;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class ScriptLib {
    private final Map<String, Integer> globals
            = new ConcurrentHashMap<>();

    /**
     * @param username The username.
     * @return The player instance.
     */
    private Player getPlayer(String username) {
        return PlayerManager.getPlayer(username);
    }

    /* Scripting methods. */

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
        var player = this.getPlayer(username);
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
        var player = this.getPlayer(username);
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
        var player = this.getPlayer(username);

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
        var player = this.getPlayer(username);

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
        var player = this.getPlayer(username);

        // Get the current position.
        var current = player.getPosition();
        // Add the delta.
        current = current.add(x, y, z);

        // Break the block.
        player._break(current.toInt());
    }

    /**
     * Places a block within a delta.
     *
     * @param username The username of the player.
     * @param block The block to place.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     */
    public int placeBlock(String username, String block, int x, int y, int z) {
        // Fetch the player.
        var player = this.getPlayer(username);

        // Get the block.
        var item = player.getInventory().getItem(block);
        if (item == null) {
            return 1;
        }

        // Get the current position.
        var current = player.getPosition();
        // Add the delta.
        current = current.add(x, y, z);

        // Break the block.
        player.place(item, current.toInt());
        return 0;
    }

    /**
     * Adds a behavior from a player.
     *
     * @param username The username of the player.
     * @param behavior The behavior to remove.
     */
    public void addBehavior(String username, String behavior) {
        // Fetch the player.
        var player = this.getPlayer(username);

        // Add the behavior.
        player.getActions().getBehaviors().add(behavior);
        player.getScriptBackend().initBehaviors();
    }

    /**
     * Removes a behavior from a player.
     *
     * @param username The username of the player.
     * @param behavior The behavior to remove.
     */
    public void removeBehavior(String username, String behavior) {
        // Fetch the player.
        var player = this.getPlayer(username);

        // Remove the behavior.
        player.getActions().getBehaviors().remove(behavior);
        player.getScriptBackend().initBehaviors();
    }

    /**
     * Checks if a message is targeted at a player.
     *
     * @param message The message.
     * @param username The username of the player.
     * @return If the message is targeted at the player.
     */
    public boolean targetedAt(String message, String username) {
        return message.startsWith(username);
    }

    /**
     * Parses a command.
     *
     * @param message The message.
     * @param username The username of the player.
     * @return The command.
     */
    public LuaTable parseCommand(String message, String username) {
        var data = new LuaTable();

        // Remove the username.
        message = message.substring(username.length() + 1);
        // Split the message.
        var split = message.split(" ");
        // Add the data to the table.
        for (var entry : split) data.add(
                LuaValue.valueOf(entry));

        return data;
    }

    /**
     * Gets the distance between two positions.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @return The distance.
     */
    public float distance(LuaTable pos1, LuaTable pos2) {
        // Parse the first position.
        var x1 = pos1.get("x").tofloat();
        var y1 = pos1.get("y").tofloat();
        var z1 = pos1.get("z").tofloat();

        // Parse the second position.
        var x2 = pos2.get("x").tofloat();
        var y2 = pos2.get("y").tofloat();
        var z2 = pos2.get("z").tofloat();

        // Calculate the distance.
        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2) +
                        Math.pow(z1 - z2, 2));
    }

    /**
     * Interacts with a block.
     *
     * @param username The username of the player.
     * @param block The block.
     */
    public void interactBlock(String username, LuaTable block) {
        // Fetch the player.
        var player = this.getPlayer(username);
        // Get the position.
        var blockPos = EncodingUtils.tableToBlock(block);

        // Interact with the block.
        player.interact(blockPos);
    }

    /**
     * Closes the currently opened inventory.
     *
     * @param username The username of the player.
     * @return If the inventory was closed.
     */
    public boolean closeInventory(String username) {
        // Fetch the player.
        var player = this.getPlayer(username);

        // Get the inventory.
        var inventory = player.getViewingInventory();
        if (inventory == null) {
            return false;
        }

        // Close the inventory.
        inventory.closeInventory();
        return true;
    }

    /**
     * Gets the contents of the inventory the player is looking at.
     * If the player is not looking at an inventory, their own inventory is returned.
     * The flag 'selfInv' is set to true when applicable.
     *
     * @param username The username of the player.
     * @return The inventory contents.
     */
    public LuaTable getInventory(String username) {
        // Fetch the player.
        var player = this.getPlayer(username);
        var table = new LuaTable();

        // Get the inventory.
        List<ItemData> items;
        var inventory = player.getViewingInventory();
        if (inventory == null) {
            items = player.getInventory().getItems();
            table.set("selfInv", LuaValue.valueOf(true));
        } else {
            items = inventory.getItems();
            table.set("selfInv", LuaValue.valueOf(false));
        }

        // Add the items to the table.
        for (var i = 0; i < items.size() - 1; i++) {
            var item = items.get(i);
            if (item == null) item = ItemData.AIR;
            table.set(
                    LuaValue.valueOf(i),
                    EncodingUtils.itemToTable(item)
            );
        }

        return table;
    }
}
