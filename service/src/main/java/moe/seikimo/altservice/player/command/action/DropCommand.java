package moe.seikimo.altservice.player.command.action;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;

import java.util.List;

public final class DropCommand extends Command {
    public DropCommand() {
        super("drop");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.size() == 0) {
            player.sendMessage("Please specify an item.");
            return;
        }

        // Get the item to drop.
        var item = player.getInventory().getItem(args.get(0));
        if (item == null) {
            player.sendMessage("Item not found.");
            return;
        }

        // Check if a quantity was specified.
        var quantity = 1;
        if (args.size() > 1) {
            try {
                quantity = Integer.parseInt(args.get(1));
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid quantity.");
                return;
            }
        }

        // Drop the item.
        player.getInventory().drop(item,
                Math.min(quantity, item.getCount()));
    }
}
