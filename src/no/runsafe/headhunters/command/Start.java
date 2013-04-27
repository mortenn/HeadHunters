package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 19:08
 */
public class Start extends PlayerCommand {

    Core core;

    public Start(Core core){
        super("start", "Forces headhunters match to start", "headunters.force-start", "time");
        this.core = core;

    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {



        return core.start(Integer.valueOf(parameters.get("time")));
    }
}