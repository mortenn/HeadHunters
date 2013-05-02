package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 2-5-13
 * Time: 18:39
 */
public class Stop extends PlayerCommand {

    private Core core;

    public Stop(Core core){
        super("stop", "Stops the current game", "headhunters.stop");

        this.core = core;

    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {
        this.core.stop(executor);
        return null;
    }
}
