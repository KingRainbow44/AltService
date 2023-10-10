package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

public final class BehaviorCommand extends Command {
    public BehaviorCommand() {
        super("behavior");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.size() < 1) {
            player.sendMessage("Invalid usage!");
            return;
        }

        var behavior = args.get(0);
        switch (behavior) {
            case "on" -> {
                player.getActions().setBehave(true);
                player.sendMessage("Behaviors enabled!");
            }
            case "off" -> {
                player.getActions().setBehave(false);
                player.sendMessage("Behaviors disabled!");
            }
            case "add" -> {
                if (args.size() < 2) {
                    player.sendMessage("Invalid usage!");
                    return;
                }

                var behaviorName = args.get(1);
                player.getActions().getBehaviors().add(behaviorName);
                player.sendMessage("Behavior added!");

                player.getScriptBackend().initBehaviors();
            }
            case "remove" -> {
                if (args.size() < 2) {
                    player.sendMessage("Invalid usage!");
                    return;
                }

                var behaviorName = args.get(1);
                player.getActions().getBehaviors().remove(behaviorName);
                player.sendMessage("Behavior removed!");

                player.getScriptBackend().initBehaviors();
            }
            case "reinit" -> {
                player.getScriptBackend().initBehaviors();
                player.sendMessage("Re-initialized behaviors!");
            }
            case "step" -> {
                var times = 1;
                if (args.size() > 1) {
                    try {
                        times = Integer.parseInt(args.get(1));
                    } catch (NumberFormatException ignored) {
                    }
                }

                for (var i = 0; i < times; i++) {
                    player.getScriptBackend().tickBehaviors();
                }
                player.sendMessage("Ticked each behavior by " + times + ".");
            }
            case "list" -> player.sendMessage("Behaviors enabled: " +
                    String.join(", ", player.getActions().getBehaviors()));
        }
    }
}
