package moe.seikimo.altservice.player.command;

import moe.seikimo.altservice.Configuration;
import moe.seikimo.altservice.command.SimpleCommandMap;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;

import java.util.Arrays;

public final class PlayerCommandMap extends SimpleCommandMap {

    @Override
    public void invoke(String input) {
        if (!Configuration.get().isRespondToCommands()) return;

        for (Player player : PlayerManager.getPlayers()) {
            this.invoke(player, input);
        }
    }

    /**
     * Parses and executes a command from in-game.
     *
     * @param player The player which should execute the command.
     * @param input The command input.
     */
    public void invoke(Player player, String input) {
        if (!Configuration.get().isRespondToCommands()) return;

        var split = input.split(" ");
        if (split.length < 1) return;

        // Parse the command label.
        var label = split[0];
        var arguments = Arrays.asList(split)
                .subList(1, split.length);

        // Get the command.
        var command = this.getCommand(label);
        if (command == null) return;

        // Execute the command.
        command.execute(player, arguments);
    }
}
