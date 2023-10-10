package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

public final class RotateCommand extends Command {
    public RotateCommand() {
        super("rotate");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.size() < 1) {
            player.sendMessage("A direction is required.");
            return;
        }

        // Get the rotation from the direction.
        var current = player.getRotation();
        var rotation = switch (args.get(0).toLowerCase()) {
            default -> null;
            case "up" -> current.add(0, 1, 0);
            case "down" -> current.sub(0, 1, 0);
            case "left" -> current.add(1, 0, 0);
            case "right" -> current.sub(1, 0, 0);
        };

        if (rotation == null) {
            player.sendMessage("An invalid direction was provided.");
            return;
        }

        // Update the player's position.
        player.move(player.getPosition(), rotation);
    }
}
