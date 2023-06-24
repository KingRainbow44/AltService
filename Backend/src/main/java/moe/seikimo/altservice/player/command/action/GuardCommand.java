package moe.seikimo.altservice.player.command.action;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

public final class GuardCommand extends Command {
    public GuardCommand() {
        super("guard");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        var actions = player.getActions();

        if (args.size() < 1) {
            if (actions.isGuard()) {
                actions.setGuard(false);
                player.sendMessage("No longer guarding.");
            } else {
                actions.setGuard(true);
                player.sendMessage("Now guarding.");
            }
        } else {
            if (!actions.isGuard())
                actions.setGuard(true);

            switch (args.get(0)) {
                case "players" -> {
                    var newState = !actions.isGuardPlayers();
                    actions.setGuardPlayers(newState);
                    player.sendMessage((newState ? "Now" : "No longer") + " guarding players.");
                }
                case "mobs" -> {
                    var newState = !actions.isGuardMobs();
                    actions.setGuardMobs(newState);
                    player.sendMessage((newState ? "Now" : "No longer") + " guarding mobs.");
                }
                default -> player.sendMessage("Invalid thing to guard.");
            }
        }
    }
}
