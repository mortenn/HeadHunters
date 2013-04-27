package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 22:42
 */
public class Spawn extends PlayerCommand {


    private Core core;

    public Spawn(Core core){
        super("spawn", "Sets the spawn point for in the wait room", "headhunters.set-spawn");
        this.core = core;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {

        return core.setSpawn(executor);


    }
}
