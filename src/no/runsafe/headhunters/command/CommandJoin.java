package no.runsafe.headhunters.command;


import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 23-4-13
 * Time: 13:43
 */
public class CommandJoin extends PlayerCommand {

    Core core;

    public CommandJoin(Core core){
        super("join", "CommandJoin a headhunters game", "headhunters.play");
        this.core = core;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {

       return this.core.join(executor);


    }
}
