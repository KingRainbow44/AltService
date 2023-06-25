package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.InteractPacket;

import java.util.List;

public final class InventoryCommand extends Command {
    public InventoryCommand() {
        super("inventory");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        var page = 1;
        if (args.size() > 0) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException exception) {
                switch (args.get(0)) {
                    case "open" -> {
                        // Open the inventory.
                        player.getInventory().openInventory();
                        player.sendMessage("Inventory opened.");
                    }
                    case "close" -> {
                        // Close the inventory.
                        player.getInventory().closeInventory();
                        player.sendMessage("Inventory closed.");
                    }
                    default -> player.sendMessage("Invalid page number.");
                }
                return;
            }
        }

        var inventory = player.getInventory();
        var items = inventory.getItems().stream()
                .filter(item -> item.getDefinition() != ItemDefinition.AIR)
                .toList();
        if (items.size() == 0) {
            player.sendMessage("I have no items in my inventory.");
            return;
        }

        var maxPages = items.size() / 10;
        var displayItems = items.subList(
                (page * 8) - 8, Math.min(items.size(), page * 8)
        );

        player.sendMessage("I have the following items in my inventory:");
        for (var item : displayItems) {
            player.sendMessage("- %s (qt. %s)".formatted(
                    item.getDefinition().getIdentifier(), item.getCount()));
        }
        if (maxPages > 1) {
            player.sendMessage("Page %s of %s".formatted(page, maxPages));
        }
    }
}
