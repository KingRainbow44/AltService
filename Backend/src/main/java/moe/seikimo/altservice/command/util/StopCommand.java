package moe.seikimo.altservice.command.util;

import moe.seikimo.altservice.command.Command;

import java.util.List;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");
    }

    @Override
    public void execute(List<String> args) {
        System.exit(0);
    }
}
