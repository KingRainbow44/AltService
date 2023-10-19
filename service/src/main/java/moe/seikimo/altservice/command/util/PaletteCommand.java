package moe.seikimo.altservice.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.utils.objects.absolute.GameConstants;

import java.util.List;

public final class PaletteCommand extends Command {
    public PaletteCommand() {
        super("palette");
    }

    @Override
    public void execute(List<String> args) {
        var definitions = GameConstants.BLOCK_DEFINITIONS;

        int startAt = 0, max = 10;
        if (args.size() >= 2) try {
            startAt = Integer.parseInt(args.get(0));
            max = Integer.parseInt(args.get(1));
        } catch (NumberFormatException ignored) {
            this.sendMessage("Invalid numbers provided.");
            return;
        }

        for (var i = startAt; i < startAt + max; i++) {
            var definition = definitions.get(i);
            if (definition == null) continue;

            this.sendMessage(i + " -> " + definition.getIdentifier());
        }
    }
}
