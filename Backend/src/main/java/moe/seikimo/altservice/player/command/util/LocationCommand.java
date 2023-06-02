package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;

import java.util.List;

public final class LocationCommand extends Command {
    public LocationCommand() {
        super("location");
    }

    @Override
    public void execute(Player player, List<String> args) {
        player.sendMessage("I am at &b" + player.getLocation().toString() + "&r.");
    }
}
