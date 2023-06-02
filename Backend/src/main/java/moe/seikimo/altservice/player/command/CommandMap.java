package moe.seikimo.altservice.player.command;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.player.command.util.DisconnectCommand;
import moe.seikimo.altservice.player.command.util.LocationCommand;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class CommandMap {
    private static final Map<String, Command> commands = new HashMap<>() {{
        // Command defaults should be registered here.
        this.put("location", new LocationCommand());
        this.put("disconnect", new DisconnectCommand());
    }};

    private static long lastGlobal = System.currentTimeMillis();

    /**
     * Get a command by its label.
     *
     * @param label The command label.
     * @return The command, or null if not found.
     */
    @Nullable
    public static Command getCommand(String label) {
        return CommandMap.commands.get(label);
    }

    /**
     * Parses and executes a command from in-game.
     * This executes the command on all bots.
     *
     * @param input The command input.
     */
    public static void invoke(String input) {
        // Check if the command is global.
        if (System.currentTimeMillis() - CommandMap.lastGlobal < 300) return;
        CommandMap.lastGlobal = System.currentTimeMillis();

        for (var player : PlayerManager.getPlayers())
            CommandMap.invoke(player, input);
    }

    /**
     * Parses and executes a command from in-game.
     *
     * @param player The player which should execute the command.
     * @param input The command input.
     */
    public static void invoke(Player player, String input) {
        var split = input.split(" ");
        if (split.length < 1) return;

        // Parse the command label.
        var label = split[0];
        var arguments = Arrays.asList(split)
                .subList(1, split.length);

        // Get the command.
        var command = CommandMap.getCommand(label);
        if (command == null) return;

        // Execute the command.
        command.execute(player, arguments);
    }
}
