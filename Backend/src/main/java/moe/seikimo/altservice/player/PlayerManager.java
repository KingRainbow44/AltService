package moe.seikimo.altservice.player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerManager {
    /**
     * Bound by Username -> Player Instance.
     */
    private static final Map<String, Player> players
            = new ConcurrentHashMap<>();

    /**
     * @param username The username to check.
     * @return Whether the player is online.
     */
    public static boolean isPlayerOnline(String username) {
        return PlayerManager.players.containsKey(username);
    }

    /**
     * Creates a new player instance.
     *
     * @param username The username.
     * @param lifetime The lifetime.
     * @return The player instance.
     */
    public static Player createPlayer(String username, long lifetime) {
        return PlayerManager.players.computeIfAbsent(username,
                k -> new Player(username, lifetime));
    }

    /**
     * @return A list of all player instances.
     */
    public static List<Player> getPlayers() {
        return List.copyOf(PlayerManager.players.values());
    }

    /**
     * Destroys a player instance.
     * This will disconnect the player from the server.
     *
     * @param username The username.
     */
    public static void destroyPlayer(String username) {
        var player = PlayerManager.players.remove(username);
        if (player != null) player.disconnect();
    }

    /**
     * Destroys a player instance.
     * This will disconnect the player from the server.
     *
     * @param player The player instance.
     */
    public static void destroyPlayer(Player player) {
        PlayerManager.destroyPlayer(player.getUsername());
    }

    /**
     * Destroys all player instances.
     */
    public static void destroyAll() {
        PlayerManager.players.values()
                .forEach(PlayerManager::destroyPlayer);
    }
}
