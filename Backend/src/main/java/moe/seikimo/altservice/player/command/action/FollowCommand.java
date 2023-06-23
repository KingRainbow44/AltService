package moe.seikimo.altservice.player.command.action;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.utils.enums.TargetAction;

import java.util.List;

public final class FollowCommand extends Command {
    public FollowCommand() {
        super("follow");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.size() < 1) {
            this.sendMessage("Please specify a username.");
            return;
        }

        // Fetch the target player.
        var username = args.get(0);
        if (username.equals("noone")) {
            player.setTarget(null);
            player.setTargetAction(TargetAction.NONE);
            player.sendMessage("No longer following anyone.");
            return;
        }

        var target = username.equals("me") ? sender :
                player.getPeerByUsername(username);

        if (target == null) {
            player.sendMessage("That player is not online.");
            return;
        }

        // Set the player's target.
        player.setTarget(target);
        player.setTargetAction(TargetAction.FOLLOW);
        player.sendMessage("Now following " + target.getUsername() + ".");
    }
}
