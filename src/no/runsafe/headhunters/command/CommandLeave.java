package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 2-5-13
 * Time: 20:04
 */
public class CommandLeave extends PlayerCommand {

    private Core core;

    public CommandLeave(Core core){
        super("leave", "Leaves the current game", null);
        this.core = core;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {
        core.leave(executor);
        return null;
    }
}
