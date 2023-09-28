package moe.seikimo.altservice.command.util;

import moe.seikimo.altservice.Configuration;
import moe.seikimo.altservice.command.Command;

import java.util.List;

public final class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload");
    }

    @Override
    public void execute(List<String> args) {
        Configuration.load();
        this.sendMessage("Data reloaded.");
    }
}
