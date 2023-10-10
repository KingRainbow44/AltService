package moe.seikimo.altservice.command.player;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

public final class DisconnectCommand extends Command {
    public DisconnectCommand() {
        super("disconnect");
    }

    @Override
    public void execute(List<String> args) {
        // Check if a username was specified.
        if (args.isEmpty()) {
            this.sendMessage("Please specify a username.");
            return;
        }

        var username = args.get(0);

        // Check if all players are being targetted.
        if (username.equals("all")) {
            PlayerManager.getPlayers()
                    .forEach(PlayerManager::destroyPlayer);
            return;
        }

        // Check if the username is valid.
        if (!PlayerManager.isPlayerOnline(username)) {
            this.sendMessage("That player is not online.");
            return;
        }

        // Disconnect the player.
        PlayerManager.destroyPlayer(username);
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        PlayerManager.destroyPlayer(player);
    }
}
