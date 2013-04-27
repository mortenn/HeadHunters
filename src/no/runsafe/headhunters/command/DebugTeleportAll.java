package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 23:08
 */
public class DebugTeleportAll extends PlayerCommand {


    private Core core;
    public DebugTeleportAll(Core core){
        super("tpa","Teleports all players","headhunters.debug-tpa");
        this.core = core;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {

        core.debugTeleportAll();

        return null;
    }
}
