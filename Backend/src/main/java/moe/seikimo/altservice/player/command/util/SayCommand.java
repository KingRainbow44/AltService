package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.PlayerManager;

import java.util.List;

public final class SayCommand extends Command {
    public SayCommand() {
        super("say");
    }

    @Override
    public void execute(List<String> args) {
        if (args.size() < 2) {
            this.sendMessage("Usage: say <player> <message>");
            return;
        }

        // Send the message to the server.
        var player = PlayerManager.createPlayer(args.get(0), -1);
        player.sendMessage(String.join(" ", args.subList(1, args.size())));
    }
}
