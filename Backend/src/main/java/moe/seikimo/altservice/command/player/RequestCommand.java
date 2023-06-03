package moe.seikimo.altservice.command.player;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.PlayerManager;
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

        // Check if the username is valid.
        var username = args.get(0);
        if (PlayerManager.isPlayerOnline(username)) {
            this.sendMessage("That player is already online.");
            return;
        }

        // Check to see if a lifetime was specified.
        var lifetime = -1L;
        if (args.size() > 1) {
            try {
                lifetime = TimeUtils.parseInputTime(args.get(1));
            } catch (IllegalArgumentException ignored) {
                this.sendMessage("Invalid lifetime specified.");
                return;
            }
        }

        this.sendMessage("Requesting player " + username + " for " +
                (lifetime == -1L ? "indefinite" : args.get(1) + "."));
        // Create the player instance.
        var player = PlayerManager.createPlayer(username, lifetime);
        player.login();
    }
}
