package no.runsafe.headhunters.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 2-5-13
 * Time: 18:39
 */
public class CommandStop extends PlayerCommand {

    private Core core;

    public CommandStop(Core core){
        super("stop", "Stops the current game", "headhunters.stop");

        this.core = core;

    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {
        this.core.stop(executor);
        return null;
    }
}
