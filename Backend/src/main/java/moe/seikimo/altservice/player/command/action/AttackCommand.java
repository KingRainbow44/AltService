package moe.seikimo.altservice.player.command.action;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

public final class AttackCommand extends Command {
    public AttackCommand() {
        super("attack");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.size() < 1) {
            player.sendMessage("Please specify a username.");
            return;
        }

        // Fetch the target player.
        var username = args.get(0);
        if (username.equals("noone")) {
            player.setTarget(null);
            player.getActions().setAttack(false);
            player.sendMessage("No longer attacking anyone.");
            return;
        }

        var target = username.equals("me") ? sender :
                player.getPeerByUsername(username);

        if (target == null) {
            player.sendMessage("That player is not online.");
            return;
        }

        player.attack(target);

        // Set the player's target.
        player.setTarget(target);
        player.getActions().setAttack(true);
        player.sendMessage("Now attacking " + target.getUsername() + ".");
    }
}
