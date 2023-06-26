package moe.seikimo.altservice.command.player;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.utils.ThreadUtils;
import moe.seikimo.altservice.utils.TimeUtils;

import java.util.List;

public final class RequestCommand extends Command {
    public RequestCommand() {
        super("request");
    }

    @Override
    public void execute(List<String> args) {
        // Check if a username was specified.
        if (args.size() < 1) {
            this.sendMessage("Please specify a username.");
            return;
        }

        // Check to see if a lifetime was specified.
        var lifetime = -1L;
        var lifetimeS = "an indefinite time";
        if (args.size() > 1) {
            try {
                lifetime = TimeUtils.parseInputTime(
                        args.get(args.size() - 1));
                lifetimeS = args.get(args.size() - 1);
            } catch (IllegalArgumentException ignored) { }
        }

        // Get the players specified.
        var players = args.subList(0, lifetime == -1L ?
                args.size() : args.size() - 1);
        for (var player : players) {
            // Check if the player is online.
            if (PlayerManager.isPlayerOnline(player)) {
                this.sendMessage("Player " + player + " is already online.");
                continue;
            }

            // Request the player.
            this.sendMessage("Requesting player " + player + " for " + lifetimeS + ".");
            // Create the player instance.
            var instance = PlayerManager.createPlayer(player, lifetime);
            instance.login();

            if (players.size() > 1) ThreadUtils.sleep(5000L);
        }
    }
}
