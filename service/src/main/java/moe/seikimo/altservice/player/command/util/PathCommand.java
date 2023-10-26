package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.utils.ThreadUtils;
import org.cloudburstmc.math.vector.Vector3f;

import java.util.List;

public final class PathCommand extends Command {
    public PathCommand() {
        super("path");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        // Check if arguments were provided.
        if (args.size() < 3) {
            player.sendMessage("Usage: /path <x> <y> <z>");
            return;
        }

        // Parse the arguments.
        var x = Integer.parseInt(args.get(0));
        var y = Integer.parseInt(args.get(1));
        var z = Integer.parseInt(args.get(2));

        // Create the target position.
        var targetPosition = Vector3f.from(x, y, z);

        // Attempt to pathfind.
        player.pathfindTo(targetPosition, nodes -> {
            if (nodes.isEmpty()) {
                player.sendMessage("Failed to find a path.");
            } else {
                player.sendMessage("Found a path in " + nodes.size() + " steps.");

                // Attempt to walk the path.
                for (var node : nodes) {
                    player.sendMessage("Moving to " + node.getPosition() + "...");
                    player.move(node.getPosition().toFloat(), player.getRotation());
                    ThreadUtils.sleep(1000);
                }
            }
        });
        player.sendMessage("Pathfinding started.");
    }
}
