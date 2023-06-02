package moe.seikimo.altservice.player.command.util;

import moe.seikimo.altservice.command.Command;
import moe.seikimo.altservice.player.Player;
import moe.seikimo.altservice.player.PlayerManager;

import java.util.List;

public final class DisconnectCommand extends Command {
    public DisconnectCommand() {
        super("disconnect");
    }

    @Override
    public void execute(Player player, List<String> args) {
        PlayerManager.destroyPlayer(player);
    }
}
