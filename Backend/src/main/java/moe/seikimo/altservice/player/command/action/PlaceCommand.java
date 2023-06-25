package moe.seikimo.altservice.player.command.action;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;

import java.util.List;

public final class PlaceCommand extends Command {
    public PlaceCommand() {
        super("place");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.size() == 0) {
            player.sendMessage("Please specify a direction and item.");
            return;
        }
        if (args.size() == 1) {
            player.sendMessage("Please specify an item.");
            return;
        }

        // Get the direction to place the block.
        var direction = args.get(0);
        var current = player.getPosition().toInt()
                .sub(0, GameConstants.OFFSET, 0);
        var block = switch (direction) {
            case "up" -> current.add(0, 1, 0);
            case "down" -> current.sub(0, 1, 0);
            case "north" -> current.sub(0, 0, 1);
            case "south" -> current.add(0, 0, 1);
            case "east" -> current.add(1, 0, 0);
            case "west" -> current.sub(1, 0, 0);
            default -> current;
        };

        // Get the item to place.
        var item = player.getInventory().getItems().stream()
                .filter(i -> i.getDefinition().getIdentifier()
                        .equalsIgnoreCase(args.get(1)))
                .findFirst().orElse(null);
        if (item == null) {
            player.sendMessage("Item not found.");
            return;
        }

        player.place(item, block);
    }
}
