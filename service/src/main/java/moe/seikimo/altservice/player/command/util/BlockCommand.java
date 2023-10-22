package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.AltBackend;
import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.server.ServerPlayer;
import moe.seikimo.altservice.utils.enums.PositionType;

import java.util.List;

public final class BlockCommand extends Command {
    public BlockCommand() {
        super("block");
    }

    @Override
    public void execute(Player player, ServerPlayer sender, List<String> args) {
        if (args.isEmpty()) {
            player.sendMessage("Usage: /block <relative|at> <position>");
            return;
        }

        try {
            var positionType = PositionType.valueOf(args.get(0).toUpperCase());
            var positionStr = String.join(" ", args.subList(1, args.size()));
            var position = PositionType.parse(
                    positionType,
                    player.getPosition().toInt(),
                    positionStr);

            this.sendMessage("Looking for block at " + position + "...");

            var block = player.getWorld().getBlockAt(0, position);
            if (block == null) {
                player.sendMessage("No block found at " + positionStr + ".");
                return;
            }

            player.sendMessage("Block at " + positionStr + ": " + block.getIdentifier());
        } catch (Exception exception) {
            player.sendMessage("Exception encountered. See console for details.");
            AltBackend.getLogger().warn("Unable to get block.", exception);
        }
    }
}
