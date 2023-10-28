package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

public final class MoveCommand extends Command {
    public MoveCommand() {
        super("move");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.isEmpty()) {
            player.sendMessage("A direction is required.");
            return;
        }

        // Get the position from the direction.
        var current = player.getPosition();
        var position = switch (args.get(0).toLowerCase()) {
            default -> null;
            case "up" -> current.add(0, 0.2, 0);
            case "down" -> current.sub(0, 0.2, 0);
            case "west" -> current.sub(0.2, 0, 0);
            case "east" -> current.add(0.2, 0, 0);
            case "north" -> current.sub(0, 0, 0.2);
            case "south" -> current.add(0, 0, 0.2);
        };

        if (position == null) {
            player.sendMessage("An invalid direction was provided.");
            return;
        }

        // Update the player's position.
        player.move(position, player.getRotation());
    }
}
